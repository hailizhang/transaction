FROM openjdk:21
VOLUME /tmp
ADD transaction-0.0.1-SNAPSHOT.jar /transaction.jar
ENTRYPOINT ["java","-Djava.security.egg=file:/dev/./urandom","-jar","/transaction.jar"]