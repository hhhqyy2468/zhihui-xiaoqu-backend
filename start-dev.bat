@echo off
echo Starting Property Management Backend...
echo.

cd /d "%~dp0"

mvn spring-boot:run

echo.
echo Application stopped.
pause