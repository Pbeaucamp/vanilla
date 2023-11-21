SET MYDATE=%DATE:~3,2%%DATE:~0,2%%DATE:~8,4%
set hour=%time:~0,2%
if "%hour:~0,1%" == " " set hour=0%hour:~1,1%
set min=%time:~3,2%
if "%min:~0,1%" == " " set min=0%min:~1,1%
set secs=%time:~6,2%
if "%secs:~0,1%" == " " set secs=0%secs:~1,1%

call stop-vanilla.cmd > logs/restart_stop_%MYDATE%_%hour%%min%%secs%.out 2<&1
rmdir /s /q work
call start-vanilla.cmd > logs/restart_start_%MYDATE%_%hour%%min%%secs%.out 2<&1
exit
