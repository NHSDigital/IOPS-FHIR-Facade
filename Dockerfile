


FROM openjdk:11.0.9

VOLUME /tmp

ENV JAVA_OPTS="-Xms128m -Xmx1024m"

ADD target/fhir-qedm.jar fhir-qedm.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/fhir-qedm.jar"]


