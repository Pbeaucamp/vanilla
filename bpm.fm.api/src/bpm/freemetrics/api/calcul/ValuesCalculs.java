package bpm.freemetrics.api.calcul;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import bpm.freemetrics.api.manager.IManager;
import bpm.freemetrics.api.organisation.metrics.MetricValues;
import bpm.freemetrics.api.utils.IConstants;
import bpm.freemetrics.api.utils.Tools;

public class ValuesCalculs {
	/**
	 * Return 0 if equals, 1 if positive, -1 instead
	 * @param cptValue
	 * @param indValue
	 * @param manager
	 * @return
	 */
	public static Integer getTendancy(MetricValues cptValue, MetricValues indValue, IManager manager) {
		String trend = Tools.computeTrendOrder(cptValue, indValue);
		if(trend.equals(IConstants.TREND_ORDER_DOWN)) {
			return -1;
		}
		else if(trend.equals(IConstants.TREND_ORDER_UP)) {
			return 1;
		}
		else {
			return 0;
		}
	}
	/**
	 * Return 0 if equals, 1 if positive, -1 instead
	 * @param cptValue
	 * @param indValue
	 * @return
	 */
	public static Integer getHealth(MetricValues cptValue, MetricValues indValue) {
		String health = Tools.computeStatus(cptValue, indValue);
		if(health.equals(IConstants.STATUS_OVER)) {
			return 1;
		}
		else if(health.equals(IConstants.STATUS_UNDER)) {
			return -1;
		}
		else {
			return 0;
		}
	}
	/**
	 * Return a pourcentage value for achievement
	 * @param cptValue
	 * @param indValue
	 * @return
	 */
	public static Float getAchievement(MetricValues cptValue, MetricValues indValue) {
		DecimalFormat form = new DecimalFormat();
		form.setMaximumFractionDigits(2);
		form.setMinimumFractionDigits(2);
		String perc = form.format(cptValue.getMvValue() / indValue.getMvGlObjectif() * 100);
		perc = perc.replace(",", ".");
		Number res = null;
		try {
			 res = form.parse(perc);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return res.floatValue();
	}
}
