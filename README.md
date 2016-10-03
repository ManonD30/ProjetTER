# Graphical User Interface for the Ontology matcher YAM++

Using Apache Tomcat 7 and Java 8

## Best to run it: using docker-compose

* Install docker (and docker-compose if not packaged with)

```bash
git pull https://gite.lirmm.fr/opendata/docker-compose-yam
docker-compose build
docker-compose up -d

# Stop and start it once it's built and up
docker-compose stop
docker-compose start
```

## Copy new war to tomcat docker

```
docker cp yam.war yam_tomcat:/usr/local/tomcat/webapps/ROOT.war
```

## Install Tomcat

* On Ubuntu

```bash
sudo apt-get install tomcat7
sudo apt-get install tomcat7-docs tomcat7-admin tomcat7-examples
```

## Install WordNet

Install WordNet on Ubuntu (used by Yam)

```bash
sudo apt-get upgrade
sudo apt-get install wordnet
```

You also have to make the dictionary used by "Main.jar" MainProgram available on your server.

Using WordNet needs you to provide a path to the wordnet dictionary in configs/WNTemplate.xml in Main.jar
By default it is `/home/emonet/wordnet_dict`

You can find the content of this directory in this Git repository under "wordnet_dict"

## Install it

```bash
mvn install:install-file -Dfile=src/main/webapp/WEB-INF/lib/procalign.jar -DgroupId=fr.inrialpes.exmo -DartifactId=align -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true

mvn clean package

cp target/yam-gui-0.1.war /opt/tomcat/webapps/
```

Then go to `http://localhost:8080/yam-gui-0.1/index`


## Config file

Fill the `src/main/resources/config.properties` file with MySQL credentials and working directory.

## Working directory

The working directory (i.e.: /srv/yam-gui) is the place where the ontologies and alignments are stored on the server. It should have a read, write and execute accessible for everyone and the followings directories in it: ontologies and save

```bash
mkdir /srv/yam-gui/ontologies
mkdir /srv/yam-gui/save
chmod -R 777 /srv/yam-gui
```

## MySQL

`mysql -u root -p`

```sql
CREATE DATABASE yam;
USE	yam;

CREATE TABLE user
(
mail varchar(255),
name varchar(255),
isAffiliateTo varchar(255),
asMatched int,
canMatch int,
password varchar(255),
PRIMARY KEY (mail)
);
```

* Start MySQL daemon

`sudo /etc/init.d/mysql start`


## Use logger

To log to tomcat catalina log
```java
Logger myLog = Logger.getLogger (MyClass.class.getName());
myLog.log(Level.INFO, "hello world");
```

## Connect to tomcat docker container

```
docker exec -i -t yam_tomcat bash
```

## Use d3.js for vizualisation

<script src="https://d3js.org/d3.v4.min.js"></script>

* GeneOntology vizualisation: http://blog.nextgenetics.net/?e=19
Pretty neat: http://blog.nextgenetics.net/demo/entry0019/demo.html

* Simple graph de force:
http://emptypipes.org/2015/02/15/selectable-force-directed-graph/

* Graph sans select avec poids
http://bl.ocks.org/mbostock/4062045

* Graph avec select sans couleur
http://bl.ocks.org/mbostock/4566102

* Graph avec select avec couleur (joli)
http://bl.ocks.org/pkerpedjiev/0389e39fad95e1cf29ce
