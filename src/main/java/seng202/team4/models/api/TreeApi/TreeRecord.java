package seng202.team4.models.api.TreeApi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Stores information from an API request of a tree
 *
 * @param type the type of the tree
 * @param id the id of the tree
 * @param geometry the location data of the tree
 * @param properties the properties of the tree
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TreeRecord(
        @JsonProperty String type,
        @JsonProperty int id,
        @JsonProperty Geometry geometry,
        @JsonProperty Properties properties
) {
}
