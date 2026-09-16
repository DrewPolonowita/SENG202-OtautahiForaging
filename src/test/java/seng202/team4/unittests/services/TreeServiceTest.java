package seng202.team4.unittests.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import seng202.team4.helpers.TestTreeData;
import seng202.team4.models.TreeModel;
import seng202.team4.models.api.*;
import seng202.team4.models.api.TreeApi.TreeRecord;
import seng202.team4.models.api.TreeApi.TreeApiResponse;
import seng202.team4.repository.DatabaseManager;
import seng202.team4.repository.FoodDAO;
import seng202.team4.repository.TreeDAO;
import seng202.team4.services.FoodService;
import seng202.team4.services.api.FoodApiClient;
import seng202.team4.services.api.FoodClientInterface;
import seng202.team4.services.treeData.TreeService;
import seng202.team4.services.api.TreeApiClient;
import seng202.team4.services.api.TreeClientInterface;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TreeServiceTest {

    private TreeService testTreeService;
    private TreeClientInterface treeApiClient;
    private DatabaseManager databaseManager;
    private TreeDAO treeDAO;
    private FoodService foodService;
    private FoodClientInterface foodApiClient;

    int tuple_max;
    String returning_string;

    @TempDir
    Path tempDirectory;

    @BeforeEach
    public void setUpTest(){
        treeApiClient = mock(TreeApiClient.class);
        foodApiClient = mock(FoodApiClient.class);
        String testUrl = "jdbc:sqlite:" + tempDirectory.resolve("test_database.db").toAbsolutePath();
        databaseManager = new DatabaseManager(testUrl);
        treeDAO = new TreeDAO(databaseManager);
        foodService = new FoodService(new FoodDAO(databaseManager), foodApiClient);
        testTreeService = new TreeService(treeDAO, treeApiClient, null, foodService);
        returning_string = "TreeID,Ownership,Genus,CommonName";
        tuple_max = 100;
    }

    //region creating specific models and records
    private List<TreeModel> fillDatabase() {
        List<TreeModel> testTrees = TestTreeData.createTreeModels();

        for (TreeModel tree : testTrees) {
            treeDAO.add(tree);
        }
        return testTrees;

    }

    @Test
    public void testGetTreesByTypeWithPopulatedDatabase() {
        fillDatabase();

        List<TreeModel> appleTrees = testTreeService.getTreesByFood("Apple");

        for (TreeModel tree : appleTrees) {
            assertEquals("Apple", tree.getCommonName());
        }

        verify(treeApiClient, never()).getTrees(anyString(), anyString(), anyInt());

    }

    @Test
    public void testGetTreesByFoodWithoutPopulatedDatabase() {
        TreeRecord[] fakeTrees = TestTreeData.createFakeAppleRecords();

        TreeApiResponse fakeResponse = new TreeApiResponse("Feature", fakeTrees, false);
        Request<TreeApiResponse> fakeRequest = new Request<TreeApiResponse>(fakeResponse, 200);

        when(treeApiClient.getTrees(anyString(), eq(returning_string), eq(tuple_max)))
                .thenReturn(fakeRequest);
        List<TreeModel> appleTrees = testTreeService.getTreesByFood("Apple");

        for (TreeModel tree : appleTrees) {
            assertEquals("Apple", tree.getFood());
        }

        // invoke just once because just the call for getting apples should be made
        verify(treeApiClient, times(1))
                .getTrees(anyString(), eq(returning_string), eq(tuple_max));
    }

    @Test
    public void convertTreeRecordToModelTest() {

        float[] coordinates = {5252,3423};

        TreeRecord testRecord = TestTreeData.createFakeRecord(5252, 3423, 2452, "Council", "Malus", "Chinese Crab Apple");

        TreeRecord[] fakeTrees = {testRecord};
        TreeApiResponse fakeResponse = new TreeApiResponse("Feature", fakeTrees, false);
        Request<TreeApiResponse> fakeRequest = new Request<TreeApiResponse>(fakeResponse, 200);

        when(treeApiClient.getTrees(anyString(), eq(returning_string), eq(tuple_max))).thenReturn(fakeRequest);

        List<TreeModel> result = testTreeService.getTreesByFood("Apple");
        TreeModel expectedTree = new TreeModel(2452, coordinates, "Council", "Malus", "Apple", "Chinese Crab Apple", null, "Serviced");
        assertEquals(result.getFirst(), expectedTree);
    }

    @Test
    public void testGetAllTreesWithPopulatedDatabase() {
        List<TreeModel> populatedList = fillDatabase();

        List<TreeModel> allTrees = testTreeService.getAllTrees();

        assertEquals(5, allTrees.size());

        for (TreeModel expectedTree : populatedList) {
            assertTrue(allTrees.contains(expectedTree));
        }
    }

    @Test
    public void testGetAllTreesWithoutPopulatedDatabase() {


        when(treeApiClient.getTrees(contains("Chinese Crab Apple"), eq(returning_string), eq(tuple_max)))
                .thenReturn(new Request<>(new TreeApiResponse("Feature", TestTreeData.createFakeAppleRecords(), false), 200));

        when(treeApiClient.getTrees(contains("Pear"), eq(returning_string), eq(tuple_max)))
                .thenReturn(new Request<>(new TreeApiResponse("Feature", TestTreeData.createFakePearRecords(), false), 200));

        when(treeApiClient.getTrees(contains("Bird Cherry"), eq(returning_string), eq(tuple_max)))
                .thenReturn(new Request<>(new TreeApiResponse("Feature", TestTreeData.createFakeCherryRecords(), false), 200));

        when(treeApiClient.getTrees(contains("Peach"), eq(returning_string), eq(tuple_max)))
                .thenReturn(new Request<>(new TreeApiResponse("Feature", TestTreeData.createFakePeachRecords(), false), 200));

        when(treeApiClient.getTrees(contains("Apricot"), eq(returning_string), eq(tuple_max)))
                .thenReturn(new Request<>(new TreeApiResponse("Feature", TestTreeData.createFakeApricotRecords(), false), 200));

        List<TreeModel> allTrees = testTreeService.getAllTrees();
        assertEquals(5, allTrees.size());

        // invoke 5 separate times, because 5 api calls should be used
        verify(treeApiClient, times(5))
                .getTrees(anyString(), eq(returning_string), eq(tuple_max));
    }

    @Test
    public void testWrongResponseCode() {

        TreeRecord testRecord = TestTreeData
                .createFakeRecord(5252,3423, 2452, "Council", "Malus", "Chinese Crab Apple");


        TreeRecord[] fakeTrees = {testRecord};
        TreeApiResponse fakeResponse = new TreeApiResponse("Feature", fakeTrees, false);
        Request<TreeApiResponse> fakeRequest = new Request<TreeApiResponse>(fakeResponse, 404);

        when(treeApiClient.getTrees(contains("Apple"), eq(returning_string), eq(tuple_max))).thenReturn(fakeRequest);
        List<TreeModel> result = testTreeService.getTreesByFood("Apple");
        assertTrue(result.isEmpty());

    }
}
