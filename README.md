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
USE	yam

CREATE TABLE user
(
mail varchar(255),
name varchar(255),
isAffiliateTo varchar(255),
canMatch int,
password varchar(255)
);
```

* Start MySQL daemon

`sudo /etc/init.d/mysql start`
