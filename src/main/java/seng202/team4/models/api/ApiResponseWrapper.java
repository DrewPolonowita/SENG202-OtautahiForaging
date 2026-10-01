package seng202.team4.models.api;


/**
 * Encapsulates the result of an API call with the payload at HTTP status code
 *
 * @param <T> the api response object
 */
public class ApiResponseWrapper<T> {
    private final T apiResponse;
    private final int responseCode;

    /**
     * Creates a request object using the returned JSON and the response code
     *
     * @param apiResponse the apiResponse JSON object
     * @param responseCode the api response code
     */
    public ApiResponseWrapper(T apiResponse, int responseCode) {
        this.apiResponse = apiResponse;
        this.responseCode = responseCode;
    }

    /**
     * Returns the response code of the HTTP request
     * @return the response code of the HTTP request
     */
    public int getResponseCode() {
        return responseCode;
    }

    public T getApiResponse() {
        return apiResponse;
    }
}
