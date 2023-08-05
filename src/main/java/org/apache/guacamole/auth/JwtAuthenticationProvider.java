package org.apache.guacamole.auth;

import java.io.IOException;

import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.auth.AbstractAuthenticationProvider;
import org.apache.guacamole.net.auth.AuthenticatedUser;
import org.apache.guacamole.net.auth.Credentials;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;

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

    /**
     * HTTP Client used to fetch JWKS credentials
     */
    private final CloseableHttpClient client;

    public JwtAuthenticationProvider() throws GuacamoleException {
        // Set up Guice injector
        injector = Guice.createInjector(new JwtAuthenticationProviderModule(this));
        client = HttpClients.createDefault();
    }

    @Override
    public String getIdentifier() {
        return "jwt";
    }

    @Override
    public AuthenticatedUser authenticateUser(Credentials credentials) throws GuacamoleException {
        // Pass credentials to authentication service
        AuthenticationProviderService authProviderService = injector.getInstance(AuthenticationProviderService.class);
        return authProviderService.authenticateUser(credentials, client);
    }

    @Override
    public void shutdown() {
        try {
            client.close();
        } catch (IOException exception) {
            // Do nothing (for now)
        }
    }
}