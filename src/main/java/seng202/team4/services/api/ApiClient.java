package seng202.team4.services.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import seng202.team4.models.api.ApiResponseWrapper;
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
    public static final int ERROR = -1;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Creates an HTTPS GET connection to the server
     *
     * @param url the url to connect to
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
     * Creates an authenticated HTTPS GET connection (to FatSecret API)
     * @param url url used to call function needed
     * @param oauthHeader authentication header
     * @return HttpsURLConnection object including response code and data
     */
    private HttpsURLConnection createConnection(String url, String oauthHeader) throws IOException {
        URL urlObject = new URL(url);
        HttpsURLConnection connection = (HttpsURLConnection) urlObject.openConnection();
        connection.setRequestProperty("Authorization", oauthHeader);
        connection.setRequestMethod("GET");
        return connection;
    }

    /**
     * Takes an {@link HttpsURLConnection} from an API request and parses response to the requested response type
     *
     * @param connection an object with request information and JSON data
     * @param responseType the type to turn the JSON response into
     * @return null if the connection is not HTTP ok and an object with the JSON data if the connection is HTTP ok
     * @throws IOException if the response cant be read or deserialized
     */
    private <T> T getJsonData(HttpsURLConnection connection, TypeReference<T> responseType) throws IOException {
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            if (connection.getErrorStream() != null) {
                try (Scanner scanner = new Scanner(connection.getErrorStream())) {
                    while (scanner.hasNextLine()) {
                        String errorLine = scanner.nextLine();
                        log.error("{}", errorLine);
                    }
                }
            } else {
                log.error("API request failed with code {}", connection.getResponseCode());
            }
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();
        try (Scanner scanner = new Scanner(connection.getInputStream())) {
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }
        }

        return objectMapper.readValue(String.valueOf(stringBuilder), responseType);
    }


    /**
     * Makes an unauthenticated request to the API and returns an {@link ApiResponseWrapper} with the JSON data
     * @param url URL to request
     * @param responseType the expected response type
     * @return {@link ApiResponseWrapper} with response data and HTTP response code, or error code if request fails
     */
    @Override
    public <T> ApiResponseWrapper<T> get(String url, TypeReference<T> responseType) {
        HttpsURLConnection connection = null;
        try {
            connection = createConnection(url);
            return getRequest(responseType, connection);
        } catch (IOException e) {
            log.warn("API request failed for {}", url, e);
            return new ApiResponseWrapper<>(null, ERROR);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

    /**
     * Get from api with oauth authentication
     * @param url url request
     * @param responseType expected response type
     * @param oauthHeader oauth 1.0 header string
     * @return {@link ApiResponseWrapper} with response data and HTTP response code, or error code if request fails
     */
    @Override
    public <T> ApiResponseWrapper<T> get(String url, TypeReference<T> responseType, String oauthHeader) {
        HttpsURLConnection connection = null;

        try {
            connection = createConnection(url, oauthHeader);
            return getRequest(responseType, connection);
        } catch (IOException e) {
            log.warn("API request failed for {}", url, e);
            return new ApiResponseWrapper<>(null, ERROR);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

    /**
     * Wraps a http api call in its {@link ApiResponseWrapper} object
     * @param responseType expected type
     * @param connection the open http connection
     * @param <T> the response body type
     * @return a wrapper with parsed response and HTTP response code
     * @throws IOException if the response code can't be read
     */
    private <T> @NotNull ApiResponseWrapper<T> getRequest(TypeReference<T> responseType, HttpsURLConnection connection) throws IOException {
        try {
            T apiResponse = getJsonData(connection, responseType);
            return new ApiResponseWrapper<>(apiResponse, connection.getResponseCode());
        } catch (IOException e) {
            log.warn("Failed to read API response", e);
            return new ApiResponseWrapper<>(null, connection.getResponseCode());
        }
    }
}
