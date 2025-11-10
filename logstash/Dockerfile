FROM docker.elastic.co/logstash/logstash:8.14.1

RUN curl -L -o /usr/share/logstash/postgresql.jar \
  https://jdbc.postgresql.org/download/postgresql-42.7.4.jar

COPY pipeline/ /usr/share/logstash/pipeline/
