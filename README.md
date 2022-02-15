# Flink Demo for Voxxed Days Australia 2021

This is a Flink demo application supporting the Voxxed Days Australia 2021 presentation "Riding the Streams". 

It demonstrates basic concepts of Flink implementing a made up fraud detection use case over hypothetical payments.

# Requirements
* Docker: required to run Kafka via docker-compose
* Gradle 7.0 or later version
* Java 11 or later

# How to Run
From the root of the project run the commands below.

You can run load runner, Flink app and fraud payments consumer in different terminal or run them
in the background.

Modify the heuristic and load patterns to see
changes on how the streaming application detects fraudulent payments.

## Build
`./gradlew build`

## Start Kafka
`docker-compose up -d`

## Setup topics
`./scripts/setup_topics.sh`

## Run Flink app
`./scripts/run_fraud_detection.sh`

## Simulate payment traffic
`./scripts/run_load.sh`

## Show fraudulent payments
`./scripts/show_fraudulent_payments.sh`

## Stop Kafka
`docker-compose down`


