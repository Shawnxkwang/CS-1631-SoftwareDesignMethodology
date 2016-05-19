@echo off
title Email

javac -sourcepath ../../Component/Email -cp ../../Components/* ../../Components/Email/*.java
start "Email" /D"../../Components/Email" java -cp .;../* CreateEmail