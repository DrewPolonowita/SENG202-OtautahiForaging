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
import java.time.LocalDateTime;
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

    @Test
    void testDuplicateItemReturnsNegativeTwo() throws DuplicateEntryException {
        regionDAO.add(region);
        Assertions.assertEquals(-2, regionDAO.add(region));
    }

    @Test
    void testDeleteWorks() throws DuplicateEntryException {
        regionDAO.add(region);
        regionDAO.delete(region);
        Assertions.assertEquals(0, regionDAO.getAll().size());
    }

    @Test
    void testGetRegion() throws DuplicateEntryException {
        regionDAO.add(region);
        Region fromDB = regionDAO.getRegion(region);
        region.setLastUpdated(fromDB.getLastUpdated());

        Assertions.assertEquals(region, fromDB);
    }

    @Test
    void testNewRegionLastUpdatedIsRecent() throws DuplicateEntryException {
        regionDAO.add(region);
        Region fromDB = regionDAO.getRegion(region);

        Assertions.assertTrue(fromDB.getLastUpdated().isAfter(fromDB.getLastUpdated().minusMinutes(1)));
    }

    @Test
    void testAddRegionWithADate() throws DuplicateEntryException {
        LocalDateTime date = LocalDateTime.of(1999, 12, 12, 12, 12, 12);
        region.setLastUpdated(date);
        regionDAO.add(region);
        Assertions.assertEquals(date, regionDAO.getRegion(region).getLastUpdated());
    }

    @Test
    void testUpdateRegion() throws DuplicateEntryException {
        LocalDateTime date = LocalDateTime.of(1999, 12, 12, 12, 12, 12);
        regionDAO.add(region);
        region.setLastUpdated(date);
        regionDAO.update(region);
        Assertions.assertEquals(date, regionDAO.getRegion(region).getLastUpdated());
    }
}
