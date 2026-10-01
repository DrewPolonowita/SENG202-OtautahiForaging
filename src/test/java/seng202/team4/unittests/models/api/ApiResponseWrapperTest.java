package seng202.team4.unittests.models.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.models.api.ApiResponseWrapper;

/**
 * A test class for the {@link ApiResponseWrapper}
 */
class ApiResponseWrapperTest {
    ApiResponseWrapper<String> apiResponseWrapper;
    String testObj;
    int errorCode;

    @BeforeEach
    void setUpTest() {
        apiResponseWrapper = new ApiResponseWrapper<>(testObj, errorCode);
    }

    @Test
    void testInitialValues() {
        Assertions.assertEquals(testObj, apiResponseWrapper.getApiResponse());
        Assertions.assertEquals(errorCode, apiResponseWrapper.getResponseCode());
    }

}
