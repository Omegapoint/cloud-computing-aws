#!/bin/bash

touch /var/log/cloud-reverser.log
nohup java -jar /tmp/*.jar -Dspring.profiles.active=production > /var/log/cloud-reverser.log 2>&1 &