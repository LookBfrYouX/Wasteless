#!/usr/bin/bash

# Run the staging backend server

source ./env.txt

fuser -k 9499/tcp || true
java -jar staging-backend/libs/backend-0.0.1-SNAPSHOT.jar --server.port=9499 -Dspring.profiles.active=staging
