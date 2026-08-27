package seng202.team4.services.api;

import com.fasterxml.jackson.core.type.TypeReference;
import seng202.team4.models.api.Request;

public interface ApiClientInterface {

    public <T> Request<T> get(String url, TypeReference<T> responseType);
}
