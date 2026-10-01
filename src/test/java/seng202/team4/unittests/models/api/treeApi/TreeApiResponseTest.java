package seng202.team4.unittests.models.api.treeApi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.models.api.TreeApi.Geometry;
import seng202.team4.models.api.TreeApi.Properties;
import seng202.team4.models.api.TreeApi.TreeApiResponse;
import seng202.team4.models.api.TreeApi.TreeRecord;

/**
 * A test class for {@link TreeApiResponse}
 */
class TreeApiResponseTest {

    TreeRecord treeRecord;
    String apiType;
    int id;

    Geometry geometry;
    String type;
    float[] coords;

    Properties properties;
    int testId;
    String testOwnership;
    String testGenus;
    String testCommonName;

    TreeApiResponse treeApiRepsponse;
    String apiResponseType;
    TreeRecord[] featureTrees;
    boolean testExceededTransferLimit;

    @BeforeEach
    void setUpTest() {
        type = "Test Type";
        coords = new float[2];
        coords[0] = 1.2f;
        coords[1] = 1.2f;
        geometry = new Geometry(type, coords);

        testId = 5;
        testOwnership = "CCC";
        testGenus = "Malus";
        testCommonName = "Apple";
        properties = new Properties(testId, testOwnership, testGenus, testCommonName);

        apiType = "test";
        id = 5;
        treeRecord = new TreeRecord(apiType, id, geometry, properties);

        apiResponseType = "test";
        featureTrees = new TreeRecord[1];
        featureTrees[0] = treeRecord;
        testExceededTransferLimit = false;

        treeApiRepsponse = new TreeApiResponse(apiResponseType, featureTrees, testExceededTransferLimit);
    }

    @Test
    void testInitialValues() {
        Assertions.assertEquals(apiResponseType, treeApiRepsponse.type());
        Assertions.assertSame(featureTrees, treeApiRepsponse.featureTrees());
        Assertions.assertEquals(testExceededTransferLimit, treeApiRepsponse.exceededTransferLimit());
    }

}
