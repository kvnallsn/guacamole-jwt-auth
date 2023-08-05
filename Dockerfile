ARG GUACAMOLE_VERSION=1.5.3
FROM maven:3-eclipse-temurin-8 AS builder

ARG GUACAMOLE_VERSION
ENV GUACAMOLE_VERSION=${GUACAMOLE_VERSION}

WORKDIR /build
COPY . .
RUN ./scripts/prepare-build.sh && mvn package

FROM guacamole/guacamole:${GUACAMOLE_VERSION}
COPY --from=builder /build/docker/start.sh /opt/guacamole/bin/start.sh
COPY --from=builder /build/target/*.jar /opt/guacamole/jwt/
