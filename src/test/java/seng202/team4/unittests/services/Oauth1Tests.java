package seng202.team4.unittests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.services.api.ApiClientInterface;
import seng202.team4.services.api.FoodApiClient;
import seng202.team4.services.api.OAuth1Signature;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class Oauth1Tests {

    OAuth1Signature oAuth1Signature;
    String authHeader;
    String fakeConsumerKey = "fakeConsumerKey";


    @BeforeEach
    public void setUpTest() {
        oAuth1Signature = new OAuth1Signature(fakeConsumerKey, "fakeSecretKey");
        Map<String, String> params = new HashMap<>();
        params.put("food_id", "123");
        params.put("format", "json");

        authHeader = oAuth1Signature.createSignature(
                "GET",
                "https://platform.fatsecret.com/rest/food/v5",
                params
        );
    }

    @Test
    public void createSignatureIncludesOAuthParams() {
        assertTrue(authHeader.contains("oauth_consumer_key"));
        assertTrue(authHeader.contains("oauth_signature_method"));
        assertTrue(authHeader.contains("oauth_timestamp"));
        assertTrue(authHeader.contains("oauth_nonce"));
        assertTrue(authHeader.contains("oauth_version"));
    }

    @Test
    public void createSignatureUsesCorrectMethod() {
        assertTrue(authHeader.contains("HMAC-SHA1"));
    }

    @Test
    public void createSignatureUsesCorrectVersion() {
        assertTrue(authHeader.contains("1.0"));
    }
    @Test
    public void createSignatureUsesProvidedKey() {
        assertTrue(authHeader.contains(fakeConsumerKey));
    }


}
