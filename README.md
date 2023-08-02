# Guacamole JWT Authentication
Plugin for [Apache Guacamole](https://guacamole.apache.org/) that supports authentication with [JSON Web Tokens (JWTs)](https://jwt.io/).

## When to Use
Normally, when configuring a single-sign-on method, you'd want to use a standard option (such as OpenID Connect or SAML) to authenticate users to Guacamole.

However, some zero trust providers (such as Cloudflare Access) integrate multiple auth providers and only pass a signed JWT through to the application after a user has successfully authenticated.  This plugin allows for verification of those JWTs and extracts user information to build a Guacamoole profile.

**Note:** Like other SSO plugins, this plugin does not store connection or related information, so another storage mechanism (such as PostgreSQL or an LDAP server) is required.

## Configuration

### `guacamole.properties`

The following fields may be set in `$GUACMOLE_HOME/guacamole.properties`:

| Field                           | Required | Default | Description                                               |
| ------------------------------- | -------- | ------- | --------------------------------------------------------- |
| `jwt-header`                    | Yes      |         | The name HTTP header containing the JWT                   |
| `jwt-jwks-url`                  | Yes      |         | Full URL of where the JSON Web Key Sets can be found      |
| `jwt-audience`                  | Yes      |         | Expected value of the `aud` claim in the JWT              |
| `jwt-jwks-path`                 | No       | `/`     | [JSONPointer](https://datatracker.ietf.org/doc/html/rfc6901) string if JWKS is nested in a JSON object |
| `jwt-user-claim`                | No       | `email` | The name of claim used to identify a user                 |
| `jwt-roles-claim`               | No       | `roles` | The name of claim used to map a user to roles/groups      |
| `jwt-custom-claims`             | No       | `null`  | Some providers nest custom claims (such as roles) under a top level claim. Provide the name of the top level claim here |

#### Example  (Cloudflare Access)
```
# ... omitted other configure ...
jwt-header: Cf-Access-Jwt-Assertion
jwt-jwks-url: https://myteam.cloudflareaccess.com/cdn-cgi/access/certs
jwt-audience: ceb84cb2a6d418999fea0ebac41b86ba37f747c3990dace944fc0f78f3dcae25
jwt-jwks-path: /keys
```

### Environment
If you'd prefer to configure using environemnt variables, you must `enable-environment-properties: true` in your `guacamole.properties` file. This is useful when using the docker image. The environment variable names are the same as above, except in upper case and the dashes are replaced with underscores.  For example, `jwt-header` becomes `JWT_HEADER`.

## Running

### Native
1. Download plugin corresponding to your Gaucamole version (i.e. `1.5.2`)
2. Place the plugin in your `$GUACAMOLE_HOME/extensions` directory and it will be automatically loaded when Guacamole starts
3. Configure your `$GUACAMOLE_HOME/guacamole.properties` file (or environment) as specified above
4. Start Guacamole

### Docker
A prebuilt docker image (`ghcr.io/kvnallsn/guacamole-jwt-auth:latest`) is available from this repository.  Simply pull the image and use as you would the regular Guacamole docker image.

#### Example Command 

The example command makes the following assumptions:
- `guacd` is running on the localhost and listening on `4822/tcp`
- `postgresql` is running on the localhost and listening on `5432/tcp`
    - username is set to `guacd`
    - password is set to `password`
    - database is set to `guacd`
    - automatically creates postgresql accounts to store connection information/groups/etc.
- A Cloudflare Zero Trust team name `myteam` exists and the has the specified audience claim

```bash
docker run \
    -d \
    --rm \
    --name guacamole-jwt \
    --add-host host.docker.internal:host-gateway \
    -e ENABLE_ENVIRONMENT_PROPERTIES=true \
    -e POSTGRESQL_DATABASE=guacd \
    -e POSTGRESQL_USER=guacd \
    -e POSTGRESQL_PASSWORD=password \
    -e POSTGRESQL_HOSTNAME=host.docker.internal \
    -e POSTGRESQL_AUTO_CREATE_ACCOUNTS=true \
    -e GUACD_HOSTNAME=host.docker.internal \
    -e GUCAD_PORT=4822 \
    -e JWT_HEADER=Cf-Access-Jwt-Assertion \
    -e JWT_JWKS_URL=https://myteam.cloudflareaccess.com/cdn-cgi/access/certs \
    -e JWT_JWKS_PATH=/keys \
    -e JWT_AUDIENCE=ceb84cb2a6d418999fea0ebac41b86ba37f747c3990dace944fc0f78f3dcae25 \
    -p "8080:8080" \
    ghcr.io/kvnallsn/guacamole-jwt-auth:1.5.2
```
