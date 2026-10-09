package seng202.team4.unittests.services.api;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.models.api.ApiResponseWrapper;
import seng202.team4.services.api.ApiClient;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A test class for {@link ApiClient}
 */
class ApiClientTest {

    private ApiClient apiClient;
    private HttpsURLConnection connection;
    private ByteArrayInputStream bytes;
    private TypeReference<TestResponse> typeReference;

    final int STATUS_OK = 200;
    final int STATUS_INTERNAL_SERVER_ERROR = 500;
    final int STATUS_UNAUTHORIZED = 401;
    final int INVALID_URL = -1;

    @BeforeEach
    void setUpTest() {
        apiClient = new ApiClient();
        connection = mock(HttpsURLConnection.class);

        bytes = new ByteArrayInputStream("{\"name\":\"Apple\",\"id\":123}".getBytes());
        typeReference = new TypeReference<TestResponse>() {};
    }

    static class TestResponse {
        public String name;
        public int id;
    }

    /**
     * calls the getRequest method
     */
    @SuppressWarnings("unchecked")
    private <T> ApiResponseWrapper<T> getRequest(
            TypeReference<T> typeReference, HttpsURLConnection connection
    ) throws Exception {

        Method method = ApiClient.class.getDeclaredMethod("getRequest", TypeReference.class, HttpsURLConnection.class);
        method.setAccessible(true);

        return (ApiResponseWrapper<T>) method.invoke(apiClient, typeReference, connection);
    }

    @Test
    void testGetResponse() throws Exception {
        when(connection.getResponseCode()).thenReturn(STATUS_OK);
        when(connection.getInputStream()).thenReturn(bytes);

        ApiResponseWrapper<TestResponse> result = getRequest(typeReference, connection);

        assertNotNull(result);
        assertEquals(STATUS_OK, result.getResponseCode());

        assertNotNull(result.getApiResponse());
        assertEquals("Apple", result.getApiResponse().name);
        assertEquals(123, result.getApiResponse().id);
    }

    @Test
    void testBadResponseHasNullAPIResponse() throws Exception {
        when(connection.getResponseCode()).thenReturn(STATUS_UNAUTHORIZED);
        ApiResponseWrapper<TestResponse> result = getRequest(typeReference, connection);

        assertNotNull(result);
        assertNull(result.getApiResponse());
        assertEquals(STATUS_UNAUTHORIZED, result.getResponseCode());
    }

    @Test
    void testBadBodyReturnsNull() throws Exception {
        String invalidFormatString = "not a valid format for the body";

        when(connection.getResponseCode()).thenReturn(STATUS_OK);
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream(invalidFormatString.getBytes()));

        ApiResponseWrapper<TestResponse> result = getRequest(typeReference, connection);

        assertNotNull(result);
        assertNull(result.getApiResponse());
        assertEquals(STATUS_OK, result.getResponseCode());
    }

    @Test
    void testExceptionThrows() throws Exception {
        String exceptionString = "generic io exception";
        when(connection.getInputStream()).thenThrow(new IOException(exceptionString));
        ApiResponseWrapper<TestResponse> result = getRequest(typeReference, connection);
        assertNotNull(result);
        assertNull(result.getApiResponse());
    }

    @Test
    void testInvalidURLReturnsMinusOne() {
        ApiResponseWrapper<TestResponse> result = apiClient.get("", typeReference);
        assertNotNull(result);
        assertNull(result.getApiResponse());
        assertEquals(INVALID_URL, result.getResponseCode());
    }

    @Test
    void testResponseCodeIsCalledCorrectly() throws Exception {
        when(connection.getInputStream()).thenReturn(bytes);
        getRequest(new TypeReference<TestResponse>() {}, connection);
        verify(connection, atLeastOnce()).getResponseCode();
    }

    @Test
    void testRequestTypeSetCorrectly() throws Exception {
        String url = "https://iAmAUrlToARealWebsitreTrust.com";

        Method method = ApiClient.class.getDeclaredMethod("createConnection", String.class);
        method.setAccessible(true);

        HttpsURLConnection connection = (HttpsURLConnection) method.invoke(apiClient, url);

        try {
            assertEquals("GET", connection.getRequestMethod());
        } finally {
            connection.disconnect();
        }
    }

    @Test
    void testInvalidURLThrowsExceptionWithAuth() throws Exception {
        String bearerToken = "Bearer this-is-a-bearer-token";
        String invalidUrl = "this is a valid url";

        Method method = ApiClient.class.getDeclaredMethod("createConnection", String.class, String.class);
        method.setAccessible(true);

        Exception exception = assertThrows(
                java.lang.reflect.InvocationTargetException.class,
                () -> method.invoke(apiClient, invalidUrl, bearerToken)
        );

        assertInstanceOf(IOException.class, exception.getCause());
    }

    @Test
    void testInvalidUrlWrapperNotNull() {
        String bearerToken = "Bearer this-is-a-bearer-token";
        String invalidUrl = "this is a valid url";

        ApiResponseWrapper<String> result = apiClient.get(invalidUrl, new TypeReference<String>() {}, bearerToken);
        assertNotNull(result);
    }
}