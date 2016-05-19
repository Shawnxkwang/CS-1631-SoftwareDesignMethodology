@echo off
title GUI

javac -sourcepath ../../Components/GUI -cp ../../Components/* ../../Components/GUI/*.java
start "GUI" /D"../../Components/GUI" java -cp .;../* CreateGUI