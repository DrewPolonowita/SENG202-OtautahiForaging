package seng202.team4.unittests.gui.bridge;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import seng202.team4.gui.bridge.AddToJournalInterface;
import seng202.team4.gui.bridge.GoToRecipesInterface;
import seng202.team4.gui.bridge.JavaScriptBridge;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JavaScriptBridgeTest {

    @Test
    void testAddTreeToJournal() {
        int testId = 45;
        AtomicInteger receivedId = new AtomicInteger(-1);
        AddToJournalInterface journalCallback = receivedId::set;
        GoToRecipesInterface recipeCallback = foodname -> { };


        JavaScriptBridge testBridge = new JavaScriptBridge(journalCallback, recipeCallback, id->{}, null);
        testBridge.addTreeToJournal(testId);
        assertEquals(testId, receivedId.get());
    }

    @Test
    void testMapMoveValuesReachCallback() {
        double[] receivedValues = new double[7];
        JavaScriptBridge testBridge = new JavaScriptBridge(
                id -> { },
                foodname -> { },
                id -> {},
                ((north, east, south, west, latitude, longitude, zoom) -> {
                    double[] values = {north, east, south, west, latitude, longitude, zoom};
                    for (int i=0; i < receivedValues.length; i++) {
                        receivedValues[i] = values[i];
                    }
                }));
        testBridge.mapMoved(-43.4, 172.8, -43.6, 172.5, -43.5, 172.65, 15);

        Assertions.assertArrayEquals(new double[]{-43.4, 172.8, -43.6, 172.5, -43.5, 172.65, 15}, receivedValues);
    }
}
