package seng202.team4.models.api.TreeApi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A record class to store properties of a tree
 *
 * @param treeId an id for the tree
 * @param ownership the owner of the tree
 * @param genus the genus of the tree
 * @param commonName the name of the tree
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Properties(
        @JsonProperty("TreeID") int treeId,
        @JsonProperty("Ownership") String ownership,
        @JsonProperty("Genus") String genus,
        @JsonProperty("CommonName") String commonName
) {
}
