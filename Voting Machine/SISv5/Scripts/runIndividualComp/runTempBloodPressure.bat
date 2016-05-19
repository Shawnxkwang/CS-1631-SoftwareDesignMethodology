@echo off
title TempBloodPressure

javac -sourcepath ../../Components/TempBloodPressure -cp ../../Components/* ../../Components/TempBloodPressure/*.java
start "TempBloodPressure" /D"../../Components/TempBloodPressure" java -cp .;../* CreateTempBloodPressure