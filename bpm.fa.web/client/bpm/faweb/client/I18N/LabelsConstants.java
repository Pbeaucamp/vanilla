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
package bpm.faweb.client.I18N;

import com.google.gwt.i18n.client.Constants;

/**
 * @author Belgarde
 *
 */
public interface LabelsConstants extends Constants {

	String Edit();
	String Please_wait___();
	String Close();
	String Comment();
	String Name();
	
	String Next();
	String Cancel();
	String Save();
	
	String LEFT();
	String RIGHT();
	String Remove();
	String Add();
	String Title();
	String CubeName();
	String Width();
	String Height();
	
	String WelcomeFa();
	
	String SelCube();

	String Open();
	String Undo();
	String Redo();
	String toXls();
	String toCsv();
	String SwapAxes();
	String filter();
	String Refresh();
	String addFilter();
	String cubeViewer();
	String drillThrough();
	String chartsViewer();
	String HideNull();
	String ShowNull();
	String ReportName();
	String reportViewer();
	String SaveOk();
	String Measures();
	String Dimensions();
	String addCol();
	String addRow();
	String del();
	String Back();
	String Finish();
	String BrowseRepository();
	String Report();
	String MDX();
	String save_failed();
	String Error();
	String Portrait();
	String Landscape();
	String Pagesize();
	String Standard();
	String Custom();
	String Margins();
	String Top();
	String Bottom();
	String Sum();
	String Diff();
	String Group();
	String EnableCalculator();
	String DisableCalculator();
	String Div();
	String SelDiv();
	String SelMul();
	String Multi();
	String Internal();
	String Public();
	String Folder();
	String SelectSeries();
	String ChartType();
	String Overview();
	String DelError();
	String AddFilterDimExistError();
	String AddFilterDimLevelError();
	String AddFilterAlreadyExistError();
	String Rows();
	String Cols();
	String AlreadyExist();
	String ErrorMultipleAdd();
	
	String MultipleAdding();
	
	String Apply();
	String OutputType();
	String CreateChart();
	String SelectMeasures();
	String SelectChartGroup();
	String TopxTarget();
	String TopxCount();
	String SearchDim();
	String SearchDimResult();
	String Percent();
	String ShowMeasure();
	String PercentTitle();
	String PercentToolBar();
	String TotalsToolBar();
	String ReporterToolBar();
	String ModeOnOff();
	String ReporterPresentation();
	
	String NoResultFound();
	
	String Level();
	String PromptList();
	String PromptText();
	
	String RolodexTitle();
	
	String ShowMap();
	String DialogChoiceMapOption();
	String ChooseMap();
	String ChooseDataset();
	String ChooseMeasure();
	String ChooseDimension();
	String ExportChart();
	String ReportTitleDescription();
	String ReportTitle();
	String ReportDescription();
	String RolodexDialogTitle();
	
	String ProjectionSaveOk();
	String ExtrapolationNone();
	String ExtrapolationLagrange();
	
	String ProjectionCompareBtn();
	String ProjectionCubeViewBtn();
	
	String ProjectionName();
	String ProjectionComment();
	String ProjectionStartDate();
	String ProjectionEndDate();
	String ProjectionType();
	String ProjectionLevel();
	String ProjectionCreate();
	String ProjectionOpenOrCreate();
	String ProjectionDifferences();
	String ProjectionCompare();
	String ProjectionMeasureFormula();
	String ProjectionBadCondtions();
	String ProjectionAddCondition();
	String ProjectionDefaultCondition();
	String ProjectionDiffLoad();
	String ProjectionNoDrillThrough();
	String ProjectionOriginalValues();
	String ProjectionProjValues();
	String ExtrapolationLinear();
	String ProjectionEdit();
	
	String ProjectionRappelFormula();
	String ProjectionFormulaTitle();
	String ProjectionFormulaMinus();
	String ProjectionFormulaPercent();
	String ProjectionFormulaCalculType();
	String ProjectionFormulaOrder();
	
	String ProjectionFilterNone();
	String ProjectionFilter();
	String ProjectionFilterStart();
	String ProjectionFilterEnd();
	
	String FilterConfig();
	String SeeProfile();
	String Logout();
	String UnknownMail();
	String AboutMe();
	String SelectTheme();
	String SaveView();
	String ExportAsDocument();
	
	String SelectChart();
	String SelectDataChart();
	String PreviewChart();
	String ClearChart();
	
	String Separator();
	String Columns();
	String Type();
	String Value();
	
	String ReportSaved();
	
	String RenameElement();
	String RemovePersonal();
	String AddTopX();
	String ModifyTopX();
	String RemoveTopX();
	String ShowMapMenu();
	String DrillUp();
	String DrillDown();
	
	String CreateDashboard();
	String CreateSnapshot();
	String ListSnapshot();
	String DrillCubeReport();
	String AddPrompt();
	String ChooseChartDimension();
	String Pie();
	String Donut();
	String Bar();
	String StackedBar();
	String Column();
	String Area();
	String StackedArea();
	String Line();
	String Spline();
	String SteppedArea();
	String OpenView();
	String OpenDefinitionPanel();
	String CloseDefinitionPanel();
	String ShowFilters();
	String HideFilters();
	String Where();
	String ProvideMeasureAndDimension();
	String ReportPreview();
	String ProvideName();
	String NoFilter();
	String ResetFilters();
	String Between();
	String Contains();
	String NotContains();
	String EndWith();
	String GreaterThan();
	String GreaterOrEqual();
	String Empty();
	String LessThan();
	String LessOrEqual();
	String NotBetween();
	String NotEmpty();
	String StartWith();
	String ClearReport();
	String NoCube();
	String NeedSelectCube();
	String ChangeTypeViewer();
	String NeedSelectView();
	String Search();
	String Clear();
	String NoView();
	String SelView();
	String SelectSnapshotToSave();
	String SnapshotSave();
	String Snapshot();
	String EnterNameSnapshot();
	String Parameters();
	String Color();
	String Minimum();
	String Maximum();
	String MeasureFormulas();
	String FilterTimeMembers();
	String AddDateFilters();
	String Elements();
	String Conditions();
	String Formula();
	String CalculCondition();
	String FilteringMembers();
	String DashboardCreation();
	String SelectViewForDashboard();
	String On();
	String Off();
	String ShowTotals();
	String HideTotals();
	String ShowProperties();
	String HideProperties();
	
	String up();
	String down();
	
	String element();
	String sortType();
	String sorting();
	
	String noViewOpen();
	String updateOk();
	String updateKo();
	String update();
	String DashboardSaveWithSuccess();
	String ElementCannotBeRemove();
	String SelectOneView();
	String Wait();
	String DatasourceName();
	String Drill();
	String NoDrillAvailable();
	String RunDrill();
	
	String Layers();
	String Maps();
}
