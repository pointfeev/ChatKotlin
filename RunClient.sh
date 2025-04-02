#!/usr/bin/env bash
./gradlew :client:installDist -q || exit
./client/build/install/client/bin/client "$@"