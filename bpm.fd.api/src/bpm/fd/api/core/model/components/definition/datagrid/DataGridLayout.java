package bpm.fd.api.core.model.components.definition.datagrid;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.datagrid.DataGridColumnOption.DataGridCellType;

public class DataGridLayout {

	private List<DataGridColumnOption> columnOptions;

	private List<String> colors = new ArrayList<String>();
	private boolean useColor = false;
	private Integer colorFieldIndex;
	
	public List<DataGridColumnOption> getColumnOptions() {
		return columnOptions;
	}
	
	public void setColumnOptions(List<DataGridColumnOption> columnOptions) {
		this.columnOptions = columnOptions;
	}

	public void setFieldType(int indexField, DataGridCellType type) {
		if (columnOptions == null) {
			this.columnOptions = new ArrayList<DataGridColumnOption>();
		}
		
		//TODO: DATAGRID
//		for (int i = columnOptions.size(); i <= indexField; i++) {
//			columnOptions.add(new DataGridColumnOption(DataGridCellType.Visible));
//		}

		if (columnOptions.get(indexField) != null) {
			columnOptions.get(indexField).setCellType(type);
		}
	}

	public void setHeaderTitle(int indexField, String header) {
		if (columnOptions == null) {
			this.columnOptions = new ArrayList<DataGridColumnOption>();
		}
		

		//TODO: DATAGRID
//		for (int i = columnOptions.size(); i <= indexField; i++) {
//			columnOptions.add(new DataGridColumnOption());
//		}

		if (columnOptions.get(indexField) != null) {
			columnOptions.get(indexField).setCustomName(header);
		}
	}

//	public String getHeaderTitle(int pos) {
//		if (columnOptions == null || pos >= columnOptions.size()) {
//			return null;
//		}
//		return columnOptions.get(pos).getCustomName();
//	}
//
//	public DataGridCellType getType(int pos) {
//		if (columnOptions == null || pos >= columnOptions.size()) {
//			return DataGridCellType.Visible;
//		}
//		return columnOptions.get(pos).getCellType();
//	}
	
	public List<DataGridColumnOption> getColumnsDescriptors() {
		return columnOptions;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("dataLayout");

		Element optionsEl = e.addElement("columnOptions");
		if (columnOptions != null) {
			for (DataGridColumnOption option : columnOptions) {
				optionsEl.add(option.getElement());
			}
		}
//		int pos = 0;
//		for (DataGridCellType i : fieldsType) {
//			e.addElement("layout-property").addAttribute("position", "" + pos++).addAttribute("type", i.toString());
//		}
//		pos = 0;
//		for (String i : headerNames) {
//			e.addElement("header-property").addAttribute("position", "" + pos++).addAttribute("label", i != null ? i : "");
//		}

		e.addAttribute("useColor", useColor + "");
		if (colorFieldIndex != null) {
			e.addAttribute("colorFieldIndex", colorFieldIndex + "");
		}
		for (String s : colors) {
			e.addElement("color").addAttribute("rgb", s);
		}
		return e;
	}

	public List<String> getColors() {
		return new ArrayList<String>(colors);
	}

	public void addColor(String s) {
		if (!Pattern.matches("[a-zA-Z0-9]{2}[a-zA-Z0-9]{2}[a-zA-Z0-9]{2}", s)) {
			return;
		}

		colors.add(s);
	}

	public void removeColor(String s) {
		for (String _s : colors) {
			if (s.equals(_s)) {
				colors.remove(_s);
				return;
			}
		}
	}

	public void setColor(int pos, String color) {
		colors.set(pos, color);
	}

	public boolean isUseColor() {
		return useColor;
	}

	public void setUseColors(boolean selection) {
		this.useColor = selection;
	}

	public Integer getColorFieldIndex() {
		return colorFieldIndex;
	}

	public void setColorFieldIndex(Integer i) {
		this.colorFieldIndex = i;
	}
}
