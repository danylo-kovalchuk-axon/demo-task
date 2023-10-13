#!/bin/bash

docker run -p 27017:27017 -d -t -i --name mongodb -v /home/mongod-data:/mongod-data --restart always mongodb:1.0 /opt/startup.sh
