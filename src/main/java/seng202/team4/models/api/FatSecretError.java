package seng202.team4.models.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FatSecretError(
        @JsonProperty("code") String code,
        @JsonProperty("message") String message
) {
}
