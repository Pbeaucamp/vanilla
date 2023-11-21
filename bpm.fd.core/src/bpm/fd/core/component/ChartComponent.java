package bpm.fd.core.component;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.api.core.model.components.definition.chart.ChartOrderingType;
import bpm.fd.api.core.model.resources.Palette;
import bpm.fd.core.DashboardComponent;
import bpm.fd.core.IComponentData;
import bpm.fd.core.IComponentOption;
import bpm.vanilla.platform.core.beans.data.Dataset;

public class ChartComponent extends DashboardComponent implements IComponentData, IComponentOption {

	private static final long serialVersionUID = 1L;

	private ChartNature nature = ChartNature.getNature(ChartNature.PIE_3D);

	private Dataset dataset;

	private int groupFieldIndex;
	private int groupFieldLabelIndex;
	private Integer subGroupFieldIndex;

	private List<DataAggregation> aggregations;

	private ChartOrderingType orderType = ChartOrderingType.CATEGORY_LABEL_ASC;

	private Palette colorPalette;

	private ChartDrill chartDrill;
	
	private ChartOption option = new ChartOption();

	@Override
	public Dataset getDataset() {
		return dataset;
	}

	@Override
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

	public ChartNature getNature() {
		return nature;
	}

	public void setNature(ChartNature nature) {
		this.nature = nature;
	}

	public int getGroupFieldIndex() {
		return groupFieldIndex;
	}

	public void setGroupFieldIndex(int groupFieldIndex) {
		this.groupFieldIndex = groupFieldIndex;
	}

	public int getGroupFieldLabelIndex() {
		return groupFieldLabelIndex;
	}

	public void setGroupFieldLabelIndex(int groupFieldLabelIndex) {
		this.groupFieldLabelIndex = groupFieldLabelIndex;
	}

	public Integer getSubGroupFieldIndex() {
		return subGroupFieldIndex;
	}

	public void setSubGroupFieldIndex(Integer subGroupFieldIndex) {
		this.subGroupFieldIndex = subGroupFieldIndex;
	}

	public List<DataAggregation> getAggregations() {
		return aggregations;
	}

	public void setAggregations(List<DataAggregation> aggregations) {
		this.aggregations = aggregations;
	}

	public void addAggregation(DataAggregation aggregation) {
		if (aggregations == null) {
			aggregations = new ArrayList<DataAggregation>();
		}
		aggregations.add(aggregation);
	}

	public void removeAggregation(DataAggregation aggregation) {
		aggregations.remove(aggregation);
	}

	public ChartOrderingType getOrderType() {
		return orderType;
	}

	public void setOrderType(ChartOrderingType orderType) {
		this.orderType = orderType;
	}

	public Palette getColorPalette() {
		return colorPalette;
	}

	public void setColorPalette(Palette colorPalette) {
		this.colorPalette = colorPalette;
	}

	public ChartDrill getChartDrill() {
		return chartDrill;
	}

	public void setChartDrill(ChartDrill chartDrill) {
		this.chartDrill = chartDrill;
	}

	public ChartOption getOption() {
		return option;
	}

	public void setOption(ChartOption option) {
		this.option = option;
	}

	@Override
	public ComponentType getType() {
		return ComponentType.CHART;
	}

	@Override
	protected void clearData() {
		this.dataset = null;

		this.groupFieldIndex = 0;
		this.groupFieldLabelIndex = 0;
		this.subGroupFieldIndex = null;

		this.aggregations = null;
		this.chartDrill = null;
	}

}
