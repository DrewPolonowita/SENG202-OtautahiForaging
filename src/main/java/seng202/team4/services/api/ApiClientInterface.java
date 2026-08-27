package seng202.team4.services.api;

import com.fasterxml.jackson.core.type.TypeReference;
import seng202.team4.models.api.Request;

/**
 * Interface for accessing an external api
 */
public interface ApiClientInterface {

    /**
     * Get data from the api.
     * @param url A url built from
     * @param responseType A type reference object, needs to correspond to a record object specific to the api being requested.
     * @return returns a {@link Request} json object
     */
    <T> Request<T> get(String url, TypeReference<T> responseType);
}
