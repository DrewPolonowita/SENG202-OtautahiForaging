package seng202.team4.unittests.gui.bridge;

import org.junit.jupiter.api.Test;
import seng202.team4.gui.bridge.AddTreeInterface;
import seng202.team4.gui.bridge.JavaScriptBridge;
import seng202.team4.gui.bridge.TreeClickedInterface;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JavaScriptBridgeTest {

    @Test
    public void testGetTreeFromClick(){
        int testid = 45;
        AtomicInteger receivedID = new AtomicInteger(-1);
        TreeClickedInterface clickedCallback = id -> {
            receivedID.set(id);
            return true;
        };
        AddTreeInterface addCallback = tree -> {};
        JavaScriptBridge testBridge = new JavaScriptBridge(addCallback,clickedCallback);
        testBridge.getTreeFromClick(testid);
        assertEquals(testid, receivedID.get());
    }
}
