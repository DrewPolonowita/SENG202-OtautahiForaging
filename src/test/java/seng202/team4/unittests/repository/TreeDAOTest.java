package seng202.team4.unittests.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.models.TreeModel;
import seng202.team4.repository.DatabaseManager;
import seng202.team4.repository.TreeDAO;
import seng202.team4.unittests.TestSuite;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TreeDAOTest extends TestSuite {

    TreeDAO treeDao;
    DatabaseManager databaseManager;
    TreeModel tree;
    List<Float> coordinates;
    int id;

    @BeforeEach
    void setUpTest() {
        databaseManager = new DatabaseManager(getTestDb());
        treeDao = new TreeDAO(databaseManager);
        List<Float> coordinates = List.of(1.0f, 1.0f);
        id = 34;
        tree = new TreeModel(id, coordinates, "CCC", "Malus", "Apple", "Apple", LocalDateTime.now(), "");
    }

    @Test
    void testDuplicateEntryException() throws DuplicateEntryException {
        treeDao.add(tree);
        assertThrows(DuplicateEntryException.class, () -> treeDao.add(tree));
    }

    @Test
    void testAddTree() throws DuplicateEntryException {
        treeDao.add(tree);
        Assertions.assertEquals(List.of(tree), treeDao.getAll());
    }

    @Test
    void testGetOne() throws DuplicateEntryException {
        treeDao.add(tree);
        TreeModel result = treeDao.getOne(id);
        Assertions.assertEquals(tree, result);
    }

    @Test
    void testGetOneNonExistentReturnsNull() {
        TreeModel result = treeDao.getOne(999);
        Assertions.assertNull(result);
    }

    @Test
    void testGetAllStartsEmpty() {
        Assertions.assertTrue(treeDao.getAll().isEmpty());
    }

    @Test
    void testDelete() throws DuplicateEntryException {
        treeDao.add(tree);
        Assertions.assertNotNull(treeDao.getOne(34));
        treeDao.delete(34);
        Assertions.assertNull(treeDao.getOne(34));
    }

    @Test
    void testGetTreesByCommonName() throws DuplicateEntryException {
        TreeModel apple = new TreeModel(
                1,
                List.of(2.0f, 2.0f),
                "CCC",
                "Malus",
                "Apple",
                "Apple",
                LocalDateTime.now(),
                ""
        );

        TreeModel walnut = new TreeModel(
                2,
                List.of(1.0f, 1.0f),
                "CCC",
                "Juglans",
                "Walnut",
                "Walnut",
                LocalDateTime.now(),
                ""
        );

        treeDao.add(apple);
        treeDao.add(walnut);

        List<TreeModel> result = treeDao.getTreesByCommonName("Apple");
        Assertions.assertEquals(List.of(apple), result);

        result = treeDao.getTreesByCommonName("Walnut");
        Assertions.assertEquals(List.of(walnut), result);
    }

    @Test
    void testGetTreesByFoodName() throws DuplicateEntryException {
        TreeModel apple = new TreeModel(
                1,
                List.of(1.0f, 1.0f),
                "Public",
                "Malus",
                "Apple",
                "Apple",
                LocalDateTime.now(),
                ""
        );

        TreeModel walnut = new TreeModel(
                2,
                List.of(2.0f, 2.0f),
                "Public",
                "Juglans",
                "Walnut",
                "Walnut",
                LocalDateTime.now(),
                ""
        );

        treeDao.add(apple);
        treeDao.add(walnut);

        List<TreeModel> result = treeDao.getTreesByFoodName("Apple");
        Assertions.assertEquals(List.of(apple), result);

        result = treeDao.getTreesByFoodName("Walnut");
        Assertions.assertEquals(List.of(walnut), result);
    }

    @Test
    void testGetTreesByCommonNameDoesntGiveInvalidTree() throws DuplicateEntryException {
        treeDao.add(tree);
        List<TreeModel> result = treeDao.getTreesByCommonName("Pear");
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testGetTreesByFoodNameDoesntGiveInvalidTree() throws DuplicateEntryException {
        treeDao.add(tree);
        List<TreeModel> result = treeDao.getTreesByFoodName("Pear");
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testGetTreesByRegion() throws DuplicateEntryException {
        TreeModel inside = new TreeModel(
                1,
                List.of(5.0f, 5.0f),
                "Public",
                "Malus",
                "Apple",
                "Apple",
                LocalDateTime.now(),
                ""
        );

        TreeModel outside = new TreeModel(
                2,
                List.of(20.0f, 20.0f),
                "Public",
                "Malus",
                "Apple",
                "Apple",
                LocalDateTime.now(),
                ""
        );

        treeDao.add(inside);
        treeDao.add(outside);

        List<TreeModel> result = treeDao.getTreesByRegion("Apple", 10.0, 10.0, 0.0, 0.0);
        Assertions.assertEquals(List.of(inside), result);
    }

    @Test
    void testGetTreesByRegionWrongFood() throws DuplicateEntryException {
        TreeModel apple = new TreeModel(
                1,
                List.of(5.0f, 5.0f),
                "Public",
                "Malus",
                "Apple",
                "Apple",
                LocalDateTime.now(),
                ""
        );

        TreeModel walnut = new TreeModel(
                2,
                List.of(5.0f, 5.0f),
                "Public",
                "Juglans",
                "Walnut",
                "Walnut",
                LocalDateTime.now(),
                ""
        );

        treeDao.add(apple);
        treeDao.add(walnut);

        List<TreeModel> result = treeDao.getTreesByRegion(
                "Apple",
                10.0,
                10.0,
                0.0,
                0.0
        );

        Assertions.assertEquals(List.of(apple), result);
    }

    @Test
    void testGetTreesByRegionEmpty() throws DuplicateEntryException {
        treeDao.add(tree);
        List<TreeModel> result = treeDao.getTreesByRegion(
                "Apple",
                10.0,
                10.0,
                0.0,
                0.0
        );

        Assertions.assertEquals(List.of(tree), result);
    }

    @Test
    void testDeleteNonExistentTree() {
        Assertions.assertDoesNotThrow(() -> treeDao.delete(999));
    }

    @Test
    void testUpdateNotImplemented() {
        Assertions.assertThrows(org.apache.commons.lang3.NotImplementedException.class, () -> treeDao.update(tree));
    }
}
