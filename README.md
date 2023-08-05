# Guacamole JWT Authentication
Plugin for [Apache Guacamole](https://guacamole.apache.org/) that supports authentication with [JSON Web Tokens (JWTs)](https://jwt.io/).

## When to Use
Normally, when configuring a single-sign-on method, you'd want to use a standard option (such as OpenID Connect or SAML) to authenticate users to Guacamole.

However, some zero trust providers (such as Cloudflare Access) integrate multiple auth providers and only pass a signed JWT through to the application after a user has successfully authenticated.  This plugin allows for verification of those JWTs and extracts user information to build a Guacamoole profile.

**Note:** Like other SSO plugins, this plugin does not store connection or related information, so another storage mechanism (such as PostgreSQL or an LDAP server) is required.

## Supported Providers
- [Cloudflare Access](https://developers.cloudflare.com/cloudflare-one/)

## Configuration

The following fields may be set in `$GUACMOLE_HOME/guacamole.properties`:

| Field                           | Required | Default | Description                                               |
| ------------------------------- | -------- | ------- | --------------------------------------------------------- |
| `jwt-header`                    | Yes*     |         | The name of the HTTP header containing the JWT            |
| `jwt-cookie`                    | Yes*     |         | The name of the HTTP cookie containing the JWT            |
| `jwt-jwks-url`                  | Yes      |         | Full URL of where the JSON Web Key Sets can be found      |
| `jwt-audience`                  | Yes      |         | Expected value of the `aud` claim in the JWT              |
| `jwt-jwks-path`                 | No       | `/`     | [JSONPointer](https://datatracker.ietf.org/doc/html/rfc6901) string if JWKS is nested in a JSON object |
| `jwt-user-claim`                | No       | `email` | The name of claim used to identify a user                 |
| `jwt-roles-claim`               | No       | `roles` | The name of claim used to map a user to roles/groups      |
| `jwt-custom-claims`             | No       | `null`  | Some providers nest custom claims (such as roles) under a top level claim. Provide the name of the top level claim here |

**Note**: Either `jwt-header` or `jwt-cookie` must be set.  If both are set, `jwt-header` is checked first, followed by `jwt-cookie`.

#### Example  (Cloudflare Access)
```
# ... omitted other configure ...
jwt-header: Cf-Access-Jwt-Assertion
jwt-jwks-url: https://myteam.cloudflareaccess.com/cdn-cgi/access/certs
jwt-audience: ceb84cb2a6d418999fea0ebac41b86ba37f747c3990dace944fc0f78f3dcae25
jwt-jwks-path: /keys
```

### Environment
If you'd prefer to configure using environemnt variables, you must set `enable-environment-properties: true` in your `guacamole.properties` file.

The environment variable names are the same as above, except in upper case and the dashes are replaced with underscores.  For example, `jwt-header` becomes `JWT_HEADER`.

## Running

### Native
1. Download plugin corresponding to your Gaucamole version (i.e. `1.5.3`)
2. Place the plugin in your `$GUACAMOLE_HOME/extensions` directory and it will be automatically loaded when Guacamole starts
3. Configure your `$GUACAMOLE_HOME/guacamole.properties` file (or environment) as specified above
4. Start Guacamole

### Docker
A prebuilt docker image (`ghcr.io/kvnallsn/guacamole-jwt-auth`) is available from this repository.  Simply pull the image and use as you would the regular Guacamole docker image.  The tags correspond to the plugin version matched to a Guacamole version.  Currently, builds are generated for Guacamole versions: `1.5.0`, `1.5.1`, `1.5.2`, `1.5.3`.

#### Example Docker Compose

The below `docker-compose.yml` makes the following assumptions:
- A Cloudflare Access team name `myteam` exists and the has the specified audience claim

```yaml
services:
    guacd:
        image: guacamole/guacd:1.5.3
        restart: always

    postgres:
        image: postgres:15.3-bookworm
        restart: always
        environment:
            POSTGRES_DB: guacd
            POSTGRES_USER: guacd
            POSTGRES_PASSWORD: password

    guacamole:
        image: ghcr.io/kvnallsn/guacamole-jwt-auth:v1.0.2-guac-1.5.3
        restart: always
        environment:
            POSTGRESQL_DATABASE: guacd
            POSTGRESQL_USER: guacd
            POSTGRESQL_PASSWORD: password
            POSTGRESQL_HOSTNAME: postgres
            POSTGRESQL_AUTO_CREATE_ACCOUNTS: true
            GUACD_HOSTNAME: guacd
            GUACD_PORT: 4822
            JWT_HEADER: Cf-Access-Jwt-Assertion
            JWT_JWKS_URL: https://myteam.cloudflareaccess.com/cdn-cgi/access/certs
            JWT_JWKS_PATH: /keys
            JWT_AUDIENCE: ceb84cb2a6d418999fea0ebac41b86ba37f747c3990dace944fc0f78f3dcae25
        ports:
            - "8080:8080"
```

## Development

### Requirements

- Java JDK 1.8+
- [Maven](https://maven.apache.org/)

For convience, an [asdf](https://asdf-vm.com/) `.tool-versions` file is provided.

### Building

Building the plugin is simple. To generate a JAR file, run:
```bash
mvn package
```

### Changing Guacamole Version

To update (or downgrade) the version of Guacamole the extension is targeting, a script is provided at `scripts/prepare-build.sh`.

For example, to change the Guacamole version to `1.5.3`:
```bash
GUACAMOLE_VERSION=1.5.3 ./scripts/prepare-build.sh
```