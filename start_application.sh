#!/bin/bash

touch app.log
nohup java -jar /tmp/*.jar -Dspring.profiles.active=production > app.log 2>&1 &