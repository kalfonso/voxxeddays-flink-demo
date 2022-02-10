#!/usr/bin/env sh

docker exec -it broker kafka-console-consumer \
    --bootstrap-server localhost:9092 \
    --topic $1 \
    --from-beginning
