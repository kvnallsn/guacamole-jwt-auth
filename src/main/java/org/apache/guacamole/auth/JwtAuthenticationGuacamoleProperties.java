package org.apache.guacamole.auth;

import org.apache.guacamole.properties.StringGuacamoleProperty;

/**
 * Provides properties required for the user of the Cloudflare
 * Access authentication provider. These properties will be read
 * from guacamole.properties when this authentication provider is used.
 */
public class JwtAuthenticationGuacamoleProperties {
     /**
      * This class should not be instantiated
      */
     private JwtAuthenticationGuacamoleProperties() {
     }

     /**
      * Name of the HTTP header storing the JWT
      */
     public static final StringGuacamoleProperty JWT_HEADER = new StringGuacamoleProperty() {
          @Override
          public String getName() {
               return "jwt-header";
          }
     };

     /**
      * URL to where the JSON Web Key Sets are stored to validate a JWT
      */
     public static final StringGuacamoleProperty JWT_JWKS_URL = new StringGuacamoleProperty() {
          @Override
          public String getName() {
               return "jwt-jwks-url";
          }
     };

     /**
      * Path to JSON Web Keys in the data return from JWT_JWKS_URL.
      * 
      * *Must* be in JSONPointer (RFC6901) format. Path segments are separated by the
      * '/' character. Additionally, the '/' character signifies the root of the
      * document, all queries should start with it.
      *
      * Example:
      * JSON: `{"a": {"b": "c"}}`
      * Path: `/a/b`
      * Result: "c"
      */
     public static final StringGuacamoleProperty JWT_JWKS_PATH = new StringGuacamoleProperty() {
          @Override
          public String getName() {
               return "jwt-jwks-path";
          }
     };

     /**
      * The expected value of the `aud` claim (used to authenticate the JWT)
      */
     public static final StringGuacamoleProperty JWT_AUDIENCE = new StringGuacamoleProperty() {
          @Override
          public String getName() {
               return "jwt-audience";
          }
     };

     /**
      * Name of the HTTP cookie storing the JWT
      */
     public static final StringGuacamoleProperty JWT_COOKE = new StringGuacamoleProperty() {
          @Override
          public String getName() {
               return "jwt-cookie";
          }
     };

     /**
      * The name of claim that will be used to identify a user in the system
      */
     public static final StringGuacamoleProperty JWT_USER_CLAIM = new StringGuacamoleProperty() {
          @Override
          public String getName() {
               return "jwt-user-claim";
          }
     };

     /**
      * The name of the roles claim in the JWT to build user groups
      */
     public static final StringGuacamoleProperty JWT_ROLES_CLAIM = new StringGuacamoleProperty() {
          @Override
          public String getName() {
               return "jwt-roles-claim";
          }
     };

     /**
      * The name of the custom claims object, if custom claims are nested
      */
     public static final StringGuacamoleProperty JWT_CUSTOM_CLAIMS = new StringGuacamoleProperty() {
          @Override
          public String getName() {
               return "jwt-custom-claims";
          }
     };
}
