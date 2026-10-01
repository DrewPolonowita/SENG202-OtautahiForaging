package seng202.team4.unittests.models.api.treeApi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.models.api.TreeApi.Properties;

/**
 * A test class for {@link Properties}
 */
class PropertiesTest {

    Properties properties;
    int testId;
    String testOwnership;
    String testGenus;
    String testCommonName;

    @BeforeEach
    void setUpTest() {
        testId = 5;
        testOwnership = "CCC";
        testGenus = "Malus";
        testCommonName = "Apple";
        properties = new Properties(testId, testOwnership, testGenus, testCommonName);
    }

    @Test
    void testInitialValues() {
        Assertions.assertEquals(testId, properties.treeId());
        Assertions.assertEquals(testOwnership, properties.ownership());
        Assertions.assertEquals(testGenus, properties.genus());
        Assertions.assertEquals(testCommonName, properties.commonName());
    }
}
