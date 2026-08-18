package seng202.team4.models;

public class Recipe {
    private int id;
    private String name;
    private String method;
    private String ingredients;
    // nutritional information?

    public Recipe(int id, String name, String method, String ingredients) {
        this.id = id;
        this.name = name;
        this.method = method;
        this.ingredients = ingredients;

    }
}
