@echo off
title Uploader

javac -sourcepath ../../Components/Uploader -cp ../../Components/* ../../Components/Uploader/*.java
start "Uploader" /D"../../Components/Uploader" java -cp .;../* CreateUploader