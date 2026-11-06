@echo off
setlocal
if "%~1"=="" (
  call mvn test
) else (
  call mvn test -Dtest=%~1
)
set "EXIT_CODE=%ERRORLEVEL%"
endlocal & exit /b %EXIT_CODE%
