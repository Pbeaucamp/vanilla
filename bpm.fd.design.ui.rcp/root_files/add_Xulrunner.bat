set OLDDIR=%CD%
@start /wait FIND /C /I "-Dorg.eclipse.swt.browser.XULRunnerPath" %CD%\VanillaDashboardDesigner.ini
IF ERRORLEVEL 1 echo -Dorg.eclipse.swt.browser.XULRunnerPath=%CD%\xulrunner>>%CD%\VanillaDashboardDesigner.ini
ELSE echo ca existe déjà>>%CD%\VanillaDashboardDesigner.ini

