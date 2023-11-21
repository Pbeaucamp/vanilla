package bpm.fd.api.core.model.components.definition.styledtext;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentDatas;
import bpm.fd.api.core.model.datas.DataSet;

public class DynamicLabelData implements IComponentDatas {

	private DataSet dataSet;

	private int columnValueIndex = -1;

	/**
	 * @return the columnValueIndex
	 */
	public int getColumnValueIndex() {
		return columnValueIndex;
	}

	/**
	 * @param columnValueIndex
	 *            the columnValueIndex to set
	 */
	public void setColumnValueIndex(int columnValueIndex) {
		this.columnValueIndex = columnValueIndex;
	}

	/**
	 * @param dataSet
	 *            the dataSet to set
	 */
	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}

	public DataSet getDataSet() {
		return dataSet;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("dynamicLabelData");
		e.addAttribute("columnValueIndex", "" + getColumnValueIndex());

		if (dataSet != null) {
			e.addElement("dataSet-ref").setText(dataSet.getName());
		}

		return e;
	}

	public IComponentDatas getAdapter(Object o) {
		return this;
	}

	public boolean isFullyDefined() {
		if (dataSet == null) {
			return false;
		}

		if (dataSet.getDataSetDescriptor() == null) {
			return false;
		}

		if (getColumnValueIndex() >= dataSet.getDataSetDescriptor().getColumnsDescriptors().size()) {
			return false;
		}
		return true;
	}

	@Override
	public IComponentDatas copy() {
		DynamicLabelData copy = new DynamicLabelData();

		copy.setColumnValueIndex(columnValueIndex);
		copy.setDataSet(dataSet);

		return copy;
	}
}
