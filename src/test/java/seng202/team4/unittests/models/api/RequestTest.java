package seng202.team4.unittests.models.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.models.api.Request;

/**
 * A test class for the {@link Request}
 */
class RequestTest {
    Request<String> request;
    String testObj;
    int errorCode;

    @BeforeEach
    void setUpTest() {
        request = new Request<>(testObj, errorCode);
    }

    @Test
    void testInitialValues() {
        Assertions.assertEquals(testObj, request.getApiResponse());
        Assertions.assertEquals(errorCode, request.getResponseCode());
    }

}
