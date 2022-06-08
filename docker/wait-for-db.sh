#!/usr/bin/env bash

until nc -z -v -w30 $DB_HOST $DB_PORT
do
  echo "Waiting until the database is receiving connections..."
  sleep 1
done