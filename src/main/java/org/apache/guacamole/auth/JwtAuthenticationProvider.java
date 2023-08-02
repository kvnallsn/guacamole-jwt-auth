package org.apache.guacamole.auth;

import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.auth.AbstractAuthenticationProvider;
import org.apache.guacamole.net.auth.AuthenticatedUser;
import org.apache.guacamole.net.auth.Credentials;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Authentication provider implementation
 */
public class JwtAuthenticationProvider extends AbstractAuthenticationProvider {

    /**
     * Injector which will manage the object graph of this authentication provider
     */
    private final Injector injector;

    public JwtAuthenticationProvider() throws GuacamoleException {
        // Set up Guice injector
        injector = Guice.createInjector(new JwtAuthenticationProviderModule(this));
    }

    @Override
    public String getIdentifier() {
        return "jwt";
    }

    @Override
    public AuthenticatedUser authenticateUser(Credentials credentials) throws GuacamoleException {
        // Pass credentials to authentication service
        AuthenticationProviderService authProviderService = injector.getInstance(AuthenticationProviderService.class);
        return authProviderService.authenticateUser(credentials);
    }
}