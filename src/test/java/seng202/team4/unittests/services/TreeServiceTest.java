package seng202.team4.unittests.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import seng202.team4.models.TreeModel;
import seng202.team4.models.api.*;
import seng202.team4.services.ApiHandler;
import seng202.team4.services.TreeService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TreeServiceTest {

    private TreeService testTreeService;
    private ApiHandler apiHandler;

    @BeforeEach
    public void setUpTest(){
        apiHandler = mock(ApiHandler.class);

        testTreeService = new TreeService(apiHandler);
    }

    @Test
    public void convertTreeRecordToModelTest() {

        int[] coordinates = {5252,3423};
        Geometry testGeometry = new Geometry("location", coordinates );
        Properties testProperties = new Properties(2452, "Council", "Malus");
        Tree testRecord = new Tree(
                "A",
                123,
                testGeometry,
                testProperties
        );
        Tree[] fakeTrees = {testRecord};
        ApiResponse fakeResponse = new ApiResponse("Feature", fakeTrees, false);
        Request<ApiResponse> fakeRequest = new Request<ApiResponse>(fakeResponse, 200);

        when(apiHandler.getTrees("CommonName='Apple'", "TreeID,Ownership,Genus", 100)).thenReturn(fakeRequest);

        List<TreeModel> result = testTreeService.getAppleTrees();
        TreeModel expectedTree = new TreeModel(2452, coordinates, "Council", "Malus", "Apple" );
        assertEquals(result.getFirst(), expectedTree);


    }
}
