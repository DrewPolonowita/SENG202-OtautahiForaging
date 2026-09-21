package seng202.team4.repository;

import seng202.team4.models.Region;

/**
 * An interface to access the regions in the database
 */
public interface RegionRepository extends DAOInterface<Region, Region> {
    /**
     * Removes the given region from the database
     * @param region the region to remove
     */
    void delete(Region region);

    /**
     * Method to get a specific region from database
     * @param region region to find
     * @return Region object found
     */
    Region getRegion(Region region);
}
