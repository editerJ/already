@ECHO OFF
SETLOCAL

SET DIRNAME=%~dp0
IF "%DIRNAME%"=="" SET DIRNAME=.
SET APP_HOME=%DIRNAME:~0,-1%
SET GRADLE_BIN=C:\Users\Jeiheewon\.gradle\wrapper\dists\gradle-8.13-bin\5xuhj0ry160q40clulazy9h7d\gradle-8.13\bin\gradle.bat
SET GRADLE_USER_HOME=C:\Users\Jeiheewon\.gradle
SET PROJECT_CACHE_DIR=C:\Users\Jeiheewon\.gradle\regret-diary-project-cache

CALL "%GRADLE_BIN%" --project-cache-dir "%PROJECT_CACHE_DIR%" -p "%APP_HOME%" %*
ENDLOCAL
