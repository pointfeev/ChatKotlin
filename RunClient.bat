@echo off
call gradlew.bat :client:installDist -q
call client/build/install/client/bin/client.bat %*