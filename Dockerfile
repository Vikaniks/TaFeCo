FROM eclipse-temurin:21-jdk
RUN apt-get update && apt-get install -y locales && \
    echo "en_US.UTF-8 UTF-8" > /etc/locale.gen && \
    locale-gen
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

WORKDIR /app
ARG JAR_FILE=target/TaFeCo-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
COPY wait-for-it.sh /wait-for-it.sh
COPY uploads /app/uploads

RUN chmod +x /wait-for-it.sh
ENTRYPOINT ["/wait-for-it.sh", "db:3306", "--", "java", "-jar", "app.jar"]