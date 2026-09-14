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

echo Checking if port 8080 is currently occupied...
powershell -NoProfile -Command "$pids = (Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue).OwningProcess | Select-Object -Unique; foreach ($p in $pids) { if ($p -and $p -ne 0) { Write-Host 'Freeing port 8080 (Process ID: '$p')...'; Stop-Process -Id $p -Force -ErrorAction SilentlyContinue } }" 2>nul

echo.
echo Launching Spring Boot server on http://localhost:8080 ...
echo Press Ctrl+C to stop the server.
echo.

call mvn spring-boot:run
pause

