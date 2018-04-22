#!/usr/bin/env bash
sudo java -Ddw.server.connector.port=80 -jar target/enpassant-1.0-SNAPSHOT.jar server config.yml
