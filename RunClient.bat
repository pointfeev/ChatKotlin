@echo off
call gradlew.bat :client:installDist -q || exit
call client/build/install/client/bin/client.bat %*