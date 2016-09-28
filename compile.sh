#!/bin/bash
mvn clean package -Dmaven.test.skip=true

# For Docker tomcat:
docker cp target/yam.war yam_tomcat:/usr/local/tomcat/webapps/ROOT.war
