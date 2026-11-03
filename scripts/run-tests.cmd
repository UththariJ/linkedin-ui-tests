@echo off
setlocal

set "TEST_NAME=%~1"
if "%TEST_NAME%"=="" (
  powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0run-tests.ps1"
) else (
  powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0run-tests.ps1" -Test "%TEST_NAME%"
)

set "EXIT_CODE=%ERRORLEVEL%"
endlocal & exit /b %EXIT_CODE%
