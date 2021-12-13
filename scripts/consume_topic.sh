#!/usr/bin/env sh

docker exec -it broker kafka-console-consumer \
    --bootstrap-server localhost:9092 \
    --topic fraudulent_transaction_events \
    --from-beginning
