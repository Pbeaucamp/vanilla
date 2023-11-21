package bpm.fmloader.server.chart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.freemetrics.api.features.infos.Unit;
import bpm.freemetrics.api.manager.FactoryManager;
import bpm.freemetrics.api.manager.IManager;
import bpm.freemetrics.api.organisation.application.Application;
import bpm.freemetrics.api.organisation.group.Group;
import bpm.freemetrics.api.organisation.metrics.Metric;
import bpm.freemetrics.api.organisation.metrics.MetricValues;
import bpm.freemetrics.api.organisation.relations.appl_metric.Assoc_Application_Metric;
import bpm.freemetrics.api.security.FmUser;
import bpm.freemetrics.api.utils.Tools;

public class FreemetricHelper {

	/**
	 * 
	 * @param prop
	 *            -url -driverClassName -username -password -fmLogin -fmPassword
	 * 
	 * @return
	 */
	public static List<String> getApplicationsNames(Properties prop) throws Exception {
		FmUser user = null;
		IManager fmMgr = null;
		try {

			FactoryManager.init("", Tools.OS_TYPE_WINDOWS);
			fmMgr = FactoryManager.getManager();

			user = fmMgr.getUserByNameAndPass(prop.getProperty("fmLogin"), prop.getProperty("fmPassword"));

		} catch(Exception e) {
			e.printStackTrace();

		}

		if(user == null) {
			throw new Exception("User does not exist or bad password.");

		}
			
		List<String> result = new ArrayList<String>();

		for(Group g : fmMgr.getGroupsForUser(user.getId())) {

			for(Application a : fmMgr.getApplicationsForGroup(g.getId())) {
				result.add(a.getName());
			}
		}

		return result;
	}

	/**
	 * 
	 * @param prop
	 *            -url -driverClassName -username -password -fmLogin -fmPassword - applicationName - metricName
	 * @return
	 */
	public static List<String> getMetricNames(Properties prop) throws Exception {
		FmUser user = null;
		IManager fmMgr = null;
		try {

			FactoryManager.init("", Tools.OS_TYPE_WINDOWS);
			fmMgr = FactoryManager.getManager();

			user = fmMgr.getUserByNameAndPass(prop.getProperty("fmLogin"), prop.getProperty("fmPassword"));

		} catch(Exception e) {
			e.printStackTrace();

		}

		if(user == null) {
			throw new Exception("User does not exist or bad password.");

		}
			
		List<String> result = new ArrayList<String>();

		for(Group g : fmMgr.getGroupsForUser(user.getId())) {

			for(Application a : fmMgr.getApplicationsForGroup(g.getId())) {

				if(a.getName().equals(prop.getProperty("applicationName"))) {

					for(Assoc_Application_Metric ass : fmMgr.getAssoc_Application_MetricsByAppId(a.getId())) {

						Metric m = fmMgr.getMetricById(ass.getMetr_ID());

						if(m != null && m.getMdGlIsCompteur()) {
							result.add(m.getName());
						}

					}
					break;
				}

			}
		}

		return result;
	}

	private static final String[] metricCalculationTimeFrame = new String[] { "YEAR", "BIANNUAL", "QUARTER", "MONTH", "WEEK", "DAY", "HOUR", "MINUTE" };

	private static final int YEARLY = 0;
	private static final int BIANNUALY = 1;
	private static final int QUARTERLY = 2;
	private static final int MONTHLY = 3;
	private static final int WEEKLY = 4;
	private static final int DAYLY = 5;
	private static final int HOURLY = 6;
	private static final int MINUTELY = 7;

	/**
	 * 
	 * @param prop
	 *            -url -driverClassName -username -password -fmLogin -fmPassword - applicationName * - metricName
	 * @return
	 */
	public static List<String> getMetricValuesDate(Properties prop) throws Exception {
		FmUser user = null;
		IManager fmMgr = null;
		try {

			FactoryManager.init("", Tools.OS_TYPE_WINDOWS);
			fmMgr = FactoryManager.getManager();

			user = fmMgr.getUserByNameAndPass(prop.getProperty("fmLogin"), prop.getProperty("fmPassword"));

		} catch(Exception e) {
			e.printStackTrace();

		}

		if(user == null) {
			throw new Exception("User does not exist or bad password.");

		}
		
		List<String> result = new ArrayList<String>();

		for(Group g : fmMgr.getGroupsForUser(user.getId())) {

			for(Application a : fmMgr.getApplicationsForGroup(g.getId())) {

				if(a.getName().equals(prop.getProperty("applicationName"))) {

					for(Assoc_Application_Metric ass : fmMgr.getAssoc_Application_MetricsByAppId(a.getId())) {

						Metric m = fmMgr.getMetricById(ass.getMetr_ID());

						if(m != null && m.getName().equals(prop.get("metricName"))) {
							SimpleDateFormat sdf = null;

							if(m.getMdCalculationTimeFrame().equals(metricCalculationTimeFrame[BIANNUALY])) {
								sdf = new SimpleDateFormat("yyyy-MM-dd");
							}
							else if(m.getMdCalculationTimeFrame().equals(metricCalculationTimeFrame[DAYLY])) {
								sdf = new SimpleDateFormat("yyyy-MM-dd");
							}
							else if(m.getMdCalculationTimeFrame().equals(metricCalculationTimeFrame[HOURLY])) {
								sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							}
							else if(m.getMdCalculationTimeFrame().equals(metricCalculationTimeFrame[MONTHLY])) {
								sdf = new SimpleDateFormat("yyyy-MM");
							}
							else if(m.getMdCalculationTimeFrame().equals(metricCalculationTimeFrame[QUARTERLY])) {
								sdf = new SimpleDateFormat("yyyy-MM-dd");
							}
							else if(m.getMdCalculationTimeFrame().equals(metricCalculationTimeFrame[WEEKLY])) {
								sdf = new SimpleDateFormat("yyyy-MM-dd");
							}
							else if(m.getMdCalculationTimeFrame().equals(metricCalculationTimeFrame[YEARLY])) {
								sdf = new SimpleDateFormat("yyyy");
							}
							else if(m.getMdCalculationTimeFrame().equals(metricCalculationTimeFrame[MINUTELY])) {
								sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							}

							for(MetricValues mv : fmMgr.getValuesForAssocId(ass.getId())) {
								Date d = mv.getMvPeriodDate();

								result.add(sdf.format(d));
							}

						}

					}
					break;
				}

			}
		}

		return result;
	}

	/**
	 * 
	 * @param prop
	 *            -url -driverClassName -username -password -fmLogin -fmPassword - applicationName * - metricName
	 * @return
	 */
	public static String getMetricGaugeXml(Properties prop) throws Exception {
		FmUser user = null;
		IManager fmMgr = null;
		try {

			FactoryManager.init("", Tools.OS_TYPE_WINDOWS);
			fmMgr = FactoryManager.getManager();

			user = fmMgr.getUserByNameAndPass(prop.getProperty("fmLogin"), prop.getProperty("fmPassword"));

		} catch(Exception e) {
			e.printStackTrace();

		}

		if(user == null) {
			throw new Exception("User does not exist or bad password.");

		}

		String result = "";

		for(Group g : fmMgr.getGroupsForUser(user.getId())) {

			for(Application a : fmMgr.getApplicationsForGroup(g.getId())) {

				if(a.getName().equals(prop.getProperty("applicationName"))) {

					for(Assoc_Application_Metric ass : fmMgr.getAssoc_Application_MetricsByAppId(a.getId())) {

						Metric m = fmMgr.getMetricById(ass.getMetr_ID());

						if(m != null && m.getName().equals(prop.get("metricName"))) {

							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

							if(m.getMdCalculationTimeFrame().equals(metricCalculationTimeFrame[BIANNUALY])) {
								sdf = new SimpleDateFormat("yyyy-MM-dd");
							}
							else if(m.getMdCalculationTimeFrame().equals(metricCalculationTimeFrame[DAYLY])) {
								sdf = new SimpleDateFormat("yyyy-MM-dd");
							}
							else if(m.getMdCalculationTimeFrame().equals(metricCalculationTimeFrame[HOURLY])) {
								sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							}
							else if(m.getMdCalculationTimeFrame().equals(metricCalculationTimeFrame[MONTHLY])) {
								sdf = new SimpleDateFormat("yyyy-MM");
							}
							else if(m.getMdCalculationTimeFrame().equals(metricCalculationTimeFrame[QUARTERLY])) {
								sdf = new SimpleDateFormat("yyyy-MM-dd");
							}
							else if(m.getMdCalculationTimeFrame().equals(metricCalculationTimeFrame[WEEKLY])) {
								sdf = new SimpleDateFormat("yyyy-MM-dd");
							}
							else if(m.getMdCalculationTimeFrame().equals(metricCalculationTimeFrame[YEARLY])) {
								sdf = new SimpleDateFormat("yyyy");
							}
							else if(m.getMdCalculationTimeFrame().equals(metricCalculationTimeFrame[MINUTELY])) {
								sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							}

							for(MetricValues mv : fmMgr.getValuesForAssocId(ass.getId())) {
								Date d = mv.getMvPeriodDate();

								if(sdf.parse(prop.getProperty("metricDate")).equals(sdf.parse(d.toString()))) {
									MetricValues val = fmMgr.getObjectifsForValueAndPeriod(mv, d);

									result = generateGaugeXml(val, mv.getMvValue(), fmMgr.getUnitById(m.getMdUnitId()));

								}
							}
						}
					}
					break;
				}

			}
		}
		/**
		 * fm_metrics isComtteur column,e OBJ / Cpts
		 */
		return result;
	}

	/**
	 * 
	 * @param prop
	 *            -url -driverClassName -username -password -fmLogin -fmPassword - applicationName * - metricName
	 * @return
	 */
	public static String getMetricEvolutionXml(Properties prop, Properties chartProp) throws Exception {

		FmUser user = null;
		IManager fmMgr = null;
		try {

			FactoryManager.init("", Tools.OS_TYPE_WINDOWS);
			fmMgr = FactoryManager.getManager();

			user = fmMgr.getUserByNameAndPass(prop.getProperty("fmLogin"), prop.getProperty("fmPassword"), Boolean.parseBoolean(prop.getProperty("isencrypted")));

		} catch(Exception e) {
			e.printStackTrace();

		}

		if(user == null) {
			throw new Exception("User does not exist or bad password.");

		}

		String result = "";

		Assoc_Application_Metric ass = fmMgr.getAssoc_Territoire_Metric_ById(Integer.parseInt(prop.getProperty("assocId")));

		Metric m = fmMgr.getMetricById(ass.getMetr_ID());

		if(m != null && m.getName().equals(prop.get("metricName"))) {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");

			if(m.getMdCalculationTimeFrame().equals(metricCalculationTimeFrame[BIANNUALY])) {
				sdf = new SimpleDateFormat("yyyy-MMM");
			}
			else if(m.getMdCalculationTimeFrame().equals(metricCalculationTimeFrame[DAYLY])) {
				sdf = new SimpleDateFormat("yyyy-MMM-dd");
			}
			else if(m.getMdCalculationTimeFrame().equals(metricCalculationTimeFrame[HOURLY])) {
				sdf = new SimpleDateFormat("yyyy-MMM-dd");
			}
			else if(m.getMdCalculationTimeFrame().equals(metricCalculationTimeFrame[MONTHLY])) {
				sdf = new SimpleDateFormat("yyyy-MMM");
			}
			else if(m.getMdCalculationTimeFrame().equals(metricCalculationTimeFrame[QUARTERLY])) {
				sdf = new SimpleDateFormat("yyyy-MMM");
			}
			else if(m.getMdCalculationTimeFrame().equals(metricCalculationTimeFrame[WEEKLY])) {
				sdf = new SimpleDateFormat("yyyy-MMM-dd");
			}
			else if(m.getMdCalculationTimeFrame().equals(metricCalculationTimeFrame[YEARLY])) {
				sdf = new SimpleDateFormat("yyyy");
			}
			else if(m.getMdCalculationTimeFrame().equals(metricCalculationTimeFrame[MINUTELY])) {
				sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
			}

			List<String> dates = new ArrayList<String>();
			List<EvolutionDatas> datas = new ArrayList<EvolutionDatas>();

			for(MetricValues mv : fmMgr.getValuesForAssocId(ass.getId())) {
				Date d = mv.getMvPeriodDate();

				Date minDate = sdf.parse(prop.getProperty("metricDateMin"));
				Date maxDate = sdf.parse(prop.getProperty("metricDateMax"));

				if(minDate.compareTo(sdf.parse(sdf.format(d))) <= 0 && maxDate.compareTo(sdf.parse(sdf.format(d))) >= 0) {

					dates.add(sdf.format(d));

					MetricValues val = fmMgr.getObjectifsForValueAndPeriod(mv, d);
					if(val != null && !Boolean.parseBoolean(prop.get("iscompteur").toString())) {
						EvolutionDatas dt = new EvolutionDatas();
						dt.val = mv.getMvValue();
						if(val != null) {
							dt.min = val.getMvMinValue();
							dt.max = val.getMvMaxValue();
							dt.obj = val.getMvGlObjectif();
						}
						datas.add(dt);
					}
					else if(Boolean.parseBoolean(prop.get("iscompteur").toString()) || val == null) {
						EvolutionDatas dt = new EvolutionDatas();
						dt.val = mv.getMvValue();
						datas.add(dt);
					}
					else {
						datas.add(null);
					}

				}
			}

			chartProp.put("iscompteur", prop.get("iscompteur"));

			return generateEvolutionXml(m.getName(), dates, datas, "Title", null, chartProp);
		}
		/**
		 * fm_metrics isComtteur column,e OBJ / Cpts
		 */
		return result;
	}

	/**
	 * minimum = MvMinValue maximum = MvMaxValue seuilMin = MvGlValeurSeuilMini seuilMaxi = MvGlValeurSeuilMaxi tolerance = (target*tolerance)/100
	 * 
	 * @param obj
	 * @param mvValue
	 * @param unit
	 * @return
	 */
	private static String generateGaugeXml(MetricValues obj, float mvValue, Unit unit) {
		float target = (obj.getMvGlObjectif() != null) ? obj.getMvGlObjectif() : 0;
		float min = (obj.getMvMinValue() != null) ? obj.getMvMinValue() : 0;
		float max = (obj.getMvMaxValue() != null) ? obj.getMvMaxValue() : 0;

		float tolerance = (obj.getMvTolerance() != null) && obj.getMvTolerance() > 0 ? obj.getMvTolerance() : 1;

		float t = (target * tolerance) / 100;
		float value = mvValue;

		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("chart");

		// create general options
		root.addAttribute("lowerLimit", min + "");
		root.addAttribute("upperLimit", max + "");
		root.addAttribute("gaugeStartAngle", "180");
		root.addAttribute("gaugeEndAngle", "0");
		root.addAttribute("palette", "1");
		root.addAttribute("showTickMarks", "0");
		root.addAttribute("showTickValues", "0");
		root.addAttribute("trendValueDistance", "25");

		String unitF = "";

		if(unitF != null) {
			unitF = unit.getName();
		}

		Element trends = root.addElement("trendpoints");

		Element trendPoint1 = trends.addElement("point");
		trendPoint1.addAttribute("startValue", obj.getMvGlValeurSeuilMini() + "");
		trendPoint1.addAttribute("displayValue", obj.getMvGlValeurSeuilMini() + " " + unitF);
		trendPoint1.addAttribute("color", "666666");
		trendPoint1.addAttribute("useMarker", "1");
		trendPoint1.addAttribute("markerColor", "F1f1f1");
		trendPoint1.addAttribute("markerBorderColor", "666666");
		trendPoint1.addAttribute("markerTooltext", obj.getMvGlValeurSeuilMini() + " " + unitF);

		Element trendPoint2 = trends.addElement("point");
		trendPoint2.addAttribute("startValue", (target - t) + "" + "");
		trendPoint2.addAttribute("displayValue", (target - t) + " " + unitF);
		trendPoint2.addAttribute("color", "666666");
		trendPoint2.addAttribute("useMarker", "1");
		trendPoint2.addAttribute("markerColor", "F1f1f1");
		trendPoint2.addAttribute("markerBorderColor", "666666");
		trendPoint2.addAttribute("markerTooltext", (target - t) + " " + unitF);

		Element trendPoint3 = trends.addElement("point");
		trendPoint3.addAttribute("startValue", (target + t) + "" + "");
		trendPoint3.addAttribute("displayValue", (target + t) + " " + unitF);
		trendPoint3.addAttribute("color", "666666");
		trendPoint3.addAttribute("useMarker", "1");
		trendPoint3.addAttribute("markerColor", "F1f1f1");
		trendPoint3.addAttribute("markerBorderColor", "666666");
		trendPoint3.addAttribute("markerTooltext", (target + t) + " " + unitF);

		Element trendPoint4 = trends.addElement("point");
		trendPoint4.addAttribute("startValue", obj.getMvGlValeurSeuilMaxi() + "");
		trendPoint4.addAttribute("displayValue", obj.getMvGlValeurSeuilMaxi() + " " + unitF);
		trendPoint4.addAttribute("color", "666666");
		trendPoint4.addAttribute("useMarker", "1");
		trendPoint4.addAttribute("markerColor", "F1f1f1");
		trendPoint4.addAttribute("markerBorderColor", "666666");
		trendPoint4.addAttribute("markerTooltext", obj.getMvGlValeurSeuilMaxi() + " " + unitF);

		// create colors
		Element colorRange = root.addElement("colorRange");

		// Calcul de la zone a afficher
		Element red1 = colorRange.addElement("color");
		red1.addAttribute("minValue", min + "");
		red1.addAttribute("maxValue", obj.getMvGlValeurSeuilMini() + "");
		red1.addAttribute("code", "FF654F");

		Element yellow1 = colorRange.addElement("color");
		yellow1.addAttribute("minValue", obj.getMvGlValeurSeuilMini() + "");
		yellow1.addAttribute("maxValue", (target - t) + "");
		yellow1.addAttribute("code", "F6BD0F");

		Element green = colorRange.addElement("color");
		green.addAttribute("minValue", (target - t) + "");
		green.addAttribute("maxValue", (target + t) + "");
		green.addAttribute("code", "8BBA00");

		Element yellow2 = colorRange.addElement("color");
		yellow2.addAttribute("minValue", (target + t) + "");
		yellow2.addAttribute("maxValue", obj.getMvGlValeurSeuilMaxi() + "");
		yellow2.addAttribute("code", "F6BD0F");

		Element red2 = colorRange.addElement("color");
		red2.addAttribute("minValue", obj.getMvGlValeurSeuilMaxi() + "");
		red2.addAttribute("maxValue", max + "");
		red2.addAttribute("code", "FF654F");

		Element dial = root.addElement("dials").addElement("dial");
		dial.addAttribute("value", value + "");
		dial.addAttribute("rearExtension", "10");
		dial.addAttribute("showValue", "1");

		return root.asXML().replace("\"", "'");
	}

	public static class EvolutionDatas {
		Float min, max, val, obj;
	}

	private static String generateEvolutionXml(String metricName, List<String> dates, List<EvolutionDatas> datas, String title, Unit unit, Properties chartProp) {
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("graph");
		root.addAttribute("rotateNames", "1");

		Enumeration en = chartProp.keys();

		while(en.hasMoreElements()) {

			String key = (String) en.nextElement();
			if(!chartProp.getProperty(key).equals("") && !chartProp.getProperty(key).equalsIgnoreCase("null")) {
				if(!key.equals("drillDown") && !key.equals("iscompteur")) {
					root.addAttribute(key, chartProp.getProperty(key));
				}
			}
		}

		root.addAttribute("palette", "1");

		root.addAttribute("yAxisName", metricName.replace("'", "&apos;"));

		Element categories = root.addElement("categories");

		for(String s : dates) {
			categories.addElement("category").addAttribute("name", s);
		}

		Element dataSetValue = root.addElement("dataset");
		if(!Boolean.parseBoolean(chartProp.get("iscompteur").toString())) {
			dataSetValue.addAttribute("seriesName", "Value");
		}

		dataSetValue.addAttribute("color", "F6BD0F");

		for(EvolutionDatas e : datas) {
			if(e == null || e.val == null) {
				dataSetValue.addElement("set");
			}
			else {
				Element set = dataSetValue.addElement("set").addAttribute("value", e.val + "");
			}

		}

		if(!Boolean.parseBoolean(chartProp.get("iscompteur").toString())) {

			Element dataSetMax = root.addElement("dataset");
			dataSetMax.addAttribute("seriesName", "Maximum");
			dataSetMax.addAttribute("color", "AFD8F8");
			for(EvolutionDatas e : datas) {
				if(e == null || e.max == null) {
					dataSetMax.addElement("set");
				}
				else {
					dataSetMax.addElement("set").addAttribute("value", e.max + "");

				}

			}

			Element dataSetMin = root.addElement("dataset");
			dataSetMin.addAttribute("seriesName", "Minimum");
			dataSetMin.addAttribute("color", "8BBA00");
			for(EvolutionDatas e : datas) {
				if(e == null || e.min == null) {
					dataSetMin.addElement("set");
				}
				else {
					dataSetMin.addElement("set").addAttribute("value", e.min + "");
				}

			}

			Element dataSetObj = root.addElement("dataset");
			dataSetObj.addAttribute("seriesName", "Target");
			dataSetObj.addAttribute("color", "D64646");
			for(EvolutionDatas e : datas) {
				if(e == null || e.obj == null) {
					dataSetObj.addElement("set");
				}
				else {
					dataSetObj.addElement("set").addAttribute("value", e.obj + "");
				}

			}
		}

		return root.asXML().replace("\"", "'");
	}

	public static void main(String[] args) {
		FactoryManager.init("C:\\BPM\\fmworkspace\\FMLoaderWeb\\war", Tools.OS_TYPE_WINDOWS);
		java.util.Properties fmMetricProp = new java.util.Properties();
		fmMetricProp.setProperty("driverClassName", "com.mysql.jdbc.Driver");
		fmMetricProp.setProperty("url", "jdbc:mysql://localhost:3306/yelo");
		fmMetricProp.setProperty("username", "biplatform");
		fmMetricProp.setProperty("password", "biplatform");
		fmMetricProp.setProperty("applicationName", "BUS_1");
		fmMetricProp.setProperty("metricName", "Frequentation");
		fmMetricProp.setProperty("iscompteur", "true");
		fmMetricProp.setProperty("metricDate", "2010-janv.-13 10:20:30");
		fmMetricProp.setProperty("metricDateMin", "2009-juil.-01 10:20:30");
		fmMetricProp.setProperty("metricDateMax", "2009-déc.-31 10:20:30");
		fmMetricProp.setProperty("fmLogin", "system");
		fmMetricProp.setProperty("fmPassword", "system");
		try {
			String zz = getMetricEvolutionXml(fmMetricProp, new Properties());

			System.out.println(zz);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
