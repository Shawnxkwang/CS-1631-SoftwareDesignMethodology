@echo off
title Temp

javac -sourcepath ../../Component/Temp -cp ../../Components/* ../../Components/Temp/*.java
start "Temp" /D"../../Components/Temp" java -cp .;../* CreateTemp