package seng202.team4.unittests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.models.Fruit;
import seng202.team4.models.TreeModel;
import seng202.team4.services.MapService;
import seng202.team4.services.dataCentre.treeData.TreeDataManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the MapService class
 */
public class MapServiceTest {

    private TreeDataManager testTreeDataManager;
    private MapService mapService;

    @BeforeEach
    void setupTest() {
        testTreeDataManager = mock(TreeDataManager.class);
        mapService = new MapService(testTreeDataManager);
    }

    /**
     * Tests that no selected fruits returns an empty list
     * @throws Exception if waiting for the result fails
     */
    @Test
    void testNoFruitsSelected() throws Exception {
        List<TreeModel> result = mapService.getAllTreesInRegion(-43.4, 172.7, -43.6, 172.5).get(2, TimeUnit.SECONDS);
        assertTrue(result.isEmpty());
        verifyNoInteractions(testTreeDataManager);
    }

    /**
     * Tests that adding a null fruit throws NullPointerException
     */
    @Test
    void testAddNullFruit() {
        assertThrows(NullPointerException.class, () -> {
            mapService.addSelectedFruit(null);
        }, "Expected NullPointerException");
    }

    /**
     * Tests that removing a null fruit throws NullPointerException
     */
    @Test
    void testRemoveNullFruit() {
        assertThrows(NullPointerException.class, () -> {
            mapService.removeSelectedFruit(null);
        }, "Expected NullPointerException");
    }

    /**
     * Tests that selecting one fruit returns its trees and queries the correct bounds
     * @throws Exception if waiting for the result fails
     */
    @Test
    void testOneFruitSelected() throws Exception {
        TreeModel appleTree = mock(TreeModel.class);
        when(testTreeDataManager.getTreesByRegion("Apple", -43.4, 172.7, -43.6, 172.5)).thenReturn(List.of(appleTree));
        mapService.addSelectedFruit(Fruit.APPLE);
        CompletableFuture<List<TreeModel>> result = mapService.getAllTreesInRegion(-43.4, 172.7, -43.6, 172.5);
        assertEquals(List.of(appleTree), result.get(2, TimeUnit.SECONDS));
        verify(testTreeDataManager).getTreesByRegion("Apple", -43.4, 172.7, -43.6, 172.5);
    }

    /**
     * Tests that selecting multiple fruits combines their trees and queries the correct bounds
     * @throws Exception if waiting for the result fails
     */
    @Test
    void testMultipleFruitsSelected() throws Exception {
        TreeModel appleTree = mock(TreeModel.class);
        TreeModel pearTree = mock(TreeModel.class);
        when(testTreeDataManager.getTreesByRegion("Apple", -43.4, 172.7, -43.6, 172.5)).thenReturn(List.of(appleTree));
        when(testTreeDataManager.getTreesByRegion("Pear", -43.4, 172.7, -43.6, 172.5)).thenReturn(List.of(pearTree));
        mapService.addSelectedFruit(Fruit.APPLE);
        mapService.addSelectedFruit(Fruit.PEAR);
        List<TreeModel> result = mapService.getAllTreesInRegion(-43.4, 172.7, -43.6, 172.5).get(2, TimeUnit.SECONDS);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(appleTree, pearTree)));
        verify(testTreeDataManager).getTreesByRegion("Apple", -43.4, 172.7, -43.6, 172.5);
        verify(testTreeDataManager).getTreesByRegion("Pear", -43.4, 172.7, -43.6, 172.5);
    }

    /**
     * Tests that removing the only selected fruit leaves no trees to query
     * @throws Exception if waiting for the result fails
     */
    @Test
    void removeSelectedFruit() throws Exception {
        mapService.addSelectedFruit(Fruit.APPLE);
        mapService.removeSelectedFruit(Fruit.APPLE);

        List<TreeModel> result = mapService.getAllTreesInRegion(-43.4, 172.7, -43.6, 172.5).get(2, TimeUnit.SECONDS);
        assertTrue(result.isEmpty());
        verifyNoInteractions(testTreeDataManager);
    }

    /**
     * Tests that removing an unselected fruit throws IllegalArgumentException
     */
    @Test
    void removeUnselectedFruit() {
        assertThrows(IllegalArgumentException.class, () -> {
            mapService.removeSelectedFruit(Fruit.APPLE);
        }, "Expected IllegalArgumentException");
    }

    /**
     * Tests that adding an already existing fruit throws IllegalArgumentException
     */
    @Test
    void addDuplicateFruit() {
        mapService.addSelectedFruit(Fruit.APPLE);
        assertThrows(IllegalArgumentException.class, () -> {
            mapService.addSelectedFruit(Fruit.APPLE);
        }, "Expected IllegalArgumentException");
    }

    /**
     * Test removing one fruit keeps the other selected
     * @throws Exception if waiting for the result fails
     */
    @Test
    void testRemoveOneOfTwoFruits() throws Exception {
        TreeModel pearTree = mock(TreeModel.class);
        when(testTreeDataManager.getTreesByRegion("Pear", -43.4, 172.7, -43.6, 172.5)).thenReturn(List.of(pearTree));
        mapService.addSelectedFruit(Fruit.APPLE);
        mapService.addSelectedFruit(Fruit.PEAR);
        mapService.removeSelectedFruit(Fruit.APPLE);
        CompletableFuture<List<TreeModel>> result = mapService.getAllTreesInRegion(-43.4, 172.7, -43.6, 172.5);
        assertEquals(List.of(pearTree), result.get(2, TimeUnit.SECONDS));
        verify(testTreeDataManager).getTreesByRegion("Pear", -43.4, 172.7, -43.6, 172.5);
        verifyNoMoreInteractions(testTreeDataManager);
    }

    /**
     * Tests that a backend failure completes the returned future exceptionally.
     */
    @Test
    void testBackendFailure() {
        RuntimeException failure = new RuntimeException("API ISSUE");
        when(testTreeDataManager.getTreesByRegion("Apple", -43.4, 172.7, -43.6, 172.5)).thenThrow(failure);
        mapService.addSelectedFruit(Fruit.APPLE);
        CompletableFuture<List<TreeModel>> result = mapService.getAllTreesInRegion(-43.4, 172.7, -43.6, 172.5);
        ExecutionException thrown = assertThrows(ExecutionException.class, () -> {
            result.get(2, TimeUnit.SECONDS);
        });
        assertSame(failure, thrown.getCause());
    }


}
