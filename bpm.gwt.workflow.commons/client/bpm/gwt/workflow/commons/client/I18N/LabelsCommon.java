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
package bpm.gwt.workflow.commons.client.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

/**
 * @author Belgarde
 *
 */
public interface LabelsCommon extends Constants {

	public static LabelsCommon lblCnst = (LabelsCommon) GWT.create(LabelsCommon.class);
	
	String CreateNewWorkflow();
	String ExecuteWorkflow();
	String ScheduleWorkflow();
	String HistoricsWorkflow();
	String Workflows();
	String DeleteBox();
	String Variables();
	String FilePath();
	String DefaultTheme();
	String Error();
	String ErrorDetails();
	String OK();
	String Cancel();
	String NeedName();
	String NameTaken();
	String creation_title();
	String Save();
	String Undo();
	String Redo();
	String Properties();
	String Refresh();
	String Parameters();
	String Send();
	String ChooseFile();
	String Reduce();
	String Maximize();
	String Close();
	String Information();
	String WorkflowNotValid();
	String ItemsCannotHaveSameName();
	String ItemsNotValid();
	String WorkflowNotValidSureSave();
	String Confirmation();
	String Name();
	String Description();
	String Author();
	String UpdateApplication();
	String Views();
	String Logout();
	String AboutMe();
	String AdminView();
	String ConnectedAs();
	String CancelImpossible();
	String ApplicationUpToDate();
	String YourApplicationVersion();
	String WishUpdateToVersion();
	String consult_title();
	String Yes();
	String No();
	String StopDateReached();
	String NotDefined();
	String Running();
	String NotRunning();
	String Unknown();
	String NoModification();
	String Status();
	String Scheduled();
	String NextExecution();
	String IsRunning();
	String CreationDate();
	String ModifiedBy();
	String ModificationDate();
	String Planification();
	String DeleteWorkflowConfirm();
	String NoLogs();
	String Success();
	String WorkflowLogs();
	String StartDate();
	String EndDate();
	String Duration();
	String Details();
	String Progress();
	String FilesTreated();
	String SuccessRunWorkflow();
	String FailedRunWorkflow();
	String StopByUser();
	String ShowLogs();
	String Download();
	String AllParameterNeedToBeSet();
	String All();
	String Yearly();
	String Monthly();
	String Weekly();
	String Daily();
	String Hourly();
	String Interval();
	String IntervalNotValid();
	String Minute();
	String PlanWorkflow();
	String DeactivateWorkflow();
	String ActivateWorkflow();
	String Start();
	String Stop();
	String StopWorkflow();
	String HasStopDate();
	String resources_title();
	String resources_description();
	String Add();
	String Edit();
	String DefaultValue();
	String AddParameter();
	String Question();
	String DeleteVariableConfirm();
	String DeleteParameterConfirm();
	String ParameterWithNoAccent();
	String Definition();
	String AutomaticNameChange();
	String Variable();
	String Day();
	String Month();
	String Year();
	String ScriptDate();
	String HelpScriptDate();
	String VariableIsDate();
	String DateFormat();
	String HelpDateFormat();
	String ScriptEmpty();
	String CurrentDate();
	String Widthdraw();
	String Adding();
	String IntervalNotValidWithZero();
	String AddVariable();
	String URL();
	String Port();
	String Secured();
	String HttpFileParam();
	String StartFolder();
	String FtpActions();
	String AddAction();
	String DeleteAction();
	String IsNetworkFolder();
	String Login();
	String Password();
	String Cible();
	String UrlIsNeeded();
	String PortIsNeeded();
	String HttpFileParamNameIsNeeded();
	String FolderPathIsNeeded();
	String DestinationPath();
	String Delete();
	String Move();
	String AddCible();
	String EditCible();
	String Type();
	String DeleteCibleConfirm();
	String Cibles();
	String SelectCible();
	String OutputFileName();
	String OverrideExistingFile();
	String Menus();
	String Toolbox();
	String Up();
	String Down();
	String Outputs();
	String HistoryOutput();
	String ListOfValues();
	String SimpleValue();
	String RangeValue();
	String SelectionList();
	String AddListOfValues();
	String EditListOfValues();
	String DeleteListOfValuesConfirm();
	String AtLeastOneValueNeeded();
	String Values();
	String AddValue();
	String EnterVectorValuesSeparatedbySemiColon();
	String ConnectionIsValid();
	String DefinitionNotValid();
	String DatabaseUrlIsNeeded();
	String DatabaseUrl();
	String TestConnection();
	String DriverJdbc();
	String DatabaseServers();
	String AddDatabaseServer();
	String DeleteDatabaseServerConfirm();
	String FromDataset();
	String Datasets();
	String Columns();
	
	String Equals();
	String IsSuperior();
	String IsInferior();
	String Contains();
	String NotContains();
	String DefineParentParameter();
	String Operator();
	
	String LoadPackages();
	String ApiKey();
	String Org();
	String Url();
	String D4C();
	String PackageNotValid();
	String ResourceName();
	String UseExistingDataset();
	String CreateNewDataset();
	String NameDatasetLimitation();
	String DatasetUploadedToCkan();
	String TheResourceNameCannotBeEmpty();
	String NewResource();
	String HelpFileName();
	String SelectedDataset();
	String SelectedResource();
	String Dataset();
	String Resource();
	String Options();
	String UseOutputName();
	String URLOrProtocol();
	String AttachmentFilter();
	String ProtocolUrlIsNeeded();
	String Mail();
	String Query();
	String Database();
}
