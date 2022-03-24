#!/bin/bash


oldVersion=$(jq -r '.["version"]' frontend/package.json)
export OLD_VERSION=$oldVersion
date=$(date +'%Y%m%d%H%M%S')
IFS='-' read -r -a versionSplitByDash <<< "$oldVersion"
IFS='.' read -r -a parts <<< "${versionSplitByDash[0]}"

if [[ $(git log -1 --pretty=format:"%s") =~ .*"major".* ]]; then
    (( parts[0]++; parts[1] = 0; parts[2] = 0 ))
elif [[ $(git log -1 --pretty=format:"%s") =~ .*"minor".* ]]; then
    (( parts[1]++; parts[2] = 0 ))
else
    (( parts[2]++ ))
fi


commit=$(git rev-parse --short HEAD)
newVersion="${parts[0]}.${parts[1]}.${parts[2]}-$date-$commit"
export NEW_VERSION="${parts[0]}.${parts[1]}.${parts[2]}-$commit"
perl -p -i -e 's/$ENV{OLD_VERSION}/$ENV{NEW_VERSION}/' frontend/package.json