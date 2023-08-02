package org.apache.guacamole.auth;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.auth.AbstractAuthenticatedUser;
import org.apache.guacamole.net.auth.AuthenticationProvider;
import org.apache.guacamole.net.auth.Credentials;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.inject.Inject;

public class JwtUser extends AbstractAuthenticatedUser {

    /**
     * The credentials passed when this user was authenticated
     */
    private Credentials credentials;

    /**
     * The list of roles/groups this user is a member of.
     */
    private HashSet<String> roles;

    /**
     * Service for retrieving configuration values
     */
    @Inject
    private ConfigurationService confService;

    /**
     * The authentication provider used to create this user
     */
    @Inject
    private AuthenticationProvider authProvider;

    @SuppressWarnings("unchecked")
    public void init(DecodedJWT token, Credentials credentials) throws GuacamoleException {
        this.credentials = credentials;
        this.roles = new HashSet<>();

        String customClaimsName = confService.getJwtCustomClaims();
        if (customClaimsName != null) {
            Claim customClaims = token.getClaim(customClaimsName);
            if (!customClaims.isMissing()) {
                Object maybeArray = customClaims.asMap().get(confService.getJwtRolesClaim());
                if (maybeArray.getClass().isArray() || maybeArray instanceof Collection) {
                    this.roles.addAll((Collection<String>) maybeArray);
                }
            }
        } else {
            Claim rolesClaim = token.getClaim(confService.getJwtRolesClaim());
            for (String role : rolesClaim.asArray(String.class)) {
                this.roles.add(role);
            }
        }

        System.out.printf("user has %d roles\n", this.roles.size());

        setIdentifier(token.getClaim(confService.getJwtUserClaim()).asString());
    }

    @Override
    public AuthenticationProvider getAuthenticationProvider() {
        return authProvider;
    }

    @Override
    public Credentials getCredentials() {
        return credentials;
    }

    @Override
    public Set<String> getEffectiveUserGroups() {
        return roles;
    }
}
