#!/bin/bash

if [[ -z "${GUACAMOLE_VERSION}" ]]; then
    echo "GUACAMOLE_VERSION not set, exiting"
    exit 1
fi

echo fixing guacamole version in guac-manifest.json
sed -ri 's|("guacamoleVersion": ")[0-9]+\.[0-9]+\.[0-9]+|\1'"${GUACAMOLE_VERSION}"'|g' src/main/resources/guac-manifest.json

echo fixing guacamole version in docker file
sed -ri 's|(FROM guacamole/guacamole:)[0-9]+\.[0-9]+\.[0-9]+|\1'"${GUACAMOLE_VERSION}"'|g' Dockerfile

echo fixing guacamole-ext version in pom.xml
sed -ri 's|(<version>)[0-9]+\.[0-9]+\.[0-9]+(</version> <!-- GUACAMOLE_VERSION -->)|\1'"${GUACAMOLE_VERSION}"'\2|g' pom.xml