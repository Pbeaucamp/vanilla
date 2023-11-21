@echo on

:: delete WORK folder
:: IF EXIST "%cd%/work" rmdir /s /q "%cd%/work"

:: Below the code to get permissions if needed 
:: Permissions are required to write system environment variables
::-------------------------------------
REM  --> Check for permissions
>nul 2>&1 "%SYSTEMROOT%\system32\cacls.exe" "%SYSTEMROOT%\system32\config\system"

REM --> If error flag set, we do not have admin.
if '%errorlevel%' NEQ '0' (
    echo Requesting administrative privileges...
    goto UACPrompt
) else ( goto gotAdmin )

:UACPrompt
    echo Set UAC = CreateObject^("Shell.Application"^) > "%temp%\getadmin.vbs"
    set params = %*:"=""
    echo UAC.ShellExecute "cmd.exe", "/c %~s0 %params%", "", "runas", 1 >> "%temp%\getadmin.vbs"
    "%temp%\getadmin.vbs"
    del "%temp%\getadmin.vbs"
    exit /B

:gotAdmin
    pushd "%CD%"
    CD /D "%~dp0"
:--------------------------------------

rem set JAVA_OPTS=-Dlog4j.configuration=log.xml -Dlog4j.debug=true -Dbpm.vanilla.configurationFile=%cd%/vanilla-conf/vanilla.properties
set JAVA_OPTS=-Dlog4j.debug=true -Dbpm.vanilla.configurationFile=%cd%/vanilla-conf/vanilla.properties -Dbpm.update.manager.configurationFile=%cd%/vanilla-conf/update_manager.properties -Dlog4j.configuration=%cd%/vanilla-conf/log.xml
echo %JAVA_OPTS%
set JAVAVER=""
set JAVAPATHINSTALL=C:\Program Files\Java\jdk1.7.0_79

::check JAVA version
for /f tokens^=2-5^ delims^=.-_^" %%j in ('java -fullversion 2^>^&1') do set "JAVAVERSION=%%j%%k"
set FALSEJAVA=false
if not "%JAVAVERSION%" == "17" set FALSEJAVA=true
if "%JAVA_HOME%" == "" set FALSEJAVA=true

if "%FALSEJAVA%" == true (
ECHO Installing java
start /w %cd%/resources/java/jdk-7u79-windows-x64.exe /s /L %cd%/resources/java/setupJava.log
rem create variable JAVA_HOME at system level for all users in the computer
set  "JAVA_HOME=C:\Program Files\Java\jdk1.7.0_79"
SETX JAVA_HOME "%JAVAPATHINSTALL%" /M
SETX PATH "%PATH%;%%JAVA_HOME%%\bin" /M
ECHO java installed successfully
goto doResume
) else ( 

goto doResume
)

:doResume
echo "%JAVA_HOME%"
set VANILLA_LIBS="bpm.vanilla.starter.jar"
set VANILLA_CLASS=bpm.vanilla.starter.VanillaConfUpdater
set JAVA_OPTS=-Dlog4j.debug=true -Dbpm.vanilla.configurationFile=%cd%/vanilla-conf/vanilla.properties -Dbpm.update.manager.configurationFile=%cd%/vanilla-conf/update_manager.properties
echo %JAVA_OPTS%
CALL "%JAVA_HOME%/bin/java.exe" %JAVA_OPTS% -cp  %VANILLA_LIBS% %VANILLA_CLASS%
CALL "cmd /c start %cd%/R-3.3.3/bin/R.bat"
echo update Done!!

if ""%1"" == ""start"" goto doStart

:doStart
bin/catalina.bat start
goto end

bin/catalina.bat run

:end
echo vanilla is ready

