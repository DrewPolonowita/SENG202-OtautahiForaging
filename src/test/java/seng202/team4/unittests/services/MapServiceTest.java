package seng202.team4.unittests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.models.FilterCategory;
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
class MapServiceTest {

    private TreeDataManager testTreeDataManager;
    private MapService mapService;

    @BeforeEach
    void setupTest() {
        testTreeDataManager = mock(TreeDataManager.class);
        mapService = new MapService(testTreeDataManager);
    }

    /**
     * Tests that no selected foods returns an empty list
     * @throws Exception if waiting for the result fails
     */
    @Test
    void testNoFoodsSelected() throws Exception {
        List<TreeModel> result = mapService.getAllTreesInRegion(-43.4, 172.7, -43.6, 172.5).get(2, TimeUnit.SECONDS);
        assertTrue(result.isEmpty());
        verifyNoInteractions(testTreeDataManager);
    }

    /**
     * Tests that adding a null food throws NullPointerException
     */
    @Test
    void testAddNullFood() {
        assertThrows(NullPointerException.class, () -> {
            mapService.addSelectedFood(null);
        }, "Expected NullPointerException");
    }

    /**
     * Tests that removing a null food throws NullPointerException
     */
    @Test
    void testRemoveNullFood() {
        assertThrows(NullPointerException.class, () -> {
            mapService.removeSelectedFood(null);
        }, "Expected NullPointerException");
    }

    /**
     * Tests that selecting one food returns its trees and queries the correct bounds
     * @throws Exception if waiting for the result fails
     */
    @Test
    void testOneFoodSelected() throws Exception {
        TreeModel appleTree = mock(TreeModel.class);
        when(testTreeDataManager.getTreesByRegion("Apple", -43.4, 172.7, -43.6, 172.5)).thenReturn(List.of(appleTree));
        mapService.addSelectedFood(FilterCategory.APPLE);
        CompletableFuture<List<TreeModel>> result = mapService.getAllTreesInRegion(-43.4, 172.7, -43.6, 172.5);
        assertEquals(List.of(appleTree), result.get(2, TimeUnit.SECONDS));
        verify(testTreeDataManager).getTreesByRegion("Apple", -43.4, 172.7, -43.6, 172.5);
    }

    /**
     * Tests that selecting multiple foods combines their trees and queries the correct bounds
     * @throws Exception if waiting for the result fails
     */
    @Test
    void testMultipleFoodsSelected() throws Exception {
        TreeModel appleTree = mock(TreeModel.class);
        TreeModel pearTree = mock(TreeModel.class);
        when(testTreeDataManager.getTreesByRegion("Apple", -43.4, 172.7, -43.6, 172.5)).thenReturn(List.of(appleTree));
        when(testTreeDataManager.getTreesByRegion("Pear", -43.4, 172.7, -43.6, 172.5)).thenReturn(List.of(pearTree));
        mapService.addSelectedFood(FilterCategory.APPLE);
        mapService.addSelectedFood(FilterCategory.PEAR);
        List<TreeModel> result = mapService.getAllTreesInRegion(-43.4, 172.7, -43.6, 172.5).get(2, TimeUnit.SECONDS);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(appleTree, pearTree)));
        verify(testTreeDataManager).getTreesByRegion("Apple", -43.4, 172.7, -43.6, 172.5);
        verify(testTreeDataManager).getTreesByRegion("Pear", -43.4, 172.7, -43.6, 172.5);
    }

    /**
     * Tests that removing the only selected food leaves no trees to query
     * @throws Exception if waiting for the result fails
     */
    @Test
    void removeSelectedFood() throws Exception {
        mapService.addSelectedFood(FilterCategory.APPLE);
        mapService.removeSelectedFood(FilterCategory.APPLE);

        List<TreeModel> result = mapService.getAllTreesInRegion(-43.4, 172.7, -43.6, 172.5).get(2, TimeUnit.SECONDS);
        assertTrue(result.isEmpty());
        verifyNoInteractions(testTreeDataManager);
    }

    /**
     * Test removing one food keeps the others selected
     * @throws Exception if waiting for the result fails
     */
    @Test
    void testRemoveOneOfTwoFoods() throws Exception {
        TreeModel pearTree = mock(TreeModel.class);
        when(testTreeDataManager.getTreesByRegion("Pear", -43.4, 172.7, -43.6, 172.5)).thenReturn(List.of(pearTree));
        mapService.addSelectedFood(FilterCategory.APPLE);
        mapService.addSelectedFood(FilterCategory.PEAR);
        mapService.removeSelectedFood(FilterCategory.APPLE);
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
        mapService.addSelectedFood(FilterCategory.APPLE);
        CompletableFuture<List<TreeModel>> result = mapService.getAllTreesInRegion(-43.4, 172.7, -43.6, 172.5);
        ExecutionException thrown = assertThrows(ExecutionException.class, () -> {
            result.get(2, TimeUnit.SECONDS);
        });
        assertSame(failure, thrown.getCause());
    }
}
