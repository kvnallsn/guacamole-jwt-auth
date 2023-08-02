package org.apache.guacamole.auth;

import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.environment.Environment;

import com.google.inject.Inject;

public class ConfigurationService {
    /**
     * The Guacamole server environment
     */
    @Inject
    private Environment environment;

    /**
     * Returns the header field storing the JWT
     * 
     * @return
     *         The name of the header field that stores the JWT
     * 
     * @throws GuacamoleException
     *                            If guacamole.properties cannot be parsed
     */
    public String getJwtHeader() throws GuacamoleException {
        return environment.getProperty(
                JwtAuthenticationGuacamoleProperties.JWT_HEADER);
    }

    /**
     * Returns the URL of the JWKS to validate a JWT
     * 
     * @return
     *         The JWKS URL
     * 
     * @throws GuacamoleException
     *                            If guacamole.properties cannot be parsed
     */
    public String getJwtJwksUrl() throws GuacamoleException {
        return environment.getRequiredProperty(
                JwtAuthenticationGuacamoleProperties.JWT_JWKS_URL);
    }

    /**
     * Returns the path to the JSON Web Keys in the data received from
     * `getJwtJwksUrl()`
     * 
     * @return
     *         Path (i.e., 'keys') to the JWKS data
     * 
     * @throws GuacamoleException
     *                            If guacamole.properties cannot be parsed
     */
    public String getJwtJwksPath() throws GuacamoleException {
        return environment.getProperty(
                JwtAuthenticationGuacamoleProperties.JWT_JWKS_PATH,
                "/");
    }

    /**
     * Returns the expected audience value in the JWT
     * 
     * @return
     *         The `aud` value to validate against in the JWT
     * 
     * @throws GuacamoleException
     *                            If guacamole.properties cannot be parsed
     */
    public String getJwtAudience() throws GuacamoleException {
        return environment.getRequiredProperty(
                JwtAuthenticationGuacamoleProperties.JWT_AUDIENCE);
    }

    /**
     * Returns the name of the HTTP cookie the storing the JWT
     * 
     * @return
     *         Name of the HTTP cookie containing the JWT
     * 
     * @throws GuacamoleException
     *                            If guacamole.properties cannot be parsed
     */
    public String getJwtCookie() throws GuacamoleException {
        return environment.getProperty(
                JwtAuthenticationGuacamoleProperties.JWT_COOKE);
    }

    /**
     * Returns the name of claim used to identify a user.
     * 
     * @return
     *         The name of user claim.
     * 
     * @throws GuacamoleException
     *                            If guacamole.properties cannot be parsed
     */
    public String getJwtUserClaim() throws GuacamoleException {
        return environment.getProperty(
                JwtAuthenticationGuacamoleProperties.JWT_USER_CLAIM,
                "email");
    }

    /**
     * Returns the name of the roles claim use to build user groups
     * 
     * @return
     *         The name of the roles claim in the JWT
     * 
     * @throws GuacamoleException
     *                            If guacamole.properties cannot be parsed
     */
    public String getJwtRolesClaim() throws GuacamoleException {
        return environment.getProperty(
                JwtAuthenticationGuacamoleProperties.JWT_ROLES_CLAIM,
                "roles");
    }

    /**
     * Some providers nest custom claims in their own object, rather than at the
     * root level.
     * 
     * @return
     *         The name of the custom claims object inside the JWT, if one is set.
     *         Otherwise null.
     * 
     * @throws GuacamoleException
     *                            If guacamole.properties cannot be parsed
     */
    public String getJwtCustomClaims() throws GuacamoleException {
        return environment.getProperty(
                JwtAuthenticationGuacamoleProperties.JWT_CUSTOM_CLAIMS);
    }
}
