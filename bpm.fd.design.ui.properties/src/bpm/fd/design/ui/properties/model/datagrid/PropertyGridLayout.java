package bpm.fd.design.ui.properties.model.datagrid;

import org.eclipse.jface.viewers.CellEditor;

import bpm.fd.api.core.model.components.definition.datagrid.DataGridLayout;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridColumnOption.DataGridCellType;
import bpm.fd.design.ui.properties.model.Property;

public class PropertyGridLayout extends Property{
	public static final int HEADER_LABEL = 1;
	public static final int HEADER_TYPE = 2;
	
	private int type;
	private DataGridLayout layout; 
	private int fieldIndex;
	public PropertyGridLayout(int type, DataGridLayout layout, int fieldIndex, String name, CellEditor cellEditor) {
		super(name, cellEditor);
		this.type = type;
		this.layout = layout;
		this.fieldIndex = fieldIndex;
	}

	public String getPropertyValueString(){
		switch (type) {
		case HEADER_LABEL:
			String s = layout.getColumnsDescriptors().get(fieldIndex).getName();
			if (s==null){
				s=""; //$NON-NLS-1$
			}
			return s;
		case HEADER_TYPE:
			return layout.getColumnsDescriptors().get(fieldIndex).getCellType().name();
		}
		return null;
	}
	
	public Object getPropertyValue(){
		switch (type) {
		case HEADER_LABEL:
			String s = layout.getColumnsDescriptors().get(fieldIndex).getName();
			if (s==null){
				s=""; //$NON-NLS-1$
			}
			return s;
		case HEADER_TYPE:
			return layout.getColumnsDescriptors().get(fieldIndex).getCellType().name();
		}
		return null;
	}
	
	public void setPropertyValue(Object value){
		switch (type) {
		case HEADER_LABEL:
			layout.setHeaderTitle(fieldIndex, (String)value);
			return;
		case HEADER_TYPE:
			layout.setFieldType(fieldIndex, (DataGridCellType)value);
			return;
		}
	}
	
}
