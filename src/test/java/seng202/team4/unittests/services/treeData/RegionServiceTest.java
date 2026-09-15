package seng202.team4.unittests.services.treeData;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.models.Region;
import seng202.team4.repository.DatabaseManager;
import seng202.team4.repository.RegionDAO;
import seng202.team4.repository.RegionRepository;
import seng202.team4.services.treeData.RegionService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A test class for the {@link RegionService}
 */
public class RegionServiceTest {
    RegionService regionService;
    RegionRepository regionRepository;
    DatabaseManager databaseManager;
    Region region;

    @BeforeEach
    public void setUpTest() {
        databaseManager = new DatabaseManager(null);
        regionRepository = new RegionDAO(databaseManager);
        regionService = new RegionService(regionRepository);
        region = new Region("Test", 1.0, 2.0, 3.0, 4.0);
    }

    @Test
    public void testGetOverlappingRegions1() {
        double north = -43.64;
        double east = 172.40;
        double south = -43.70;
        double west = 172.35;
        String type = "Test";

        List<Region> actualRegions = regionService.getOverlappingRegions(type, north, east, south, west);
        List<Region> expectedRegions = List.of(
                new Region("Test", south + 0.03, west + 0.03, south, west),
                new Region("Test", south + 0.03, west + 0.06, south, west + 0.03),
                new Region("Test", south + 0.06, west + 0.03, south + 0.03, west),
                new Region("Test", south + 0.06, west + 0.06, south + 0.03, west + 0.03)
        );

        Assertions.assertEquals(expectedRegions, actualRegions);
    }

    @Test
    public void testGetOverlappingRegions2() {
        double north = -43.40;
        double east = 172.85;
        double south = -43.46;
        double west = 172.79;
        String type = "Test";

        List<Region> actualRegions = regionService.getOverlappingRegions(type, north, east, south, west);
        List<Region> expectedRegions = List.of(
                new Region("Test", -43.43, 172.8, -43.46, 172.77),
                new Region("Test", -43.43, 172.83, -43.46, 172.8),
                new Region("Test", -43.43, 172.86, -43.46, 172.83),
                new Region("Test", -43.40, 172.8, -43.43, 172.77),
                new Region("Test", -43.40, 172.83, -43.43, 172.8),
                new Region("Test", -43.40, 172.86, -43.43, 172.83)
        );

        Assertions.assertEquals(expectedRegions, actualRegions);
    }

    @Test
    public void testGetOverlappingRegionsDoesntGiveRegionsOutsideChristchurch1() {
        double north = -43.64;
        double east = 172.40;
        double south = -43.70;
        double west = 172.35;
        String type = "Test";

        List<Region> actualRegions = regionService.getOverlappingRegions(type, north, east, -500, 0);
        List<Region> expectedRegions = List.of(
                new Region("Test", south + 0.03, west + 0.03, south, west),
                new Region("Test", south + 0.03, west + 0.06, south, west + 0.03),
                new Region("Test", south + 0.06, west + 0.03, south + 0.03, west),
                new Region("Test", south + 0.06, west + 0.06, south + 0.03, west + 0.03)
        );

        Assertions.assertEquals(expectedRegions, actualRegions);
    }

    @Test
    public void testGetOverlappingRegionsDoesntGiveRegionsOutsideChristchurch2() {
        double north = -43.40;
        double east = 172.85;
        double south = -43.46;
        double west = 172.79;
        String type = "Test";

        List<Region> actualRegions = regionService.getOverlappingRegions(type, 0, 500, south, west);
        List<Region> expectedRegions = List.of(
                new Region("Test", -43.43, 172.8, -43.46, 172.77),
                new Region("Test", -43.43, 172.83, -43.46, 172.8),
                new Region("Test", -43.43, 172.86, -43.46, 172.83),
                new Region("Test", -43.40, 172.8, -43.43, 172.77),
                new Region("Test", -43.40, 172.83, -43.43, 172.8),
                new Region("Test", -43.40, 172.86, -43.43, 172.83)
        );

        Assertions.assertEquals(expectedRegions, actualRegions);
    }

    @Test
    public void testNewDateIsRecent() {
        LocalDateTime dateTime = LocalDateTime.now();
        region.setLastUpdated(dateTime);
        Assertions.assertTrue(regionService.isRegionRecent(region));
    }

    @Test
    public void testOldDateIsNotRecent() {
        LocalDateTime dateTime = LocalDateTime.now().minusWeeks(2);
        region.setLastUpdated(dateTime);
        Assertions.assertFalse(regionService.isRegionRecent(region));
    }
}
