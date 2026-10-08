package seng202.team4.unittests.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.helpers.TestTreeData;
import seng202.team4.models.TreeModel;
import seng202.team4.models.api.*;
import seng202.team4.models.api.TreeApi.TreeRecord;
import seng202.team4.models.api.TreeApi.TreeApiResponse;
import seng202.team4.repository.DatabaseManager;
import seng202.team4.repository.TreeDAO;
import seng202.team4.services.FoodMapper;
import seng202.team4.services.dataCentre.treeData.TreeService;
import seng202.team4.services.api.TreeApiClient;
import seng202.team4.services.api.TreeClientInterface;
import seng202.team4.unittests.TestSuite;

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

class TreeServiceTest extends TestSuite {

    private TreeService testTreeService;
    private TreeClientInterface treeApiClient;
    private DatabaseManager databaseManager;
    private TreeDAO treeDAO;
    private FoodMapper foodMapper;

    int tupleMax;
    String returningString;


    @BeforeEach
    void setUpTest(){
        foodMapper = new FoodMapper();
        treeApiClient = mock(TreeApiClient.class);
        databaseManager = new DatabaseManager(getTestDb());
        treeDAO = new TreeDAO(databaseManager);
        testTreeService = new TreeService(treeDAO, treeApiClient, null, foodMapper);
        returningString = "TreeID,Ownership,Genus,CommonName";
        tupleMax = 100;
    }

    //region creating specific models and records
    private List<TreeModel> fillDatabase() throws DuplicateEntryException  {
        List<TreeModel> testTrees = TestTreeData.createTreeModels();

        for (TreeModel tree : testTrees) {
            treeDAO.add(tree);
        }
        return testTrees;

    }

    @Test
    void testGetTreesByTypeWithPopulatedDatabase() throws DuplicateEntryException {
        fillDatabase();

        List<TreeModel> appleTrees = testTreeService.getTreesByFood("Apple");

        for (TreeModel tree : appleTrees) {
            assertEquals("Apple", tree.getCommonName());
        }

        verify(treeApiClient, never()).getTrees(anyString(), anyString(), anyInt());

    }

    @Test
    void testGetTreesByFoodWithoutPopulatedDatabase() {
        TreeRecord[] fakeTrees = TestTreeData.createFakeAppleRecords();

        TreeApiResponse fakeResponse = new TreeApiResponse("Feature", fakeTrees, false);
        ApiResponseWrapper<TreeApiResponse>
                fakeApiResponseWrapper = new ApiResponseWrapper<TreeApiResponse>(fakeResponse, 200);

        when(treeApiClient.getTrees(anyString(), eq(returningString), eq(tupleMax)))
                .thenReturn(fakeApiResponseWrapper);
        List<TreeModel> appleTrees = testTreeService.getTreesByFood("Apple");

        for (TreeModel tree : appleTrees) {
            assertEquals("Apple", tree.getFood());
        }

        // invoke just once because just the call for getting apples should be made
        verify(treeApiClient, times(1))
                .getTrees(anyString(), eq(returningString), eq(tupleMax));
    }

    @Test
    void testWrongResponseCode() {

        TreeRecord testRecord = TestTreeData
                .createFakeRecord(5252,3423, 2452, "Council", "Malus", "Chinese Crab Apple");


        TreeRecord[] fakeTrees = {testRecord};
        TreeApiResponse fakeResponse = new TreeApiResponse("Feature", fakeTrees, false);
        ApiResponseWrapper<TreeApiResponse>
                fakeApiResponseWrapper = new ApiResponseWrapper<TreeApiResponse>(fakeResponse, 404);

        when(treeApiClient.getTrees(contains("Apple"), eq(returningString), eq(tupleMax))).thenReturn(
                fakeApiResponseWrapper);
        List<TreeModel> result = testTreeService.getTreesByFood("Apple");
        assertTrue(result.isEmpty());

    }
}
