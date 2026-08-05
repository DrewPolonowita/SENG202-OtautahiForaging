package seng202.team4.unittests.models;

import org.junit.jupiter.api.Test;
import seng202.team4.models.TreeModel;

import static org.junit.jupiter.api.Assertions.*;

public class TreeModelTest {
    @Test
    public void testHashCodeEquals(){
        int[] aCoords = {500, 1200};
        TreeModel a = new TreeModel(50, aCoords, "Private", "Acer");
        TreeModel b = new TreeModel(50, aCoords, "Private", "Acer");
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void testHashCodeNotEquals(){
        int[] aCoords = {500, 1200};
        TreeModel a = new TreeModel(50, aCoords, "Private", "Acer");
        TreeModel b = new TreeModel(51, aCoords, "Private", "Acer");
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void testCustomEquals() {
        int[] testCoords1 = {423,4234};
        TreeModel testModel1 = new TreeModel(432, testCoords1, "Council", "Acer");

        assertEquals(testModel1, testModel1);

        assertNotEquals(testModel1, null);

        int[] testCoords2 = {423, 4234};
        TreeModel testModel2 = new TreeModel(432, testCoords2, "Council", "Acer");

        assertEquals(testModel1, testModel2);
        int[] testCoords3 = {4, 1337};
        TreeModel testModel3 = new TreeModel(432, testCoords3, "Council", "Acer");
        assertNotEquals(testModel1, testModel3);
    }

    @Test
    public void testGetterCoordinates(){
        int[] testCoords = {4, 1337};
        TreeModel testModel = new TreeModel(432, testCoords, "Council", "Acer");
        int[] expectedCoords = {4, 1337};
        assertArrayEquals(expectedCoords, testModel.getCoordinates());
    }

    @Test
    public void testGetterID(){
        int testID = 333;
        int[] testCoords = {4, 1337};
        TreeModel testModel = new TreeModel(testID, testCoords, "Council", "Acer");
        assertEquals(testID,testModel.getId());
    }

    @Test
    public void testGetterOwnership(){
        int[] testCoords = {4, 1337};
        TreeModel testModel = new TreeModel(432, testCoords, "Council", "Acer");
        assertEquals("Council", testModel.getOwnership());
    }

    @Test
    public void testGetterGenus(){
        int[] testCoords = {4, 1337};
        TreeModel testModel = new TreeModel(432, testCoords, "Council", "Acer");
        assertEquals("Acer", testModel.getGenus());
    }
}
