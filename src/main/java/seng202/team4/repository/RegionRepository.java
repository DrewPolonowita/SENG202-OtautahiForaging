package seng202.team4.repository;

import seng202.team4.models.Region;

/**
 * An interface to access the regions in the database
 */
public interface RegionRepository extends DAOInterface<Region> {
    /**
     * Removes the given region from the database
     * @param region the region to remove
     */
    void delete(Region region);

    Region getRegion(Region region);
}
