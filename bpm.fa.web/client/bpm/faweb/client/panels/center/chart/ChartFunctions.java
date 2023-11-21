package bpm.faweb.client.panels.center.chart;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.tree.FaWebTreeItem;
import bpm.faweb.shared.GroupChart;
import bpm.faweb.shared.SerieChart;
import bpm.gwt.commons.client.charts.ChartObject;
import bpm.gwt.commons.client.charts.ChartValue;
import bpm.gwt.commons.client.charts.ValueMultiSerie;
import bpm.gwt.commons.client.charts.ValueSimpleSerie;

import com.google.gwt.dom.client.Style.Unit;

public class ChartFunctions {

	public static String getMemberName(String uname) {
		String unameSplit = uname.substring(uname.lastIndexOf('.') + 1, uname.length());
		if (unameSplit.length() > 0)
			return unameSplit;
		else
			return uname;
	}

	public static String getMeasureName(String uname) {
		if (uname != null) {
			String unameSplit = uname.substring(uname.lastIndexOf('.') + 1, uname.length());
			if (unameSplit.length() > 0) {
				String response = unameSplit.replace("]", "").replace("[", "");
				return response;
			}
		}
		return uname;
	}

	public static ChartObject createPreviewChart(List<GroupChart> groupsChart, String title) {
		ChartObject chart = new ChartObject();

		chart.setWidthUnit(Unit.PCT);
		chart.setWidth(100);
		chart.setHeight(400);
		chart.setTitle(title);
		chart.setyAxisName(ChartFunctions.getMeasureName(groupsChart.get(0).getMeasure()));

		List<ChartValue> chartValues = new ArrayList<ChartValue>();

		if (groupsChart.get(0).getSeries().size() > 1) {
			for (GroupChart groupChart : groupsChart) {
				ValueMultiSerie multiSerie = new ValueMultiSerie();
				multiSerie.setCategory(showCaption(groupChart.getUname()));
				multiSerie.setSerieName(showCaption(groupChart.getUname()));
				multiSerie.setSerieValues(getSeries(groupChart));
				chartValues.add(multiSerie);
			}
			chart.setValues(chartValues);
		}
		else {
			for (GroupChart groupChart : groupsChart) {
				ValueSimpleSerie value = new ValueSimpleSerie();
				value.setCategory(showCaption(groupChart.getUname()));
				value.setValue(Double.parseDouble(groupChart.getSeries().get(0).getValue()));
				chartValues.add(value);
			}

			chart.setValues(chartValues);
			chart.setxAxisName(ChartFunctions.getMemberName(groupsChart.get(0).getSeries().get(0).getName()));
		}

		return chart;
	}

	public static List<ChartValue> getSeries(GroupChart groupChart) {
		List<ChartValue> values = new ArrayList<ChartValue>();
		for (SerieChart serie : groupChart.getSeries()) {
			ValueSimpleSerie value = new ValueSimpleSerie();
			value.setCategory(serie.getName());
			value.setValue(Double.parseDouble(serie.getValue()));
			values.add(value);
		}
		return values;
	}

	public static String showCaption(String text) {
		try {
			FaWebTreeItem it = MainPanel.getInstance().getNavigationPanel().getDimensionPanel().findRootItem2(text);
			return findCaption(it, text);
		} catch (Exception e) {
			return text;
		}

	}

	private static String findCaption(FaWebTreeItem it, String text) {
		for (int i = 0; i < it.getChildCount(); i++) {
			FaWebTreeItem child = (FaWebTreeItem) it.getChild(i);
			if (child.getItemDim().getUname().equals(text)) {

				String[] f = text.split("\\.");
				f[f.length - 1] = "[" + child.getItemDim().getName() + "]";
				String newval = "";
				boolean first = true;
				for (String s : f) {
					if (first) {
						first = false;
					}
					else {
						newval += ".";
					}
					newval += s;
				}
				return newval;
			}
			findCaption(child, text);
		}
		return text;
	}

}
