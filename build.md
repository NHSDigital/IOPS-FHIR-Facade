
mvn clean install

docker build -t fhir-qedm .

docker tag fhir-qedm:latest 365027538941.dkr.ecr.eu-west-2.amazonaws.com/fhir-qedm:latest
docker tag fhir-qedm:latest 365027538941.dkr.ecr.eu-west-2.amazonaws.com/fhir-qedm:1.0.8

docker push 365027538941.dkr.ecr.eu-west-2.amazonaws.com/fhir-qedm:latest

docker push 365027538941.dkr.ecr.eu-west-2.amazonaws.com/fhir-qedm:1.0.8
