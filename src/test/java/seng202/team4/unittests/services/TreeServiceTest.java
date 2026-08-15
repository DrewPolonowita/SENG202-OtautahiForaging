package seng202.team4.unittests.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import seng202.team4.helpers.TestTreeData;
import seng202.team4.models.TreeModel;
import seng202.team4.models.api.*;
import seng202.team4.repository.DatabaseManager;
import seng202.team4.repository.TreeDAO;
import seng202.team4.services.ApiHandler;
import seng202.team4.services.TreeService;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TreeServiceTest {

    private TreeService testTreeService;
    private ApiHandler apiHandler;
    private DatabaseManager databaseManager;
    private TreeDAO treeDAO;

    @TempDir
    Path tempDirectory;

    @BeforeEach
    public void setUpTest(){
        apiHandler = mock(ApiHandler.class);
        String testUrl = "jdbc:sqlite:" + tempDirectory.resolve("test_database.db").toAbsolutePath();
        databaseManager = new DatabaseManager(testUrl);
        treeDAO = new TreeDAO(databaseManager);
        testTreeService = new TreeService(apiHandler, treeDAO);


    }
    //region creating specific models and records
    private List<TreeModel> fillDatabase() {
        List<TreeModel> testTrees = TestTreeData.createTreeModels();

        for (TreeModel tree : testTrees) {
            treeDAO.add(tree);
        }
        return testTrees;

    }



    //endregion


    @Test
    public void testGetTreesByTypeWithPopulatedDatabase() {
        fillDatabase();

        List<TreeModel> appleTrees = testTreeService.getTreesByType("Apple");

        for (TreeModel tree : appleTrees) {
            assertEquals("Apple", tree.getCommonName());
        }

        verify(apiHandler, never()).getTrees(anyString(), anyString(), anyInt());

    }

    @Test
    public void testGetTreesByTypeWithoutPopulatedDatabase() {
        Tree[] fakeTrees = TestTreeData.createFakeAppleRecords();

        ApiResponse fakeResponse = new ApiResponse("Feature", fakeTrees, false);
        Request<ApiResponse> fakeRequest = new Request<ApiResponse>(fakeResponse, 200);

        when(apiHandler.getTrees("CommonName='Apple'", "TreeID,Ownership,Genus,CommonName", 100))
                .thenReturn(fakeRequest);
        List<TreeModel> appleTrees = testTreeService.getTreesByType("Apple");

        for (TreeModel tree : appleTrees) {
            assertEquals("Apple", tree.getCommonName());
        }

        verify(apiHandler, times(1))
                .getTrees("CommonName='Apple'", "TreeID,Ownership,Genus,CommonName", 100);
    }




    @Test
    public void convertTreeRecordToModelTest() {

        int[] coordinates = {5252,3423};

        Tree testRecord = TestTreeData.createFakeRecord(5252, 3423, 2452, "Council", "Malus", "Apple");

        Tree[] fakeTrees = {testRecord};
        ApiResponse fakeResponse = new ApiResponse("Feature", fakeTrees, false);
        Request<ApiResponse> fakeRequest = new Request<ApiResponse>(fakeResponse, 200);

        when(apiHandler.getTrees("CommonName='Apple'", "TreeID,Ownership,Genus,CommonName", 100)).thenReturn(fakeRequest);

        List<TreeModel> result = testTreeService.getAppleTrees();
        TreeModel expectedTree = new TreeModel(2452, coordinates, "Council", "Malus", "Apple", "Apple" );
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
    public void testGetAllTreesWithoutDatabase() {

        when(apiHandler.getTrees("CommonName='Apple'", "TreeID,Ownership,Genus,CommonName", 100))
                .thenReturn(new Request<>(new ApiResponse("Feature", TestTreeData.createFakeAppleRecords(), false), 200));
        when(apiHandler.getTrees("CommonName='Pear'", "TreeID,Ownership,Genus,CommonName", 100))
                .thenReturn(new Request<>(new ApiResponse("Feature", TestTreeData.createFakePearRecords(), false), 200));
        when(apiHandler.getTrees("CommonName='Cherry'", "TreeID,Ownership,Genus,CommonName", 100))
                .thenReturn(new Request<>(new ApiResponse("Feature", TestTreeData.createFakeCherryRecords(), false), 200));
        when(apiHandler.getTrees("CommonName='Peach'", "TreeID,Ownership,Genus,CommonName", 100))
                .thenReturn(new Request<>(new ApiResponse("Feature", TestTreeData.createFakePeachRecords(), false), 200));
        when(apiHandler.getTrees("CommonName='Apricot'", "TreeID,Ownership,Genus,CommonName", 100))
                .thenReturn(new Request<>(new ApiResponse("Feature", TestTreeData.createFakeApricotRecords(), false), 200));

        List<TreeModel> allTrees = testTreeService.getAllTrees();

        assertEquals(5, allTrees.size());

        List<String> types = List.of("Apple", "Pear", "Cherry", "Peach", "Apricot");

        for (String type : types) {
            verify(apiHandler, times(1  ))
                    .getTrees("CommonName='" + type + "'",
                            "TreeID,Ownership,Genus,CommonName",
                            100);
        }
    }

    @Test
    public void testWrongResponseCode() {

        Tree testRecord = TestTreeData
                .createFakeRecord(5252,3423, 2452, "Council", "Malus", "Apple");


        Tree[] fakeTrees = {testRecord};
        ApiResponse fakeResponse = new ApiResponse("Feature", fakeTrees, false);
        Request<ApiResponse> fakeRequest = new Request<ApiResponse>(fakeResponse, 404);

        when(apiHandler.getTrees("CommonName='Apple'", "TreeID,Ownership,Genus,CommonName", 100)).thenReturn(fakeRequest);
        List<TreeModel> result = testTreeService.getAppleTrees();
        assertTrue(result.isEmpty());

    }
}
