package bpm.fd.api.core.model.components.definition.filter;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentDatas;
import bpm.fd.api.core.model.components.definition.OrderingType;
import bpm.fd.api.core.model.datas.DataSet;

public class FilterData implements IComponentDatas{

	private int columnLabelIndex = -1;
	private int columnValueIndex = -1;
	private int columnOrderIndex = -1;
	
	private OrderingType orderType = OrderingType.ASC;
	
	/**
	 * @return the columnOrderIndex
	 */
	public int getColumnOrderIndex() {
		return columnOrderIndex;
	}

	/**
	 * @param columnOrderIndex the columnOrderIndex to set
	 */
	public void setColumnOrderIndex(int columnOrderIndex) {
		this.columnOrderIndex = columnOrderIndex;
	}

	private DataSet dataSet;
	
	
	/**
	 * @return the columnLabelIndex
	 */
	public int getColumnLabelIndex() {
		return columnLabelIndex;
	}

	/**
	 * @param columnLabelIndex the columnLabelIndex to set
	 */
	public void setColumnLabelIndex(int columnLabelIndex) {
		this.columnLabelIndex = columnLabelIndex;
	}

	/**
	 * @return the columnValueIndex
	 */
	public int getColumnValueIndex() {
		return columnValueIndex;
	}

	/**
	 * @param columnValueIndex the columnValueIndex to set
	 */
	public void setColumnValueIndex(int columnValueIndex) {
		this.columnValueIndex = columnValueIndex;
	}

	/**
	 * @param dataSet the dataSet to set
	 */
	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}
	
	public DataSet getDataSet() {
		return dataSet;
	}
	
	public Element getElement(){
		Element e = DocumentHelper.createElement("filterData");
		e.addAttribute("columnLabelIndex", "" + getColumnLabelIndex());
		e.addAttribute("columnValueIndex", "" + getColumnValueIndex());
		
		e.addAttribute("columnOrderIndex", "" + getColumnOrderIndex());;
		
		if (dataSet != null){
			e.addElement("dataSet-ref").setText(dataSet.getName());
		}
		if (getOrderType() != null){
			e.addAttribute("orderType", "" + getOrderType().name());
		}
		
		return e;
	}

	public IComponentDatas getAdapter(Object o) {
		
		return this;
	}
	
	public OrderingType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderingType orderType) {
		this.orderType = orderType;
	}
	
	public boolean isFullyDefined() {
		if (dataSet == null){
			return false;
		}
		
		if (dataSet.getDataSetDescriptor() == null){
			return false;
		}
		
		
		
		if (getColumnLabelIndex() >=  dataSet.getDataSetDescriptor().getColumnsDescriptors().size()){
			return false;
		}
		

		if (getColumnOrderIndex() >=  dataSet.getDataSetDescriptor().getColumnsDescriptors().size()){
			return false;
		}
		
		if (getColumnOrderIndex() >=  dataSet.getDataSetDescriptor().getColumnsDescriptors().size()){
			return false;
		}
		if (getColumnValueIndex() >=  dataSet.getDataSetDescriptor().getColumnsDescriptors().size()){
			return false;
		}
		return true;
	}

	@Override
	public IComponentDatas copy() {
		FilterData copy = new FilterData();
		
		copy.setColumnLabelIndex(columnLabelIndex);
		copy.setColumnOrderIndex(columnOrderIndex);
		copy.setColumnValueIndex(columnValueIndex);
		copy.setDataSet(dataSet);
		
		return copy;
	}
}
