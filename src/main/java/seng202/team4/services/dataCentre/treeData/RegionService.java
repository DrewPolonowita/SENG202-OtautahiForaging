package seng202.team4.services.dataCentre.treeData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.models.Region;
import seng202.team4.repository.RegionRepository;
import seng202.team4.services.DoubleService;

/**
 * A class to manage regions in which data is requested together to help keep data up to date
 */
public class RegionService {

    private final static double NORTH_BOUND = -43.40;
    private final static double EAST_BOUND = 172.85;
    private final static double SOUTH_BOUND = -43.70;
    private final static double WEST_BOUND = 172.35;

    private final static double REGION_SIZE = 0.03;
    private final static int BUFFER_IN_WEEKS = 1;

    private final RegionRepository regionRepository;

    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    /**
     * Returns all the regions that overlap with the given bounds and are in the Christchurch region
     *
     * @param northBound the northbound
     * @param eastBound the eastbound
     * @param southBound the southbound
     * @param westBound the westbound
     * @return an {@link ArrayList} of all the overlapping {@link Region}s with the bounds
     */
    public List<Region> getOverlappingRegions(
            String type, double northBound, double eastBound, double southBound, double westBound
    ) {
        ArrayList<Region> regionsList = new ArrayList<>();
        DoubleService doubleService = new DoubleService();

        for (int i = 0; doubleService.greaterThanTolerance(NORTH_BOUND - SOUTH_BOUND, i * REGION_SIZE); i++) {
            double north = (i + 1) * REGION_SIZE + SOUTH_BOUND;
            double south = i * REGION_SIZE + SOUTH_BOUND;

            if (northBound > south && southBound < north &&
                    !(doubleService.equalityTolerance(north, southBound) ||
                            doubleService.equalityTolerance(south, northBound))
            ) {
                for (int j = 0; doubleService.greaterThanTolerance(EAST_BOUND - WEST_BOUND, j * REGION_SIZE); j++) {
                    double east = (j + 1) * REGION_SIZE + WEST_BOUND;
                    double west = j * REGION_SIZE + WEST_BOUND;

                    if (eastBound > west && westBound < east &&
                        !(doubleService.equalityTolerance(west, eastBound) ||
                                doubleService.equalityTolerance(east, westBound))
                    ) {
                        Region region = new Region(type, north, east, south, west);
                        try {
                            regionRepository.add(region);
                        } catch (DuplicateEntryException e) {
                            region = regionRepository.getRegion(region);
                        }
                        regionsList.add(region);
                    }
                }
            }
        }
        return regionsList;
    }

    public Region getQueryRegion(List<Region> regions) {
        ArrayList<Region> notUpToDateRegions = new ArrayList<>();

        for (Region region: regions) {
            if (!isRegionRecent(region)) {
                notUpToDateRegions.add(region);
            }
        }

        if (notUpToDateRegions.isEmpty()) {
            return null;
        }

        double maxLat = notUpToDateRegions.getFirst().getNorthBound();
        double maxLon = notUpToDateRegions.getFirst().getEastBound();
        double minLat = notUpToDateRegions.getFirst().getSouthBound();
        double minLon = notUpToDateRegions.getFirst().getWestBound();

        for (Region region: notUpToDateRegions) {
            if (region.getNorthBound() > maxLat) {
                maxLat = region.getNorthBound();
            }

            if (region.getEastBound() > maxLon) {
                maxLon = region.getEastBound();
            }

            if (region.getSouthBound() < minLat) {
                minLat = region.getSouthBound();
            }

            if (region.getWestBound() < minLon) {
                minLon = region.getWestBound();
            }
        }

        return new Region(null, maxLat, maxLon, minLat, minLon);
    }

    public List<Region> getRegionsInside(Region masterRegion, List<Region> regions) {
        ArrayList<Region> outsideRegions = new ArrayList<>();
        ArrayList<Region> insideRegions = new ArrayList<>();

        for (Region region: regions) {
            if (region.getNorthBound() <= masterRegion.getSouthBound() - REGION_SIZE / 2) {
                outsideRegions.add(region);
            } else if (region.getEastBound() <= masterRegion.getWestBound() - REGION_SIZE / 2) {
                outsideRegions.add(region);
            } else if (region.getSouthBound() >= masterRegion.getNorthBound() + REGION_SIZE / 2) {
                outsideRegions.add(region);
            } else if (region.getWestBound() >= masterRegion.getEastBound() + REGION_SIZE / 2) {
                outsideRegions.add(region);
            } else {
                insideRegions.add(region);
            }
        }

        return insideRegions;
    }

    /**
     * Returns true if the region was updated within the last x weeks. Where x is defined by the
     * BUFFER_IN_WEEKS static constant class attribute. Returns false if the region is null or if the
     * lastUpdated attribute is null.
     *
     * @param region the region to check
     * @return true if the region was updated within the last x weeks when x is defined by the BUFFER_IN_WEEKS attribute
     */
    public boolean isRegionRecent(Region region) {
        if (region == null || region.getLastUpdated() == null) {
            return false;
        }

        return region.getLastUpdated().isAfter(LocalDateTime.now().minusWeeks(BUFFER_IN_WEEKS));
    }
}
