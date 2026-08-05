package seng202.team4.unittests.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import seng202.team4.services.ApiHandler;

/**
 * A test class for {@link ApiHandler}
 * The class is difficult to test since API's may not be deterministic from the client POV
 * The only classes tested are those which are not reliant on an API
 *
 * @author Drew Polonowita
 */
public class ApiHandlerTest {
    private ApiHandler apiHandeler;

    @BeforeEach
    public void setUpTest() {
        apiHandeler = new ApiHandler();
    }

    @Test
    public void testEmptyValues() {
        String urlEncode = apiHandeler.urlEncodeParams("", "", 0);
        String expectedUrl = "query?where=1%3D1&outFields=*&resultRecordCount=0&f=geojson";

        Assertions.assertEquals(expectedUrl, urlEncode);
    }

    @Test
    public void testSingleWhereClause() {
        String urlEncode = apiHandeler.urlEncodeParams("TreeID=38083", "", 0);
        String expectedUrl = "query?where=TreeID%3D38083&outFields=*&resultRecordCount=0&f=geojson";

        Assertions.assertEquals(expectedUrl, urlEncode);
    }

    @Test
    public void testSingleWhereLikeClause() {
        String urlEncode = apiHandeler.urlEncodeParams("CommonName LIKE '%Apple%'", "", 0);
        String expectedUrl =
                "query?where=CommonName%20LIKE%20%27%25Apple%25%27&outFields=*&resultRecordCount=0&f=geojson";

        Assertions.assertEquals(expectedUrl, urlEncode);
    }

    @Test
    public void testMultipleWhereClauses() {
        String urlEncode = apiHandeler.urlEncodeParams("CommonName LIKE '%Apple%' AND TreeID=38083", "", 0);
        String expectedUrl = "query?where=CommonName%20LIKE%20%27%25Ap" +
                "ple%25%27%20AND%20TreeID%3D38083&outFields=*&resultRecordCount=0&f=geojson";

        Assertions.assertEquals(expectedUrl, urlEncode);
    }

    @Test
    public void testOutFieldClause() {
        String urlEncode = apiHandeler.urlEncodeParams("", "TreeID", 0);
        String expectedUrl = "query?where=1%3D1&outFields=TreeID&resultRecordCount=0&f=geojson";

        Assertions.assertEquals(expectedUrl, urlEncode);
    }

    @Test
    public void testMultipleOutFieldClause() {
        String urlEncode = apiHandeler.urlEncodeParams("", "TreeID,Ownership,Genus", 0);
        String expectedUrl = "query?where=1%3D1&outFields=TreeID%2COwnership%2CGenus&resultRecordCount=0&f=geojson";

        Assertions.assertEquals(expectedUrl, urlEncode);
    }

    @Test
    public void testMaxRequests() {
        String urlEncode = apiHandeler.urlEncodeParams("", "", 100);
        String expectedUrl = "query?where=1%3D1&outFields=*&resultRecordCount=100&f=geojson";

        Assertions.assertEquals(expectedUrl, urlEncode);
    }
}
