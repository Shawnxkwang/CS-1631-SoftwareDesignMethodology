@echo off
title !name!

javac -sourcepath ../../Components/!name! -cp ../../Components/* ../../Components/!name!/*.java
start "!name!" /D"../../Components/!name!" java -cp .;../* Create!name!
