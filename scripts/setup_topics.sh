#!/usr/bin/env sh

# Create source topic
docker exec -it broker kafka-topics --create --topic transaction_events --partitions 1 --replication-factor 1 --bootstrap-server localhost:9092

# Create target topic
docker exec -it broker kafka-topics --create --topic fraudulent_transaction_events --partitions 1 --replication-factor 1 --bootstrap-server localhost:9092