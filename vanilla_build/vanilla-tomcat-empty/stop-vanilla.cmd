@echo on

:: Below the code to get permissions if needed 
:: Permissions are required to delete PID
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

:: looking for PID
(jps -lv)>>%cd%\temp\contains_pid_temp.txt

::get PID's TOMCAT
for /F %%a in ('findstr "catalina.home" %cd%\temp\contains_pid_temp.txt') do taskkill /F /PID %%a

:: delete temp file which contains the PID
del "%cd%\temp\contains_pid_temp.txt"

:: del meta.log
del "%cd%\meta.log"
