package seng202.team4.services.api;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Class to handle signing in to the FatSecret API using OAuth 1.0
 * @author Team 4
 */
public class OAuth1Signature {

    private final String consumerKey;
    private final String secretKey;

    /**
     * Constructor
     * @param consumerKey consumer key
     * @param secretKey secret key
     */
    public OAuth1Signature(String consumerKey, String secretKey) {
        this.consumerKey = consumerKey;
        this.secretKey = secretKey;
    }

    /**
     * Returns the authenticated signature string
     * format is:
     * OAuth oauth_consumer_key="key", oauth_signature_method="HMAC-SHA1", oauth_timestamp="178934891",
     * oauth_nonce="jkl32i4h9", oauth_version="1.0", oauth_signature="fakeSignature"
     * @param http GET or PUT (we only use get, case-sensitive)
     * @param baseUrl URL for the method we are trying to use e.g get food by id or food search
     * @param requestParams params for the particular method
     * @return Signature
     */
    public String createSignature(
            String http,
            String baseUrl,
            Map<String, String> requestParams
    ) {
        Map<String, String> authParams = new HashMap<>();
        authParams.put("oauth_consumer_key", consumerKey);
        authParams.put("oauth_signature_method", "HMAC-SHA1");
        authParams.put("oauth_timestamp", String.valueOf(getTimeInSecondsSince1970()));
        authParams.put("oauth_nonce", UUID.randomUUID().toString());
        authParams.put("oauth_version", "1.0");

        Map<String, String> finalParams = new TreeMap<>();
        finalParams.putAll(authParams);
        finalParams.putAll(requestParams);

        String signatureBaseString = http
                + "&" + percentEncode(baseUrl)
                + "&" + percentEncode(concatenateParams(finalParams));

        String signature = hmacSignature(signatureBaseString);
        authParams.put("oauth_signature", signature);

        return buildAuthorisationHeader(authParams);
    }

    /**
     * From params, build final authorisation header for use in the request
     * @param authParams Authentication parameters
     * @return final header for use in a http request
     */
    private String buildAuthorisationHeader(Map<String, String> authParams) {
        StringBuilder header = new StringBuilder("OAuth ");
        boolean isFirstParam = true;

        for (Map.Entry<String, String> param : authParams.entrySet()) {
            if (!isFirstParam) {
                header.append(", ");
            }

            header.append(percentEncode(param.getKey()))
                    .append("=\"")
                    .append(percentEncode(param.getValue()))
                    .append("\"");

            isFirstParam = false;

        }
        return header.toString();
    }

    /**
     * <a href="https://stackoverflow.com/questions/3208160/how-to-generate-an-hmac-in-java-equivalent-to-a-python-example">...</a>
     * RFC2104 HmacSHA1 signature conversion (method outlined as "oauth_signature_method")
     * @param signatureBaseString The pre signature authentication string
     * @return RFC2104 encoded hmac signature
     */
    private String hmacSignature(String signatureBaseString) {
        try {
            String signingKey = percentEncode(secretKey) + "&";
            SecretKeySpec keySpec = new SecretKeySpec(signingKey.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA1");

            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(keySpec);
            byte[] result = mac.doFinal(signatureBaseString.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(result);

        } catch (InvalidKeyException | NoSuchAlgorithmException _) {
            throw new IllegalStateException("Failed to create OAuth HMAC-SHA1 signature");
        }
    }


    /**
     * Concatenates the parameters. Because it's using a TreeMap it is in order of keys as required
     * Encodes each parameter as they are concatenated
     * @param params all params to concatenate
     * @return concatenated parameter string
     */
    private String concatenateParams(Map<String, String> params) {
        StringBuilder output = new StringBuilder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (!output.isEmpty()) {
                output.append("&");
            }

            String key = percentEncode(param.getKey());
            String value = percentEncode(param.getValue());

            output.append(key).append("=").append(value);
        }
        return output.toString();
    }

    /**
     * <a href="https://stackoverflow.com/questions/5864954/java-and-rfc-3986-uri-encoding">...</a>
     * RFC 3986 encode as required by OAuth 1.0
     * @param toEncode String to encode
     * @return encoded string
     */
    private String percentEncode(String toEncode) {
        if (toEncode == null) {
            return "";
        }

        return URLEncoder.encode(toEncode, StandardCharsets.UTF_8)
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("%7E", "~");
    }

    /**
     * Method to get time in seconds since Jan 1 1970
     * @return time in seconds
     */
    private long getTimeInSecondsSince1970() {
        return System.currentTimeMillis() / 1000;
    }
}
