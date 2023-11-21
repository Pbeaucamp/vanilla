touch ./VanillaDashboardDesigner_2.ini
cp ./VanillaDashboardDesigner.ini VanillaDashboardDesigner_2.ini
sed '-Dorg.eclipse.swt.browser.XULRunnerPath=' ./VanillaDashboardDesigner_2.ini > ./VanillaDashboardDesigner.ini
rm -f ./VanillaDashboardDesigner_2.ini
curr_dir=$(pwd)
echo "-Dorg.eclipse.swt.browser.XULRunnerPath=$curr_dir/xulrunner" >> ./VanillaDashboardDesigner.ini