package seng202.team4.services;

/**
 * A class to create equality and strict gt and lt functions using tolerances
 */
public class DoubleService {
    final static double EPSILON = 0.00001;

    /**
     * Returns true if a and b are within some tolerance
     *
     * @param a a double to be compared
     * @param b a double to be compared
     * @return true if a and b are within some tolerance
     */
    public boolean equalityTolerance(double a, double b) {
        return Math.abs(a - b) < EPSILON;
    }

    /**
     * Returns true if a > b and a and b are not within some tolerance
     *
     * @param a a double to be compared
     * @param b a double to be compared
     * @return true if a > b and a and b are not within some tolerance
     */
    public boolean greaterThanTolerance(double a, double b) {
        return a > b && !equalityTolerance(a, b);
    }

    /**
     * Returns true if a < b and a and b are not within some tolerance
     *
     * @param a a double to be compared
     * @param b a double to be compared
     * @return true if a < b and a and b are not within some tolerance
     */
    public boolean lesserThanTolerance(double a, double b) {
        return a < b && !equalityTolerance(a, b);
    }
}
