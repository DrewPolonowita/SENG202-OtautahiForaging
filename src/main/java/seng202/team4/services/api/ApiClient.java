package seng202.team4.services.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.api.TreeApi.TreeApiResponse;
import seng202.team4.models.api.Request;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ApiClient implements ApiClientInterface {

    private static final Logger log = LogManager.getLogger(ApiClient.class);

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
                log.error(scanner.nextLine());
            }
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(connection.getInputStream());
        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(
                String.valueOf(stringBuilder), responseType);
    }

    @Override
    public <T> Request<T> get(String url, TypeReference<T> responseType) {
        try {
            HttpsURLConnection connection = createConnection(url);
            try {
                T apiResponse = getJsonData(connection, responseType);
                return new Request<>(apiResponse, connection.getResponseCode());
            } catch (IOException e) {
                return new Request<>(null, connection.getResponseCode());
            }
        } catch (IOException e) {
            return new Request<>(null, -1);
        }

    }


    @Override
    public <T> Request<T> getOauth(String url, TypeReference<T> responseType, String oauthHeader) {
        try {
            HttpsURLConnection connection = createOAuthConnection(url, oauthHeader);
            try {
                T apiResponse = getJsonData(connection, responseType);
                return new Request<>(apiResponse, connection.getResponseCode());
            } catch (IOException e) {
                return new Request<>(null, connection.getResponseCode());
            }
        } catch (IOException e) {
            return new Request<>(null, -1);
        }

    }
}
