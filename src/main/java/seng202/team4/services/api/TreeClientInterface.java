package seng202.team4.services.api;

import seng202.team4.models.api.ApiResponse;
import seng202.team4.models.api.Request;

public interface TreeClientInterface {


    Request<ApiResponse> getTrees(String params, String outFields, int maxTuples);

    public Request<ApiResponse> getTrees(
            String params, String outFields, int maxTuples,  double north, double east, double south, double west
    );
}
