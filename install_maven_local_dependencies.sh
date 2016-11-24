mvn install:install-file -Dfile=src/main/webapp/WEB-INF/lib/align.jar -DgroupId=org.semanticweb.owl -DartifactId=org.semanticweb.owl.owlapi -Dversion=3.4.4 -Dpackaging=jar -DgeneratePom=true

#mvn install:install-file -Dfile=src/main/webapp/WEB-INF/lib/Main2.jar -DgroupId=MainYam -DartifactId=main -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=src/main/webapp/WEB-INF/lib/yampp-ls.jar -DgroupId=fr.lirmm.yamplusplus -DartifactId=yampp-ls -Dversion=0.1.1 -Dpackaging=jar -DgeneratePom=true

mvn install:install-file -Dfile=src/main/webapp/WEB-INF/lib/procalign.jar -DgroupId=fr.inrialpes.exmo -DartifactId=align -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true

mvn install:install-file -Dfile=src/main/webapp/WEB-INF/lib/skos-api-onejar.jar -DgroupId=org.semanticweb.skos -DartifactId=skos-api -Dversion=3.1 -Dpackaging=jar -DgeneratePom=true
