package seng202.team4.gui.bridge;

@FunctionalInterface
public interface MapMovedInterface {
    void mapMoved(double north, double east, double south, double west, double latitude, double longitude, double zoom);
}
