package bpm.fd.core.component;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.chart.ChartData;
import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.api.core.model.components.definition.chart.ChartOrderingType;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.api.core.model.components.definition.chart.MultiSerieChartData;
import bpm.fd.api.core.model.components.definition.chart.RChartNature;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.GenericOptions;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.PieGenericOptions;
import bpm.fd.api.core.model.resources.Palette;
import bpm.fd.core.DashboardComponent;
import bpm.fd.core.IComponentData;
import bpm.fd.core.IComponentOption;
import bpm.vanilla.platform.core.beans.data.Dataset;

public class RChartComponent extends DashboardComponent implements IComponentData, IComponentOption {
	
	private static final long serialVersionUID = 1L;
	
	private RChartNature nature = RChartNature.getNature( RChartNature.POINT );
	
	private Dataset dataset;
	private int groupFieldIndex;
	private int axeXField;
	private int axeYField;
	private String groupFieldLabel , axeXFieldLabel , axeYFieldLabel ;
	private List<String> selectedColumList;
	private List<String>  selectedColumName;
	
	

	public String getGroupFieldLabel() {
		return groupFieldLabel;
	}
	public void setGroupFieldLabel(String groupFieldLabel) {
		this.groupFieldLabel = groupFieldLabel;
	}
	public String getAxeXFieldLabel() {
		return axeXFieldLabel;
	}
	public void setAxeXFieldLabel(String axeXFieldLabel) {
		this.axeXFieldLabel = axeXFieldLabel;
	}
	public String getAxeYFieldLabel() {
		return axeYFieldLabel;
	}
	public void setAxeYFieldLabel(String axeYFieldLabel) {
		this.axeYFieldLabel = axeYFieldLabel;
	}
	private RChartOption option = new RChartOption();
	
	public int getGroupFieldIndex() {
		return groupFieldIndex;
	}
	public void setGroupFieldIndex(int groupFieldIndex) {
		this.groupFieldIndex = groupFieldIndex;
	}
	@Override
	public Dataset getDataset() {
		// TODO Auto-generated method stub
		return this.dataset;
	}

	@Override
	public void setDataset(Dataset dataset) {
		// TODO Auto-generated method stub
		this.dataset = dataset ;
		
	}

	@Override
	public ComponentType getType() {
		// TODO Auto-generated method stub
		return ComponentType.RCHART;
	}

	@Override
	protected void clearData() {
		// TODO Auto-generated method stub
		this.dataset = null ;
		this.groupFieldIndex = 0;
		this.axeXField=0;
		this.axeYField=0;
	}
	public RChartNature getNature() {
		return nature;
	}
	public void setNature(RChartNature nature) {
		this.nature = nature;
	}
	public RChartOption getOption() {
		return option;
	}
	public void setOption(RChartOption option) {
		this.option = option;
	}
	public int getAxeXField() {
		return this.axeXField;
	}
	public void setAxeXField(int axe) {
		this.axeXField = axe;
	}
	public int getAxeYField() {
		return this.axeYField;
	}
	public void setAxeYField(int axe) {
		this.axeYField = axe;
	}
	public List<String> getSelectedColumList() {
		return selectedColumList;
	}
	public void setSelectedColumList(List<String> selectedColumList) {
		this.selectedColumList = selectedColumList;
	}
	public List<String> getSelectedColumName() {
		return selectedColumName;
	}
	public void setSelectedColumName(List<String> selectedColumName) {
		this.selectedColumName = selectedColumName;
	}
}
