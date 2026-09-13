@echo off
title DevOps Qualification Verification System
echo ========================================================
echo  Starting DevOps Qualification Verification System...
echo ========================================================

if exist "%~dp0..\tools\jdk-17.0.10+7" (
    set "JAVA_HOME=%~dp0..\tools\jdk-17.0.10+7"
    set "PATH=%JAVA_HOME%\bin;%~dp0..\tools\apache-maven-3.9.6\bin;%PATH%"
)

echo Java Version:
java -version 2>nul || (
    echo [WARNING] Java not found in PATH or ..\tools\. Please install Java 17+ or ensure JAVA_HOME is set.
)

echo.
echo Launching Spring Boot server on http://localhost:8080 ...
echo Press Ctrl+C to stop the server.
echo.

call mvn spring-boot:run
pause

