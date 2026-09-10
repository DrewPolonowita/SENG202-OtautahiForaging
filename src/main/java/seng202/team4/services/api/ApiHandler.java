package seng202.team4.services.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import seng202.team4.models.api.TreeApi.TreeApiResponse;
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
 * TODO Delete and use new interfaces for remaining usages
 * @author Team 4
 * @deprecated deprecated
 */
@Deprecated(forRemoval = true)
public class ApiHandler {

    static final String API_LINK = "https://gis.ccc.govt.nz/server/rest/services/OpenData/Tree/FeatureServer/0/";

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
     * Takes an {@link HttpsURLConnection} from an API request and returns an {@link TreeApiResponse} with the JSON data
     *
     * @param connection an object with request information and JSON data
     * @return null if the connection is not HTTP ok and an object with the JSON data if the connection is HTTP ok
     * @throws IOException if the connection is invalid
     */
    private TreeApiResponse getJsonData(HttpsURLConnection connection) throws IOException {
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
                String.valueOf(stringBuilder), new TypeReference<TreeApiResponse>() {

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
     * Takes a {@link String} of where clauses and of outField clauses for an
     * ARC-GIS API and returns the URL encoded query link bounded by coordinates provided in lat and lon
     *
     * @param params an ARC-GIS where clause e.g. TreeID=38083 and Genus='Malus'
     * @param outFields and ARC-GIS outField clause e.g. TreeID,Ownership,Genus
     * @return a {@link String} to be appended to the end of an API link
     */
    private String urlEncodeParams(
            String params, String outFields, int maxTuples, double north, double east, double south, double west
    ) {
        String url = urlEncodeParams(params, outFields, maxTuples)
                + "&geometryType=esriGeometryEnvelope&spatialRel=esriSpatialRelIntersects&inSR=4326&"
                + "geometry=" + URLEncoder.encode(
                String.format("%f,%f,%f,%f", west, south, east, north), StandardCharsets.UTF_8
        );

        System.out.println(API_LINK + url);

        return url;
    }

    /**
     * Makes a request to the API and returns an {@link TreeApiResponse} with the JSON data
     *
     * @return {@link TreeApiResponse} if the request is valid, otherwise returns a null
     */
    public Request<TreeApiResponse> getTrees(String params, String outFields, int maxTuples) {
        try {
            HttpsURLConnection connection = createConnection(API_LINK
                    + urlEncodeParams(params, outFields, maxTuples));
            try {
                TreeApiResponse apiResponse = getJsonData(connection);
                return new Request<>(apiResponse, connection.getResponseCode());
            } catch (IOException e) {
                return new Request<>(null, connection.getResponseCode());
            }
        } catch (IOException e) {
            return new Request<>(null, -1);
        }
    }

    /**
     * Makes a request to the API and returns an {@link TreeApiResponse} with the JSON data
     *
     * @return {@link TreeApiResponse} if the request is valid, otherwise returns a null
     */
    public Request<TreeApiResponse> getTrees(
            String params, String outFields, int maxTuples,  double north, double east, double south, double west
    ) {
        try {
            HttpsURLConnection connection = createConnection(API_LINK
                    + urlEncodeParams(params, outFields, maxTuples, north, east, south, west));
            try {
                TreeApiResponse apiResponse = getJsonData(connection);
                return new Request<>(apiResponse, connection.getResponseCode());
            } catch (IOException e) {
                return new Request<>(null, connection.getResponseCode());
            }
        } catch (IOException e) {
            return new Request<>(null, -1);
        }
    }
}
