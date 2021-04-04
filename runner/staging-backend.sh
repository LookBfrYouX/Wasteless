#!/usr/bin/bash

# Run the staging backend server
pwd

source ./env.txt

# Makes Spring use application-production.properties
export SPRING_PROFILES_ACTIVE=staging

fuser -k 9499/tcp || true
java -jar staging-backend/libs/backend-0.0.1-SNAPSHOT.jar --server.port=9499
