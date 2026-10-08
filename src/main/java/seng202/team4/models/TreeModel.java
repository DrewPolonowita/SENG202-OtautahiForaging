package seng202.team4.models;



import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * Model representation of a tree
 * @author Team 4
 */
public class TreeModel {

    private final int id;
    private final List<Float> coordinates;
    private final String ownership;
    private final String genus;
    private final String food;
    private final String commonName;
    private final LocalDateTime lastUpdated;
    private final String serviceStatus;

    /**
     * Constructor
     * @param id Tree id
     * @param coordinates Tree location
     * @param ownership Who owns the tree
     * @param genus Genus of tree
     * @param food Food type of a tree
     * @param commonName commonName a tree
     * @param lastUpdated time of last update
     * @param serviceStatus service
     */
    public TreeModel(int id, List<Float> coordinates, String ownership, String genus,
                     String food, String commonName, LocalDateTime lastUpdated, String serviceStatus) {
        this.id = id;
        this.coordinates = coordinates;
        this.ownership = ownership;
        this.genus = genus;
        this.food = food;
        this.commonName = commonName;
        this.lastUpdated = lastUpdated;
        this.serviceStatus = serviceStatus;

    }

    /**
     * Getter for tree id
     * @return returns integer id
     */
    public int getId() {

        return id;
    }

    /**
     * Getter for coordinates
     * @return returns integer array coordinates
     */
    public List<Float> getCoordinates() {
        return Collections.unmodifiableList(coordinates);
    }

    public float getXCoord() {
        return coordinates.get(0);
    }

    public float getYCoord() {
        return coordinates.get(1);
    }

    /**
     * Getter for ownership
     * @return returns String ownership
     */
    public String getOwnership() {
        return ownership;
    }


    /**
     * Getter for genus
     * @return returns String genus
     */
    public String getGenus() {
        return genus;
    }

    /**
     * Getter method for food type
     * @return returns String food
     */
    public String getFood() {
        return food;
    }

    /**
     * Getter method for tree common name
     * @return returns String commonName
     */
    public String getCommonName() {
        return commonName;
    }

    /**
     * Equals operator rewrite so 2 trees with the same attributes can be evaluated as the same
     * @param o object to compare
     * @return returns boolean, true if this equals o
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TreeModel treeModel = (TreeModel) o;
        return id == treeModel.id;
    }

    /**
     * Overwrite hashCode
     * @return integer hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

}
