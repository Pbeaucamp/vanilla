package bpm.fm.designer.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

public interface Messages extends Constants {

	public static Messages lbl = (Messages) GWT.create(Messages.class);

	String name();
	String user();
	String password();
	String jdbcurl();
	String driver();
	String username();
	
	String table();
	String columnId();
	String columnName();
	String datasource();
	
	String columnValue();
	String periodicity();
	String columnDate();
	
	String columnObjective();
	String columnMax();
	String columnMin();
	
	String columnMetricAxis();
	String columnObjectiveAxis();
	String axis();
	
	String Home();
	String Add();
	String Delete();
	String Refresh();
	String Edit();
	String Metrics();
	String Save();
	String Assos();
	String Properties();
	String SignIn();
	
	String Ok();
	String Cancel();
	String AxisAssociation();
	String NoLevels();
	
	String Success();
	String AxisCreated();
	String Error();
	String AxisError();
	String DeleteLevel();
	String DeleteLevelPartOne();
	String DeleteLevelPartTwo();

	String AddEditLevel();

	String AddEditMetric();
	String ProblemSaveMetric();
	String MetricCreatedSuccess();
	String DeleteAxis();
	String DeleteAxisPartOne();
	String DeleteAxisPartTwo();
	String DeleteAxisError();
	String DeleteAxisSuccess();
	String DeleteMetric();
	String DeleteMetricPartOne();
	String DeleteMetricPartTwo();
	String DeleteMetricError();
	String DeleteMetricSuccess();
	String GetAxisError();
	String GetMetricError();
	
	String CreationDate();
	String NoMetric();
	String NoAxis();
	
	String information();
	String value();
	String description();
	String alert();
	String etl();
	String owner();
	String responsible();
	
	String security();
	
	String problemSaveTheme();
	String successSaveTheme();
	
	String addEditTheme();
	
	String deleteTheme();
	String deleteThemePartOne();
	String deleteThemePartTwo();
	
	String successSaveGroupTheme();
	
	String linkGroupTheme();
	String successSaveAxisTheme();
	String successSaveMetricTheme();
	
	String parentColumnName();
	
	String createTables();
	
	String ProblemCreateQueries();
	
	String factQueryTitle();
	String factQueryCheck();
	String ObjQueryTitle();
	String ObjQueryCheck();
	String AxisQueryTitle1();
	String AxisQueryTitle2();
	String AxisQueryCheck();
	
	String executeQueriesProblem();
	String executeQueriesSuccess();
	
	String calculation();
	
	String errorOnConnection();
	String welcome();
	
	String alertCreateError();
	String alertCreateSuccess();
	
	String columnTableSelection();
	String elementFormula();
	String databaseStructureError();
	String GetDatasourceError();
	String ProblemLoadDatasource();
	String DatasourceManager();
	String observatoryAdd();
	
	String queryErrorExec();
	
	String gatewaySelect();
	
	String formula();
	String eventType();
	
	String kaplanOne();
	String kaplanAll();
	String kaplanMaj();
	String kaplan();
	String kaplanDef();
	String linkAxisMetricTheme();
	
	String alertType();
	String alertTypeValue();
	String alertTypeMissing();
	String alertTypeState();
	String alertEvent();
	
	String missingVal();
	String missingObj();
	String missingValObj();
	String stateAbove();
	String stateEqual();
	String stateUnder();
	String mailContent();
	String selectType();
	String leftField();
	String rightField();
	String operator();
	
	String observatoryManagement();
	String observatoryAddTheme();
	String linkGroupObs();
	String linkToGroup();
	String linkToTheme();
	
	String observatories();
	String themes();
	
	String allThemes();
	String allObs();
	
	String metricValue();
	String objectiveValue();
	String aggregator();
	
	String pingSuccessful();
	String pingFailed();
	
	String levelNeeded();
	String cubeSelect();
	String testConnection();
	String linkToAMTheme();
	String isCalc();
	String recipients();
	
	String startDate();
	String endDate();
	String objectiveFormula();
	String addMetricAction();
	String maps();
	String addEditMap();
	
	String geoCol();
	
	String problemSaveObs();
	String successSaveObs();
	
	String deleteObs();
	String deleteObsPart1();
	String deleteObsPart2();
	
	String assosEdit();
	String assosDelete();
	String NoThemeLinked();
	String NoThemeLinkedMessage();
	
	String noMaps();
	String noActions();
	String noAlerts();
	
	String allTableExists();
	String showModifyQuery();
	
	String level();
	
	String browseAxis();
	
	String filterAxis();
	String existingAssociation();
	String existingAssociationMessage();
	
	String dataset();
	String TheMetadataHasBeenCreated();
}
