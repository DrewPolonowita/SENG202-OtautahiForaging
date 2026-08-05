package seng202.team4.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import seng202.team4.models.api.ApiResponse;
import seng202.team4.models.api.Request;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * A service class to create a connection to the tree API and get the required data
 *
 * @author Drew Polonowita
 */
public class ApiHandler {
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
     * Takes an {@link HttpsURLConnection} from an API request and returns an {@link ApiResponse} with the JSON data
     *
     * @param connection an object with request information and JSON data
     * @return null if the connection is not HTTP ok and an object with the JSON data if the connection is HTTP ok
     * @throws IOException if the connection is invalid
     */
    private ApiResponse getJsonData(HttpsURLConnection connection) throws IOException {
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(connection.getInputStream());
        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(
                String.valueOf(stringBuilder), new TypeReference<ApiResponse>() {

                });
    }

    /**
     * Takes a {@link String} of where clauses and of outField clauses for an
     * ARC-GIS API and returns the URL encoded query link
     *
     * @param params an ARC-GIS where clause e.g. TreeID=38083 and Genus='Malus'
     * @param outFields and ARC-GIS outField clause e.g. TreeID,Ownership,Genus
     * @return a {@link String} to be appended to the end of an API link
     */
    public String urlEncodeParams(String params, String outFields, int maxTuples) {
        return "query?where=" + URLEncoder
                .encode(params.isEmpty() ? "1=1" : params, StandardCharsets.UTF_8)
                .replace("+", "%20")
                + "&outFields=" + URLEncoder
                .encode(outFields.isEmpty() ? "*" : outFields, StandardCharsets.UTF_8)
                + String.format("&resultRecordCount=%d", maxTuples) + "&f=geojson";
    }

    /**
     * Makes a request to the API and returns an {@link ApiResponse} with the JSON data
     *
     * @return {@link ApiResponse} if the request is valid, otherwise returns a null
     */
    public Request<ApiResponse> getTrees(String params, String outFields, int maxTuples) {
        try {
            Dotenv dotenv = Dotenv.load();
            HttpsURLConnection connection = createConnection(dotenv.get("API_LINK")
                    + urlEncodeParams(params, outFields, maxTuples));
            try {
                ApiResponse apiResponse = getJsonData(connection);
                return new Request<>(apiResponse, connection.getResponseCode());
            } catch (IOException e) {
                return new Request<>(null, connection.getResponseCode());
            }
        } catch (IOException e) {
            return new Request<>(null, -1);
        }
    }
}
