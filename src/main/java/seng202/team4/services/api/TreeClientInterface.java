package seng202.team4.services.api;

import seng202.team4.models.api.TreeApi.TreeApiResponse;
import seng202.team4.models.api.Request;

/**
 * Interface containing methods that can be used to access data from the Christchurch
 * City Council database
 *
 * @author Team 4
 */
public interface TreeClientInterface {

    /**
     * Makes a request to the API and returns an {@link TreeApiResponse} with the JSON data
     *
     * @return {@link TreeApiResponse} if the request is valid, otherwise returns a null
     */
    Request<TreeApiResponse> getTrees(String params, String outFields, int maxTuples);

    /**
     * Makes a request to the API and returns an {@link TreeApiResponse} with the JSON data
     * TODO Update function name and usage
     * @return {@link TreeApiResponse} if the request is valid, otherwise returns a null
     */
    Request<TreeApiResponse> getTrees(
            String params, String outFields, int maxTuples,  double north, double east, double south, double west
    );
}
