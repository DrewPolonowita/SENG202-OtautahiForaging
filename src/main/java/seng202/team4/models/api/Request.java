package seng202.team4.models.api;


/**
 * Returns the apiResponse JSON object and a code. The apiResponse can be null so the responseCode must be verified
 *
 * @param <T> the api response object
 */
public class Request<T> {
    private final T apiResponse;
    private final int responseCode;

    /**
     * Creates a request object using the returned JSON and the response code
     *
     * @param apiResponse the apiResponse JSON object
     * @param responseCode the api response code
     */
    public Request(T apiResponse, int responseCode) {
        this.apiResponse = apiResponse;
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public T getApiResponse() {
        return apiResponse;
    }
}
