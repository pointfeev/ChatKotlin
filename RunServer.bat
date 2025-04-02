@echo off
call gradlew.bat :server:installDist -q
call server/build/install/server/bin/server.bat