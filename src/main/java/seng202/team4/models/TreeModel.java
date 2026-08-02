package seng202.team4.models;

public class Tree {

    private int id;
    private int[] coordinates;
    private String ownership;
    private String genus;

    public Tree(int id, int[] coordinates, String ownership, String genus) {
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

}
