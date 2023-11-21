package bpm.fd.runtime.model.datas;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;

import bpm.fd.api.core.model.components.definition.IComponentDataFilter;
import bpm.fd.api.core.model.components.definition.chart.ChartOrderingType;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.api.core.model.components.definition.chart.DatasLimit;
import bpm.fd.api.core.model.components.definition.chart.IChartData;
import bpm.fd.api.core.model.components.definition.chart.MultiSerieChartData;
import bpm.fd.api.core.model.datas.filters.ColumnFilter;
import bpm.fd.runtime.model.DrillState;

public class ChartAggregationer {

	private static AlphanumComparator comparator = new AlphanumComparator();
	
	private static IChartData chartData;

	/**
	 * This class contains data's for a FusionChart dataSet The subCategorie may be null for non-splited categories
	 * 
	 * @author ludo
	 * 
	 */
	public static class DataSetRow {
		private List<DataPlot> plots;
		private List<String> categorie;
		private String subCategorie;

		public void orderByCategories() {
			Collections.sort(plots, new PlotCategoryComparator(categorie));

		}

		/**
		 * The categories are writed in a seperate block within FusionChart XML We need this list to add empty values for the dataSet if the provided datas has no datas for some categories otherwise, the displayed chart will have wrong datas
		 * 
		 * @param categories
		 * @param subCategorie
		 */
		public DataSetRow(List<String> categories, String subCategorie) {
			this.categorie = categories;
			this.subCategorie = subCategorie;
		}

		/**
		 * return each datas plot for the dataSet
		 * 
		 * @return
		 */
		public List<DataPlot> getPlots() {
			return plots;
		}

		/**
		 * we set the datas for this datas, if incoming dataplots are missing (no dataplot for some categories) datasplot created to avoid wrond chart rendering we also re-order the plots from the categories order
		 * 
		 * @param plots
		 */
		public void setPlots(List<DataPlot> plots) {
			this.plots = plots;

			// check if all categories has a value

			if(this.categorie != null) {

			}
			for(String s : categorie) {
				boolean found = false;
				for(DataPlot p : plots) {
					if(p.getLabel().equals(s)) {
						found = true;
						break;
					}
				}
				if(!found) {
					try {
						plots.add(new DataPlot(s, s, subCategorie, null, plots.get(0).getCategoryOrder(), plots.get(0).getSubCategoryOrder()));
					} catch (Exception e) {
						plots.add(new DataPlot(s, s, subCategorie, null, null, null));
					}
				}
			}
			Collections.sort(plots, new Comparator<DataPlot>() {
				@Override
				public int compare(DataPlot o1, DataPlot o2) {
					int i1 = categorie.indexOf(o1.getCategory());
					int i2 = categorie.indexOf(o2.getCategory());
					return i1 - i2;
				}
			});
		}

		public String getSubCategory() {
			return subCategorie;
		}
	}

	/**
	 * Allows to reorder DataSetRow's DataPlots from the chart Categories
	 * 
	 * @author ludo
	 * 
	 */
	public static class PlotCategoryComparator implements Comparator<DataPlot> {
		private List<String> categories;

		public PlotCategoryComparator(List<String> categories) {
			this.categories = categories;
		}

		@Override
		public int compare(DataPlot o1, DataPlot o2) {

//			Integer i1 = -1;
//			Integer i2 = -1;
//			for(int i = 0; i < categories.size(); i++) {
//				if(categories.get(i).equals(o1.getLabel())) {
//					i1 = i;
//				}
//				if(categories.get(i).equals(o2.getLabel())) {
//					i2 = i;
//				}
//			}
//			int res = i1.compareTo(i2);
//			if(res == 0) {
//				
//				
//				
//				return comparator.compare(o1.getSubCategory(), o2.getSubCategory());
//
//			}
//			return res;
			
			Object t1 = null;
			Object t2 = null;
			
			ChartOrderingType type = null;
			if(o1.getCategoryOrder() != null) {
				type = o1.getCategoryOrder();
			}
			else {
				type = o2.getCategoryOrder();
			}
			
			switch(type) {
				case CATEGORY_ASC:
				case CATEGORY_DESC:
					t1 = o1.getCategory();
					t2 = o2.getCategory();
					break;
				case CATEGORY_LABEL_ASC:
				case CATEGORY_LABEL_DESC:
					t1 = o1.getLabel();
					t2 = o2.getLabel();
					break;
				case VALUE_ASC:
				case VALUE_DESC:
					if((o1.getSubCategory() != null && !o1.getSubCategory().isEmpty())) {
						if(o1.getCategory().equals(o2.getCategory())) {
							t1 = o1.getValue();
							t2 = o2.getValue();
						}
					}
					else {
						t1 = o1.getValue();
						t2 = o2.getValue();
					}
					break;
			}

			if(t1 == null) {
				return 1;
			}
			else if(t2 == null) {
				return -1;
			}
			int i = 0;
			if(type == ChartOrderingType.CATEGORY_DESC || type == ChartOrderingType.CATEGORY_LABEL_DESC || type == ChartOrderingType.VALUE_DESC) {
				i = comparator.compare(String.valueOf(t2), String.valueOf(t1));
			}
			else {
				i = comparator.compare(String.valueOf(t1), String.valueOf(t2));
			}

			if(i == 0 && o1.getSubCategoryOrder() != ChartOrderingType.VALUE_ASC && o1.getSubCategoryOrder() != ChartOrderingType.VALUE_DESC) {
				String s1 = o1.getSubCategory();
				String s2 = o2.getSubCategory();
				if(s1 == null && s2 == null) {
					return i;
				}
				else if(s1 == null) {
					return 1;
				}
				else if(s2 == null) {
					return -1;
				}
				else {
					if(o1.getSubCategoryOrder() == ChartOrderingType.CATEGORY_DESC || o1.getSubCategoryOrder() == ChartOrderingType.CATEGORY_LABEL_DESC) {
						return comparator.compare(s2, s1);
					}
					else {
						return comparator.compare(s1, s2);
					}
				}
			}
			return i;

		}
	}

	/**
	 * Reorder DataPlots lists from the chartData OrderingType
	 * 
	 * @author ludo
	 * 
	 */
	public static class PlotComparator implements Comparator<DataPlot> {
		private ChartOrderingType type;

		public PlotComparator(ChartOrderingType type) {
			this.type = type;
		}

		@Override
		public int compare(DataPlot o1, DataPlot o2) {
			Object t1 = null;
			Object t2 = null;
			switch(type) {
				case CATEGORY_ASC:
				case CATEGORY_DESC:
					t1 = o1.getCategory();
					t2 = o2.getCategory();
					break;
				case CATEGORY_LABEL_ASC:
				case CATEGORY_LABEL_DESC:
					t1 = o1.getLabel();
					t2 = o2.getLabel();
					break;
				case VALUE_ASC:
				case VALUE_DESC:
					t1 = o1.getValue();
					t2 = o2.getValue();
					break;
			}

			if(t1 == null) {
				return 1;
			}
			else if(t2 == null) {
				return -1;
			}
			int i = 0;
			if(type == ChartOrderingType.CATEGORY_DESC || type == ChartOrderingType.CATEGORY_LABEL_DESC || type == ChartOrderingType.VALUE_DESC) {
				i = comparator.compare(t2, t1);
			}
			else {
				i = comparator.compare(t1, t2);
			}

			if(i == 0 && o1.getSubCategoryOrder() != ChartOrderingType.VALUE_ASC && o1.getSubCategoryOrder() != ChartOrderingType.VALUE_DESC) {
				String s1 = o1.getSubCategory();
				String s2 = o2.getSubCategory();
				if(s1 == null && s2 == null) {
					return i;
				}
				else if(s1 == null) {
					return 1;
				}
				else if(s2 == null) {
					return -1;
				}
				else {
					if(o1.getSubCategoryOrder() == ChartOrderingType.CATEGORY_DESC || o1.getSubCategoryOrder() == ChartOrderingType.CATEGORY_LABEL_DESC) {
						return comparator.compare(s2, s1);
					}
					else {
						return comparator.compare(s1, s2);
					}
				}
			}
			return i;
		}

	}

	public static class DataPlot {
		private List<Object> distinctValues = new ArrayList<Object>();
		private List<Object> allValues = new ArrayList<Object>();
		private int currentNumberValues = 1;

		private String label;
		private Object category;
		private String subCategory;
		private Object value;
		private ChartOrderingType categoryOrder;
		private ChartOrderingType subCategoryOrder;

		public DataPlot(String label, Object category, String subCategory, Object value, ChartOrderingType categoryOrder, ChartOrderingType subCategoryOrder) {
			super();
			this.label = label;
			this.category = category;
			this.subCategory = subCategory;
			this.value = value;
			this.categoryOrder = categoryOrder;
			this.subCategoryOrder = subCategoryOrder;
			if(this.value instanceof Number) {
				try {
					sum(null);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			allValues.add(value);
		}
		
		public List<Object> getAllValues() {
			return allValues;
		}

		public Double getRealValue() {
			if(!(this.value instanceof Double)) {
				return (Double) getDouble(this.value);
			}
			return (Double) this.value;
		}

		/**
		 * @return the label
		 */
		public String getLabel() {
//			return label.replace("'", "&apos;");
			return label;
		}

		/**
		 * @return the category
		 */
		public Object getCategory() {
			if(category == null) {
				return "null";
			}
			return category;
		}

		/**
		 * @return the subCategory
		 */
		public String getSubCategory() {
			return subCategory;
		}

		/**
		 * @return the value
		 */
		public Object getValue() {
			return value;
		}

		public ChartOrderingType getCategoryOrder() {
			return categoryOrder;
		}

		public ChartOrderingType getSubCategoryOrder() {
			return subCategoryOrder;
		}

		// /**
		// * @return the orderValue
		// */
		// public Object getOrderValue() {
		// return orderValue;
		// }
		private void addDistinctValue(Object value) {
			for(Object o : distinctValues) {
				if(o.equals(value)) {
					return;
				}
			}
			distinctValues.add(value);
			this.value = Integer.valueOf(distinctValues.size());
			allValues.add(value);
		}

		public void aggregate(DataAggregation aggregation, DataPlot plot) throws Exception {
			// do not aggregate if a filter is not satisfied
			if(plot.getValue() == null) {
				return;
			}
			for(IComponentDataFilter f : aggregation.getFilter()) {
				if(!f.isSatisfied(plot.getValue())) {
					return;
				}
			}
			
			this.allValues.add(plot.getValue());
			
			switch(aggregation.getAggregator()) {
				case DataAggregation.AGG_MAX:
					if(comparator.compare(getValue(), plot.getValue()) < 0) {
//					if(((Comparable) getValue()).compareTo((Comparable) plot.getValue()) < 0) {
						this.value = plot.getValue();
					}
					break;
				case DataAggregation.AGG_MIN:
					if(comparator.compare(getValue(), plot.getValue()) > 0) {
//					if(((Comparable) getValue()).compareTo((Comparable) plot.getValue()) > 0) {
						this.value = plot.getValue();
					}
					break;

				case DataAggregation.AGG_COUNT:
					currentNumberValues++;
					this.value = currentNumberValues;
					break;
				case DataAggregation.AGG_COUNT_DISTINCT:
					addDistinctValue(plot.getValue());
					break;
				case DataAggregation.AGG_NONE:
					this.value = plot.getValue();
					break;
				case DataAggregation.AGG_AVG:
					currentNumberValues++;
				case DataAggregation.AGG_SUM:
					sum(plot.getValue());
					break;
			}
		}

		private Double getDouble(Object o) /* throws Exception */{
			if(o == null) {
				return null;
			}
			if(o instanceof Double) {
				return (Double) o;
			}
			else if(o instanceof Float) {
				return ((Float) o).doubleValue();
			}
			else if(o instanceof Integer) {
				return ((Integer) o).doubleValue();
			}
			else if(o instanceof Long) {
				return ((Long) o).doubleValue();
			}
			else if(o instanceof BigDecimal) {
				return ((BigDecimal) o).doubleValue();
			}
			else if(o instanceof String) {
				try {
					return Double.parseDouble((String) o);
				} catch(Exception e) {
					
				}
			}
			return null;
			// throw new Exception("Cannot perform a sum on non numeric data : " + o.toString());
		}

		private void sum(Object o) throws Exception {
			// convert this value into Double
			if(!(this.value instanceof Double)) {
				this.value = getDouble(this.value);
			}
			if(o == null) {
				return;
			}
			if(this.value == null) {
				return;
			}

			this.value = ((Double) this.value) + getDouble(o);
		}

		public void finalizeAvgComputation() {
			this.value = currentNumberValues == 1 ? getDouble(this.value) : getDouble(this.value) / currentNumberValues;
			release();
		}

		public void release() {
			distinctValues.clear();
		}

	}

	/**
	 * used to store all the datas for a Chart DataAggregation When adding a DataPlot, the aggregation is performed(or partially performed in case of DistinctCount or Average aggregation type)
	 * 
	 * Once all datas has been added, the method endComputations must be called to apply limits and finalize partially calculated aggregations)
	 * 
	 * Once thos steps are perfomed, calling getOrganizedDataPlots will create all the fusionChart dataSets (for splitted aggregation and non splited aggregations)
	 * 
	 * @author ludo
	 * 
	 */
	public static class DataSerie {
		private DataAggregation aggregation;
		private List<DataPlot> plots = new ArrayList<DataPlot>();

		public DataSerie(DataAggregation aggregation) {
			this.aggregation = aggregation;
		}

		public List<DataPlot> getPlots() {
			return plots;
		}

		private boolean exist() {
			return !plots.isEmpty();
		}

		private DataPlot getPlotForKey(DataPlot plot) {
			for(DataPlot p : plots) {
				if(p.getCategory().equals(plot.getCategory())) {
					if(this.aggregation.isApplyOnDistinctSerie()) {
						if(p.getSubCategory() == null) {
							if(plot.getSubCategory() == null) {
								return p;
							}
						}
						else if(p.getSubCategory().equals(plot.getSubCategory())) {
							return p;
						}
					}
					else {
						if(p.getSubCategory() == null || p.getSubCategory().equals("null")) {
							return p;
						}

					}

				}
			}
			return null;

		}

		public void addDataPlot(DataPlot plot) throws Exception {
			if(!exist()) {
				this.plots.add(plot);
				if(aggregation.getAggregator() == DataAggregation.AGG_COUNT || aggregation.getAggregator() == DataAggregation.AGG_COUNT_DISTINCT) {
					plot.aggregate(aggregation, plot);
				}
			}
			else {
				DataPlot plot4Key = getPlotForKey(plot);
				if(plot4Key == null) {
					plot4Key = plot;
					this.plots.add(plot4Key);
					if(aggregation.getAggregator() == DataAggregation.AGG_COUNT || aggregation.getAggregator() == DataAggregation.AGG_COUNT_DISTINCT) {
						plot4Key.aggregate(aggregation, plot);
					}
				}
				else {

					plot4Key.aggregate(aggregation, plot);
				}
			}
		}

		public void endComputations(DatasLimit limit) {
			for(DataPlot plot : plots) {
				if(aggregation.getAggregator() == DataAggregation.AGG_AVG) {
					plot.finalizeAvgComputation();
					plot.release();

				}

			}

			// apply limits
			if(limit != null && limit.getType() != DatasLimit.LIMIT_NONE) {
				/*
				 * we order the plots by their values to easily find top or bottom elements
				 */
				Collections.sort(plots, new PlotComparator(ChartOrderingType.VALUE_ASC));
				if(plots.size() > limit.getSize()) {
					if(limit.getType() == DatasLimit.LIMIT_BOTTOM) {
						plots = plots.subList(0, limit.getSize());
					}
					else if(limit.getType() == DatasLimit.LIMIT_TOP) {
						plots = plots.subList(plots.size() - limit.getSize(), plots.size());
					}
				}
			}

		}

		public void orderPlots(Comparator<DataPlot> comparator) {
			if(comparator != null) {
				// order plots by their orderValue
				Collections.sort(plots, comparator);
			}
		}

		/**
		 * create the dataSets for the chart catgories are requested to insert empty values(if a catgeory has no value for some splitted series)
		 * 
		 * @param categories
		 * @return
		 */
		public List<DataSetRow> getOrganizedDataPlots(List<String> categories) {
			HashMap<String, List<DataPlot>> splited = new LinkedHashMap<String, List<DataPlot>>();
			List<DataPlot> nonSplited = null;
			for(DataPlot p : getPlots()) {
				if(p.getSubCategory() != null) {
					String k = p.getSubCategory();
					List<DataPlot> l = splited.get(k);
					if(l == null) {
						l = new ArrayList<DataPlot>();
						splited.put(k, l);
					}
					l.add(p);
				}
				else {
					if(nonSplited == null) {
						nonSplited = new ArrayList<DataPlot>();
					}
					nonSplited.add(p);
				}

			}

			List<DataSetRow> l = new ArrayList<DataSetRow>();
			for(String s : splited.keySet()) {
				DataSetRow row = new DataSetRow(categories, s);
				row.setPlots(splited.get(s));
				l.add(row);
			}

			if(nonSplited != null) {
				DataSetRow row = new DataSetRow(categories, null);
				row.setPlots(nonSplited);
				l.add(row);
			}
			
			Collections.sort(l, new Comparator<DataSetRow>() {

				@Override
				public int compare(DataSetRow o1, DataSetRow o2) {
					
					if(chartData.getOrderType() == ChartOrderingType.CATEGORY_DESC || chartData.getOrderType() == ChartOrderingType.CATEGORY_LABEL_DESC) {
						return comparator.compare(o2.getSubCategory(), o1.getSubCategory());
					}
					else {
						return 0;//comparator.compare(o1.getSubCategory(), o2.getSubCategory());
					}
				}
			});

			for(DataSetRow r : l) {
				r.orderByCategories();
			}

			return l;
		}
	}

	/**
	 * return a map for each designed DataAggregation for the given chart The DataSeries are fully built.
	 * 
	 * @param chart
	 * @param rs
	 * @return
	 * @throws Exception
	 */
	public static HashMap<DataAggregation, DataSerie> aggregateDatas(DrillState drillState, ComponentChartDefinition chart, IResultSet rs) throws Exception {
		IChartData dt = (IChartData) chart.getDatas();
		chartData = dt;
		Integer subCategorieIndex = null;
		if(dt instanceof MultiSerieChartData) {
			subCategorieIndex = ((MultiSerieChartData) dt).getSerieFieldIndex();
		}
		HashMap<DataAggregation, DataSerie> series = new LinkedHashMap<DataAggregation, DataSerie>();
		try {

			IResultSetMetaData rsmd = rs.getMetaData();

			while(rs.next()) {
				LOOK:for(DataAggregation a : dt.getAggregation()) {
					
					for(ColumnFilter f : a.getColumnFilters()) {
						String val = rs.getString(f.getColumnIndex());
						if(!val.equals(f.getValue())) {
							continue LOOK;
						}
					}
					
					DataSerie serie = series.get(a);
					if(serie == null) {
						serie = new DataSerie(a);
						series.put(a, serie);
					}
					Object label = null;
					Object category = null;

					if(drillState == null || ((IChartData) chart.getDatas()).getLevelCategoriesIndex().isEmpty()) {
						label = extractResultSetValue(java.sql.Types.VARCHAR, rs, dt.getCategorieFieldLabelIndex());

						category = extractResultSetValue(rsmd.getColumnType(dt.getCategorieFieldIndex()), rs, dt.getCategorieFieldIndex());
					}
					else {
						label = extractResultSetValue(java.sql.Types.VARCHAR, rs, ((IChartData) chart.getDatas()).getLevelCategoriesIndex().get(drillState.getCurrentLevel()) + 1);

						category = extractResultSetValue(rsmd.getColumnType(dt.getCategorieFieldIndex()), rs, ((IChartData) chart.getDatas()).getLevelCategoriesIndex().get(drillState.getCurrentLevel()) + 1);

						if(!validateOnDimension((IChartData) chart.getDatas(), rs, drillState)) {
							continue;
						}

					}

					// extract the datas from the current row
					Object value = extractResultSetValue(rsmd.getColumnType(a.getValueFieldIndex()), rs, a.getValueFieldIndex());

					Object subcategory = a.isApplyOnDistinctSerie() ? extractResultSetValue(rsmd.getColumnType(subCategorieIndex), rs, subCategorieIndex) : null;

					// create a plot
					DataPlot plot = new DataPlot(String.valueOf(label), category, String.valueOf(subcategory), value, dt.getOrderType(), a.getOrderType());
					serie.addDataPlot(plot);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			if(rs != null) {
				rs.close();
			}
		}

		// finalize
		for(DataAggregation a : series.keySet()) {
			series.get(a).endComputations(dt.getLimit());
			// order plots by their orderValue
		}

		/*
		 * dump System.out.println("***** Dump Chart Aggegations *****"); for(DataAggregation a : series.keySet()){ System.out.println("***** " + a.getMeasureName() + " *****"); DataSerie serie = series.get(a); for(DataPlot plot : serie.getPlots()){ System.out.println("Catgeory : " + plot.getCategory()); System.out.println("SubCatgeory : " + plot.getSubCategory()); System.out.println("value :" + plot.getValue()); System.out.println("***** " ); } }
		 */
		return series;

	}

	private static boolean validateOnDimension(IChartData datas, IResultSet rs, DrillState state) throws Exception {
		for(int i = 0; i < state.getCurrentLevel(); i++) {
			Integer colIndex = datas.getLevelCategoriesIndex().get(i);
			String lvlValue = (String) extractResultSetValue(java.sql.Types.VARCHAR, rs, colIndex + 1);

			if(!state.getLevelValue(i).equals(lvlValue)) {
				return false;
			}

		}
		return true;
	}

	private static Object extractResultSetValue(int columnType, IResultSet resultSet, int colIndex) throws Exception {
		switch(columnType) {
			case Types.BIGINT:
			case Types.SMALLINT:
			case Types.TINYINT:
			case Types.INTEGER:

				return resultSet.getInt(colIndex);

			case Types.FLOAT:
			case Types.DOUBLE:
				return resultSet.getDouble(colIndex);

			case Types.DECIMAL:
				return resultSet.getBigDecimal(colIndex);

			case Types.DATE:
				return resultSet.getDate(colIndex);
			case Types.TIME:
				return resultSet.getTime(colIndex);
			case Types.TIMESTAMP:
				return resultSet.getTimestamp(colIndex);
			default:
				return resultSet.getString(colIndex);
		}
	}
}
