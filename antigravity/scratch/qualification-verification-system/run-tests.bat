@echo off
title Run DevOps QVS Automated Test Suite
echo ========================================================
echo  Executing Automated Test Suite & JaCoCo Coverage...
echo ========================================================

if exist "%~dp0..\tools\jdk-17.0.10+7" (
    set "JAVA_HOME=%~dp0..\tools\jdk-17.0.10+7"
    set "PATH=%JAVA_HOME%\bin;%~dp0..\tools\apache-maven-3.9.6\bin;%PATH%"
)

call mvn clean test jacoco:report
echo.
echo ========================================================
echo  Test suite execution complete!
echo  JaCoCo report: target\site\jacoco\index.html
echo ========================================================
pause

