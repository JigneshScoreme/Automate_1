#!/usr/bin/env bash
cd /home/ubuntu/server 
pwd
sudo nohup java -jar -Dspring.config.location=./application-DevDec27.properties ./DynamicFormsV2-0.0.1-SNAPSHOT.jar --server.port=8091 & disown &
