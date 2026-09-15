package seng202.team4.unittests.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.models.Region;
import seng202.team4.repository.DatabaseManager;
import seng202.team4.repository.RegionDAO;
import seng202.team4.repository.RegionRepository;
import seng202.team4.services.DoubleService;
import seng202.team4.unittests.TestSuite;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * A test class for the {@link RegionDAO}
 */
public class RegionDAOTest extends TestSuite {
    RegionRepository regionDAO;
    DatabaseManager databaseManager;
    Region region;

    @BeforeEach
    void setUpTest() {
        databaseManager = new DatabaseManager(this.getTestDb());
        region = new Region("Test", 1.0, 2.0, 3.0, 4.0);
        regionDAO = new RegionDAO(databaseManager);
    }

    @Test
    void testAddToDatabase() throws DuplicateEntryException {
        regionDAO.add(region);
        List<Region> regions = regionDAO.getAll();
        Assertions.assertEquals(1, regions.size());

        region.setLastUpdated(regions.getFirst().getLastUpdated());
        Assertions.assertEquals(region, regions.getFirst());
    }

}
