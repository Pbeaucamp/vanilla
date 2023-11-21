package bpm.fd.runtime.model.datas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;

import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.api.core.model.components.definition.chart.IChartData;
import bpm.fd.runtime.model.datas.ChartAggregationer.DataPlot;
import bpm.fd.runtime.model.datas.ChartAggregationer.DataSerie;

public class BubbleAggregationer {

	public class BubblePlot {

		private String category;
		private String sub;
		private String label;
		private double x;
		private double y;

		private int nbX;
		private int nbY;

		private String xOperator;
		private String yOperator;

		public BubblePlot(String category, String label, String sub) {
			this.category = category;
			this.label = label;
			this.sub = sub;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public double getX() {
			return x;
		}

		public void setX(double x) {
			this.x = x;
		}

		public double getY() {
			return y;
		}

		public void setY(double y) {
			this.y = y;
		}

		public void addX(double x) {
			this.x += x;
			this.nbX++;
		}

		public void addY(double y) {
			this.y += y;
			this.nbY++;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getxOperator() {
			return xOperator;
		}

		public void setxOperator(String xOperator) {
			this.xOperator = xOperator;
		}

		public String getyOperator() {
			return yOperator;
		}

		public void setyOperator(String yOperator) {
			this.yOperator = yOperator;
		}

		public void endCalculations() {
			// XXX
		}

		public String getSub() {
			return sub;
		}

		public void setSub(String sub) {
			this.sub = sub;
		}
	}

	public List<BubblePlot> getBubblePlots(ComponentChartDefinition chart, HashMap<DataAggregation, DataSerie> series) throws Exception {
		IChartData dt = (IChartData) chart.getDatas();
		HashMap<String, BubblePlot> plots = new HashMap<String, BubblePlot>();

		try {
			boolean x = true;
			for(DataAggregation a : dt.getAggregation()) {
				
				for(DataPlot data : series.get(a).getPlots()) {
					
					String category = String.valueOf(data.getCategory());
					String label = data.getLabel();
					// extract the datas from the current row
					Double value = data.getRealValue();
	
					if(x) {
						if(plots.get(category) == null) {
							BubblePlot plot = new BubblePlot(category, label, data.getSubCategory());
							plots.put(category, plot);
						}
						plots.get(category).addX(value);
					}
					else {
						if(plots.get(category) == null) {
							BubblePlot plot = new BubblePlot(category, label, data.getSubCategory());
							plots.put(category, plot);
						}
						plots.get(category).addY(value);
					}
				}
				x = false;
			}
		} catch(Exception ex) {
			throw ex;
		} finally {}

		return new ArrayList<BubblePlot>(plots.values());
	}

}
