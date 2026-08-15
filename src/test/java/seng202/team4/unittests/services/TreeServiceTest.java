package seng202.team4.unittests.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import seng202.team4.models.TreeModel;
import seng202.team4.models.api.*;
import seng202.team4.repository.DatabaseManager;
import seng202.team4.repository.TreeDAO;
import seng202.team4.services.ApiHandler;
import seng202.team4.services.TreeService;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
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

    private void fillDatabase() {
        List<TreeModel> testTrees = new ArrayList<>();
        testTrees.add(new TreeModel(2452, new int[]{5252, 3423}, "Council", "Malus", "Apple", "Apple"));
        testTrees.add(new TreeModel(2453, new int[]{5253, 3424}, "Council", "Pyrus", "Pear", "Pear"));

        for (TreeModel tree : testTrees) {
            treeDAO.add(tree);
        }

    }





    @Test
    public void convertTreeRecordToModelTest() {

        int[] coordinates = {5252,3423};
        Geometry testGeometry = new Geometry("location", coordinates );
        Properties testProperties = new Properties(2452, "Council", "Malus", "Apple");
        Tree testRecord = new Tree(
                "A",
                123,
                testGeometry,
                testProperties
        );
        Tree[] fakeTrees = {testRecord};
        ApiResponse fakeResponse = new ApiResponse("Feature", fakeTrees, false);
        Request<ApiResponse> fakeRequest = new Request<ApiResponse>(fakeResponse, 200);

        when(apiHandler.getTrees("CommonName='Apple'", "TreeID,Ownership,Genus,CommonName", 100)).thenReturn(fakeRequest);

        List<TreeModel> result = testTreeService.getAppleTrees();
        TreeModel expectedTree = new TreeModel(2452, coordinates, "Council", "Malus", "Apple", "Apple" );
        assertEquals(result.getFirst(), expectedTree);


    }

    @Test
    public void testWrongResponseCode() {
        int[] coordinates = {5252,3423};
        Geometry testGeometry = new Geometry("location", coordinates );
        Properties testProperties = new Properties(2452, "Council", "Malus", "Apple");
        Tree testRecord = new Tree(
                "A",
                123,
                testGeometry,
                testProperties
        );

        Tree[] fakeTrees = {testRecord};
        ApiResponse fakeResponse = new ApiResponse("Feature", fakeTrees, false);
        Request<ApiResponse> fakeRequest = new Request<ApiResponse>(fakeResponse, 404);

        when(apiHandler.getTrees("CommonName='Apple'", "TreeID,Ownership,Genus,CommonName", 100)).thenReturn(fakeRequest);
        List<TreeModel> result = testTreeService.getAppleTrees();
        assertTrue(result.isEmpty());

    }
}
