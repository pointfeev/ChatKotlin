@echo off
call gradlew.bat :server:installDist -q || exit
call server/build/install/server/bin/server.bat %*