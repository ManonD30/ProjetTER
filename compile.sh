#!/bin/bash
mvn clean package

# For Docker tomcat:
docker cp target/yam.war yam_tomcat:/usr/local/tomcat/webapps/ROOT.war
