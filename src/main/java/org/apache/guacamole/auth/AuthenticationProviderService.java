package org.apache.guacamole.auth;

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.auth.Credentials;
import org.apache.guacamole.net.auth.credentials.CredentialsInfo;
import org.apache.guacamole.net.auth.credentials.GuacamoleInvalidCredentialsException;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.core5.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class AuthenticationProviderService {

    /**
     * Service for retrieving configuration values
     */
    @Inject
    private ConfigurationService confService;

    /**
     * Provider for JwtUser objects
     */
    @Inject
    private Provider<JwtUser> authenticatedUserProvider;

    public JwtUser authenticateUser(Credentials credentials, HttpClient client) throws GuacamoleException {
        HttpServletRequest req = credentials.getRequest();

        String authToken = null;
        String header = confService.getJwtHeader();
        String cookie = confService.getJwtCookie();

        // first try the header (if a value was set)
        if (header != null) {
            authToken = req.getHeader(confService.getJwtHeader());
        }

        // if we didn't find the header (or didn't look) try to get via the cookie
        if (authToken == null && cookie != null) {
            Cookie[] cookies = req.getCookies();
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals(cookie)) {
                    authToken = cookies[i].getValue();
                    break;
                }
            }
        }

        if (authToken == null) {
            throw new GuacamoleInvalidCredentialsException("JWT Not Found(bad header or cookie?)",
                    CredentialsInfo.EMPTY);
        }

        try {
            // decode JWT
            DecodedJWT token = JWT.decode(authToken);

            // fetch cert
            RSAPublicKey publicKey = getJwksKeys(token.getKeyId(), client);

            // validate token
            Algorithm algo = Algorithm.RSA256(publicKey, null);
            JWTVerifier verifier = JWT.require(algo)
                    .withAudience(confService.getJwtAudience())
                    .build();

            DecodedJWT verifiedToken = verifier.verify(token);

            JwtUser user = authenticatedUserProvider.get();
            user.init(verifiedToken, credentials);
            return user;

        } catch (Exception exception) {
            // catch all other exceptions
            throw new GuacamoleInvalidCredentialsException("jwt auth failed", exception,
                    CredentialsInfo.EMPTY);
        }
    }

    private RSAPublicKey getJwksKeys(String keyId, HttpClient client)
            throws URISyntaxException, IOException, InterruptedException, Exception {

        HttpGet req = new HttpGet(confService.getJwtJwksUrl());
        String payload = client.execute(req, response -> {
            HttpEntity entity = response.getEntity();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            entity.writeTo(os);
            return os.toString();
        });

        // Extract the keys array using a JSONPointer query/path (if provided)
        JSONArray arr;
        JSONObject obj = new JSONObject(payload);
        Object arrObj = obj.query(confService.getJwtJwksPath());
        if (arrObj != null && arrObj instanceof JSONArray) {
            arr = (JSONArray) arrObj;
        } else {
            throw new Exception("JWKS object is not an array");
        }

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Decoder decoder = Base64.getUrlDecoder();

        // Loop through all keys until we find a matching key id, or throw an error if
        // never found
        for (int i = 0; i < arr.length(); i++) {
            JSONObject key = arr.getJSONObject(i);
            String kid = key.getString("kid");
            if (keyId.equals(kid)) {
                BigInteger exp = new BigInteger(1, decoder.decode(key.getString("e")));
                BigInteger mod = new BigInteger(1, decoder.decode(key.getString("n")));

                RSAPublicKeySpec spec = new RSAPublicKeySpec(mod, exp);
                return (RSAPublicKey) keyFactory.generatePublic(spec);
            }
        }

        throw new Exception("cert not found");
    }
}
