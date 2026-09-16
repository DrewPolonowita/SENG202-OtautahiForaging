package seng202.team4.models;

/**
 * An enum class to handle different fruits using for filtering and querying.
 */
public enum Fruit {
    APPLE,
    PEAR,
    CHERRY,
    PEACH,
    APRICOT;

    /**
     * Returns the string format of a given fruit for the api and database
     * @return the string format of a given fruit for the api and database
     */
    public String toString() {
        return switch (this) {
            case APPLE -> "Apple";
            case PEAR -> "Pear";
            case CHERRY -> "Cherry";
            case PEACH -> "Peach";
            case APRICOT -> "Apricot";
            default -> {
                String message = "Not yet implemented for " + this.name();
                throw new UnsupportedOperationException(message);
            }
        };
    }
}
