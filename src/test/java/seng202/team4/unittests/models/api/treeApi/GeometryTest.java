package seng202.team4.unittests.models.api.treeApi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.models.api.TreeApi.Geometry;

/**
 * A test class for {@link Geometry}
 */
public class GeometryTest {

    Geometry geometry;
    String type;
    float[] coords;

    @BeforeEach
    void setUpTest() {
        type = "Test Type";
        coords = new float[2];
        coords[0] = 1.2f;
        coords[1] = 1.2f;

        geometry = new Geometry(type, coords);
    }

    @Test
    void testInitialValues() {
        Assertions.assertEquals(type, geometry.type());
        Assertions.assertEquals(coords, geometry.coordinates());
    }
}
