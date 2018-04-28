#!/usr/bin/env bash
sudo java -Ddw.server.connector.port=$PORT -Ddw.initialStateAPIKey=$INITIAL_STATE_API_KEY -jar target/enpassant-1.0-SNAPSHOT.jar server config.yml
