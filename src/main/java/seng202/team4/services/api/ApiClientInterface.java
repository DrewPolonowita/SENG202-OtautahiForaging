package seng202.team4.services.api;

import com.fasterxml.jackson.core.type.TypeReference;
import seng202.team4.models.api.ApiResponseWrapper;

/**
 * Interface encapsulating logic for connection to external apis through HTTPS method get
 */
public interface ApiClientInterface {

    /**
     * Get from api without authentication, used with CCC tree data
     * @param url url request
     * @param responseType type of record object to be created
     * @return {@link ApiResponseWrapper<T>} wrapper from external api
     */
    <T> ApiResponseWrapper<T> get(String url, TypeReference<T> responseType);

    /**
     * Get from api with oauth 1.0 authentication, used with FatSecret apis
     * @param url url request
     * @param responseType type of record object to be created
     * @param oauthHeader oauth header string
     * @return {@link ApiResponseWrapper<T>} wrapper from external api
     */
    <T> ApiResponseWrapper<T> get(String url, TypeReference<T> responseType, String oauthHeader);
}
