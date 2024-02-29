FROM eclipse-temurin:11-jre
RUN apt-get update && apt-get install -y unzip
COPY run-docker.properties /opt/app/serve.properties
COPY ./target/algo-jar-with-dependencies.jar /opt/app/serve.jar

ENTRYPOINT ["java", "-jar", "/opt/app/algo.jar"]