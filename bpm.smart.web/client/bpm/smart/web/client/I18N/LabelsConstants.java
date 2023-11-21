/*
 * Copyright 2007 BPM-conseil.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package bpm.smart.web.client.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

/**
 * @author KMO
 *
 */
public interface LabelsConstants extends Constants {

	public static LabelsConstants lblCnst = (LabelsConstants) GWT.create(LabelsConstants.class);
	
	String Error();
	String Name();
	String Edit();
	String About();
	
	String Previous();
	String Next();
	String Cancel();
	String Save();
	String ExportAsPDF();
	
	String Clear();
	String Refresh();
	
	String Public();
	String Run();
	String Author();
	String Subject();
	String Private();
	String Help();
	String Status();
	String Type();
	String Export();
	String Copy();
	String Paste();
	String Delete();
	String ExportAsHTML();
	String ExportAsXLS();
	String ExportAsDOC();
	String Log();
	String Apply();
	String Information();
	String Test();
	String AddDataSet();
	
	String ConfirmDelete();
	String Yes();
	String No();
	
	String Version();
	String Folder();
	String NoData();
	String NoResult();
	
	String Ok();
	String Checkin();
	String Checkout();
	
	String LogOut();
	String Date();
	String Mail();
	
	String Success();
	String Copyright();
	
	//Project
	String Project();
	String ProjectCreation();
	String ProjectEdition();
	String Icon();
	String UnableToLoadProjects();
	String AddANewProject();
	String DeleteProject();
	String UnableToAddAProject();
	String ConfirmDeleteProject();
	String UnableToDeleteProject();
	String UnableToExecuteProject();
	String DeleteProjectSuccessfull();
	String UnableToLoadProject();
	String ShareProject();
	String UnableToShareProject();
	String CanAccessToThisProject();
	String ChooseColumn();
	String MissingInformation();
	
	//smart web admin
	String SaveConfiguration();
	String ChooseDefaultCranMirror();
	String EnterNewPackage();
	String Install();
	String NoMirrorSelected();
	String Configuration();
	String UnableToLoadConfig();
	String UnableToSaveConfig();
	String UnableToLoadMirrors();
	String UnableToLoadPackages();
	String UnableToInstallPackage();
	String UnableToInstallPackageError();
	String PackageAlreadyInstalled();
	String PackageInstallationSuccessfull();
	String UnableToExecuteScript();
	String UnableToLoadScripts();
	String UnableToLoadScriptModels();
	
	String UnableToLoadDatasets();
	String UnableToDeleteDataset();
	
	String TabViewer();
	String TabDatasetManager();
	String TabWorkspace();
	String TabAdmin();
	String HideStats();
	String ShowStats();
	String HideDetails();
	String ShowDetails();
	String Request();
	String Min();
	String Max();
	String Mean();
	String Deviation();
	String Distribution();
	String UnableToLoadStats();
	
	String AddScript();
	String DeleteScript();
	String UnableToDeleteScript();
	String UnableToSaveScript();
	String DeleteScriptSuccessfull();
	String ConfirmDeleteScript();
	String SaveAsNewScript();
	String SaveAsNewVersion();
	String Undo();
	String Redo();
	String RunSelection();
	String ImportRCode();
	String ExportRCode();
	String ImportRMDCode();
	String ExportRMDCode();
	String VersionsList();
	String SeeVersions();
	String UnableToCheckInScript();
	String UnableToCheckOutScript();
	String UnableToRefresh();
	String UnableToImportCode();
	String CheckInScriptSuccessfull();
	String CheckOutScriptSuccessfull();
	String ScriptAlreadyUsed();
	String VersionViewer();
	String SelectAVersion();
	String Downgrade();
	String ConfirmDowngrade();
	String ExistantName();
	String ScriptImport();
	String AddSelectedCode();
	String SelectAll();
	String UnableToLoadUsers();
	String Image();
	String SendToR();
	String UnableToSendToR();
	String ShareOfProject();
	String ImportProject();
	String ExportProject();
	String AllVersions();
	String OnlyTheLast();
	String ExportProjectVersions();
	String NewProjectName();
	String ImportProjectSuccessfull1();
	String ImportProjectSuccessfull2();
	String ImportProjectError1();
	String ImportProjectError2();
	String Filter();
	String ErrorFileType();
	
	String ExecuteCommand();
	String ToListVariables();
	String ToListInstalledPackages();
	String ToListExecutedLines();
	String CommandLine();
	String ClearView();
	String ScriptType();
	String MissingName();
	String UnableToRenderMarkdown();
	String Library();
	String LibraryOrFunction();
	String HelpOnSelectedPackage();
	
	String TitleApplication();
	String Action();
	String AirScript();
	String CubeViewer();
	String OutputFile();
	String SelectTypeOutput();
	String Update();
	String AvailableUpdate();
	String NoFileSelected();
	
	String ActivityRecode();
	String ActivityChart();
	String ActivityFieldSelection();
	String ActivityHead();
	
	String RefreshWorkflow();
	String Dataset();
	String Column();
	
	String OutputDataset();
	String AirProject();

	String ViewDataset();
	String Summary();
	String UnableToUpdatePackage();	
	
	String SelectTypeHead();
	String LinesNumber();
	String ActivitySorting();
	String ActivityFilter();
	String FilterValue();
	String FilterOperator();
	String ConfirmDeletePackage();
	String UnableToDeletePackage();
	String UpdatePackageSuccessfull();
	String DeletePackageSuccessfull();
	String LinkedDatasets();
	String AddOutputgraph();
	String ActivitySimpleLinearReg();
	String ActivityHACClustering();
	String ActivityDecisionTree();
	String ActivityKMeans();
	String ActivityCorrelationMatrix();
	String SimpleMethods();
	String Launch();
	String DistanceMeasure();
	String AgglomerationMethod();
	String Rotate();
	String Algorithm();
	String Centers();
	String IterMax();
	String NStart();
	String Method();
	String CorrelationCoef();
	String NumColumns();
	String NonNumColumns();
	String ReloadDatasets();
	String Reload();
	String UpdatePackage();
	String DeletePackage();
	
	String ChangeStyle();
	String ExportToRepository();
	String QuitWithoutSave();
	String UnsavedModificationsMessage();
	String UseTemplate();
	String ImportImage();
	String MarkdownTemplate();
	String RCode();
	String NewScriptName();
	String ConfirmClearText();
	
	String RCodeTools();
	String ConvertDateToAge();
	String ConvertAgeToRange();
	String CreateClassificationGroup();
	String CreateCalculatedColumn();
	String FilterColumn();
	String GeoMappingColumn();
	String RecodeColumn();
	String ChooseDatasetToRecode();
	String NewColumn();
	String GeneratedCode();
	String Formula();
	String Allocation();
	String NewLevels();
	String Options();
	String DateFormatExample();
	String NewDataset();
	
	String Equals();
	String IsSuperior();
	String IsInferior();
	String Contains();
	String NotContains();
	String NoComment();
}
