package bpm.fd.api.core.model.components.definition.chart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.fd.api.core.model.components.definition.IComponentDataFilter;
import bpm.fd.api.core.model.components.definition.IFilterable;
import bpm.fd.api.core.model.datas.filters.ColumnFilter;

public class DataAggregation implements IFilterable, Serializable {

	private static final long serialVersionUID = 1L;

	public static enum MeasureRendering {
		Area, Line, Column;

		public static String[] names = new String[] { "Area", "Line", "Column" };

		public static List<MeasureRendering> getList() {
			List<MeasureRendering> renders = new ArrayList<MeasureRendering>();
			renders.add(Column);
			renders.add(Line);
			renders.add(Area);
			return renders;
		}
	}

	public static class DataAggragationComparator implements Comparator<DataAggregation>, Serializable {
		private static final long serialVersionUID = 1L;

		public int compare(DataAggregation arg0, DataAggregation arg1) {
			if (arg0.isApplyOnDistinctSerie() && !arg1.isApplyOnDistinctSerie()) {
				return 1;
			}
			else if (!arg0.isApplyOnDistinctSerie() && arg1.isApplyOnDistinctSerie()) {
				return -1;
			}
			else {
				return arg0.getMeasureName().compareTo(arg1.getMeasureName());
			}
		}

	}

	public static final int AGG_NONE = 0;
	public static final int AGG_SUM = 1;
	public static final int AGG_AVG = 2;
	public static final int AGG_COUNT = 3;
	public static final int AGG_COUNT_DISTINCT = 4;
	public static final int AGG_MIN = 5;
	public static final int AGG_MAX = 6;

	public static final String[] COLORS = { "FF0000", "00FF00", "0000FF", "AA00FF", "AAAAFF", "AA0020", "AAAA20" };
	public static final String[] AGGREGATORS_NAME = new String[] { "None", "Sum", "Average", "Count", "Distinct Count", "Minimum", "Maximum" };

	private Integer valueFieldIndex;
	private boolean applyOnDistinctSerie = false;
	private MeasureRendering rendering = MeasureRendering.Column;
	private boolean primaryAxis = true;
	private int aggregator = AGG_NONE;
	private String measureName = "measure";
	private List<String> colorsCode = new ArrayList<String>();
	private List<IComponentDataFilter> filters = new ArrayList<IComponentDataFilter>();
	private List<ColumnFilter> columnFilters = new ArrayList<ColumnFilter>();

	private ChartOrderingType orderType = ChartOrderingType.CATEGORY_LABEL_ASC;

	public DataAggregation() {
		super();
		colorsCode.add("FF0000");
		colorsCode.add("00FF00");
		colorsCode.add("0000FF");
		colorsCode.add("AA00FF");
		colorsCode.add("AAAAFF");
		colorsCode.add("AA0020");
		colorsCode.add("AAAA20");
	}

	public boolean isApplyOnDistinctSerie() {
		return applyOnDistinctSerie;
	}

	public void setApplyOnDistinctSerie(boolean applyOnDistinctSerie) {
		this.applyOnDistinctSerie = applyOnDistinctSerie;
	}

	public void addColorCode(String color) {
		colorsCode.add(color);
	}

	public void setColorCode(String color, int i) {
		if (i < colorsCode.size()) {
			colorsCode.set(i, color);
		}
		else {
			colorsCode.add(color);
		}
	}

	/**
	 * @return the rendering
	 */
	public MeasureRendering getRendering() {
		return rendering;
	}

	/**
	 * @param rendering
	 *            the rendering to set
	 */
	public void setRendering(MeasureRendering rendering) {
		this.rendering = rendering;
	}

	/**
	 * @return the primaryAxis
	 */
	public boolean isPrimaryAxis() {
		return primaryAxis;
	}

	/**
	 * @param primaryAxis
	 *            the primaryAxis to set
	 */
	public void setPrimaryAxis(boolean primaryAxis) {
		this.primaryAxis = primaryAxis;
	}

	public void swapColors(int a, int b) {
		String tmp = colorsCode.get(a);
		colorsCode.set(a, colorsCode.get(b));
		colorsCode.set(b, tmp);
	}

	/**
	 * @return the colorsCode
	 */
	public List<String> getColorsCode() {
		return colorsCode;
	}

	/**
	 * @return the measureName
	 */
	public String getMeasureName() {
		return measureName == null ? AGGREGATORS_NAME[getAggregator()] : measureName;
	}

	/**
	 * @param measureName
	 *            the measureName to set
	 */
	public void setMeasureName(String measureName) {
		this.measureName = measureName;
	}

	/**
	 * @return the aggregator
	 */
	public int getAggregator() {
		return aggregator;
	}

	public String getAggregatorName() {
		return getAggregator() >= 0 ? AGGREGATORS_NAME[getAggregator()] : "";
	}

	/**
	 * @param aggregator
	 *            the aggregator to set
	 */
	public void setAggregator(int aggregator) {
		this.aggregator = aggregator;
	}

	public void setAggregator(String agg) {
		int i = 0;
		for (String a : AGGREGATORS_NAME) {
			if (agg.equals(a)) {
				setAggregator(i);
				break;
			}
			i++;
		}
	}

	/**
	 * @return the valueFieldIndex
	 */
	public Integer getValueFieldIndex() {
		return valueFieldIndex;
	}

	/**
	 * @param valueFieldIndex
	 *            the valueFieldIndex to set
	 */
	public void setValueFieldIndex(Integer valueFieldIndex) {
		this.valueFieldIndex = valueFieldIndex;
	}

	@Override
	public void addFilter(IComponentDataFilter filter) {
		filters.add(filter);
	}

	@Override
	public List<IComponentDataFilter> getFilter() {
		return new ArrayList<IComponentDataFilter>(filters);
	}

	@Override
	public void removeFilter(IComponentDataFilter filter) {
		filters.remove(filter);

	}

	public ChartOrderingType getOrderType() {
		return orderType;
	}

	public void setOrderType(ChartOrderingType orderType) {
		this.orderType = orderType;
	}

	public List<ColumnFilter> getColumnFilters() {
		return columnFilters;
	}

	public void setColumnFilters(List<ColumnFilter> columnFilters) {
		this.columnFilters = columnFilters;
	}

	public void addColumnFilter(ColumnFilter filter) {
		this.columnFilters.add(filter);
	}
}
