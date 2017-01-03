#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
read -p "Enter Jenkins UserName: " name
read -s -p "Enter Jenkins Password: " password
curl -i -k -u $name:$password 'https://jenkins-master-sp.nm.flipkart.com/createItem?name=fk-sp-inventory-planner-build' -X POST -H 'Content-Type: application/xml' --data-binary @$DIR/config.xml

