spring boot + shiro + orient + ionic
====

Libraries:
* Spring Boot
* OrientDB (remote object persistence by means of Spring Data)
* Apache Shiro (session-management with OrientDB-based authorizing realm)
* Hazelcast (powered session distributed persistence)
* Ionic Material (user interface)


## Build
```
mvn clean package
java -jar spring-boot-ionic-material-web/target/spring-boot-ionic-material-web-<version>.jar 
```

## Development
```
mvn compile
mvn spring-boot:run -pl spring-boot-ionic-material-web
```

## Test
```
mvn clean test
```


## Archetype

Generate an archetype from project
```
mvn archetype:create-from-project
```

Install archetype to local repository
```
cd target/generated-sources/archetype
mvn clean install
```

Generate a new project
```
mvn archetype:generate -DarchetypeCatalog=local
```
or directly
```
mvn archetype:generate \
 -DarchetypeGroupId=com.github.theborakompanioni \
 -DarchetypeArtifactId=spring-boot-shiro-orient-ionic-parent-archetype \
 -DarchetypeVersion=0.0.1-SNAPSHOT \
 -DgroupId=mycompany.app.web \
 -Dpackage=mycompany.app \
 -DartifactId=app-new \
 -Dversion=1.0.0-SNAPSHOT
```
