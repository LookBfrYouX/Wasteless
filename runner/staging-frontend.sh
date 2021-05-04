#!/usr/bin/bash

# Run the staging frontend app

source ./env.txt

fuser -k 9500/tcp || true
http-server staging-frontend/dist/ -p 9500
