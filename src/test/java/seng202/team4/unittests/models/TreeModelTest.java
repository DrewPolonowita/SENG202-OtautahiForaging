package seng202.team4.unittests.models;

import org.junit.jupiter.api.Test;
import seng202.team4.models.TreeModel;

import static org.junit.jupiter.api.Assertions.*;

public class TreeModelTest {
    @Test
    public void testHashCodeEquals(){
        float[] aCoords = {500, 1200};
        TreeModel a = new TreeModel(50, aCoords, "Private", "Malus", "Apple", "Apple", null, "Serviced");
        TreeModel b = new TreeModel(50, aCoords, "Private", "Malus", "Apple", "Apple", null, "Serviced");
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void testHashCodeNotEquals(){
        float[] aCoords = {500, 1200};
        TreeModel a = new TreeModel(50, aCoords, "Private", "Malus", "Apple", "Apple", null, "Serviced");
        TreeModel b = new TreeModel(51, aCoords, "Private", "Malus", "Apple", "Apple", null, "Serviced");
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void testCustomEquals() {
        float[] testCoords1 = {423,4234};
        TreeModel testModel1 = new TreeModel(432, testCoords1, "Council", "Acer", "Apple", "Apple", null, "Serviced");

        assertEquals(testModel1, testModel1);

        assertNotEquals(testModel1, null);

        float[] testCoords2 = {423, 4234};
        TreeModel testModel2 = new TreeModel(432, testCoords2, "Council", "Acer", "Apple", "Apple", null, "Serviced");

        assertEquals(testModel1, testModel2);
        float[] testCoords3 = {4, 1337};
        TreeModel testModel3 = new TreeModel(432, testCoords3, "Council", "Acer", "Apple", "Apple", null, "Serviced");
        assertEquals(testModel1, testModel3);
    }

    @Test
    public void testGetterCoordinates(){
        float[] testCoords = {4, 1337};
        TreeModel testModel = new TreeModel(432, testCoords, "Council", "Acer", "Apple", "Apple", null, "Serviced");
        float[] expectedCoords = {4, 1337};
        assertArrayEquals(expectedCoords, testModel.getCoordinates());
    }

    @Test
    public void testGetterID(){
        int testID = 333;
        float[] testCoords = {4, 1337};
        TreeModel testModel = new TreeModel(testID, testCoords, "Council", "Malus", "Apple", "Apple", null, "Serviced");
        assertEquals(testID,testModel.getId());
    }

    @Test
    public void testGetterOwnership(){
        float[] testCoords = {4, 1337};
        TreeModel testModel = new TreeModel(432, testCoords, "Council", "Malus", "Apple", "Apple", null, "Serviced");
        assertEquals("Council", testModel.getOwnership());
    }

    @Test
    public void testGetterGenus(){
        float[] testCoords = {4, 1337};
        TreeModel testModel = new TreeModel(432, testCoords, "Council", "Malus", "Apple", "Apple", null, "Serviced");
        assertEquals("Malus", testModel.getGenus());
    }
}
