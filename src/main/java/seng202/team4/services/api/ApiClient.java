package seng202.team4.services.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import seng202.team4.models.api.ApiResponseWrapper;
import seng202.team4.models.api.TreeApi.TreeApiResponse;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Implementation for accessing HTTPS Api interface
 * @author Team 4
 */
public class ApiClient implements ApiClientInterface {

    private static final Logger log = LogManager.getLogger(ApiClient.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Creates a connection to the server
     *
     * @return an object with details about connection status and JSON information
     * @throws IOException if the URL is invalid
     */
    private HttpsURLConnection createConnection(String url) throws IOException {
        URL urlObject = new URL(url);
        HttpsURLConnection connection = (HttpsURLConnection) urlObject.openConnection();
        connection.setRequestMethod("GET");
        return connection;
    }

    /**
     * Creates a connection to FatSecret API
     * @param url url used to call function needed
     * @param oauthHeader authentication header
     * @return HttpsURLConnection object including response code and data
     */
    private HttpsURLConnection createOAuthConnection(String url, String oauthHeader) throws IOException {
        URL urlObject = new URL(url);
        HttpsURLConnection connection = (HttpsURLConnection) urlObject.openConnection();
        connection.setRequestProperty("Authorization", oauthHeader);
        connection.setRequestMethod("GET");
        return connection;
    }

    /**
     * Takes an {@link HttpsURLConnection} from an API request and returns an {@link TreeApiResponse} with the JSON data
     *
     * @param connection an object with request information and JSON data
     * @return null if the connection is not HTTP ok and an object with the JSON data if the connection is HTTP ok
     * @throws IOException if the connection is invalid
     */
    private <T> T getJsonData(HttpsURLConnection connection, TypeReference<T> responseType) throws IOException {
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            Scanner scanner = new Scanner(connection.getErrorStream());

            while (scanner.hasNextLine()) {
                String errorLine = scanner.nextLine();
                log.error("{}", errorLine);
            }
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(connection.getInputStream());
        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine());
        }
        return objectMapper.readValue(
                String.valueOf(stringBuilder), responseType);
    }

    /**
     * Makes a request to the API and returns an {@link ApiResponseWrapper} with the JSON data
     *
     * @return {@link ApiResponseWrapper} if the request is valid, otherwise returns a null
     */
    @Override
    public <T> ApiResponseWrapper<T> get(String url, TypeReference<T> responseType) {
        try {
            HttpsURLConnection connection = createConnection(url);
            return getRequest(responseType, connection);
        } catch (IOException _) {
            return new ApiResponseWrapper<>(null, -1);
        }

    }

    /**
     * Get from api with oauth authentication
     * @param url url request
     * @param responseType type of record object to be create
     * @param oauthHeader oauth header string
     * @return Request object of type request
     */
    @Override
    public <T> ApiResponseWrapper<T> getOauth(String url, TypeReference<T> responseType, String oauthHeader) {
        try {
            HttpsURLConnection connection = createOAuthConnection(url, oauthHeader);
            return getRequest(responseType, connection);
        } catch (IOException _) {
            return new ApiResponseWrapper<>(null, -1);
        }

    }

    /**
     * Wraps a http api call in its {@link ApiResponseWrapper} object
     * @param responseType expected type
     * @param connection the open http connection
     * @param <T> the response body type
     * @return a request wrapper with the parsed response
     * @throws IOException if the response code can't be read
     */
    private <T> @NotNull ApiResponseWrapper<T> getRequest(TypeReference<T> responseType, HttpsURLConnection connection)
            throws IOException {
        try {
            T apiResponse = getJsonData(connection, responseType);
            return new ApiResponseWrapper<>(apiResponse, connection.getResponseCode());
        } catch (IOException _) {
            return new ApiResponseWrapper<>(null, connection.getResponseCode());
        }
    }
}
