package seng202.team4.models.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Location data of a tree object
 *
 * @param type the type of the tree
 * @param coordinates the coordinates of the tree
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Geometry(
        @JsonProperty String type,
        @JsonProperty float[] coordinates
) {
}
