package seng202.team4.services.api;

import com.fasterxml.jackson.core.type.TypeReference;
import seng202.team4.models.api.Request;

/**
 * Interface for https methods
 */
public interface ApiClientInterface {

    /**
     * Get from api without authentication
     * @param url url request
     * @param responseType type of record object to be created
     * @return request object of type request
     */
    <T> Request<T> get(String url, TypeReference<T> responseType);

    /**
     * Get from api with oauth authentication
     * @param url url request
     * @param responseType type of record object to be create
     * @param oauthHeader oauth header string
     * @return Request object of type request
     */
    <T> Request<T> getOauth(String url, TypeReference<T> responseType, String oauthHeader);
}
