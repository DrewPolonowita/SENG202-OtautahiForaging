package seng202.team4.models;

public class Food {


    private String name;
    private String type;
    private String nutritionalInformation;


    public Food(String name, String type, String nutritionalInformation) {
        this.name = name;
        this.type = type;
        this.nutritionalInformation = nutritionalInformation;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNutritionalInformation() { return nutritionalInformation; }
}
