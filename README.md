  # Graphical User Interface for the Ontology matcher YAM++

Using Apache Tomcat 7 and Java 8

## Improvements to do

### Now

* **ATTENTION: IL FAUT RETIRER LES scope provided DU POM.XML**

* **FORMATS**
  * OAEI XML (format de "sauvegarde des données" qui permet de sauvegarder l'état des mappings et de reprendre)
  * Simple RDF : simple format RDF (à implémenter dans yampp-ls)
  * Format RDF réifier avec les scores
  * On parle d'export pour le RDF ? Au lieu d'avoir un Radio button + ddl button, on fait 3 ddl button. Et on ne sort que les mappings "valid" quand on exporte vers RDF (alors qu'on garde tout pour OAEI qui est un format pour enregistrer)

* **RAPIDE:** Ajouter les paramètres pour OAEI

* **BUG**
  * Le langage sélectionné n'est pas forcément celui affiché (voir iaml-MIMO sur certains concepts, mais pas tous)
  * Le nom des ontologies n'est pas encore au top.. (mettre une URL file:/ nom du file original si rien dans l'onto)

* **UI improvements**
  * Voir dernier Mail Marie : utiliser même taille et police qu'elle
  * Voir mail Zohra et Konstantin pour mettre à jour l'UI (surtout about us)

* A faire aussi dans yampp-ls
  * Label of table columns should be name of ontologies (not source and target). Use filename if ontology name not found. BOF, on display le nom du fichier /tmp/yamppls/... parfois...
  * Return l'error pour la display (return "error: " + e)
  * Contacter OWLAPI pour résoudre le problème de TO (les imports vers des pURL qui redirect ne sont pas gérés) ?

* Pour éviter de loader plusieurs fois dans Jena : générer le JSON direct dans yampp-ls à partir de l'ontology déjà loadé dans Jena (Pour la conversion en SKOS). Dans YamppUtils:
  * Faire une fonction qui load l'onto dans Jena et retourne le model
  * Fonction qui converti SKOS en OWL en prenant le model en param
  * Fonction qui retourne le JSON Object préparé pour le javascript de yampp-online
  * **FAIT ???**

* Graphical visualization 
  * Afficher tous les concepts à une distance 1 qui sont également dans l'ontologie

### Later

* Faire en sorte que header & footer fonctionne partout pareil : dans results et ailleurs (pour le moment "fixed" seulement dans results)
  * OU MIEUX : fixed nul part. Et la colonne de droit prend TOUTE la largeur de la page, on met la nav bar et le footer autour de la main section à gauche !

* Passer le projet en 2 submodule ? ls et online

* SKOS to OWL conversion: gérer SKOS-XL (labels réifiés)

* Solution pour faire en sorte que le docker soit toujours up (demander à Joël) : 

  * docker run -d --restart always my-docker-image
  * marathon

### Add javadoc to tomcat

* Create a folder in webapps folder e.g. javadoc
* Put your html and css in that folder and name the html file, which you want to be the starting page for your application, index.html
* Start tomcat and point your browser to url "http://localhost:8080/javadoc". Your index.html page will pop up in the browser

## Best to run it: using docker-compose

* Install docker (and docker-compose if not packaged with)

```shell
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

```shell
sudo apt-get install tomcat7
sudo apt-get install tomcat7-docs tomcat7-admin tomcat7-examples
```

## Install WordNet

Install WordNet on Ubuntu (used by Yam)

```shell
sudo apt-get upgrade
sudo apt-get install wordnet
```

You also have to make the dictionary used by "Main.jar" MainProgram available on your server.

Using WordNet needs you to provide a path to the wordnet dictionary in configs/WNTemplate.xml in Main.jar
By default it is `/home/emonet/wordnet_dict`

You can find the content of this directory in this Git repository under "wordnet_dict"

## Install it

```shell
mvn install:install-file -Dfile=src/main/webapp/WEB-INF/lib/procalign.jar -DgroupId=fr.inrialpes.exmo -DartifactId=align -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true

mvn clean package

cp target/yam-gui-0.1.war /opt/tomcat/webapps/
```

Then go to `http://localhost:8080/yam-gui-0.1/index`


## Config file

Fill the `src/main/resources/config.properties` file with MySQL credentials and working directory.

## Working directory

The working directory (i.e.: /srv/yam-gui) is the place where the ontologies and alignments are stored on the server. It should have a read, write and execute accessible for everyone and the followings directories in it: ontologies and save

```shell
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

* **Autre méthodes:**
  Y'a des libs plutôt puissantes pour SKOS: Skosmos (très utilisé par beaucoup de monde) ou SkosPlay (dev open source par un français proche, utilisé pour afficher MIMO par exemple))
