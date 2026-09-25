package seng202.team4.unittests.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import seng202.team4.models.Region;
import java.time.LocalDateTime;

/**
 * A test class for {@link Region}
 */
public class RegionModelTest {
    Region region;
    String type;
    double north;
    double east;
    double south;
    double west;

    LocalDateTime date;

    @BeforeEach
    public void setUpTest() {
        type = "test";

        north = 1.0;
        east = 2.0;
        south = 3.0;
        west = 4.0;

        date = LocalDateTime.now();

        region = new Region(type, north, east, south, west);
    }

    @Test
    public void testInitialValues() {
        Assertions.assertEquals(type, region.getType());
        Assertions.assertEquals(north, region.getNorthBound());
        Assertions.assertEquals(east, region.getEastBound());
        Assertions.assertEquals(south, region.getSouthBound());
        Assertions.assertEquals(west, region.getWestBound());
        Assertions.assertNull(region.getLastUpdated());
    }

    @Test
    public void testSetDate() {
        region.setLastUpdated(date);
        Assertions.assertEquals(date, region.getLastUpdated());
    }

    @Test
    public void testEquals() {
        Assertions.assertEquals(region, region);
    }

    @Test
    public void testNotEquals() {
        Region region2 = new Region("", 0, 0, 0, 0);
        Assertions.assertNotEquals(region, region2);
    }

    @Test
    public void testEqualsNullReturnsFalse() {
        Assertions.assertNotEquals(null, region);
    }
}
