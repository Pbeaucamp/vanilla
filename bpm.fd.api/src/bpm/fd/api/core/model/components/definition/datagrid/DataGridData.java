package bpm.fd.api.core.model.components.definition.datagrid;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentDatas;
import bpm.fd.api.core.model.components.definition.OrderingType;
import bpm.fd.api.core.model.datas.DataSet;

public class DataGridData implements IComponentDatas{

	private DataSet dataSet;
	private OrderingType orderType = OrderingType.NONE;
	private Integer orderFieldPosition;
	
	
	public IComponentDatas getAdapter(Object o) {
		return this;
	}

	public DataSet getDataSet() {
		return dataSet;
	}

	public OrderingType getOrderType() {
		return orderType;
	}
	
	public void setOrderType(OrderingType orderType) {
		this.orderType = orderType;
	}

	/**
	 * @return the orderFieldPosition
	 */
	public Integer getOrderFieldPosition() {
		return orderFieldPosition;
	}

	/**
	 * @param orderFieldPosition the orderFieldPosition to set
	 */
	public void setOrderFieldPosition(Integer orderFieldPosition) {
		this.orderFieldPosition = orderFieldPosition;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("dataGridData");
		
		if (dataSet != null){
			e.addElement("dataSet-ref").setText(dataSet.getName());
		}
		e.addAttribute("orderDatas", getOrderType().name());
		if (orderFieldPosition != null){
			e.addElement("orderField").setText(orderFieldPosition + "");
		}
		
		
		return e;
	}

	public boolean isFullyDefined() {
		return dataSet != null;
	}
	
	/**
	 * @param dataSet the dataSet to set
	 * if the OrderFieldPosition is defined, it may be reset to null if the new dataset does
	 * not contains enough columns
	 */
	public void setDataSet(DataSet dataSet) {
		if (orderFieldPosition != null){
			//to avoid the outofbound problems in the designer
			if (orderFieldPosition >= dataSet.getDataSetDescriptor().getColumnsDescriptors().size()){
				setOrderFieldPosition(null);
			}
			
		}
		this.dataSet = dataSet;
	}

	@Override
	public IComponentDatas copy() {
		DataGridData copy = new DataGridData();
		
		copy.setDataSet(dataSet);
		copy.setOrderType(orderType);
		copy.setOrderFieldPosition(orderFieldPosition);
		
		return copy;
	}
	
	

}
