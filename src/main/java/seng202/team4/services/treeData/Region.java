package seng202.team4.services.treeData;

/**
 * A region of an area in Christchurch to be Querried together with the api.
 */
public class Region {
    private double northBound;
    private double eastBound;
    private double southBound;
    private double westBound;

    /**
     * Creates a region with the given bounds in Christchurch
     * @param northBound the northbound
     * @param eastBound the eastbound
     * @param southBound the southbound
     * @param westBound the westbound
     **/
    public Region(double northBound, double eastBound, double southBound, double westBound) {
        this.northBound = northBound;
        this.eastBound = eastBound;
        this.southBound = southBound;
        this.westBound = westBound;
    }
}
