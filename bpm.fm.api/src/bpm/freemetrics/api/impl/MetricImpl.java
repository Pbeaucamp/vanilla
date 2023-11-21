/**
 * 
 */
package bpm.freemetrics.api.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.freemetrics.api.manager.SQLManager;
import bpm.freemetrics.api.organisation.application.Application;
import bpm.freemetrics.api.organisation.metrics.AssocCompteurIndicateur;
import bpm.freemetrics.api.organisation.metrics.Assoc_Compteur_IndicateurManager;
import bpm.freemetrics.api.organisation.metrics.Metric;
import bpm.freemetrics.api.organisation.metrics.MetricInteraction;
import bpm.freemetrics.api.organisation.metrics.MetricInteractionManager;
import bpm.freemetrics.api.organisation.metrics.MetricManager;
import bpm.freemetrics.api.organisation.metrics.MetricValues;
import bpm.freemetrics.api.organisation.metrics.MetricValuesManager;
import bpm.freemetrics.api.organisation.relations.appl_metric.Assoc_Application_Metric;
import bpm.freemetrics.api.organisation.relations.appl_metric.Assoc_Application_MetricManager;
import bpm.freemetrics.api.utils.IConstants;
import bpm.freemetrics.api.utils.Tools;

/**
 * @author Belgarde
 * 
 */
public class MetricImpl {

	public static List<Metric> getMetricsForApplicationId(int id, int filter, MetricManager metrMgr, Assoc_Application_MetricManager aTMMgr) {
		List<Metric> metrics = new ArrayList<Metric>();

		List<Metric> lstMetricIds = aTMMgr.getMetricsForApplicationId(id);

		for(Metric tmp : lstMetricIds) {

			// Metric tmp = metrMgr.getMetricById(metrIds);

			switch(filter) {
				case IConstants.METRIC_FILTER_INDICATEUR:
					if(tmp != null && !tmp.getMdGlIsCompteur()) {
						metrics.add(tmp);
					}
					break;

				case IConstants.METRIC_FILTER_COMPTEUR:
					if(tmp != null && tmp.getMdGlIsCompteur()) {
						metrics.add(tmp);
					}
					break;
				default:
					if(tmp != null) {
						metrics.add(tmp);
					}

					break;
			}
		}
		return metrics;
	}

	public static List<Metric> getMetricsForAppAndTheme(int appId, int themeId, MetricManager metrMgr, Assoc_Application_MetricManager aTMMgr) {

		List<Metric> metrs = getMetricsForApplicationId(appId, IConstants.METRIC_FILTER_COMPTEUR, metrMgr, aTMMgr);
		List<Metric> lstmForAT = new ArrayList<Metric>();

		for(Metric m : metrs) {

			if(m != null && m.getMdGlThemeId() == themeId) {
				lstmForAT.add(m);
			}
		}

		return lstmForAT;
	}

	public static List<Metric> getMetricsForAppThemeAndSubTheme(int appId, int thmId, int sthmId, MetricManager metrMgr, Assoc_Application_MetricManager aTMMgr) {
		List<Metric> metrs = getMetricsForAppAndTheme(appId, thmId, metrMgr, aTMMgr);

		List<Metric> lstmForAT = new ArrayList<Metric>();

		for(Metric m : metrs) {

			if(m != null && m.getMdGlSubThemeId() != null && m.getMdGlSubThemeId() == sthmId) {
				lstmForAT.add(m);
			}
		}

		return lstmForAT;
	}

	public static List<Metric> getAllowedMetricsForUser(int userId, int filter, MetricManager metrMgr) {
		List<Metric> res = new ArrayList<Metric>();

		switch(filter) {
			case IConstants.METRIC_FILTER_INDICATEUR:
				res = metrMgr.getIndicateurs();
				break;

			case IConstants.METRIC_FILTER_COMPTEUR:
				res = metrMgr.getCompteurs();
				break;
			default:
				res = metrMgr.getMetrics();

				break;
		}

		return res;
	}

	/**
	 * Return a value for each existing period
	 * 
	 * @param assos
	 * @param mValsMgr
	 * @param metrMgr
	 * @param sqlManager 
	 * @param appId 
	 * @param endDate 
	 * @param startDate 
	 * @return
	 */
	public static List<MetricValues> getValuesForAssocIds(List<Assoc_Application_Metric> assos, MetricValuesManager mValsMgr, MetricManager metrMgr, SQLManager sqlManager, int appId, Date startDate, Date endDate, String datePattern) {
		List<MetricValues> values = new ArrayList<MetricValues>();

		Metric met = null;
		HashMap<Date, MetricValues> valueHash = new HashMap<Date, MetricValues>();
		for(Assoc_Application_Metric assoc : assos) {
			if(assoc != null) {

				met = metrMgr.getMetricById(assoc.getMetr_ID());

				//set metric values
				setMetricValues(valueHash, assoc, mValsMgr, met, startDate, endDate, datePattern);

				
			}
		}
		
		Metric ind = metrMgr.getAssciatedIndicateur(met.getId()).get(0);
		for(Assoc_Application_Metric assoc : sqlManager.getAssoMetricAppByMetricAndAppIds(appId, ind.getId())) {
			//set indicator values
			setMetricValues(valueHash, assoc, mValsMgr, ind, startDate, endDate, datePattern);
		}
		

		for(Date d : valueHash.keySet()) {
			MetricValues val = valueHash.get(d);
			val.setMvPeriodDate(d);
			values.add(val);
		}

		Collections.sort(values, new Comparator<MetricValues>() {
			@Override
			public int compare(MetricValues o1, MetricValues o2) {
				return o1.getMvPeriodDate().compareTo(o2.getMvPeriodDate());
			}
		});

		return values;
	}

	private static void setMetricValues(HashMap<Date, MetricValues> valueHash, Assoc_Application_Metric assoc, MetricValuesManager mValsMgr, Metric met, Date startDate, Date endDate, String datePattern) {
		List<MetricValues> tmp = mValsMgr.getMetricValuesByAssocId(assoc.getId(), startDate, endDate, datePattern);

		if(tmp != null && !tmp.isEmpty()) {
			for(MetricValues metricValues : tmp) {
				if(metricValues != null && metricValues.getMvPeriodDate() != null) {

					Date d = metricValues.getMvPeriodDate();
					boolean finded = false;
					for(Date existingDate : valueHash.keySet()) {

						if(Tools.isInSamePeriod(d, existingDate, met.getMdCalculationTimeFrame())) {

							MetricValues v = valueHash.get(existingDate);
							if(metricValues.getMvValue() != null) {
								v.setMvValue(v.getMvValue() + metricValues.getMvValue());
							}
							if(metricValues.getMvGlObjectif() != null) {
								v.setMvGlObjectif(v.getMvGlObjectif() + metricValues.getMvGlObjectif());
							}
							if(metricValues.getMvMaxValue() != null) {
								v.setMvMaxValue(v.getMvMaxValue() + metricValues.getMvMaxValue());
							}
							if(metricValues.getMvMinValue() != null) {
								v.setMvMinValue(v.getMvMinValue() + metricValues.getMvMinValue());
							}
							if(metricValues.getMvGlValeurSeuilMaxi() != null) {
								v.setMvGlValeurSeuilMaxi(v.getMvGlValeurSeuilMaxi() + metricValues.getMvGlValeurSeuilMaxi());
							}
							if(metricValues.getMvGlValeurSeuilMini() != null) {
								v.setMvGlValeurSeuilMini(v.getMvGlValeurSeuilMini() + metricValues.getMvGlValeurSeuilMini());
							}

							finded = true;
							break;
						}

					}
					if(!finded) {
						MetricValues v = new MetricValues();
						if(metricValues.getMvValue() != null) {
							v.setMvValue(metricValues.getMvValue());
						}
						else {
							v.setMvValue(0f);
						}
						if(metricValues.getMvGlObjectif() != null) {
							v.setMvGlObjectif(metricValues.getMvGlObjectif());
						}
						else {
							v.setMvGlObjectif(0f);
						}
						if(metricValues.getMvMaxValue() != null) {
							v.setMvMaxValue(metricValues.getMvMaxValue());
						}
						else {
							v.setMvMaxValue(0f);
						}
						if(metricValues.getMvMinValue() != null) {
							v.setMvMinValue(metricValues.getMvMinValue());
						}
						else {
							v.setMvMinValue(0f);
						}
						if(metricValues.getMvGlValeurSeuilMaxi() != null) {
							v.setMvGlValeurSeuilMaxi(metricValues.getMvGlValeurSeuilMaxi());
						}
						else {
							v.setMvGlValeurSeuilMaxi(0f);
						}
						if(metricValues.getMvGlValeurSeuilMini() != null) {
							v.setMvGlValeurSeuilMini(metricValues.getMvGlValeurSeuilMini());
						}
						else {
							v.setMvGlValeurSeuilMini(0f);
						}
						v.setMvTolerance(metricValues.getMvTolerance() != null ? metricValues.getMvTolerance() : 0);

						valueHash.put(d, v);
					}

				}
			}
		}
		
	}

	/**
	 * 
	 * @param appId
	 * @param metrId
	 * @param dper
	 * @param aTMMgr
	 * @param mValsMgr
	 * @param metrMgr
	 * @return
	 */
	/*
	 * TODO Modifier cette methode afin d'avoir une requête en base qui filtre directement en fonction de la date
	 */
	public static List<MetricValues> getValuesForMetricAppPeridDate(List<Assoc_Application_Metric> assos, Date dper, Assoc_Application_MetricManager aTMMgr, MetricValuesManager mValsMgr, MetricManager metrMgr) {

		Calendar cperiod = Calendar.getInstance();
		cperiod.setTime(dper);

		List<MetricValues> values = new ArrayList<MetricValues>();

		boolean isCompteur = true;

		for(Assoc_Application_Metric assoc : assos) {
			if(assoc != null) {

				Metric met = metrMgr.getMetricById(assoc.getMetr_ID());
				isCompteur = met.getMdGlIsCompteur();

				String timeFrame = met.getMdCalculationTimeFrame();

				List<MetricValues> tmp = mValsMgr.getMetricValuesByAssocId(assoc.getId());

				if(tmp != null && !tmp.isEmpty()) {
					for(MetricValues metricValues : tmp) {
						if(metricValues != null && metricValues.getMvPeriodDate() != null) {

							Calendar datePer = Calendar.getInstance();
							datePer.setTime(metricValues.getMvPeriodDate());

							if(timeFrame.trim().equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_YEARLY].trim())) {

								if(datePer.get(Calendar.YEAR) == cperiod.get(Calendar.YEAR)) {
									values.add(metricValues);
								}

							}
							else if(timeFrame.trim().equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_BIANNUAL].trim()) || timeFrame.trim().equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_QUARTERLY].trim()) || timeFrame.trim().equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_MONTHLY].trim())) {

								if(datePer.get(Calendar.YEAR) == cperiod.get(Calendar.YEAR) && datePer.get(Calendar.MONTH) == cperiod.get(Calendar.MONTH)) {
									values.add(metricValues);
								}

							}
							else if(timeFrame.trim().equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_WEEKLY].trim()) || timeFrame.trim().equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_DAYLY].trim())) {

								if(datePer.get(Calendar.YEAR) == cperiod.get(Calendar.YEAR) && datePer.get(Calendar.MONTH) == cperiod.get(Calendar.MONTH) && datePer.get(Calendar.DATE) == cperiod.get(Calendar.DATE)) {
									values.add(metricValues);
								}

							}
							else if(timeFrame.trim().equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_HOURLY].trim())) {

								if(datePer.get(Calendar.YEAR) == cperiod.get(Calendar.YEAR) && datePer.get(Calendar.MONTH) == cperiod.get(Calendar.MONTH) && datePer.get(Calendar.DATE) == cperiod.get(Calendar.DATE) && datePer.get(Calendar.HOUR_OF_DAY) == cperiod.get(Calendar.HOUR_OF_DAY)) {
									values.add(metricValues);
								}

							}
							else if(timeFrame.trim().equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_MINUTLY].trim())) {

								if(datePer.get(Calendar.YEAR) == cperiod.get(Calendar.YEAR) && datePer.get(Calendar.MONTH) == cperiod.get(Calendar.MONTH) && datePer.get(Calendar.DATE) == cperiod.get(Calendar.DATE) && datePer.get(Calendar.HOUR_OF_DAY) == cperiod.get(Calendar.HOUR_OF_DAY) && datePer.get(Calendar.MINUTE) == cperiod.get(Calendar.MINUTE)) {
									values.add(metricValues);
								}

							}
							else if(timeFrame.trim().equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_SECONDLY].trim())) {

								if(datePer.get(Calendar.YEAR) == cperiod.get(Calendar.YEAR) && datePer.get(Calendar.MONTH) == cperiod.get(Calendar.MONTH) && datePer.get(Calendar.DATE) == cperiod.get(Calendar.DATE) && datePer.get(Calendar.HOUR_OF_DAY) == cperiod.get(Calendar.HOUR_OF_DAY) && datePer.get(Calendar.MINUTE) == cperiod.get(Calendar.MINUTE) && datePer.get(Calendar.SECOND) == cperiod.get(Calendar.SECOND)) {
									values.add(metricValues);
								}

							}
						}
					}
				}
			}
		}

		MetricValues val = null;

		if(isCompteur) {
			val = calculMetricVal(values);
			val.setMvPeriodDate(dper);
		}
		else {
			val = calculMetricIndVal(values);
			val.setMvPeriodDate(dper);
		}

		values.clear();

		if(val.getMvPeriodDate() != null) {
			values.add(val);
		}

		return values;
	}

	private static MetricValues calculMetricIndVal(List<MetricValues> indValues) {
		MetricValues result = new MetricValues();

		Float obj = null;
		Float maxSeuil = null;
		Float minSeuil = null;
		Float maxVal = null;
		Float minVal = null;

		for(MetricValues val : indValues) {
			if(obj == null) {
				obj = 0f;
			}
			if(val.getMvGlObjectif() != null) {
				obj += val.getMvGlObjectif();
			}

			if(maxSeuil == null) {
				maxSeuil = 0f;
			}
			if(val.getMvGlValeurSeuilMaxi() != null) {
				maxSeuil += val.getMvGlValeurSeuilMaxi();
			}

			if(minSeuil == null) {
				minSeuil = 0f;
			}
			if(val.getMvGlValeurSeuilMini() != null) {
				minSeuil += val.getMvGlValeurSeuilMini();
			}

			if(maxVal == null) {
				maxVal = 0f;
			}
			if(val.getMvMaxValue() != null) {
				maxVal += val.getMvMaxValue();
			}

			if(minVal == null) {
				minVal = 0f;
			}
			if(val.getMvMinValue() != null) {
				minVal += val.getMvMinValue();
			}
		}

		result.setMvGlObjectif(obj);
		result.setMvGlValeurSeuilMaxi(maxSeuil);
		result.setMvGlValeurSeuilMini(minSeuil);
		result.setMvMaxValue(maxVal);
		result.setMvMinValue(minVal);

		return result;
	}

	private static MetricValues calculMetricVal(List<MetricValues> values) {
		MetricValues result = new MetricValues();

		Float resVal = null;

		for(MetricValues val : values) {
			if(resVal == null) {
				resVal = 0f;
			}
			if(val.getMvValue() != null) {
				resVal += val.getMvValue();
			}
		}

		result.setMvValue(resVal);

		return result;
	}

	public static List<MetricValues> getObjectifsForValueWithSamePeriod(MetricValues value, Assoc_Application_MetricManager aTMMgr, MetricValuesManager mValsMgr, Assoc_Compteur_IndicateurManager aCIMgr, MetricManager metrMgr) {

		Date period = value.getMvPeriodDate();

		Assoc_Application_Metric assoc = aTMMgr.getAssoc_Territoire_MetricById(value.getMvGlAssoc_ID());

		List<MetricValues> values = new ArrayList<MetricValues>();
		if(assoc != null) {

			values.add(getObjectifForMetricForPeriode(assoc.getApp_ID(), assoc.getMetr_ID(), period, mValsMgr, aTMMgr, aCIMgr, metrMgr));

			// Assoc_Compteur_Indicateur assocCI = aCIMgr.getAssoc_Compteur_IndicateurByComptId(assoc.getMetr_ID());

			// if(assocCI != null ){

			// assoc = aTMMgr.getAssocForMetrIdAndAppId(assocCI.getIndic_ID(), assoc.getApp_ID());
			// if(assoc != null){

			// List<MetricValues> tmp = mValsMgr.getMetricValuesByAssocId(assoc.getId());

			// if(tmp != null && !tmp.isEmpty()){
			// for (MetricValues metricValues : tmp) {
			// if(metricValues != null && period != null && metricValues.getMvPeriodDate() != null
			// && metricValues.getMvPeriodDate().compareTo(period)==0){
			// values.add(metricValues);
			// }
			// }
			// }
			// }
			// }
		}
		return values;
	}

	public static MetricValues getPreviousMetricValue(MetricValues value, String timeFrame, MetricValuesManager mValsMgr) {

		Date prevoiusPeriod = Tools.computePreviousDateForPeriodicity(value.getMvPeriodDate(), timeFrame);

		List<MetricValues> tmp = mValsMgr.getMetricValuesByAssocId(value.getMvGlAssoc_ID());

		if(tmp != null && !tmp.isEmpty()) {
			return Tools.getCorrespondingMetricValue(prevoiusPeriod, tmp, value.getId(), timeFrame);
		}
		return null;
	}

	public static MetricValues getMetricValueAtPeriod(MetricValues values, Date period, MetricValuesManager mValsMgr, Assoc_Application_MetricManager aTMMgr, MetricManager metrMgr) {

		String timeFrame = "";

		if(values.getMvGlAssoc_ID() != null) {

			Assoc_Application_Metric assoc = aTMMgr.getAssoc_Territoire_MetricById(values.getMvGlAssoc_ID());

			if(assoc != null) {

				Metric met = metrMgr.getMetricById(assoc.getMetr_ID());

				if(met != null && !met.getMdGlIsCompteur() && values.getMvTolerance() != null && Tools.isNumeric(met.getMdCustom1())) {
					timeFrame = met.getMdCalculationTimeFrame();
				}

			}
		}

		List<MetricValues> tmp = mValsMgr.getMetricValuesByAssocId(values.getMvGlAssoc_ID());

		if(tmp != null && !tmp.isEmpty()) {
			return Tools.getCorrespondingMetricValue(period, tmp, values.getId(), timeFrame);
		}
		return null;
	}

	private static void checkMetricsTrends(Assoc_Application_Metric assoc, MetricValues value, MetricValues previous, MetricValuesManager mValsMgr, MetricManager metrMgr, Assoc_Application_MetricManager aTMMgr, Assoc_Compteur_IndicateurManager aCIMgr) {

		if(assoc != null) {

			Metric metr = metrMgr.getMetricById(assoc.getMetr_ID());

			if(metr != null) {

				// String timeFrame = metr.getMdCalculationTimeFrame();
				MetricValues objectif = getObjectifsForValueWithSamePeriod(value, value.getMvPeriodDate(), aTMMgr, mValsMgr, aCIMgr, metrMgr);

				if(objectif != null) {

					objectif.setMvStatus(Tools.computeStatus(value, objectif));

					if(metr.getMdGlIsCompteur()) {

						value.setMvTrendOrder(Tools.computeTrendOrder(value, objectif));
						value.setMvTrendDate(Tools.computeTrend(value, previous, objectif));
						value.setMvStatus(Tools.computeStatus(value, objectif));

						List<MetricValues> vals = mValsMgr.getMetricValuesByAssocId(assoc.getId());

						Date period = value.getMvPeriodDate();

						Date dYear = Tools.computePreviousDateForPeriodicity(period, IConstants.PERIODS[IConstants.PERIOD_YEARLY]);
						Date dSemiyear = Tools.computePreviousDateForPeriodicity(period, IConstants.PERIODS[IConstants.PERIOD_BIANNUAL]);
						Date dQuarter = Tools.computePreviousDateForPeriodicity(period, IConstants.PERIODS[IConstants.PERIOD_QUARTERLY]);
						Date dMonth = Tools.computePreviousDateForPeriodicity(period, IConstants.PERIODS[IConstants.PERIOD_MONTHLY]);
						Date dWeek = Tools.computePreviousDateForPeriodicity(period, IConstants.PERIODS[IConstants.PERIOD_WEEKLY]);
						Date dDay = Tools.computePreviousDateForPeriodicity(period, IConstants.PERIODS[IConstants.PERIOD_DAYLY]);

						Calendar cperiod = Calendar.getInstance();
						Calendar datePer = Calendar.getInstance();

						for(MetricValues pVal : vals) {

							if(pVal != null && pVal.getId() != value.getId() && pVal.getMvPeriodDate() != null) {

								Date refPeriod = pVal.getMvPeriodDate();

								cperiod.setTime(refPeriod);

								datePer.setTime(dYear);

								if(datePer.get(Calendar.YEAR) == cperiod.get(Calendar.YEAR)) {
									value.setMvTrendYear(Tools.computeTrend(value, pVal, objectif));
								}

								datePer.setTime(dSemiyear);

								if(datePer.get(Calendar.YEAR) == cperiod.get(Calendar.YEAR) && datePer.get(Calendar.MONTH) == cperiod.get(Calendar.MONTH)) {
									value.setMvTrendSemiYear(Tools.computeTrend(value, pVal, objectif));
								}

								datePer.setTime(dQuarter);

								if(datePer.get(Calendar.YEAR) == cperiod.get(Calendar.YEAR) && datePer.get(Calendar.MONTH) == cperiod.get(Calendar.MONTH)) {
									value.setMvTrendQuarter(Tools.computeTrend(value, pVal, objectif));
								}

								datePer.setTime(dMonth);

								if(datePer.get(Calendar.YEAR) == cperiod.get(Calendar.YEAR) && datePer.get(Calendar.MONTH) == cperiod.get(Calendar.MONTH)) {
									value.setMvTrendMonth(Tools.computeTrend(value, pVal, objectif));
								}
								datePer.setTime(dWeek);

								if(datePer.get(Calendar.YEAR) == cperiod.get(Calendar.YEAR) && datePer.get(Calendar.MONTH) == cperiod.get(Calendar.MONTH) && datePer.get(Calendar.DATE) == cperiod.get(Calendar.DATE)) {
									value.setMvTrendDay(Tools.computeTrend(value, pVal, objectif));
								}

								datePer.setTime(dDay);

								if(datePer.get(Calendar.YEAR) == cperiod.get(Calendar.YEAR) && datePer.get(Calendar.MONTH) == cperiod.get(Calendar.MONTH) && datePer.get(Calendar.DATE) == cperiod.get(Calendar.DATE)) {
									value.setMvTrendDay(Tools.computeTrend(value, pVal, objectif));
								}
							}
						}

						try {
							mValsMgr.updateMetricValues(value);
							mValsMgr.updateMetricValues(objectif);
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public static int addMetricValue(MetricValues val, MetricValuesManager mValsMgr, MetricManager metrMgr, Assoc_Application_MetricManager aTMMgr, Assoc_Compteur_IndicateurManager aCIMgr) throws Exception {

		int mvId = 0;

		if(val.getMvGlAssoc_ID() != null) {

			Assoc_Application_Metric assoc = aTMMgr.getAssoc_Territoire_MetricById(val.getMvGlAssoc_ID());

			if(assoc != null) {

				Metric obj = metrMgr.getMetricById(assoc.getMetr_ID());

				if(obj != null && !obj.getMdGlIsCompteur() && val.getMvTolerance() == null && Tools.isNumeric(obj.getMdCustom1())) {
					val.setMvTolerance(Integer.valueOf(obj.getMdCustom1()));
				}

				MetricValues previous = mValsMgr.getPreviousValue(val);

				if(previous != null) {
					val.setMvPreviousMetricsValueId(previous.getId());
				}

				mvId = mValsMgr.addMetricValues(val);

				val.setId(mvId);

				checkMetricsTrends(assoc, val, previous, mValsMgr, metrMgr, aTMMgr, aCIMgr);
			}
		}
		// computeDependentRef(val);

		return mvId;
	}

	public static List<MetricValues> getValuesForMetricAppBeforeDate(int appId, int metrId, Date date, MetricValuesManager mValsMgr, Assoc_Application_MetricManager aTMMgr) {

		if(date == null)
			date = new Date();

		List<MetricValues> values = new ArrayList<MetricValues>();

		Assoc_Application_Metric assoc = aTMMgr.getAssocForMetrIdAndAppId(metrId, appId);
		if(assoc != null) {

			List<MetricValues> tmp = mValsMgr.getMetricValuesByAssocId(assoc.getId());

			if(tmp != null && !tmp.isEmpty()) {
				for(MetricValues metricValues : tmp) {
					if(metricValues != null && metricValues.getMvValueDate() != null && !metricValues.getMvValueDate().after(date)) {
						values.add(metricValues);
					}
				}
			}
		}
		return values;
	}

	public static boolean isMetricHasValues(int metricId, MetricValuesManager mValsMgr, Assoc_Application_MetricManager aTMMgr) {

		List<Assoc_Application_Metric> assocs = aTMMgr.getAssoc_Application_MetricByMetrId(metricId);
		for(Assoc_Application_Metric assoc : assocs) {
			return mValsMgr.getMetricValuesByAssocId(assoc.getId()).isEmpty();
		}
		return false;
	}

	public static MetricValues getObjectifForMetricForPeriode(int appId, int comptId, Date date, MetricValuesManager mValsMgr, Assoc_Application_MetricManager aTMMgr, Assoc_Compteur_IndicateurManager aCIMgr, MetricManager metrMgr) {

		AssocCompteurIndicateur assoc = aCIMgr.getAssoc_Compteur_IndicateurByComptId(comptId);

		if(assoc != null) {
			int assocId = aTMMgr.getAssocIdForMetrIdAndAppId(assoc.getIndic_ID(), appId);

			return getValuesForAssocIdPeridDate(assocId, date, aTMMgr, mValsMgr, metrMgr);
		}

		return null;
	}

	public static MetricValues getValuesForAssocIdPeridDate(int mvGlAssoc_ID, Date period, Assoc_Application_MetricManager aTMMgr, MetricValuesManager mValsMgr, MetricManager metrMgr) {

		// Calendar cperiod = Calendar.getInstance();
		// cperiod.setTime(period);

		Assoc_Application_Metric assoc = aTMMgr.getAssoc_Territoire_MetricById(mvGlAssoc_ID);
		if(assoc != null) {

			Metric met = metrMgr.getMetricById(assoc.getMetr_ID());

			List<MetricValues> tmp = mValsMgr.getMetricValuesByAssocId(assoc.getId());

			if(tmp != null && !tmp.isEmpty()) {
				// for (MetricValues metricValues : tmp) {
				// if(metricValues != null && metricValues.getMvPeriodDate() != null){
				// Calendar datePer = Calendar.getInstance();
				// datePer.setTime(metricValues.getMvPeriodDate());
				// if( datePer.get(Calendar.YEAR) == cperiod.get(Calendar.YEAR)
				// && datePer.get(Calendar.MONTH) == cperiod.get(Calendar.MONTH)
				// && datePer.get(Calendar.DATE) == cperiod.get(Calendar.DATE)
				// && datePer.get(Calendar.HOUR_OF_DAY) == cperiod.get(Calendar.HOUR_OF_DAY)
				// && datePer.get(Calendar.MINUTE) == cperiod.get(Calendar.MINUTE)
				// && datePer.get(Calendar.SECOND) == cperiod.get(Calendar.SECOND)
				// ){
				// return metricValues;
				// }
				// }
				// }
				return Tools.getCorrespondingMetricValue(period, tmp, 0, met.getMdCalculationTimeFrame());
			}
		}
		return null;
	}

	public static MetricValues getObjectifsForValueWithSamePeriod(MetricValues val, Date period, Assoc_Application_MetricManager aTMMgr, MetricValuesManager mValsMgr, Assoc_Compteur_IndicateurManager aCIMgr, MetricManager metrMgr) {

		Assoc_Application_Metric assoc = aTMMgr.getAssoc_Territoire_MetricById(val.getMvGlAssoc_ID());

		if(assoc != null) {

			AssocCompteurIndicateur assocCI = aCIMgr.getAssoc_Compteur_IndicateurByComptId(assoc.getMetr_ID());

			if(assocCI != null) {

				Metric obj = metrMgr.getMetricById(assocCI.getIndic_ID());

				assoc = aTMMgr.getAssocForMetrIdAndAppId(assocCI.getIndic_ID(), assoc.getApp_ID());
				if(assoc != null) {

					List<MetricValues> tmp = mValsMgr.getMetricValuesByAssocId(assoc.getId());

					if(tmp != null && !tmp.isEmpty()) {

						return Tools.getCorrespondingMetricValue(period, tmp, val.getId(), obj.getMdCalculationTimeFrame());
					}
				}
			}
		}
		return null;
	}

	public static void updateMetricValues(MetricValues val, MetricValuesManager mValsMgr, MetricManager metrMgr, Assoc_Application_MetricManager aTMMgr, Assoc_Compteur_IndicateurManager aCIMgr) throws Exception {

		if(val.getMvGlAssoc_ID() != null) {

			Assoc_Application_Metric assoc = aTMMgr.getAssoc_Territoire_MetricById(val.getMvGlAssoc_ID());

			if(assoc != null) {

				Metric obj = metrMgr.getMetricById(assoc.getMetr_ID());

				if(obj != null && !obj.getMdGlIsCompteur() && val.getMvTolerance() == null && Tools.isNumeric(obj.getMdCustom1())) {
					val.setMvTolerance(Integer.valueOf(obj.getMdCustom1()));
				}

				MetricValues previous = null;

				// if(val.getMvPreviousMetricsValueId() != null && val.getMvPreviousMetricsValueId() > 0){
				// previous = mValsMgr.getMetricValuesById(val.getMvPreviousMetricsValueId());
				// }else{

				previous = getPreviousMetricValue(val, obj.getMdCalculationTimeFrame(), mValsMgr);

				if(previous != null && (val.getMvPreviousMetricsValueId() == null || previous.getId() != val.getMvPreviousMetricsValueId())) {
					val.setMvPreviousMetricsValueId(previous.getId());
				}
				// }

				mValsMgr.updateMetricValues(val);

				checkMetricsTrends(assoc, val, previous, mValsMgr, metrMgr, aTMMgr, aCIMgr);
			}
		}
		// computeDependentRef(val);

	}

	public static Integer[] getMetricsDownIDs(int appId, int metricID, MetricInteractionManager mInteractManager) {

		List<Integer> inter = new ArrayList<Integer>();

		List<MetricInteraction> inters = mInteractManager.getMetricInteractions();

		for(MetricInteraction tmp : inters) {

			if(tmp.getMiApplicationId() == appId && tmp.getMiMetricTopId() == metricID) {
				inter.add(tmp.getMiMetricDownId());
			}
		}

		return inter.toArray(new Integer[inter.size()]);
	}

	public static Integer[] getMetricsTopIDs(int appID, int metricID, MetricInteractionManager mInteractManager) {

		List<Integer> inter = new ArrayList<Integer>();
		List<MetricInteraction> inters = mInteractManager.getMetricInteractions();

		for(MetricInteraction tmp : inters) {
			if(tmp.getMiApplicationId() == appID && tmp.getMiMetricDownId() == metricID) {
				inter.add(tmp.getMiMetricTopId());
			}
		}
		return inter.toArray(new Integer[inter.size()]);
	}

	public static String[] getMetricsDownNames(int appID, int metricID, MetricInteractionManager mInteractManager, MetricManager metrMgr) {
		String[] resultat = null;
		List<MetricInteraction> inter = new ArrayList<MetricInteraction>();

		List<MetricInteraction> inters = mInteractManager.getMetricInteractions();
		for(MetricInteraction tmp : inters) {

			if(tmp.getMiApplicationId() == appID && tmp.getMiMetricTopId() == metricID) {
				inter.add(tmp);
			}
		}
		resultat = new String[inter.size()];

		int index = 0;
		for(MetricInteraction object : inter) {

			Metric met = metrMgr.getMetricById(object.getMiMetricDownId());
			if(met != null) {
				resultat[index] = met.getName();
			}
			else {
				resultat[index] = "";
			}
			index++;
		}
		return resultat;
	}

	public static String[] getMetricsTopNames(int appID, int metricID, MetricInteractionManager mInteractManager, MetricManager metrMgr) {
		String[] resultat = null;
		List<MetricInteraction> inter = new ArrayList<MetricInteraction>();

		List<MetricInteraction> inters = mInteractManager.getMetricInteractions();
		for(MetricInteraction tmp : inters) {

			if(tmp.getMiApplicationId() == appID && tmp.getMiMetricDownId() == metricID) {
				inter.add(tmp);
			}
		}
		resultat = new String[inter.size()];

		int index = 0;
		for(MetricInteraction object : inter) {

			Metric met = metrMgr.getMetricById(object.getMiMetricTopId());
			if(met != null) {
				resultat[index] = met.getName();
			}
			else {
				resultat[index] = "";
			}
			index++;
		}
		return resultat;
	}

	public static float[] getMetricsDownMinimum(int appId, int metricID, MetricInteractionManager mInteractManager, MetricValuesManager mValsMgr, Assoc_Application_MetricManager aTMMgr, Assoc_Compteur_IndicateurManager aCIMgr, MetricManager metrMgr) {
		float[] resultat = null;

		List<Integer> inter = new ArrayList<Integer>();
		List<MetricInteraction> inters = mInteractManager.getMetricInteractions();

		Date now = new Date();

		for(MetricInteraction tmp : inters) {

			if(tmp.getMiApplicationId() == appId && tmp.getMiMetricTopId() == metricID) {
				inter.add(tmp.getMiMetricDownId());
			}
		}
		resultat = new float[inter.size()];

		int index = 0;
		for(Integer mId : inter) {

			MetricValues obj = getObjectifForMetricForPeriode(appId, mId, now, mValsMgr, aTMMgr, aCIMgr, metrMgr);
			if(obj != null) {
				resultat[index] = obj.getMvMinValue();
				index++;
			}
		}
		return resultat;
	}

	public static float[] getMetricsTopMinimum(int appId, int metricID, MetricInteractionManager mInteractManager, MetricValuesManager mValsMgr, Assoc_Application_MetricManager aTMMgr, Assoc_Compteur_IndicateurManager aCIMgr, MetricManager metrMgr) {
		float[] resultat = null;

		List<Integer> inter = new ArrayList<Integer>();
		List<MetricInteraction> inters = mInteractManager.getMetricInteractions();

		Date now = new Date();

		for(MetricInteraction tmp : inters) {

			if(tmp.getMiApplicationId() == appId && tmp.getMiMetricDownId() == metricID) {
				inter.add(tmp.getMiMetricTopId());
			}
		}
		resultat = new float[inter.size()];

		int index = 0;
		for(Integer mId : inter) {

			MetricValues obj = getObjectifForMetricForPeriode(appId, mId, now, mValsMgr, aTMMgr, aCIMgr, metrMgr);
			if(obj != null) {
				resultat[index] = obj.getMvMinValue();
				index++;
			}
		}
		return resultat;
	}

	public static float[] getMetricsDownTargets(int appId, int metricID, MetricInteractionManager mInteractManager, MetricValuesManager mValsMgr, Assoc_Application_MetricManager aTMMgr, Assoc_Compteur_IndicateurManager aCIMgr, MetricManager metrMgr) {
		float[] resultat = null;

		List<Integer> inter = new ArrayList<Integer>();
		List<MetricInteraction> inters = mInteractManager.getMetricInteractions();

		Date now = new Date();

		for(MetricInteraction tmp : inters) {

			if(tmp.getMiApplicationId() == appId && tmp.getMiMetricTopId() == metricID) {
				inter.add(tmp.getMiMetricDownId());
			}
		}
		resultat = new float[inter.size()];

		int index = 0;
		for(Integer mId : inter) {

			MetricValues obj = getObjectifForMetricForPeriode(appId, mId, now, mValsMgr, aTMMgr, aCIMgr, metrMgr);
			if(obj != null) {
				resultat[index] = obj.getMvGlObjectif();
				index++;
			}
		}
		return resultat;
	}

	public static float[] getMetricsTopTargets(int appId, int metricID, MetricInteractionManager mInteractManager, MetricValuesManager mValsMgr, Assoc_Application_MetricManager aTMMgr, Assoc_Compteur_IndicateurManager aCIMgr, MetricManager metrMgr) {
		float[] resultat = null;

		List<Integer> inter = new ArrayList<Integer>();
		List<MetricInteraction> inters = mInteractManager.getMetricInteractions();

		Date now = new Date();

		for(MetricInteraction tmp : inters) {

			if(tmp.getMiApplicationId() == appId && tmp.getMiMetricDownId() == metricID) {
				inter.add(tmp.getMiMetricTopId());
			}
		}
		resultat = new float[inter.size()];

		int index = 0;
		for(Integer mId : inter) {

			MetricValues obj = getObjectifForMetricForPeriode(appId, mId, now, mValsMgr, aTMMgr, aCIMgr, metrMgr);
			if(obj != null) {
				resultat[index] = obj.getMvGlObjectif();
				index++;
			}
		}
		return resultat;
	}

}
