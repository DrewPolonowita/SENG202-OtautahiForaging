package seng202.team4.unittests.models;

import org.junit.jupiter.api.Test;
import seng202.team4.models.TreeModel;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TreeModelTest {
    @Test
    void testHashCodeEquals(){
        List<Float> aCoords = List.of(500f, 1200f);
        TreeModel a = new TreeModel(50, aCoords, "Private", "Malus", "Apple", "Apple", null, "Serviced");
        TreeModel b = new TreeModel(50, aCoords, "Private", "Malus", "Apple", "Apple", null, "Serviced");
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testHashCodeNotEquals(){
        List<Float> aCoords = List.of(500f, 1200f);
        TreeModel a = new TreeModel(50, aCoords, "Private", "Malus", "Apple", "Apple", null, "Serviced");
        TreeModel b = new TreeModel(51, aCoords, "Private", "Malus", "Apple", "Apple", null, "Serviced");
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testCustomEquals() {
        List<Float> testCoords1 = List.of(423f, 4234f);
        TreeModel testModel1 = new TreeModel(432, testCoords1, "Council", "Acer", "Apple", "Apple", null, "Serviced");

        assertEquals(testModel1, testModel1);

        assertNotEquals(null, testModel1);

        List<Float> testCoords2 = List.of(423f, 4234f);
        TreeModel testModel2 = new TreeModel(432, testCoords2, "Council", "Acer", "Apple", "Apple", null, "Serviced");

        assertEquals(testModel1, testModel2);
        List<Float> testCoords3 = List.of(4f, 1337f);

        TreeModel testModel3 = new TreeModel(432, testCoords3, "Council", "Acer", "Apple", "Apple", null, "Serviced");
        assertEquals(testModel1, testModel3);
    }

    @Test
    void testGetterCoordinates(){
        List<Float> testCoords = List.of(4f, 1337f);
        TreeModel testModel = new TreeModel(432, testCoords, "Council", "Acer", "Apple", "Apple", null, "Serviced");
        List<Float> expectedCoords = List.of(4f, 1337f);
        assertEquals(expectedCoords, testModel.getCoordinates());
    }

    @Test
    void testGetterID(){
        int testID = 333;
        List<Float> testCoords = List.of(4f, 1337f);
        TreeModel testModel = new TreeModel(testID, testCoords, "Council", "Malus", "Apple", "Apple", null, "Serviced");
        assertEquals(testID,testModel.getId());
    }

    @Test
    void testGetterOwnership(){
        List<Float> testCoords = List.of(4f, 1337f);
        TreeModel testModel = new TreeModel(432, testCoords, "Council", "Malus", "Apple", "Apple", null, "Serviced");
        assertEquals("Council", testModel.getOwnership());
    }

    @Test
    void testGetterGenus(){
        List<Float> testCoords = List.of(4f, 1337f);
        TreeModel testModel = new TreeModel(432, testCoords, "Council", "Malus", "Apple", "Apple", null, "Serviced");
        assertEquals("Malus", testModel.getGenus());
    }
}
