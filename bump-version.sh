#!/bin/bash

if [ "$1" == "git" ]; then
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

    export NEW_VERSION="${parts[0]}.${parts[1]}.${parts[2]}"
    perl -p -i -e 's/$ENV{OLD_VERSION}/$ENV{NEW_VERSION}/' frontend/package.json

elif [ "$1" == "build" ]; then
    oldVersion=$(jq -r '.["version"]' frontend/package.json)
    commit=$(git rev-parse --short HEAD)
    date=$(date +'%Y%m%d%H%M%S')
    export OLD_VERSION=$oldVersion
    export NEW_VERSION="$oldVersion-$date-$commit"
    perl -p -i -e 's/$ENV{OLD_VERSION}/$ENV{NEW_VERSION}/' frontend/package.json
fi