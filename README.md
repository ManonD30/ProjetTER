# Graphical User Interface for the Ontology matcher YAM++

## Install it

```bash
mvn install:install-file -Dfile=src/main/webapp/WEB-INF/lib/procalign.jar -DgroupId=fr.inrialpes.exmo -DartifactId=align -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true

mvn clean package

cp target/yam-gui-0.1.war /opt/tomcat/webapps/
```

Then go to `http://localhost:8080/yam-gui-0.1/index`

## MySQL

`mysql -u root -p`

```sql
CREATE DATABASE yam;
```
