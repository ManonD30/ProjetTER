# Graphical User Interface for the Ontology matcher YAM++

Using Apache Tomcat 7 and Java 8

## Bug to resolve

Quand on essaie avec des BK_ontologies le fichier direct ddl dans /tmp/yam-gui est mauvais:
il contient Could not load provided ontology file

Execution de mouse - human : en pratique 1min24s d'attente, mais le matcher prend seulement 15s

iaml diabolo depuis les URL : 17s d'attente mais annonce 14s
imal diabolo depuis fichiers: 24s contre 10 annonce

doid ma en localhost : plus de 10 min réelles, 39s annoncé. 

debut doid ma localhost : 21h51

L'upload prend beaucoup de temps

ESSAYER
http://commons.apache.org/proper/commons-fileupload/streaming.html

## Improvements to do

Augmenter la vitesse de chargement des ontologies : ne charger que les concepts qui ont matché et leurs proches ? Risque d'être tout aussi long

Ajouter des détails : comment on gére la visualisation dans le readme (vis js)

X Add skos:relatedMatch

X Retirer <valid>false</valid> du RDF et "export ou save"
X Vérifier si le Validator reprend bien où il s'était arrêté

X "To reduce the number of mapping faut décocher"

X On demande nom et groupe relié à chaque onto (select)

X ajouter point d'interrogation pour hover

add la promotion à l'état d'admin dans page d'administration

X stocker ontologies en utilisant le field des users

X Stocker alignements (on demande domaine, noms des ontologies). Onto stockées par paires avec leur alignement

Stocker le dernier alignment enregistrer

Faire difference entre save et export

Voir comment on peut augmenter le nombre de mappings en résultats (on fait la restriction au curseur en tout)

Normalement "mop" et "genre" : doit pas y avoir de mappings entre ces 2 là. Apparemment y'a pas beaucoupde mapping avec Genre (de doremus), voir pourquoi

Trouver et/ou Demander  à Hoa comment est calculé le score qu'on voit dans l'alignement final

### Now

* **FORMATS**
  * OAEI XML (format de "sauvegarde des données" qui permet de sauvegarder l'état des mappings et de reprendre)
  * Simple RDF : simple format RDF (à implémenter dans yampp-ls)
  * Format RDF réifier avec les scores
  * On parle d'export pour le RDF ? Au lieu d'avoir un Radio button + ddl button, on fait 3 ddl button. Et on ne sort que les mappings "valid" quand on exporte vers RDF (alors qu'on garde tout pour OAEI qui est un format pour enregistrer)

* Edit les textes de description dans la page index

* Login
  * Le top serait d'être capable d'envoyer des **mails** automatiquement
    * Pour notifier de la création de nouveaux utilisateurs
    * Pour réinitialiser les mots de passes perdus (en donnant un mot de passe au hasard)

* **X** Faire en sorte que le %age de recouvrement marche pour le validator (c'est fait dans yampp-ls pour l'instant)

* Stocker les ontologies données en input, par domaine. Par exemple, les ontologies en biomedical devrait être dans le même groupe. L'objectif est de pouvoir les retrouver facilement pour les recommander à des utilisateurs ou pour les utiliser comme connaissance a priori pour l'alignement.
  * Pour faciliter la classification des ontologies on pourrait peut être demander le "domaine" au moment du download.
  * Il faudrait également éviter les doublons (comparer les ontologies, demander un "nom" pour l'ontologie et l'enregistrer avec ce nom comme ID ?)
  * Donc, demander: nom (ID) et domaine?

  * Le nom des ontologies n'est pas encore au top.. (mettre une URL file:/ nom du file original si rien dans l'onto)

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
  * X Afficher tous les concepts à une distance 1 qui sont également dans l'ontologie

* Safari
  * La table de results se resize pas super bien quand on la rapetissit trop

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

## User administration

User can be admin. An admin can see the list of user in its account page. An admin can reset any user password to "changeme"

The admin role is automatically given to the user created with the username "admin"

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

* Run mysql in MySQL container to check tables content

```shell
docker exec -i -t yam_mysqlDb mysql
```

```sql
/* Use database */
show databases;
use yam;

/* Get user table content */
show tables;
select * from user;
```

## Run the yampp-online application

### Generate war

```shell
# Install maven local dependencies
./maven_install_local_dependencies.sh

# mvn package and copy to yam_tomcat docker container
./compile.sh


# Install maven local dependencies, then generate war and upload to docker yam_tomcat
./compile.sh -i
```

### Copy new war to tomcat docker

```
docker cp yam.war yam_tomcat:/usr/local/tomcat/webapps/ROOT.war
```

## Install Tomcat

* On Ubuntu

```shell
sudo apt-get install tomcat7
sudo apt-get install tomcat7-docs tomcat7-admin tomcat7-examples
```

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

* Connect to the MySQL container

```shell
docker exec -i -t yam_mysqlDb mysql
```

* Script used to create the table

```sql
CREATE DATABASE IF NOT EXISTS yam;
USE yam;

CREATE TABLE IF NOT EXISTS user
(
apikey varchar(16),
mail varchar(255) NOT NULL,
username varchar(255) NOT NULL,
isAffiliateTo varchar(255),
matchCount int,
canMatch int,
password varchar(255),
PRIMARY KEY (apikey),
UNIQUE (mail),
UNIQUE (username)
);
```


## Use logger

To log to tomcat catalina log
```java
import java.util.logging.Level;
import java.util.logging.Logger;

Logger myLog = Logger.getLogger (MyClass.class.getName());
myLog.log(Level.DEBUG, "hello world");
```

## Connect to tomcat docker container

```
docker exec -i -t yam_tomcat bash
```

## Use d3.js for vizualisation

http://visjs.org/network_examples.html : javascript vraiment intéressant
http://visjs.org/examples/network/events/interactionEvents.html
http://visjs.org/examples/network/labels/labelAlignment.html

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
