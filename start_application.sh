#!/bin/bash

touch /var/log/cloud-reverser.log
nohup java -jar -Dspring.profiles.active=production /tmp/*.jar > /var/log/cloud-reverser.log 2>&1 &