#!/bin/bash

if [ "$1" == "-i" ] || [ "$1" == "--install" ]
then
  ./maven_install_local_dependencies.sh
fi

mvn clean package -Dmaven.test.skip=true

# Upload to Docker yam_tomcat:
docker cp target/yam.war yam_tomcat:/usr/local/tomcat/webapps/ROOT.war
