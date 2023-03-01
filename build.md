
mvn clean install

docker build -t fhir-qedm .

docker tag fhir-qedm:latest 365027538941.dkr.ecr.eu-west-2.amazonaws.com/fhir-qedm:latest
docker tag fhir-qedm:latest 365027538941.dkr.ecr.eu-west-2.amazonaws.com/fhir-qedm:1.1.5

docker push 365027538941.dkr.ecr.eu-west-2.amazonaws.com/fhir-qedm:latest

docker push 365027538941.dkr.ecr.eu-west-2.amazonaws.com/fhir-qedm:1.1.5
