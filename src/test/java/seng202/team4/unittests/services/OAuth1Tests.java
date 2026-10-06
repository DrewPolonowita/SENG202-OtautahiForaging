package seng202.team4.unittests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seng202.team4.services.api.OAuth1Signature;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OAuth1Tests {

    OAuth1Signature oAuth1Signature;
    String authHeader;
    String fakeConsumerKey = "fakeConsumerKey";


    @BeforeEach
    void setUpTest() {
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
    void createSignatureIncludesOAuthParams() {
        assertAll(
                () -> assertTrue(authHeader.contains("oauth_consumer_key")),
                () -> assertTrue(authHeader.contains("oauth_signature_method")),
                () -> assertTrue(authHeader.contains("oauth_timestamp")),
                () -> assertTrue(authHeader.contains("oauth_nonce")),
                () -> assertTrue(authHeader.contains("oauth_version"))
        );
    }

    @Test
    void createSignatureUsesCorrectMethod() {
        assertTrue(authHeader.contains("HMAC-SHA1"));
    }

    @Test
    void createSignatureUsesCorrectVersion() {
        assertTrue(authHeader.contains("1.0"));
    }
    @Test
    void createSignatureUsesProvidedKey() {
        assertTrue(authHeader.contains(fakeConsumerKey));
    }


}
