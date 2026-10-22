@echo off
setlocal
cd /d "%~dp0"
set "INSTRUCTOR_INVITE_CODE=quizly-demo"
call mvnw.cmd spring-boot:run
endlocal
