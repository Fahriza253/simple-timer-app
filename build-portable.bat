@echo off
setlocal enabledelayedexpansion

REM Portable EXE build script for simple-timer-app
REM Requirements: Maven installed and jpackage available in PATH

set PROJECT_ROOT=%~dp0
set APP_NAME=SimpleTimerApp
set APP_VERSION=1.2.2
set MAIN_MODULE=com.dpzstudio.timer
set MAIN_CLASS=com.dpzstudio.timer.App
set JLINK_IMAGE=target\jlink-image\TimerApp
set OUTPUT_DIR=target\installer
set ICON_FILE=%PROJECT_ROOT%src\main\resources\icons\app.ico

necho.
echo [1/4] Verify tools...
where mvn > nul 2>&1
if errorlevel 1 (
    echo ERROR: Maven not found in PATH.
    goto end
)
where jpackage > nul 2>&1
if errorlevel 1 (
    echo ERROR: jpackage not found in PATH.
    echo Install JDK 14+ and make sure jpackage is available.
    goto end
)

echo [2/4] Building project and creating runtime image...
cd /d "%PROJECT_ROOT%"
mvn clean javafx:jlink
if errorlevel 1 (
    echo ERROR: Maven build or jlink failed.
    goto end
)

if not exist "%JLINK_IMAGE%" (
    echo ERROR: Runtime image not found at %JLINK_IMAGE%.
    goto end
)

echo [3/4] Packaging portable EXE...
if exist "%ICON_FILE%" (
    set ICON_OPTION=--icon "%ICON_FILE%"
) else (
    set ICON_OPTION=
)

mkdir "%OUTPUT_DIR%" > nul 2>&1
jpackage --type exe ^
         --name "%APP_NAME%" ^
         --app-version "%APP_VERSION%" ^
         --module %MAIN_MODULE%/%MAIN_CLASS% ^
         --runtime-image "%JLINK_IMAGE%" ^
         --dest "%OUTPUT_DIR%" ^
         --vendor "DPZ Studio" ^
         --win-menu ^
         --win-shortcut %ICON_OPTION%

if errorlevel 1 (
    echo ERROR: jpackage failed.
    goto end
)

echo [4/4] Done.
echo Portable EXE is available in %OUTPUT_DIR%.

:end
endlocal
pause
