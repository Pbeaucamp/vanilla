package bpm.es.sessionmanager.views.charts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Month;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Week;

import bpm.es.sessionmanager.Messages;
import bpm.es.sessionmanager.api.IndexedComponent;
import bpm.es.sessionmanager.api.SessionManager;
import bpm.es.sessionmanager.views.composite.ActionsByDateChartComposite;
import bpm.vanilla.platform.core.beans.VanillaLogs;

public class ChartHelper {

	public static JFreeChart createActionsPieSetChart(SessionManager manager, int userId, 
			int objectId) throws Exception {
		
		List<VanillaLogs> rlogs ;
		
		if (manager.getVanillaLogsByItems() == null) {
			rlogs = new ArrayList<VanillaLogs>();
		}
		else {
			rlogs = manager.getVanillaLogsByItems().getLogsByObjectId(objectId);
		}
		
		DefaultKeyedValues kv = new DefaultKeyedValues();
		
		for (VanillaLogs log : rlogs) {
			String op = log.getOperation();
			
			int index = kv.getIndex(op);
			
			if (index >= 0) {
				int count = kv.getValue(op).intValue() + 1;
				kv.setValue(op, count);
			}
			else {
				kv.addValue(op, 1);
			}
		}
		DefaultPieDataset dataset = new DefaultPieDataset(kv);
		
		JFreeChart chart = ChartFactory.createPieChart(null, dataset, 
				true, true, false);
		
		return chart;
	}

	public static JFreeChart createApplicationBarChart(SessionManager manager, int userId, 
			String applicationId) {
		
		List<VanillaLogs> rlogs ;
		
		if (manager.getVanillaLogsByApplication() == null) {
			rlogs = new ArrayList<VanillaLogs>();
		}
		else {
			rlogs = manager.getVanillaLogsByApplication()
						.getLogsByApplication(applicationId);
		}
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		for (VanillaLogs log : rlogs) {
			String applicationType = log.getObjectType();
			String operationType = log.getOperation();
			long valueTime = log.getDelay();
				
//				System.out.println("Data :\n" +
//						"	app = " + applicationType +
//						"	op  = " + operationType + 
//						"	time = " + valueTime);
				
			dataset.addValue(valueTime, operationType, applicationType);
		}

		JFreeChart chart = ChartFactory.createBarChart3D("",  //$NON-NLS-1$
				Messages.ChartHelper_1, 
				Messages.ChartHelper_2, 
				dataset, 
				PlotOrientation.VERTICAL, //orientation 
				true, //legend 
				true, //
				false); //urls
		
		return chart;
	}
	
	public static JFreeChart createActionTimeChart(SessionManager manager, int userId, 
			String date) throws Exception {
		
		IndexedComponent indexLog;
		
		if (manager.getVanillaLogsByComponent() == null) {
			indexLog = new IndexedComponent();
		}
		else {
			indexLog = manager.getVanillaLogsByComponent();
		}
		
		List<TimeSeries> series = new ArrayList<TimeSeries>();
		
		for (String key : indexLog.getIndexKeys()) {
			HashMap<RegularTimePeriod, Integer> hashedValues = new HashMap<RegularTimePeriod, Integer>();
			
			for (VanillaLogs log : indexLog.getSessionLogForKey(key)) {
				Date d = log.getDate();
				if (log.getDate() == null) {
					//skip
				}
				else if (hashedValues.containsKey(new Hour(d)) ||
							hashedValues.containsKey(new Day(d)) ||
							hashedValues.containsKey(new Week(d)) ||
							hashedValues.containsKey(new Month(d))) {
					
					if (date == ActionsByDateChartComposite.timeAll) {
						Integer val = hashedValues.get(new Day(d));
						hashedValues.put(new Day(d), (val + 1));
					}
					else if (date == ActionsByDateChartComposite.timeToday) {
						Integer val = hashedValues.get(new Hour(d));
						hashedValues.put(new Hour(d), (val + 1));
					}
					else if (date == ActionsByDateChartComposite.timeLastWeek) {
						Integer val = hashedValues.get(new Week(d));
						hashedValues.put(new Week(d), (val + 1));
					}				
					else if (date == ActionsByDateChartComposite.timeLastMonth) {
						Integer val = hashedValues.get(new Month(d));
						hashedValues.put(new Month(d), (val + 1));
					}
				}
				else {
					if (date == ActionsByDateChartComposite.timeAll) {
						hashedValues.put(new Day(d), 1);
					}
					else if (date == ActionsByDateChartComposite.timeToday) {
						if(new Date().getYear() == d.getYear() && new Date().getMonth() == d.getMonth() && new Date().getDate() == d.getDate()) {
							hashedValues.put(new Hour(d), 1);
						}
					}
					else if (date == ActionsByDateChartComposite.timeLastWeek) {
						Calendar cal = Calendar.getInstance();
						cal.add(Calendar.DAY_OF_MONTH, - 7);
						if(cal.getTime().before(d)) {
							hashedValues.put(new Week(d), 1);
						}
					}				
					else if (date == ActionsByDateChartComposite.timeLastMonth) {
						hashedValues.put(new Month(d), 1);
					}
				}
			}
			
			TimeSeries s = new TimeSeries(key); //key is application name
			
			for (RegularTimePeriod period : hashedValues.keySet()) {
				s.add(period, hashedValues.get(period));
			}
			
			series.add(s);
		}
		
//		if (date == ActionsByDateChartComposite.timeAll) {
//			s.add(new Day(d), 1);
//		}
//		else if (date == ActionsByDateChartComposite.timeToday) {
//			s.add(new Day(d), 1);
//		}
//		else if (date == ActionsByDateChartComposite.timeLastWeek) {
//			s.add(new Week(d), 1);
//		}				
//		else if (date == ActionsByDateChartComposite.timeLastMonth) {
//			s.add(new Month(d), 1);
//		}

		
		//s.add(period, value)
		
		//XYSeries serie1 = new XYSeries(0);
		//serie1.add(x, y) date, value
		TimeSeriesCollection collection = new TimeSeriesCollection();
		//collection.addSeries(series.get(0));
		for (TimeSeries serie : series) {
			collection.addSeries(serie);
		}
		//ChartFactory.createTimeSeriesChart
		JFreeChart chart = ChartFactory.createTimeSeriesChart("",  //$NON-NLS-1$
				Messages.ChartHelper_4, //xAxisLabel, 
				Messages.ChartHelper_5, //yAxisLabel, 
				collection, //dataset 
				//PlotOrientation.VERTICAL,//orientation, 
				true, //legend, 
				true, //tooltips, 
				false); //urls) 
		
		return chart;
	}
}
