package seng202.team4.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Objects;

public class TreeModel {
    private static final Logger log = LogManager.getLogger(TreeModel.class);

    private final int id;
    private final int[] coordinates;
    private final String ownership;
    private final String genus;

    public TreeModel(int id, int[] coordinates, String ownership, String genus) {
        this.id = id;
        this.coordinates = coordinates;
        this.ownership = ownership;
        this.genus = genus;
    }

    public int getId(){

        return id;
    }
    public int[] getCoordinates() {
        return coordinates;
    }
    public String getOwnership() {
        return ownership;
    }
    public String getGenus() {
        return genus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeModel treeModel = (TreeModel) o;
        return id == treeModel.id
                && Arrays.equals(coordinates, treeModel.coordinates)
                && Objects.equals(ownership, treeModel.ownership)
                && Objects.equals(genus, treeModel.genus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, Arrays.hashCode(coordinates), ownership, genus);
    }

}
