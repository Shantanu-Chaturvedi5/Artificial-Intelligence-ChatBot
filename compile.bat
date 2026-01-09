@echo off
REM Change to the script's directory
cd /d "%~dp0"

REM Set your JavaFX SDK path here
set JAVAFX_SDK=C:\javafx-sdk-21
set GSON_JAR=lib\gson-2.10.1.jar

REM Compile with JavaFX and gson in classpath
javac --module-path "%JAVAFX_SDK%\lib" --add-modules javafx.controls,javafx.fxml -cp .;"%GSON_JAR%" *.java

echo.
echo Compilation complete!
pause
