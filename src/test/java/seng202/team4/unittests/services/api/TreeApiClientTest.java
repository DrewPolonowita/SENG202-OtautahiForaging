package seng202.team4.unittests.services.api;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import seng202.team4.models.api.ApiResponseWrapper;
import seng202.team4.models.api.TreeApi.TreeApiResponse;
import seng202.team4.services.api.ApiClient;
import seng202.team4.services.api.TreeApiClient;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TreeApiClientTest {
    private ApiClient apiClient;
    private TreeApiClient treeApiClient;

    @BeforeEach
    void setUpTest() {
        apiClient = mock(ApiClient.class);
        treeApiClient = new TreeApiClient(apiClient);
    }

    /**
     * calls the getRequest method
     */
    @SuppressWarnings("unchecked")
    private String urlEncodeParams(String params, String outFields, int maxTuples) throws Exception {

        Method method = TreeApiClient.class.getDeclaredMethod("urlEncodeParams", String.class, String.class, int.class);
        method.setAccessible(true);

        return (String) method.invoke(treeApiClient, params, outFields, maxTuples);
    }

    /**
     * calls the getRequest method
     */
    @SuppressWarnings("unchecked")
    private String urlEncodeParams(
            String params, String outFields, int maxTuples, double north, double east, double south, double west
    ) throws Exception {

        Method method = TreeApiClient.class.getDeclaredMethod(
                "urlEncodeParams", String.class, String.class, int.class, double.class, double.class, double.class, double.class
        );
        method.setAccessible(true);

        return (String) method.invoke(treeApiClient, params, outFields, maxTuples, north, east, south, west);
    }

    @Test
    void testUrlEncodeParams() throws Exception {
        String ExpectedUrl = "query?where=CommonName%3D%27Apple%27&outFields=TreeID%2COwnership%2CGenus%2CCommonName&resultRecordCount=100&f=geojson";

        String params = "CommonName='Apple'";
        String outFields = "TreeID,Ownership,Genus,CommonName";

        String ActualUrl = urlEncodeParams(params, outFields, 100);
        Assertions.assertEquals(ExpectedUrl, ActualUrl);
    }

    @Test
    void testUrlEncodeParamsWithNoParams() throws Exception {
        String ExpectedUrl = "query?where=1%3D1&outFields=TreeID%2COwnership%2CGenus%2CCommonName&resultRecordCount=100&f=geojson";

        String params = "";
        String outFields = "TreeID,Ownership,Genus,CommonName";

        String ActualUrl = urlEncodeParams(params, outFields, 100);
        Assertions.assertEquals(ExpectedUrl, ActualUrl);
    }

    @Test
    void testUrlEncodeParamsWithNoOutFields() throws Exception {
        String ExpectedUrl = "query?where=CommonName%3D%27Apple%27&outFields=*&resultRecordCount=100&f=geojson";

        String params = "CommonName='Apple'";
        String outFields = "";

        String ActualUrl = urlEncodeParams(params, outFields, 100);
        Assertions.assertEquals(ExpectedUrl, ActualUrl);
    }

    @Test
    void testUrlEncodeParamsWithARegion() throws Exception {
        String ExpectedUrl = "query?where=1%3D1&outFields=*&resultRecordCount=100&f=geojson&geometryType=esriGeometryEnvelope&spatialRel=esriSpatialRelIntersects&inSR=4326&outSR=4326&geometry=4.000000%2C3.000000%2C2.000000%2C1.000000";

        String params = "";
        String outFields = "";

        String ActualUrl = urlEncodeParams(params, outFields, 100, 1.0, 2.0, 3.0, 4.0);
        Assertions.assertEquals(ExpectedUrl, ActualUrl);
    }

    @Test
    void testGetTrees() {
        ApiResponseWrapper<TreeApiResponse> expected = mock(ApiResponseWrapper.class);
        when(apiClient.get(anyString(), ArgumentMatchers.<TypeReference<TreeApiResponse>>any()))
                .thenReturn(expected);

        String params = "CommonName='Apple'";
        String outFields = "TreeID,Ownership,Genus,CommonName";

        ApiResponseWrapper<TreeApiResponse> actual = treeApiClient.getTrees(params, outFields,100);
        assertSame(expected, actual);
    }

    @Test
    void testGetTreesWithBounds() {
        ApiResponseWrapper<TreeApiResponse> expected = mock(ApiResponseWrapper.class);
        when(apiClient.get(anyString(), ArgumentMatchers.<TypeReference<TreeApiResponse>>any()))
                .thenReturn(expected);

        String params = "CommonName='Apple'";
        String outFields = "TreeID,Ownership,Genus,CommonName";

        ApiResponseWrapper<TreeApiResponse> actual = treeApiClient.getTrees(
                params, outFields,100, 1.0, 2.0, 3.0, 4.0
        );
        assertSame(expected, actual);
    }
}