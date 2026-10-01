package seng202.team4.models;

/**
 * An enum class to handle different fruits using for filtering and querying.
 */
public enum Fruit {
    APPLE,
    PEAR,
    PEACH,
    APRICOT,
    WALNUT,
    STRAWBERRY;
    //ONLY add food that are keys in FoodMapper createFoodMap()
    /**
     * Returns the string format of a given fruit for the api and database
     * @return the string format of a given fruit for the api and database
     */
    @Override
    public String toString() {
        return switch (this) {
            case APPLE -> "Apple";
            case PEAR -> "Pear";
            case PEACH -> "Peach";
            case APRICOT -> "Apricot";
            case WALNUT -> "Walnut";
            case STRAWBERRY -> "Strawberry";
            default -> {
                String message = "Not yet implemented for " + this.name();
                throw new UnsupportedOperationException(message);
            }
        };
    }
}
