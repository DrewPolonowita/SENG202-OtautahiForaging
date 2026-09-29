package seng202.team4.unittests.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.models.Region;
import seng202.team4.repository.DatabaseManager;
import seng202.team4.repository.RegionDAO;
import seng202.team4.repository.RegionRepository;
import seng202.team4.unittests.TestSuite;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertEquals(1, regions.size());

        region.setLastUpdated(regions.getFirst().getLastUpdated());
        assertEquals(region, regions.getFirst());
    }

    @Test
    void testDuplicateItemThrowsDuplicateError() throws DuplicateEntryException {
        regionDAO.add(region);
        assertThrows(DuplicateEntryException.class, () -> regionDAO.add(region));
    }

    @Test
    void testDeleteWorks() throws DuplicateEntryException {
        regionDAO.add(region);
        regionDAO.delete(region);
        assertEquals(0, regionDAO.getAll().size());
    }

    @Test
    void testGetRegion() throws DuplicateEntryException {
        regionDAO.add(region);
        Region fromDB = regionDAO.getRegion(region);
        region.setLastUpdated(fromDB.getLastUpdated());

        assertEquals(region, fromDB);
    }

    @Test
    void testNewRegionLastUpdatedIsRecent() throws DuplicateEntryException {
        regionDAO.add(region);
        Region fromDB = regionDAO.getRegion(region);

        assertTrue(fromDB.getLastUpdated().isAfter(fromDB.getLastUpdated().minusMinutes(1)));
    }

    @Test
    void testAddRegionWithADate() throws DuplicateEntryException {
        LocalDateTime date = LocalDateTime.of(1999, 12, 12, 12, 12, 12);
        region.setLastUpdated(date);
        regionDAO.add(region);
        assertEquals(date, regionDAO.getRegion(region).getLastUpdated());
    }

    @Test
    void testUpdateRegion() throws DuplicateEntryException {
        LocalDateTime date = LocalDateTime.of(1999, 12, 12, 12, 12, 12);
        regionDAO.add(region);
        region.setLastUpdated(date);
        regionDAO.update(region);
        assertEquals(date, regionDAO.getRegion(region).getLastUpdated());
    }
}
