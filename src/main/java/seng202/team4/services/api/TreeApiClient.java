package seng202.team4.services.api;

import com.fasterxml.jackson.core.type.TypeReference;
import seng202.team4.models.api.TreeApi.TreeApiResponse;
import seng202.team4.models.api.Request;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Implementation for TreeClientInterface, used to link specifically to the Christchurch
 * City Council's arcgis tree api
 * @author Team 4
 */
public class TreeApiClient implements TreeClientInterface {

    static final String API_LINK = "https://gis.ccc.govt.nz/server/rest/services/OpenData/Tree/FeatureServer/0/";


    private final ApiClientInterface apiClient;

    /**
     * Constructor
     * @param apiClient Generic api client for use to connect to the api
     */
    public TreeApiClient(ApiClientInterface apiClient) {
        this.apiClient = apiClient;
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
                + "&geometryType=esriGeometryEnvelope&spatialRel=esriSpatialRelIntersects&inSR=4326&outSR=4326&"
                + "geometry=" + URLEncoder.encode(
                String.format("%f,%f,%f,%f",
                        west,
                        south,
                        east,
                        north
                ), StandardCharsets.UTF_8
        );

        System.out.println(API_LINK + url);

        return url;
    }


    /**
     * Makes a request to the API and returns an {@link TreeApiResponse} with the JSON data
     *
     * @return {@link TreeApiResponse} if the request is valid, otherwise returns a null
     */
    @Override
    public Request<TreeApiResponse> getTrees(
            String params, String outFields, int maxTuples,  double north, double east, double south, double west
    ) {
        String url = API_LINK
                + urlEncodeParams(params, outFields, maxTuples, north, east, south, west);

        return apiClient.get(url, new TypeReference<TreeApiResponse>() {

        });

    }

    /**
     * Makes a request to the API and returns an {@link TreeApiResponse} with the JSON data
     *
     * @return {@link TreeApiResponse} if the request is valid, otherwise returns a null
     */
    @Override
    public Request<TreeApiResponse> getTrees(String params, String outFields, int maxTuples) {
        String url = API_LINK
                + urlEncodeParams(params, outFields, maxTuples);
        return apiClient.get(url, new TypeReference<TreeApiResponse>() {

        });
    }


}


