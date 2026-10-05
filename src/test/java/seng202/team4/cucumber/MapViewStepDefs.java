package seng202.team4.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import seng202.team4.gui.bridge.JavaScriptBridge;

public class MapViewStepDefs {
    private JavaScriptBridge bridge;
    private double receivedLatitude;
    private double receivedLongitude;
    private double receivedZoom;
    private boolean callbackCalled;

    @Given("the map movement callback is registered")
    public void registerMapCallback() {
        callbackCalled = false;

        bridge = new JavaScriptBridge(
                id -> {},
                foodname -> { },
                tree -> {},
                id -> {},
                (north, east, south, west, latitude, longitude, zoom) -> {
                    receivedLatitude = latitude;
                    receivedLongitude = longitude;
                    receivedZoom = zoom;
                    callbackCalled = true;
                }
        );
    }

    @When("the map reports latitude {double} and longitude {double} at zoom {double}")
    public void reportMapMovement(double latitude, double longitude, double zoom) {
        bridge.mapMoved(-43.4, 172.8, -43.6, 172.5, latitude, longitude, zoom);
    }

    @Then("the callback receives latitude {double} and longitude {double} at zoom {double}")
    public void checkReceivedView(double latitude, double longitude, double zoom) {
        Assertions.assertTrue(callbackCalled);
        Assertions.assertEquals(latitude, receivedLatitude);
        Assertions.assertEquals(longitude, receivedLongitude);
        Assertions.assertEquals(zoom, receivedZoom);
    }
}
