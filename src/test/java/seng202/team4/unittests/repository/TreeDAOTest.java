package seng202.team4.unittests.repository;

import org.junit.jupiter.api.BeforeEach;
import seng202.team4.models.Region;
import seng202.team4.repository.DatabaseManager;
import seng202.team4.repository.RegionRepository;
import seng202.team4.repository.TreeDAO;
import seng202.team4.repository.TreeRepository;
import seng202.team4.unittests.TestSuite;

public class TreeDAOTest extends TestSuite {

    TreeRepository treeDao;
    DatabaseManager databaseManager;
    Region region;

    @BeforeEach
    void setUpTest() {
        databaseManager = new DatabaseManager(TEST_DB);
        treeDao = new TreeDAO(databaseManager);
    }
}
