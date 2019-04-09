@echo off
mvn install:install-file -Dfile=docker-maven-plugin-1.0-SNAPSHOT.jar -DgroupId=com.github.lazyBoyl -DartifactId=docker-maven-plugin -Dversion=1.0-SNAPSHOT -Dpackaging=jar
exit