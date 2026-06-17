# Pin to base image of eclipse-temurin that supports python2, needed for TransVar
FROM eclipse-temurin:21-jre-jammy@sha256:199aebeb3adcde4910695cdebfe782ada38dadb6cc8013159b58d3724451befd
RUN apt-get update && apt-get install -fy unzip python2 python2-dev zlib1g-dev libncurses5-dev build-essential
RUN curl https://bootstrap.pypa.io/pip/2.7/get-pip.py -o get-pip.py && python2 get-pip.py
RUN python2 -m pip install TransVar==2.3.4.20161215
COPY ./target/algo-jar-with-dependencies.jar /opt/app/serve.jar