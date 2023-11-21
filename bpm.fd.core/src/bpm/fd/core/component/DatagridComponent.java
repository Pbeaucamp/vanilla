package bpm.fd.core.component;

import java.util.List;

import bpm.fd.api.core.model.components.definition.OrderingType;
import bpm.fd.core.DashboardComponent;
import bpm.fd.core.IComponentData;
import bpm.fd.core.IComponentOption;
import bpm.vanilla.platform.core.beans.data.Dataset;

public class DatagridComponent extends DashboardComponent implements IComponentData, IComponentOption {

	private static final long serialVersionUID = 1L;

	private Dataset dataset;

	private OrderingType orderType = OrderingType.NONE;
	private Integer orderFieldIndex;

	private boolean headersVisible = true;
	private boolean includeTotal = false;
	private boolean rowsCanBeAdded = false;

	private ChartDrill drill;

	private String drillHeader = "Drill";
	private String drillText = "Details";

	private List<DatagridColumn> columns;

	@Override
	public Dataset getDataset() {
		return dataset;
	}

	@Override
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

	public ChartDrill getDrill() {
		return drill;
	}

	public void setDrill(ChartDrill drill) {
		this.drill = drill;
	}

	public OrderingType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderingType orderType) {
		this.orderType = orderType;
	}

	public Integer getOrderFieldIndex() {
		return orderFieldIndex;
	}

	public void setOrderFieldIndex(Integer orderFieldIndex) {
		this.orderFieldIndex = orderFieldIndex;
	}

	public boolean isHeadersVisible() {
		return headersVisible;
	}

	public void setHeadersVisible(boolean headersVisible) {
		this.headersVisible = headersVisible;
	}

	public boolean isRowsCanBeAdded() {
		return rowsCanBeAdded;
	}

	public void setRowsCanBeAdded(boolean rowsCanBeAdded) {
		this.rowsCanBeAdded = rowsCanBeAdded;
	}

	public boolean isIncludeTotal() {
		return includeTotal;
	}

	public void setIncludeTotal(boolean includeTotal) {
		this.includeTotal = includeTotal;
	}

	public String getDrillHeader() {
		return drillHeader;
	}

	public void setDrillHeader(String drillHeader) {
		this.drillHeader = drillHeader;
	}

	public String getDrillText() {
		return drillText;
	}

	public void setDrillText(String drillText) {
		this.drillText = drillText;
	}

	public List<DatagridColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<DatagridColumn> columns) {
		this.columns = columns;
	}

	@Override
	public ComponentType getType() {
		return ComponentType.DATA_GRID;
	}

	@Override
	protected void clearData() {
		this.dataset = null;
		this.orderFieldIndex = null;
		this.drill = null;
		this.columns = null;
	}
}
