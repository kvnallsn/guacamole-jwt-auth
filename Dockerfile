FROM guacamole/guacamole:1.5.3
COPY docker/start.sh /opt/guacamole/bin/start.sh
COPY target/*.jar /opt/guacamole/jwt/
