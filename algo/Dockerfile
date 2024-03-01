FROM eclipse-temurin:11-jre
RUN apt-get update && apt-get install -fy unzip python-pip python2-dev zlib1g-dev libncurses5-dev
RUN python2 -m pip install TransVar==2.3.4.20161215
COPY run-docker.properties /opt/app/serve.properties
COPY ./target/algo-jar-with-dependencies.jar /opt/app/serve.jar

ENTRYPOINT ["java", "-jar", "/opt/app/algo.jar"]