package bpm.freematrix.reborn.web.client.i18n;

import com.google.gwt.core.client.GWT;

public interface LabelConstants extends com.google.gwt.i18n.client.Constants{

	public static LabelConstants lblCnst = (LabelConstants) GWT.create(LabelConstants.class);
	
	String English();
	String French();
	String RememberMe();
	String SignIn();
	String UserName();
	String Password();
	String FreeMetrics();
	String axis();
	String item();
	String value();
	String addComment();
	String name();
	String dateColumn();
	String objectives();
	String periodicity();
	String tableName();
	String valueColumn();
	String axisInfos();
	String objInfos();
	String generalInfo();
	String objColName();
	String maxCol();
	String minCol();
	
	String Tendancy();
	String Health();
	String Metric();
	String Date();
	String Maximum();
	String Minimum();
	String Objective();
	String Value();
	String changeDate();
	
	String Home();
	String Settings();
	String Search();
	String Watchlist();
	String Alert();
	String metrics();
	String groups();
	String observatories();
	String themes();
	
	public final String COOKIE_EMAIL = "cookie_email";
	public final String LOCALE = "locale";
	public final String LOCALE_ENGLISH = "en";
	public final String LOCALE_FRENCH = "fr";

	String level();
	String aggregator();
	String changeChartType();
	
	String pieChart();
	String donutChart();
	String lineChart();
	String barChart();
	String columnChart();
	
	String selectMetricFirst();
	
	String responsible();
	String type();
	String date();
	String dateRes();
	
	String missingValue();
	String missingObj();
	String missingBoth();
	
	String stateOk();
	String stateEqual();
	String stateNotOK();
	
	String noValues();
	String resolveAlert();
	String Ok();
	String Cancel();
	
	String comment();
	String direction();
	
	String metricAxisNotLinked();
	
	String allObs();
	String allThemes();
	String jumpToYear();
	String solveAlert();
	
	String previous();
	String next();
	
	String noAlerts();
	String chart();
	String map();
	String gauge();
	
	String reduce();
	String restore();
	String repartition();
	String Yearly();
	String Biannual();
	String Quaterly();
	String Monthly();
	String Weekly();
	String Daily();
	String Hourly();
	String Minute();
	String Unknown();
	String ShowCalendar();
	String CalendarView();
	String Calendar();
	String TheCalendarViewIsNotAvailableForThisPeriodicity();
}
