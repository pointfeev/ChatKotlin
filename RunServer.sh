#!/usr/bin/env bash
./gradlew :server:installDist -q || exit
./server/build/install/server/bin/server "$@"