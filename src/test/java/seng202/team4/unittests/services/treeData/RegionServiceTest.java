package seng202.team4.unittests.services.treeData;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.models.Region;
import seng202.team4.repository.DatabaseManager;
import seng202.team4.repository.RegionDAO;
import seng202.team4.repository.RegionRepository;
import seng202.team4.services.dataCentre.treeData.RegionService;
import seng202.team4.unittests.TestSuite;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A test class for the {@link RegionService}
 */
public class RegionServiceTest extends TestSuite {
    RegionService regionService;
    RegionRepository regionRepository;
    DatabaseManager databaseManager;
    Region region;

    Region region1;
    Region region2;
    Region region3;
    Region region4;
    List<Region> regions;

    @BeforeEach
    public void setUpTest() {
        databaseManager = new DatabaseManager(this.getTestDb());
        databaseManager.resetDB();
        regionRepository = new RegionDAO(databaseManager);
        regionService = new RegionService(regionRepository);
        region = new Region("Test", 1.0, 2.0, 3.0, 4.0);

        region1 = new Region(null, 200, 100, 100, 0);
        region2 = new Region(null, 200, 200, 100, 100);
        region3 = new Region(null, 100, 100, 0, 0);
        region4 = new Region(null, 100, 200, 0, 100);

        regions = List.of(region1, region2, region3, region4);
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

    @Test
    public void testNewDateIsRecentWithNullRegionIsFalse() {
        Assertions.assertFalse(regionService.isRegionRecent(null));
    }

    @Test
    public void testNewDateIsRecentWithNullDateIsFalse() {
        Assertions.assertFalse(regionService.isRegionRecent(region));
    }

    @Test
    public void testGetInsideRegions1() {
        Region master = new Region("", 200, 200, 0, 0);

        Assertions.assertEquals(
                List.of(
                    region1, region2, region3, region4
                ),
                regionService.getRegionsInside(master, regions));
    }

    @Test
    public void testGetInsideRegions2() {
        Region master = new Region("", 50, 200, 0, 0);

        Assertions.assertEquals(
                List.of(
                        region3, region4
                ),
                regionService.getRegionsInside(master, regions));
    }

    @Test
    public void testGetInsideRegions3() {
        Region master = new Region("", 200, 200, 150, 0);

        Assertions.assertEquals(
                List.of(
                        region1, region2
                ),
                regionService.getRegionsInside(master, regions));
    }

    @Test
    public void testGetInsideRegions4() {
        Region master = new Region("", 200, 50, 0, 0);

        Assertions.assertEquals(
                List.of(
                        region1, region3
                ),
                regionService.getRegionsInside(master, regions));
    }

    @Test
    public void testGetInsideRegions5() {
        Region master = new Region("", 200, 200, 0, 150);

        Assertions.assertEquals(
                List.of(
                        region2, region4
                ),
                regionService.getRegionsInside(master, regions));
    }

    @Test
    public void testGetQuerryRegionWhileUpToDateIsNull() {
        region1.setLastUpdated(LocalDateTime.now());
        region2.setLastUpdated(LocalDateTime.now());
        region3.setLastUpdated(LocalDateTime.now());
        region4.setLastUpdated(LocalDateTime.now());

        Assertions.assertNull(regionService.getQueryRegion(regions));
    }

    @Test
    public void testGetQuerryRegionWhileUpToDate1() {
        region1.setLastUpdated(LocalDateTime.now());
        region2.setLastUpdated(LocalDateTime.now());
        region3.setLastUpdated(LocalDateTime.now());

        Assertions.assertEquals(region4, regionService.getQueryRegion(regions));
    }

    @Test
    public void testGetQuerryRegionWhileUpToDate2() {
        region1.setLastUpdated(LocalDateTime.now());
        region3.setLastUpdated(LocalDateTime.now());

        Assertions.assertEquals(new Region(
                null,
                region2.getNorthBound(),
                region2.getEastBound(),
                region4.getSouthBound(),
                region4.getWestBound()
        ), regionService.getQueryRegion(regions));
    }

    @Test
    public void testGetQuerryRegionWhileUpToDate3() {
        region2.setLastUpdated(LocalDateTime.now());
        region3.setLastUpdated(LocalDateTime.now());

        Assertions.assertEquals(new Region(
                null,
                region1.getNorthBound(),
                region2.getEastBound(),
                region4.getSouthBound(),
                region3.getWestBound()
        ), regionService.getQueryRegion(regions));
    }

    @Test
    public void testGetQuerryRegionWhileUpToDate4() {
        region1.setLastUpdated(LocalDateTime.now());
        region2.setLastUpdated(LocalDateTime.now());

        Assertions.assertEquals(new Region(
                null,
                region3.getNorthBound(),
                region4.getEastBound(),
                region4.getSouthBound(),
                region3.getWestBound()
        ), regionService.getQueryRegion(regions));
    }

    @Test
    public void testGetQuerryRegionWhileUpToDate5() {
        region3.setLastUpdated(LocalDateTime.now());
        region4.setLastUpdated(LocalDateTime.now());

        Assertions.assertEquals(new Region(
                null,
                region1.getNorthBound(),
                region2.getEastBound(),
                region2.getSouthBound(),
                region1.getWestBound()
        ), regionService.getQueryRegion(regions));
    }

    @Test
    public void testGetQuerryRegionWhileUpToDate6() {
        Assertions.assertEquals(new Region(
                null,
                region1.getNorthBound(),
                region2.getEastBound(),
                region4.getSouthBound(),
                region3.getWestBound()
        ), regionService.getQueryRegion(regions));
    }
}
