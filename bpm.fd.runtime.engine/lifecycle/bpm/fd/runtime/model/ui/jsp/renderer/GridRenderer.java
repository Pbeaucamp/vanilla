package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.fd.api.core.model.components.definition.OrderingType;
import bpm.fd.api.core.model.components.definition.datagrid.Aggregation;
import bpm.fd.api.core.model.components.definition.datagrid.ComponentDataGrid;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridColumnOption;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridColumnOption.DataGridCellType;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridData;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridDrill.DrillTarget;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridOptions;
import bpm.fd.api.core.model.datas.ColumnDescriptor;
import bpm.fd.runtime.engine.datas.helper.Sorter;
import bpm.fd.runtime.model.DashState;

public class GridRenderer extends AsbtractCssApplier implements IHTMLRenderer<ComponentDataGrid> {

	public String getJavaScriptFdObjectVariable(ComponentDataGrid grid) {
		StringBuffer buf = new StringBuffer();
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + grid.getName() + "\"]= new FdDataGrid(\"" + grid.getName() + "\")" + ";\n");
		buf.append("</script>\n");
		return buf.toString();
	}

	public String getHTML(Rectangle _layout, ComponentDataGrid grid, DashState state, IResultSet datas, boolean refresh) {

		try {
			DataGridOptions opt = (DataGridOptions) grid.getOptions(DataGridOptions.class);
			DataGridData dgd = (DataGridData) grid.getDatas();

			StringBuffer buf = new StringBuffer();
			buf.append(this.getComponentDefinitionDivStart(_layout, grid));

			// generate table
			StringBuilder layoutS = new StringBuilder();
			if (_layout != null) {
				layoutS.append(" style='height:");
				layoutS.append(_layout.height);
				layoutS.append("px;");
				layoutS.append("width:");
				layoutS.append(_layout.width);
				layoutS.append("px;'");
			}

			// generate table
			StringBuffer content = new StringBuffer();
			content.append("<table class=\"table100\" " + layoutS.toString() + ">");
			int countCols = 0;

			// header
			if (opt.isHeadersVisible()) {
				content.append("<thead>");
				content.append("<tr class=\"table100-head\">");

				int index = 0;
				for (DataGridColumnOption c : grid.getLayout().getColumnsDescriptors()) {

					// We set the index to manage data after
					for (int i = 0; i < grid.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors().size(); i++) {
						ColumnDescriptor column = grid.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(i);
						if (column.getName().equals(c.getName())) {
							c.setIndex(i);
							break;
						}
					}

					DataGridCellType type = c.getCellType();

					if (c.getCustomName() != null && !c.getCustomName().isEmpty()) {
						if (type != DataGridCellType.Hidden) {
							content.append("<th class=\"column" + index + "\">" + c.getCustomName() + "</th>");
						}
						else {
							content.append("<th  style='display:none;'  >&nbsp;</th>");
						}
					}
					else {
						if (type != DataGridCellType.Hidden) {

							content.append("<th class=\"column" + index + "\">" + c.getName() + "</th>");
						}
						else {
							content.append("<th  style='display:none;' >&nbsp;</th>");
						}
					}

					index++;

					countCols++;
				}
				content.append("</tr>");
				content.append("</thead>");
			}

			// body
			content.append("<tbody>");

			try {
				Data data = prepareData(datas, dgd, grid);

				StringBuffer gridDatas = new StringBuffer();
				
				if (data.hasGroup()) {
					addGridGroup(gridDatas, grid, data, 0, 1);
				}
				else {
					List<Row> rows = data.getRows();
					
					if (rows != null) {
						int index = 0;
						for (Row row : rows) {

							gridDatas.append("<tr>\n");
							for (DataGridColumnOption column : row.getColumns()) {
								Object value = row.getValue(column);
								
								addGridValue(gridDatas, grid, column, value, index, false);
								
								index++;
							}
							gridDatas.append("</tr>\n");
						}
					}
				}
				
				content.append(gridDatas.toString());
			} catch (Exception ex) {
				ex.printStackTrace();
				content.append("<tr><td colspan=\"" + countCols + "\">Failed to get Datas : " + ex.getMessage() + "</td></tr>");
			}

			content.append("</tbody>");

			content.append("</table>");
			buf.append(content.toString());
			buf.append(this.getComponentDefinitionDivEnd());

			// Javascript
			buf.append("<script type=\"text/javascript\">\n");
			buf.append("    fdObjects[\"" + grid.getName() + "\"]= new FdDataGrid(\"" + grid.getName() + "\")" + ";\n");
			buf.append("</script>\n");

			if (!refresh) {
				return buf.toString();
			}
			else {
				return content.toString();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return "<div id=\"" + grid.getName() + "\" name=\"" + grid.getId() + "\">Failed to render :" + ex.getMessage() + "</div>\n";
		}
	}
	
	private void addGridGroup(StringBuffer gridDatas, ComponentDataGrid grid, Data data, int line, int nbGroups) {
		DataGridColumnOption column = data.getColumn();
		
		HashMap<Object, Data> groupData = data.getData();
		for (Object groupValue : groupData.keySet()) {
			if (nbGroups == 1) {
				line = 0;
			}
			
			Data d = groupData.get(groupValue);
			
			if (line == 0) {
				gridDatas.append("<tr>\n");
				line++;
			}
			else if (line > 1) {
				for (int j = 0; j < nbGroups - 1; j++) {
					gridDatas.append("<td>&nbsp;</td>\n");
				}
			}
			
			int index = 0;
			addGridValue(gridDatas, grid, column, groupValue, index, false);

			if (d.hasGroup()) {
				addGridGroup(gridDatas, grid, d, line, nbGroups + 1);
			}
			else {
				List<Row> rows = d.getRows();
				if (rows != null) {
					int i = 0;
					for (Row row : rows) {

						if (i != 0) {
							gridDatas.append("<tr>\n");
							
							for (int j = 0; j < nbGroups; j++) {
								gridDatas.append("<td>&nbsp;</td>\n");
							}
						}
						
						for (DataGridColumnOption childCol : row.getColumns()) {
							Object value = row.getValue(childCol);
							
							addGridValue(gridDatas, grid, childCol, value, index, false);
							
							index++;
						}
						gridDatas.append("</tr>\n");
						line++;
						i++;
					}
					
					addTotalGroup(gridDatas,  grid, d);
				}
			}
		}
		
		addTotalGroup(gridDatas,  grid, data);
	}
		
	private void addTotalGroup(StringBuffer gridDatas, ComponentDataGrid grid, Data d) {
		if (d.hasTotal() && d.getGroupParent() != null) {
			int index = 0;

			gridDatas.append("<tr class=\"tableTotal\">\n");
			
			boolean isTotalSet = false;
			for (DataGridColumnOption c : grid.getLayout().getColumnsDescriptors()) {
				if (d.getGroupParent().getColumn().equals(c)) {
					addGridValue(gridDatas, grid, c, "Total", index, true);
					isTotalSet = true;
				}
				else if (c.getCellType() != DataGridCellType.Hidden) {
					Value value = d.getTotal(c);
					if (value != null) {
						if (c.getAggregation() == Aggregation.COUNT) {
							addGridValue(gridDatas, grid, c, value.getCount(), index, true);
						}
						else {
							Double total = c.getAggregation() == Aggregation.AVG ? value.getAverage() : value.getGroupValue();
							
							addGridValue(gridDatas, grid, c, total, index, true);
						}
					}
					else {
						String classCell = isTotalSet ? "cellTotal" : "";
						
						gridDatas.append("<td class=\"" + classCell + "\">&nbsp;</td>\n");
					}
				}
				
				index++;
			}

			gridDatas.append("</tr>\n");
		}
	}
	
	private void addGridValue(StringBuffer gridDatas, ComponentDataGrid grid, DataGridColumnOption column, Object value, int index, boolean isTotal) {
		String borderClass = isTotal ? "cellTotal" : "cellWithBorder";
		
		String layout = "";
		switch (column.getCellType()) {
		case Editable:
			break;
		case Hidden:
			layout = " type='hidden' ";
			break;
		default:
			layout = " disabled='true' ";
		}

		String tdStyle = "";
		if (column.getCellType() == DataGridCellType.Hidden) {
			tdStyle = " style='display:none;' ";
		}

		if (grid.getDrillInfo().getDrillableFieldIndex() != null && grid.getDrillInfo().getDrillableFieldIndex().intValue() == column.getIndex()) {
			gridDatas.append("<td class=\"" + borderClass + " column" + index + "\"" + tdStyle + "><a href='#' onclick=\"javascript:" + "setParameter('" + grid.getName() + "','" + value + "', true);");
			if (grid.getDrillInfo().getType() == DrillTarget.PopupPage) {
				gridDatas.append("popupModelPage('" + grid.getDrillInfo().getModelPage().getName() + "'," + grid.getDrillInfo().getPopupWidth() + " , " + grid.getDrillInfo().getPopupHeight() + ");");
			}

			gridDatas.append("\" >" + value + "</a></td>");
		}
		else {
			gridDatas.append("<td class=\"" + borderClass + " column" + index + "\"" + tdStyle + "><input id='" + grid.getId() + "_col" + grid.getId() + "RowNumber_row" + index + "' " + layout + " value='" + value + "' /></td>\n");
		}
	}

	private Data prepareData(IResultSet datas, DataGridData dgd, ComponentDataGrid grid) throws OdaException {
		Data finalData = new Data();
		
		List<List<Object>> values = Sorter.sort(datas, dgd.getOrderType() == OrderingType.NONE ? -1 : dgd.getOrderFieldPosition(), dgd.getOrderType());

		Iterator<List<Object>> it = values.iterator();
		while (it != null && it.hasNext()) {
			Data groupParent = null;
			if (groupParent == null) {
				groupParent = finalData;
			}
			
			List<Object> row = it.next();
			try {
				Row newRow = new Row();
				for (DataGridColumnOption c : grid.getLayout().getColumnsDescriptors()) {
					Object value = row.get(c.getIndex());

					if (c.isGroup()) {
						groupParent.setColumn(c);
						
						Data data = groupParent.getGroupData(value);
						if (data == null) {
							data = groupParent.addGroupData(value);
						}
						groupParent = data;
					}
					else {
						newRow.setValue(c, value);
						
						if (c.getAggregation() != null) {
							updateGroupTotals(groupParent, c, value);
						}
					}
				}
				groupParent.addRow(newRow);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return finalData;
	}

	private void updateGroupTotals(Data group, DataGridColumnOption c, Object value) {
		group.updateValue(c, value);
		if (group.getGroupParent() != null) {
			updateGroupTotals(group.getGroupParent(), c, value);
		}
	}

	private class Row {

		private HashMap<DataGridColumnOption, Object> values;
		
		public void setValue(DataGridColumnOption column, Object value) {
			if (this.values == null) {
				this.values = new LinkedHashMap<DataGridColumnOption, Object>();
			}
			
			this.values.put(column, value);
		}
		
		public Collection<DataGridColumnOption> getColumns() {
			return values != null ? values.keySet() : new ArrayList<DataGridColumnOption>();
		}
		
		public Object getValue(DataGridColumnOption column) {
			return values.get(column);
		}
	}

	private class Data {
		
		private Data groupParent;

		private DataGridColumnOption column;
		private HashMap<Object, Data> data = new LinkedHashMap<Object, Data>();
		private List<Row> rows;
		private HashMap<DataGridColumnOption, Value> totals;
		
		public Data() {
		}
		
		public Data getGroupParent() {
			return groupParent;
		}
		
		public void setGroupParent(Data groupParent) {
			this.groupParent = groupParent;
		}
		
		public DataGridColumnOption getColumn() {
			return column;
		}
		
		public void setColumn(DataGridColumnOption column) {
			this.column = column;
		}
		
		public boolean hasGroup() {
			return data != null && data.keySet() != null && !data.keySet().isEmpty();
		}
		
		public HashMap<Object, Data> getData() {
			return data;
		}

		public Data getGroupData(Object value) {
			return data.get(value);
		}
		
		private Data addGroupData(Object value) {
			Data valueData = new Data();
			valueData.setGroupParent(this);
			
			data.put(value, valueData);
			return valueData;
		}
		
		public List<Row> getRows() {
			return rows;
		}
		
		private void addRow(Row row) {
			if (this.rows == null) {
				this.rows = new ArrayList<Row>();
			}
			this.rows.add(row);
		}
		
		private boolean hasTotal() {
			return totals != null && !totals.isEmpty();
		}
		
		private Value getTotal(DataGridColumnOption column) {
			return totals != null ? totals.get(column) : null;
		}

		private void updateValue(DataGridColumnOption column, Object newValue) {
			if (totals == null) {
				this.totals = new LinkedHashMap<DataGridColumnOption, Value>();
			}
			
			Value value = totals.get(column);
			if (value == null) {
				value = new Value();
			}
			
			Double groupValue = value != null ? value.getGroupValue() : null;
			Double newValueAsDouble = null;
			try {
				newValueAsDouble = Double.parseDouble(newValue.toString());
			} catch(Exception e) {
				//Cannot parse value, we can't group it
			}
			
			
			switch (column.getAggregation()) {
			case SUM:
			case AVG:
				if (groupValue == null) {
					groupValue = 0D;
				}
				
				//TODO: DATAGRID Que fait on si la valeur est null
				if (newValueAsDouble == null)
					return;
				
				groupValue = groupValue + newValueAsDouble;
				
				break;
			case COUNT:
				if (groupValue == null) {
					groupValue = 0D;
				}
				
				groupValue++;
				
				break;
			case MIN:
				
				//TODO: DATAGRID Que fait on si la valeur est null
				if (newValueAsDouble == null)
					return;
				
				if (groupValue == null || groupValue > newValueAsDouble) {
					groupValue = newValueAsDouble;
				}
				
				break;
			case MAX:
				
				//TODO: DATAGRID Que fait on si la valeur est null
				if (newValueAsDouble == null)
					return;
				
				if (groupValue == null || groupValue < newValueAsDouble) {
					groupValue = newValueAsDouble;
				}
				
				break;
			default:
				break;
			}
			value.updateValue(groupValue);
			
			totals.put(column, value);
		}
	}

	private class Value {

		private Double groupValue;
		private int size;

		public void updateValue(Double groupValue) {
			this.groupValue = groupValue;
			this.size++;
		}

		public Double getGroupValue() {
			return groupValue;
		}

		public Double getAverage() {
			return groupValue / size;
		}

		public Integer getCount() {
			return groupValue.intValue();
		}
	}
}
