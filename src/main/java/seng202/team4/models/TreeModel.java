package seng202.team4.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Objects;

/**
 * Model representation of a tree
 * @author Team 4
 */
public class TreeModel {
    private static final Logger log = LogManager.getLogger(TreeModel.class);

    private final int id;
    private final int[] coordinates;
    private final String ownership;
    private final String genus;
    private final String fruit;
    private final String commonName;

    /**
     * Constructor
     * @param id Tree id
     * @param coordinates Tree location
     * @param ownership Who owns the tree
     * @param genus Genus of tree
     * @param fruit Fruit type of a tree
     */
    public TreeModel(int id, int[] coordinates, String ownership, String genus, String fruit, String commonName) {
        this.id = id;
        this.coordinates = coordinates;
        this.ownership = ownership;
        this.genus = genus;
        this.fruit = fruit;
        this.commonName = commonName;
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
    public int[] getCoordinates() {
        return coordinates;
    }

    public int getXCoord() {
        return coordinates[0];
    }

    public int getYCoord() {
        return coordinates[1];
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
     * Getter method for fruit type
     * @return returns String fruit
     */
    public String getFruit() {
        return fruit;
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
        return id == treeModel.id
                && Arrays.equals(coordinates, treeModel.coordinates)
                && Objects.equals(ownership, treeModel.ownership)
                && Objects.equals(genus, treeModel.genus)
                && Objects.equals(fruit, treeModel.fruit)
                && Objects.equals(commonName, treeModel.commonName);
    }

    /**
     * Overwrite hashCode
     * @return integer hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, Arrays.hashCode(coordinates), ownership, genus, fruit, commonName);
    }

}
