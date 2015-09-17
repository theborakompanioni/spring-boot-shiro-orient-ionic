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
´´´

## Test
```
mvn clean test
```