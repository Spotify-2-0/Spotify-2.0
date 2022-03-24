#!/bin/bash


oldVersion=$(jq -r '.["version"]' frontend/package.json)
export OLD_VERSION=$oldVersion

IFS='.' read -r -a parts <<< "$oldVersion"

if [[ $(git log -1 --pretty=format:"%s") =~ .*"major".* ]]; then
    (( parts[0]++; parts[1] = 0; parts[2] = 0 ))
elif [[ $(git log -1 --pretty=format:"%s") =~ .*"minor".* ]]; then
    (( parts[1]++; parts[2] = 0 ))
else
    (( parts[2]++ ))
fi


commit=$(git rev-parse --short HEAD)
newVersion="${parts[0]}.${parts[1]}.${parts[2]}-$commit"
export NEW_VERSION="${parts[0]}.${parts[1]}.${parts[2]}-$commit"
perl -p -i -e 's/$ENV{OLD_VERSION}/$ENV{NEW_VERSION}/' frontend/package.json
echo "$oldVersion"
echo "$newVersion"