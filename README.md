# Graphical User Interface for the Ontology matcher YAM++

Using Apache Tomcat 7 and Java 8

## Démo


* Virer les grosse OAEI car marchent pas... (résoudre le bug d'abord)

* Montrer un matching Doremus et un matching Anatomy

* Save l'alignement (export aussi pour montrer)

* Montrer le validateur avec l'alignement qu'on vient de save

* Utiliser l'API (via command line)

* Remplacer examples par "Available datasets"

* Réflexion de fin : on pourrait imaginer de la collaboration des experts


## A résoudre

* Emballement. Parfois ça s'emballe (surtout quand on s'amuse avec des grosses ontologies)
  * Est-ce que ça viendrait de conflits quand on essaie de faire 2 matchings en même temps (lancer 2 matchings et regarder s'il y a overlap dans les logs)


SOLUTION : il faut lancer le JAR dans une JVM à part (au moins ça sera bien compartimenté)

http://stackoverflow.com/questions/7268824/call-new-jvm-from-existing-jvm

http://stackoverflow.com/questions/12533799/execute-shell-command-in-tomcat

Andon: http://stackoverflow.com/questions/3468987/executing-another-application-from-java

Utiliser ProcessBuilder
Je peux recup l'output stream du process et le lire...
Le mieux serait de définir le dir dans lequel on travaillera direct dans yampp-online et de le passer comme argument


## Résolu

~ **Sur écran 4:3 le validation.jsp ne resize pas correctement la table**: table-layout: fixed; permet de le résoudre mais, la taille des cells ne s'adaptent pas au contenu

Enlever les 3 box (mettre le recouvrement proche de la table). Et running time dans le texte

X **Permettre de choisir plus d'exemples ! (différents couples)**
2 Doremus, Anatomy, 2 Large Bio (FMA, NCI, SNOMED; utiliser une URL BioPortal ? Elles sont sur NCBO normalement). FMA NCI à partir des URLs de BioPortal NCBO : bloque entre "start processRequest" et "endUploadFile"

X **On vire le choix de plusieurs matchers : seulement Very Large Scale**

X **On vire la propriété "undefined"**

X **Bug nullPointer quand on ddl l'alignement parfois** (avec largeBio par ex)

* Save la dernière version de l'alignment téléchargée



Stocker à côté des ontologies les alignements produits et validés
Ce que veut Zohra : pouvoir facilement chercher des ontologies dans les onto enregistrées (par son nom par exemple)

Faire un affichage des concepts de l'ontologie source/target pour permettre à l'user de trouver des nouveaux mappings pas trouvés (voir dessin)

Trouver comment faire pour modifier le seuil des mappings qui sortent en résultat (mettre le seuil des mappings qui sont pris au plus bas)

* Permettre de rechercher les packages "onto1 + onto2 + alignement" de manière auto (pas manuelle). Avec text search par exemple ou autre (pas une priorité, vaut mieux faire le reste)

**IMPORTANT**

Séparer page user et admin ?

Nommer admin direct dans la page admin

* Yampp online publi: 
  * Screenshot des trucs importants
  * Petit paragraphe en anglais sur l'API REST (à quoi ça sert)

* Dans la liste des concepts pour une onto marquer les concepts qui ont déjà matchés


Parser le suivant:
http://liris.cnrs.fr/~fduchate/research/tools/xbenchmatch/#datasets

Donc on utilise un convertisseur qui convertit XSD to OWL (COMA++, http://xml2owl.sourceforge.net/index.php)


## Bug SLOW UPLOAD

#### Test using DOID - MA

* Files in yampp-ls
* URL:

https://gite.lirmm.fr/opendata/yampp-ls/raw/develop/src/test/resources/BK_ontologies/uberon/doid.owl

https://gite.lirmm.fr/opendata/yampp-ls/raw/develop/src/test/resources/BK_ontologies/uberon/ma.owl

* Résultats :
  * Sur localhost : 
    * URL: Start of doPost of MatcherInterface proc direct. 25s de traitement (pour arriver jusqu'à "end processRequest"). Mais 1min30s en tout (alors que peu de mappings). Je pense que c'est le load des ontologies pour add mappings qui prend du temps. Et les %age plantent. Virer les load des ontologies pour voir
    * File : 1min42
  * Sur yamplusplus.lirmm.fr : 
    * URL: 3min13 (60s de traitement réel). Beaucoup de temps passe après "End of processRequest". Beaucoup plus de résultats qu'en local....




#### Tests NCI - FMA (Large Bio)

Ca a planté. Faire un run de test sur infodemo pour voir combiend e temps ça prend

Tests avec NCI/FMA:

* Run test direct (mvn -Dtest=TestMatchOntologies#testMatchOAEI test -Dmaven.test.skip=false)
  * Sur mon PC : 200s
  * Sur infodemo : 5min
* Run Validator (pour voir si c'est juste le load de l'onto le problème)
  * Jena loading: 1 minute or less
  * Crash caused by passing ontologies from Java to Javascript : https://gite.lirmm.fr/opendata/yampp-online/blob/master/src/main/webapp/WEB-INF/validation.jsp#L58

Stack question: http://stackoverflow.com/questions/42507697/passing-java-to-javascript-takes-a-long-time

**Solution:**

Paging the data via AJAX seems like an ideal approach then. It's a bit broad, but overall the idea is that you wouldn't send any data to the page when it initially loads, but the page itself would contain JavaScript code to fetch data from the server. So the page would load quickly, and would then start fetching the data after it loads



**SOLUTION:**

Afficher à chaque fois le nombre de concepts et de classes pour chaque onto !

Checker le nombre de concepts par ontologies. Quand ça passe ou pas ? A partir de combien ça bug

IF ontologies > qu'une certaine taille : on ne prends que les concepts qui ont été alignés qu'on met dans les objets ontologies



*Nombre de concepts*

SNOMED: 120 454

FMA: 78 989

NCI: 66 724

MOUSE (mouse anatomy, ma.owl): 2744

NCIT: 3 404



Doremus : 




Quand on essaie avec des BK_ontologies le fichier direct ddl dans /tmp/yam-gui parfois est mauvais:
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

add la promotion à l'état d'admin dans page d'administration

Stocker le dernier alignment enregistré

Voir comment on peut augmenter le nombre de mappings en résultats (on fait la restriction au curseur en tout)a

Ajouter le spin quand on lance le validator

Il semble que ça plante (au niveau du CandidateCombination) parfois et reste planté jusqu'à re exec du war (dû à trop de soumission ?)

Normalement "mop" et "genre" : doit pas y avoir de mappings entre ces 2 là. Apparemment y'a pas beaucoupde mapping avec Genre (de doremus), voir pourquoi

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




## Restart Tomcat if overload

SSH connect to info-demo.lirmm.fr

```shell
ssh emonet@info-demo.lirmm.fr
cd /home/emonet/docker-compose-yam
docker-compose restart
```




## How to change yampp-online code

In case of problem contact Vincent Emonet or Joël Maizi

```shell
# Retrieve the yampp-online project (careful you need to be connected to gite.lirmm.fr to make modifications)
git clone https://gite.lirmm.fr/opendata/yampp-online.git
# If project already on your machine, you just need to get the latest version
git pull

# Now modify the text you want to modify. Exemple for the about page:
# https://gite.lirmm.fr/opendata/yampp-online/blob/master/src/main/webapp/WEB-INF/aboutus.jsp

# git add, commit and push the modifications to gite.lirmm.fr
git add .
git commit -m "my modifications"
git push

# Connect to info-demo.lirmm.fr server
ssh -A info-demo.lirmm.fr

# Pull the latest version of yampp-online (either pull or clone, if you already cloned the project)
git clone https://gite.lirmm.fr/opendata/yampp-online.git
git pull

# Compile the project (it will compile the newly modificated project and put it in docker). Careful you need the right to copy files into docker
./compile.sh
```

### Change in yampp-ls

If you change something in yampp-ls, you need to 
* put the new yampp-ls.jar in `src/main/webapp/WEB-INF/lib`
* In docker-compose, needed to run the lib from the commandline to create a new JVM and avoid conflict when multiple matching simultaneously:
  * put the new yampp-ls.jar in `docker-compose-yam/service-app`
  * `docker-compose build` to build the new image
  * `docker-compose up -d` to restart the container



## User administration

User can be admin. An admin can see the list of user in its account page. An admin can reset any user password to "changeme"

The admin role is automatically given to the user created with the username "admin"

## Best to run it: using docker-compose

* Install docker (and docker-compose if not packaged with)

```shell
git clone https://gite.lirmm.fr/opendata/docker-compose-yam
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
