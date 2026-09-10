package seng202.team4.services.treeData;

import java.util.ArrayList;

import seng202.team4.models.Region;

/**
 * A class to manage regions in which data is requested together to help keep data up to date
 */
public class RegionService {

    private final static double NORTH_BOUND = -43.40;
    private final static double EAST_BOUND = 172.85;
    private final static double SOUTH_BOUND = -43.70;
    private final static double WEST_BOUND = 172.35;

    private final static double REGION_SIZE = 0.03;

    /**
     * Returns all the regions that overlap with the given bounds and in Christchurch
     * @param northBound the northbound
     * @param eastBound the eastbound
     * @param southBound the southbound
     * @param westBound the westbound
     * @return an {@link ArrayList} of all the overlapping {@link Region}s with the bounds
     */
    public ArrayList<Region> getOverlappingRegions(
            String type, double northBound, double eastBound, double southBound, double westBound
    ) {
        ArrayList<Region> regionsList = new ArrayList<>();

        for (int i = 0; i * REGION_SIZE < NORTH_BOUND - SOUTH_BOUND; i++) {
            double north = (i + 1) * REGION_SIZE + SOUTH_BOUND;
            double south = i * REGION_SIZE + SOUTH_BOUND;

            if (northBound >= south && southBound <= north) {
                for (int j = 0; j * REGION_SIZE < EAST_BOUND - WEST_BOUND; j++) {
                    double east = (j + 1) * REGION_SIZE + WEST_BOUND;
                    double west = j * REGION_SIZE + WEST_BOUND;

                    if (eastBound >= west && westBound <= east) {
                        regionsList.add(new Region(type, north, east, south, west));
                    }
                }
            }
        }
        return regionsList;
    }
}
