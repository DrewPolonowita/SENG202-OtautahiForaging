package seng202.team4.models.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Stores information from an API request
 *
 * @param type the type of trees
 * @param featureTrees the trees from the request
 * @param exceededTransferLimit a boolean for if too many trees were requested that some got cut
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiResponse(
        @JsonProperty String type,
        @JsonProperty("features") Tree[] featureTrees,
        @JsonProperty boolean exceededTransferLimit
) {
}
