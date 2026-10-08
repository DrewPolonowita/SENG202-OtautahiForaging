package seng202.team4.models;

import seng202.team4.services.DoubleService;

import java.time.LocalDateTime;

/**
 * A region of an area in Christchurch to be Queried together with the api.
 */
public class Region {
    private final double northBound;
    private final double eastBound;
    private final double southBound;
    private final double westBound;

    private final String type;
    private LocalDateTime lastUpdated;

    /**
     * Creates a region with the given bounds in Christchurch
     * @param northBound the northbound
     * @param eastBound the eastbound
     * @param southBound the southbound
     * @param westBound the westbound
     **/
    public Region(
            String type, double northBound, double eastBound, double southBound, double westBound
    ) {
        this.northBound = northBound;
        this.eastBound = eastBound;
        this.southBound = southBound;
        this.westBound = westBound;

        this.type = type;
    }

    /**
     * Sets lastUpdated field
     *
     * @param lastUpdated the new value
     */
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * returns the northbound
     *
     * @return the northbound
     */
    public double getNorthBound() {
        return northBound;
    }

    /**
     * returns the eastbound
     *
     * @return the eastbound
     */
    public double getEastBound() {
        return eastBound;
    }

    /**
     * returns the southbound
     *
     * @return the southbound
     */
    public double getSouthBound() {
        return southBound;
    }

    /**
     * returns the westbound
     *
     * @return the westbound
     */
    public double getWestBound() {
        return westBound;
    }

    /**
     * returns the type
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * return the lastUpdated time
     *
     * @return the lastUpdated time
     */
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Overrides the equality trait for {@link Region}
     *
     * @param obj the obj to compare equality with
     * @return true if the object is both a region of the same attributes
     */
    @Override
    public boolean equals(Object obj) {
        DoubleService doubleService = new DoubleService();

        if (obj instanceof Region region) {
            return doubleService.equalityTolerance(this.getNorthBound(), region.getNorthBound())
                    && doubleService.equalityTolerance(this.getEastBound(), region.getEastBound())
                    && doubleService.equalityTolerance(this.getSouthBound(), region.getSouthBound())
                    && doubleService.equalityTolerance(this.getWestBound(), region.getWestBound())
                    && (this.getType() == region.getType()
                    || this.getType().equals(region.getType()))
                    && (this.getLastUpdated() == region.getLastUpdated()
                    || this.getLastUpdated().equals(region.getLastUpdated()));
        }

        return false;
    }
}
