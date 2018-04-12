#!/bin/bash

for (( i=4; i>0; i--)); do
java -jar target/ai-4.0.jar 192.168.0.101 39405 1 5 false &
echo "Started usual bot"
done

java -jar target/ai-4.0.jar 192.168.0.101 39405 1 5 true 3 &
echo "Started hosting bot"
java -jar target/ai-4.0.jar 192.168.0.101 39405 0 5 true 3 &
echo "Started hosting bot"
