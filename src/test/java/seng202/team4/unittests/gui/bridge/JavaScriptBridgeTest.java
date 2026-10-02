package seng202.team4.unittests.gui.bridge;

import org.junit.jupiter.api.Test;
import seng202.team4.gui.bridge.AddToJournalInterface;
import seng202.team4.gui.bridge.AddTreeInterface;
import seng202.team4.gui.bridge.JavaScriptBridge;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JavaScriptBridgeTest {

    @Test
    void testAddTreeToJournal() {
        int testId = 45;
        AtomicInteger receivedId = new AtomicInteger(-1);
        AddToJournalInterface journalCallback = receivedId::set;
        AddTreeInterface addCallback = tree -> {};

        JavaScriptBridge testBridge = new JavaScriptBridge(journalCallback, addCallback, null);
        testBridge.addTreeToJournal(testId);
        assertEquals(testId, receivedId.get());
    }
}
