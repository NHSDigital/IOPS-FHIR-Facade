FROM openjdk:23

VOLUME /tmp

ENV JAVA_OPTS="-Xms128m -Xmx4096m"

ADD target/fhir-qedm.jar fhir-qedm.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/fhir-qedm.jar"]


