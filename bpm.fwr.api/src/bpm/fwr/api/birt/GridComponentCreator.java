package bpm.fwr.api.birt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.birt.report.model.api.CellHandle;
import org.eclipse.birt.report.model.api.DataItemHandle;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ElementFactory;
import org.eclipse.birt.report.model.api.GridHandle;
import org.eclipse.birt.report.model.api.LabelHandle;
import org.eclipse.birt.report.model.api.PropertyHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.RowHandle;
import org.eclipse.birt.report.model.api.SharedStyleHandle;
import org.eclipse.birt.report.model.api.StructureFactory;
import org.eclipse.birt.report.model.api.StyleHandle;
import org.eclipse.birt.report.model.api.TableGroupHandle;
import org.eclipse.birt.report.model.api.TableHandle;
import org.eclipse.birt.report.model.api.TextDataHandle;
import org.eclipse.birt.report.model.api.TextItemHandle;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.eclipse.birt.report.model.api.elements.structures.AggregationArgument;
import org.eclipse.birt.report.model.api.elements.structures.ComputedColumn;
import org.eclipse.birt.report.model.api.elements.structures.HighlightRule;
import org.eclipse.birt.report.model.api.elements.structures.SortKey;

import bpm.fwr.api.FwrReportManager;
import bpm.fwr.api.beans.Agregate;
import bpm.fwr.api.beans.HyperlinkColumn;
import bpm.fwr.api.beans.Constants.Colors;
import bpm.fwr.api.beans.Constants.Formats;
import bpm.fwr.api.beans.components.GridComponent;
import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.Column.SubType;
import bpm.fwr.api.beans.dataset.GroupAgregation;
import bpm.fwr.api.beans.template.Style;

public class GridComponentCreator implements IComponentCreator<GridComponent> {

	private String[] groupGreyStyle = { "#4F81BD", "#79ABD5", "#99BFDF", "#B0CEE6", "#C8DDEE", "#D6E5F2" };

	@Override
	public DesignElementHandle createComponent(ReportDesignHandle designHandle, ElementFactory designFactory, String selectedLanguage, GridComponent component, String outputFormat) throws Exception {
		int detailColumn = getNumberDetailColumn(component);
		String datasetLanguage = component.getDataset().getLanguage();

		TableHandle table = null;
		if (component.isAutomaticGroupingList()) {
			table = designFactory.newTableItem("Table_" + new Object().hashCode(), detailColumn + 1);
		}
		else {
			table = designFactory.newTableItem("Table_" + new Object().hashCode(), detailColumn);
		}
		table.setPageBreakInterval(0);
		table.setWidth("100%");
		if (component.getDataset() != null && !component.getDataset().equals("")) {
			table.setDataSet(designHandle.findDataSet(component.getDataset().getName()));
		}
		else {
			table.setDataSet(designHandle.findDataSet("ds"));
		}

		PropertyHandle computedSet = table.getColumnBindings();

		List<Column> all = new ArrayList<Column>();
		all.addAll(component.getGroups());
		all.addAll(component.getColumns());

		for (Column c : all) {
			ComputedColumn compColumn = createComputedColumn(c, selectedLanguage, datasetLanguage);
			computedSet.addItem(compColumn);
		}

		// Create report groups
		if (component.getGroups() != null) {
			List<TableGroupHandle> groups = createReportGroups(designHandle, designFactory, component, computedSet, selectedLanguage, datasetLanguage, detailColumn);
			if (groups != null) {
				for (TableGroupHandle group : groups) {
					table.getGroups().add(group);
				}
			}
		}

		if (component.getAgregates() != null && !component.getAgregates().isEmpty()) {
			createReportAgregates(component, computedSet, table, selectedLanguage);
		}

		if (component.showHeader()) {
			showHeader(designFactory, component, computedSet, table, all, selectedLanguage, detailColumn);
		}

		RowHandle tabledetail = (RowHandle) table.getDetail().get(0);
		buildMainPart(designHandle, designFactory, component, computedSet, table, all, tabledetail, selectedLanguage, datasetLanguage, detailColumn);

//		table.dro
		
//		((TableGroupHandle) table.getGroups().get(0)).getFooter().add(footergroup);
		
		return table;
	}

	private ComputedColumn createComputedColumn(Column column, String selectedLanguage, String datasetLanguage) {
		ComputedColumn cs1 = StructureFactory.createComputedColumn();
		cs1.setName(column.getBusinessTableParent() + "_" + column.getTitle(selectedLanguage));
		cs1.setDisplayName(column.getBusinessTableParent() + "_" + column.getTitle(datasetLanguage));
		if (column.isCalculated() && (column.getInvolvedDatastreams() == null || column.getInvolvedDatastreams().size() == 0)) {
			cs1.setExpression(column.getExpression());
		}
		else {
			cs1.setExpression("dataSetRow[\"" + column.getDatasetRowExpr(selectedLanguage) + "\"]");
		}

		if (column.getFormat() != null && !column.getFormat().isEmpty()) {
			boolean isDate = false;
			for (String form : Formats.DATE_FORMATS) {
				if (form.equals(column.getFormat())) {
					isDate = true;
					break;
				}
			}
			if (isDate) {
				cs1.setDataType(DesignChoiceConstants.PARAM_TYPE_DATE);
			}
			else {
				cs1.setDataType(DesignChoiceConstants.PARAM_TYPE_DECIMAL);
			}
		}
		else {
			cs1.setDataType("string");
		}
		return cs1;
	}

	private List<TableGroupHandle> createReportGroups(ReportDesignHandle designHandle, ElementFactory designFactory, GridComponent component, PropertyHandle computedSet, String selectedLanguage, String datasetLanguage, int detailColumn) throws Exception {
		List<TableGroupHandle> groups = new ArrayList<TableGroupHandle>();

		for (int i = 0; i < component.getGroups().size(); i++) {
			Column c = component.getGroups().get(i);

			String key = "dataSetRow[\"" + c.getDatasetRowExpr(selectedLanguage) + "\"]";

			TableGroupHandle group = designHandle.getElementFactory().newTableGroup();
			group.setName("Group" + c.getTitle(selectedLanguage).replace(".", ""));
			group.setRepeatHeader(false);
			group.setKeyExpr(key);

			if (c.isSorted()) {
				SortKey sortKey = getSortKey(c, "dataSetRow[\"" + c.getDatasetRowExpr(selectedLanguage) + "\"]");

				PropertyHandle prop = group.getPropertyHandle(TableGroupHandle.SORT_PROP);
				prop.addItem(sortKey);
			}

			// add report group agregates
			if (component.getAgregates() != null && !component.getAgregates().isEmpty() && i == component.getGroups().size() - 1) {
				RowHandle footergroup = designFactory.newTableRow();
				for (int k = 0; k < detailColumn; k++) {
					CellHandle cell = designFactory.newCell();
					footergroup.getCells().add(cell);
				}

				for (Agregate a : component.getAgregates()) {
					for (int j = 0; j < component.getColumns().size(); j++) {
						if (component.getColumns().get(j).getTitle(datasetLanguage).equalsIgnoreCase(a.getOnColumn())) {
							DataItemHandle colcalc = designFactory.newDataItem(a.getOnColumn() + "." + a.getFormula());
							colcalc.setProperty("resultSetColumn", a.getFormula());
							if (component.getDataCellsStyle() != null) {
								colcalc.setProperties(FwrReportManager.addBirtStyle(component.getDataCellsStyle()));
							}
							colcalc.setProperty("backgroundColor", "white");
							colcalc.setProperty("fontWeight", "bold");

							CellHandle cell = (CellHandle) footergroup.getCells().get(j);
							cell.getContent().add(colcalc);
							footergroup.getCells().get(j).addElement(cell, j);

							TextItemHandle nomform = designFactory.newTextItem("nomformule" + j);

							CellHandle cellf = (CellHandle) footergroup.getCells().get(j);
							cellf.getContent().add(nomform);
							footergroup.getCells().get(j).addElement(cellf, j + 1);

						}
					}
				}

				group.getFooter().add(footergroup, 0);
			}

			if (component.isAutomaticGroupingList()) {
				RowHandle aggGroup = designFactory.newTableRow();
				group.getHeader().add(aggGroup, 0);
				
				for (int k = 0; k < detailColumn + 1; k++) {
					CellHandle cell = designFactory.newCell();
					aggGroup.getCells().add(cell);
				}

				if (i < groupGreyStyle.length) {
					aggGroup.setProperty(StyleHandle.BACKGROUND_COLOR_PROP, groupGreyStyle[i]);
				}
				else {
					aggGroup.setProperty(StyleHandle.BACKGROUND_COLOR_PROP, groupGreyStyle[groupGreyStyle.length - 1]);
				}

				for (int j = 0; j < component.getColumns().size(); j++) {
					CellHandle cell2 = (CellHandle) aggGroup.getCells().get(j + 1);

					Column col = component.getColumns().get(j);

					AggregationArgument argument = new AggregationArgument();
					argument.setName("Expression");
					argument.setValue("dataSetRow[\"" + col.getDatasetRowExpr(selectedLanguage) + "\"]");

					String nom = group.getName() + "." + col.getName() + "." + col.getFormula();

					ComputedColumn cs2 = StructureFactory.createComputedColumn();
					cs2.setAggregateOn(group.getName());
					cs2.setDataType("Integer");
					cs2.setDisplayName(nom);
					cs2.setName(nom);
					cs2.setAggregateFunction(findAgregateFunction(col.getType()));
					cs2.addArgument(argument);

					computedSet.addItem(cs2);

					DataItemHandle dataAgg = designFactory.newDataItem("dataAgg." + nom);
					dataAgg.setProperty(StyleHandle.TEXT_ALIGN_PROP, "right");
					dataAgg.setProperty(StyleHandle.FONT_SIZE_PROP, "8");
					dataAgg.setProperty(StyleHandle.PADDING_RIGHT_PROP, "10");
					if (i == 0) {
						dataAgg.setProperty(StyleHandle.COLOR_PROP, FwrReportManager.birtcolor(Colors.WHITE));
					}
					dataAgg.setResultSetColumn(nom);
					cell2.getContent().add(dataAgg);
				}
				
//				iterateAndDeleteFrom(group.getFooter(), 0);
			}

			groups.add(group);
		}

		return groups;
	}

	private void createReportAgregates(GridComponent component, PropertyHandle computedSet, TableHandle table, String selectedLanguage) throws Exception {
		for (Agregate a : component.getAgregates()) {
			String nom = a.getOnColumn() + "." + a.getFormula();

			ComputedColumn cs2 = StructureFactory.createComputedColumn();
			cs2.setAggregateOn(table.getGroups().get(table.getGroups().getCount() - 1).getName());
			cs2.setDataType("Integer");
			cs2.setDisplayName(nom);
			cs2.setName(nom);
			cs2.setAggregateFunction(FwrReportManager.birtExpression(a.getFormula()));
			AggregationArgument argument = new AggregationArgument();
			argument.setName("Expression");
			String colomne = "";

			for (Column c : component.getColumns()) {
				if (c.getName().equalsIgnoreCase(a.getOnColumn())) {
					colomne = c.getDatasetRowExpr(selectedLanguage);
				}
			}

			argument.setValue("dataSetRow[\"" + colomne + "\"]");
			cs2.addArgument(argument);
			computedSet.addItem(cs2);

			ComputedColumn cs3 = StructureFactory.createComputedColumn();
			cs3.setName(a.getFormula());
			cs3.setDisplayName(nom);
			cs3.setExpression("\"" + a.getFormula() + "\"+\" \" + row[\"" + nom + "\"]");
			cs3.setDataType("string");
			computedSet.addItem(cs3);
		}
	}

	private void showHeader(ElementFactory designFactory, GridComponent component, PropertyHandle computedSet, TableHandle table, List<Column> all, String selectedLanguage, int detailColumn) throws Exception {
		// Part for hidding detail at runtime in html
		StringBuffer buf = new StringBuffer("<script>");
		buf.append("function hideDetail() {");
		buf.append("var imgMoins = document.getElementById('imgMoins');");
		buf.append("var imgPlus = document.getElementById('imgPlus');");
		buf.append("imgMoins.style.visibility = \"hidden\";");
		buf.append("imgPlus.style.visibility = \"visible\";");
		buf.append("var array = document.getElementsByClassName('classReference');");
		buf.append("for(var i in array){");
		buf.append("var parentTr = array[i].parentNode.parentNode;");
		// buf.append("parentTr.style.visibility = \"hidden\";");
		buf.append("parentTr.style.display = \"none\";");
		buf.append("}");
		buf.append("}");
		buf.append("function showDetail() {");
		buf.append("var imgMoins = document.getElementById('imgMoins');	");
		buf.append("var imgPlus = document.getElementById('imgPlus');");
		buf.append("imgMoins.style.visibility = \"visible\";");
		buf.append("imgPlus.style.visibility = \"hidden\";");
		buf.append("var array = document.getElementsByClassName('classReference');");
		buf.append("for(var i in array){");
		buf.append("var parentTr = array[i].parentNode.parentNode;");
		// buf.append("parentTr.style.visibility = \"visible\";");
		buf.append("parentTr.style.display = \"table-cell\";");
		buf.append("}");
		buf.append("}");
		buf.append("</script>");
		buf.append("<img id=\"imgMoins\" href=\"javascript:;\" onClick=\"hideDetail();\" src=\"http://www.agiciel.fr/images/IconeMoins.png\"/>");
		buf.append("<img id=\"imgPlus\" href=\"javascript:;\" onClick=\"showDetail();\" src=\"http://agiciel.com/images/IconePlus.png\" style=\"visibility:hidden;\"/>");

		TextItemHandle btnPlusAndMinus = designFactory.newTextItem("title" + new Object().hashCode());
		btnPlusAndMinus.setContentType("html");
		btnPlusAndMinus.setContent(buf.toString());
		btnPlusAndMinus.setProperty(StyleHandle.TEXT_ALIGN_PROP, "right");
		btnPlusAndMinus.setProperty(StyleHandle.MARGIN_TOP_PROP, "1px");
		btnPlusAndMinus.setProperty(StyleHandle.MARGIN_RIGHT_PROP, "5px");

		// add the selected columns and groups labels
		int index = 0;
		int paddingLeft = 5;
		int indexRowDetail = 0;

		GridHandle gridDetail = null;
		if (component.isAutomaticGroupingList()) {
			gridDetail = designFactory.newGridItem("gridDetail_" + index + "_" + new Object().hashCode(), detailColumn + 1, 1);
		}
		else {
			gridDetail = designFactory.newGridItem("gridDetail_" + index + "_" + new Object().hashCode(), detailColumn, 1);
		}

		// We add totals for columns on the entire report
		for (Column c : component.getColumns()) {
			if (c.getGroupAggregation() != null && c.getGroupAggregation().getColumn().equals(GroupAgregation.AGGREGATE_ON_TOTAL)) {
				String nameCompCol = "Agg_Col_Header_" + new Object().hashCode();

				ComputedColumn compCol = StructureFactory.createComputedColumn();
				compCol.setName(nameCompCol);
				compCol.setExpression("dataSetRow[\"" + c.getDatasetRowExpr(selectedLanguage) + "\"]");
				compCol.setAggregateFunction(c.getGroupAggregation().getType());
				computedSet.addItem(compCol);

				TextItemHandle lblAgg = designFactory.newTextItem("Lbl_Agg_" + new Object().hashCode());
				lblAgg.setContentType("html");
				if (c.getGroupAggregation().getCustomLabel() != null && !c.getGroupAggregation().getCustomLabel().isEmpty()) {
					lblAgg.setContent(c.getGroupAggregation().getCustomLabel());
				}
				else {
					lblAgg.setContent(c.getGroupAggregation().getLabel());
					lblAgg.setProperty("textAlign", "left");
				}
				lblAgg.setProperty("width", "100%");

				DataItemHandle dataAgg = designFactory.newDataItem("Data_Agg_" + new Object().hashCode());
				dataAgg.setResultSetColumn(nameCompCol);
				dataAgg.setProperty("textAlign", "right");

				CellHandle cell = designFactory.newCell();
				cell.setProperty(StyleHandle.PADDING_TOP_PROP, "0pt");
				cell.setProperty(StyleHandle.PADDING_LEFT_PROP, "0pt");
				cell.setProperty(StyleHandle.PADDING_BOTTOM_PROP, "0pt");
				cell.setProperty(StyleHandle.PADDING_RIGHT_PROP, "0pt");
				cell.setColumnSpan(detailColumn);

				GridHandle gridTotalsRow = designFactory.newGridItem("TotalHeader_" + new Object().hashCode(), 2, 1);
				RowHandle grRow = (RowHandle) gridTotalsRow.getRows().get(0);

				((CellHandle) grRow.getCells().get(0)).getContent().add(lblAgg);
				((CellHandle) grRow.getCells().get(1)).getContent().add(dataAgg);
				cell.getContent().add(gridTotalsRow);

				RowHandle tableHeader = designFactory.newTableRow();
				tableHeader.getCells().add(cell);
				table.getHeader().add(tableHeader);

				Style o = component.getHeaderCellsStyle();
				if (o != null) {
					if (c.getGroupAggregation().getCustomLabel() == null || c.getGroupAggregation().getCustomLabel().isEmpty()) {
						grRow.setProperties(FwrReportManager.addBirtStyle(o));
					}
				}
			}
		}

		for (Column c : all) {
			boolean isGroup = false;
			for (Column gr : component.getGroups()) {
				if (gr.getName().equals(c.getName())) {
					isGroup = true;
					break;
				}
			}

			LabelHandle label1 = null;
			TextItemHandle columnName = null;
			Style o = null;
			if (c.getCustomColumnName() != null) {
				columnName = designFactory.newTextItem("title" + new Object().hashCode());
				columnName.setContentType("html");
				columnName.setContent(c.getCustomColumnName());
				columnName.setWidth("100%");
				columnName.setProperty(StyleHandle.PADDING_LEFT_PROP, paddingLeft + "px");
				columnName.setProperty(StyleHandle.TEXT_ALIGN_PROP, c.getTextAlign());
				
				if (c.isUnderline()) {
					columnName.setProperty(StyleHandle.TEXT_UNDERLINE_PROP, "underline");
				}
				if (c.isBold()) {
					columnName.setProperty(StyleHandle.FONT_WEIGHT_PROP, "bold");
				}
				if (c.isItalic()) {
					columnName.setProperty(StyleHandle.FONT_STYLE_PROP, "italic");
				}
			}
			else {
				label1 = designFactory.newLabel("head_" + c.getName() + "_" + new Object().hashCode());
				label1.setText(c.getTitle(selectedLanguage));

				o = component.getHeaderCellsStyle();

				label1.setProperty(StyleHandle.TEXT_ALIGN_PROP, c.getTextAlign());
				label1.setProperty(StyleHandle.VERTICAL_ALIGN_PROP, "bottom");
				// label1.setProperty("height", 0.05);
				label1.setProperty(StyleHandle.PADDING_TOP_PROP, "2px");
				label1.setProperty(StyleHandle.PADDING_BOTTOM_PROP, "2px");
				label1.setProperty(StyleHandle.PADDING_LEFT_PROP, paddingLeft + "px");
				label1.setProperty(StyleHandle.MARGIN_TOP_PROP, "0pt");
				label1.setProperty(StyleHandle.FONT_SIZE_PROP, "8");
				if (c.isUnderline()) {
					label1.setProperty(StyleHandle.TEXT_UNDERLINE_PROP, "underline");
				}
				if (c.isBold()) {
					label1.setProperty(StyleHandle.FONT_WEIGHT_PROP, "bold");
				}
				if (c.isItalic()) {
					label1.setProperty(StyleHandle.FONT_STYLE_PROP, "italic");
				}
			}

			if (index == 0 && isGroup) {
				CellHandle cell = designFactory.newCell();
				cell.setProperty(StyleHandle.PADDING_TOP_PROP, "0pt");
				cell.setProperty(StyleHandle.PADDING_LEFT_PROP, "0pt");
				cell.setProperty(StyleHandle.PADDING_BOTTOM_PROP, "0pt");
				cell.setProperty(StyleHandle.PADDING_RIGHT_PROP, "0pt");

				if (component.isAutomaticGroupingList()) {
					cell.setColumnSpan(detailColumn + 1);
				}
				else {
					cell.setColumnSpan(detailColumn);
				}

				GridHandle gridTitleAndBtnHideShowDetail = designFactory.newGridItem("hideShowDetail_" + index + "_" + new Object().hashCode(), 2, 1);
				RowHandle grRow = (RowHandle) gridTitleAndBtnHideShowDetail.getRows().get(0);

				if (c.getCustomColumnName() != null) {
					((CellHandle) grRow.getCells().get(0)).getContent().add(columnName);
				}
				else {
					if (o != null) {
						grRow.setProperties(FwrReportManager.addBirtStyle(o));
					}

					((CellHandle) grRow.getCells().get(0)).getContent().add(label1);
				}

				cell.getContent().add(gridTitleAndBtnHideShowDetail);

				RowHandle tableHeader = designFactory.newTableRow();
				tableHeader.getCells().add(cell);
				table.getHeader().add(tableHeader);
			}
			else if (index != 0 && isGroup) {
				CellHandle cell = designFactory.newCell();
				cell.setProperty(StyleHandle.PADDING_TOP_PROP, "0pt");
				cell.setProperty(StyleHandle.PADDING_LEFT_PROP, "0pt");
				cell.setProperty(StyleHandle.PADDING_BOTTOM_PROP, "0pt");
				cell.setProperty(StyleHandle.PADDING_RIGHT_PROP, "0pt");
				if (component.isAutomaticGroupingList()) {
					cell.setColumnSpan(detailColumn + 1);
				}
				else {
					cell.setColumnSpan(detailColumn);
				}

				if (c.getCustomColumnName() != null) {
					cell.getContent().add(columnName);
				}
				else {
					if (o != null) {
						cell.setProperties(FwrReportManager.addBirtStyle(o));
					}

					cell.getContent().add(label1);
				}

				RowHandle tableHeader = designFactory.newTableRow();
				tableHeader.getCells().add(cell);
				table.getHeader().add(tableHeader);
			}
			else if (!isGroup && indexRowDetail == 0) {
				RowHandle tableHeader = designFactory.newTableRow();
				table.getHeader().add(tableHeader);

				CellHandle cellDetail = designFactory.newCell();
				cellDetail.setProperty(StyleHandle.PADDING_TOP_PROP, "0pt");
				cellDetail.setProperty(StyleHandle.PADDING_LEFT_PROP, "0pt");
				cellDetail.setProperty(StyleHandle.PADDING_BOTTOM_PROP, "0pt");
				cellDetail.setProperty(StyleHandle.PADDING_RIGHT_PROP, "0pt");

				if (component.isAutomaticGroupingList()) {
					cellDetail.setColumnSpan(detailColumn + 1);
				}
				else {
					cellDetail.setColumnSpan(detailColumn);
				}

				cellDetail.getContent().add(gridDetail);
				tableHeader.getCells().add(cellDetail);

				if (c.getCustomColumnName() != null) {
					((CellHandle) ((RowHandle) gridDetail.getRows().get(0)).getCells().get(indexRowDetail)).getContent().add(columnName);
				}
				else {
					if (o != null) {
						cellDetail.setProperties(FwrReportManager.addBirtStyle(o));
						gridDetail.setProperties(FwrReportManager.addBirtStyle(o));
					}

					if (component.isAutomaticGroupingList()) {
						label1.setProperty(StyleHandle.TEXT_ALIGN_PROP, "right");
						label1.setProperty(StyleHandle.MARGIN_RIGHT_PROP, "10pt");

						((CellHandle) ((RowHandle) gridDetail.getRows().get(0)).getCells().get(indexRowDetail + 1)).getContent().add(label1);
					}
					else {
						((CellHandle) ((RowHandle) gridDetail.getRows().get(0)).getCells().get(indexRowDetail)).getContent().add(label1);
					}
				}

				indexRowDetail++;
			}
			else {
				if (c.getCustomColumnName() != null) {
					((CellHandle) ((RowHandle) gridDetail.getRows().get(0)).getCells().get(indexRowDetail)).getContent().add(columnName);
				}
				else {
					if (o != null) {
						((RowHandle) gridDetail.getRows().get(0)).setProperties(FwrReportManager.addBirtStyle(o));
					}

					if (component.isAutomaticGroupingList()) {
						label1.setProperty(StyleHandle.TEXT_ALIGN_PROP, "right");
						label1.setProperty(StyleHandle.MARGIN_RIGHT_PROP, "10pt");

						((CellHandle) ((RowHandle) gridDetail.getRows().get(0)).getCells().get(indexRowDetail + 1)).getContent().add(label1);
					}
					else {
						((CellHandle) ((RowHandle) gridDetail.getRows().get(0)).getCells().get(indexRowDetail)).getContent().add(label1);
					}
				}

				indexRowDetail++;
			}
			paddingLeft = paddingLeft + 15;
			index++;
		}
	}

	private void buildMainPart(ReportDesignHandle designHandle, ElementFactory designFactory, GridComponent component, PropertyHandle computedSet, TableHandle table, List<Column> all, RowHandle tabledetail, String selectedLanguage, String datasetLanguage, int detailColumn) throws Exception {
		int indexGroup = 0;
		int indexDetail = 0;
		int paddingLeftDetail = 5;
		for (Column c : all) {
			DataItemHandle data = designFactory.newDataItem("colName" + c.getName() + "_" + new Object().hashCode());
			data.setResultSetColumn(c.getBusinessTableParent() + "_" + c.getTitle(selectedLanguage));
			data.setProperty("textAlign", c.getTextAlign());
			data.setProperty("paddingLeft", paddingLeftDetail + "px");
			if (indexGroup == 1 && indexDetail == 0) {
				data.setName("DataName_" + new Object().hashCode());
			}

			Style o = component.getDataCellsStyle();
			if (o != null) {
				data.setProperty("fontFamily", ((Style) o).getFontType());
				data.setProperty("fontSize", ((Style) o).getFontSize());
				if (o.getTextColor() != null)
					data.setProperty("color", FwrReportManager.birtcolor(o.getTextColor()));
			}

			if (c.getFormat() != null) {
				SharedStyleHandle st = designFactory.newStyle(("style" + new Object().hashCode()).replace(" ", ""));
				boolean isDate = false;
				for (String form : Formats.DATE_FORMATS) {
					if (form.equals(c.getFormat())) {
						isDate = true;
						break;
					}
				}
				if (isDate) {
					st.setDateTimeFormat(c.getFormat());
				}
				else {
					st.setNumberFormat(c.getFormat());
				}
				designHandle.getStyles().add(st);
				data.setStyle(st);
			}

			if (component.getOddRowsBackgroundColor() != null) {
				PropertyHandle highlightHandle = tabledetail.getPropertyHandle(StyleHandle.HIGHLIGHT_RULES_PROP);
				HighlightRule highLight = new HighlightRule();
				highLight.setOperator("eq");
				highLight.setTestExpression("row.__rownum%2");

				if (component.getOddRowsBackgroundColor() != null)
					highLight.setProperty(HighlightRule.BACKGROUND_COLOR_MEMBER, FwrReportManager.birtcolor(component.getOddRowsBackgroundColor()));
				highLight.setValue1("0");
				HighlightRule highLight2 = new HighlightRule();
				highLight2.setOperator("eq");
				highLight2.setTestExpression("row.__rownum%2");

				if (o.getBackgroundColor() != null)
					highLight2.setProperty(HighlightRule.BACKGROUND_COLOR_MEMBER, FwrReportManager.birtcolor(o.getBackgroundColor()));
				highLight2.setValue1("1");
				highlightHandle.addItem(highLight);
				highlightHandle.addItem(highLight2);

			}
			else if (o != null) {
				if (o.getBackgroundColor() != null) {
					data.setProperty("backgroundColor", FwrReportManager.birtcolor(o.getBackgroundColor()));
				}
			}
			boolean isGroup = false;
			Column group = null;
			for (Column gr : component.getGroups()) {
				if (gr.getName().equals(c.getName())) {
					isGroup = true;
					group = gr;
					break;
				}
			}

			if (isGroup) {
				if (indexGroup < groupGreyStyle.length) {
					data.setProperty("backgroundColor", groupGreyStyle[indexGroup]);
				}
				else {
					data.setProperty("backgroundColor", groupGreyStyle[groupGreyStyle.length - 1]);
				}

				TableGroupHandle gr = (TableGroupHandle) table.getGroups().get(indexGroup);
				if (component.isAutomaticGroupingList()) {
					RowHandle row = (RowHandle) gr.getHeader().getContents().get(0);
					row.setProperty(StyleHandle.BORDER_TOP_COLOR_PROP, "#FFFFFF");
					row.setProperty(StyleHandle.BORDER_TOP_STYLE_PROP, "solid");
					row.setProperty(StyleHandle.BORDER_TOP_WIDTH_PROP, "1px");
					
					CellHandle cellGroup = (CellHandle) row.getCells().get(0);
					cellGroup.getContent().add(data);
				}
				else {
					RowHandle row = designFactory.newTableRow(1);
					CellHandle cellGroup = (CellHandle) row.getCells().get(0);
					cellGroup.setColumnSpan(detailColumn);

					GridHandle aggregGrid = designFactory.newGridItem("gridGroupAndAggreg_" + indexGroup + "_" + new Object().hashCode(), 3, 1);
					if (indexGroup < groupGreyStyle.length) {
						aggregGrid.setProperty("backgroundColor", groupGreyStyle[indexGroup]);
					}
					else {
						aggregGrid.setProperty("backgroundColor", groupGreyStyle[groupGreyStyle.length - 1]);
					}

					RowHandle grRow = (RowHandle) aggregGrid.getRows().get(0);

					gr.getHeader().add(row, 0);
					((CellHandle) grRow.getCells().get(0)).getContent().add(data);

					GroupAgregation aggreg = null;
					String aggregExp = null;
					for (Column col : component.getGroups()) {
						if (col.getGroupAggregation() != null && col.getGroupAggregation().getColumn().equals(group.getName())) {
							aggreg = col.getGroupAggregation();
							aggregExp = col.getName();
							break;
						}
					}
					if (aggreg == null) {
						for (Column col : component.getColumns()) {
							if (col.getGroupAggregation() != null && col.getGroupAggregation().getColumn().equals(group.getName())) {
								aggreg = col.getGroupAggregation();
								aggregExp = col.getName();
								break;
							}
						}
					}

					int aggIndex = 0;
					if (aggreg != null && aggregExp != null) {
						if (!aggreg.getColumn().equals(GroupAgregation.AGGREGATE_ON_TOTAL)) {
							ComputedColumn compCol = StructureFactory.createComputedColumn();

							String name = null;
							for (Column column : all) {
								if (column.getName().equals(aggregExp)) {
									name = column.getDatasetRowExpr(selectedLanguage);
									break;
								}
							}

							compCol.setName("aggCol" + indexGroup + aggreg.getIndex() + aggIndex);
							compCol.setExpression("dataSetRow[\"" + name + "\"]");
							compCol.setAggregateFunction(aggreg.getType());
							compCol.setAggregateOn(gr.getName());
							computedSet.addItem(compCol);

							TextItemHandle lblAgg = designFactory.newTextItem("lblAgg" + indexGroup + aggreg.getIndex() + aggIndex);
							lblAgg.setContentType("html");
							if (aggreg.getCustomLabel() != null && !aggreg.getCustomLabel().isEmpty()) {
								lblAgg.setContent(aggreg.getCustomLabel());
							}
							else {
								lblAgg.setContent(aggreg.getLabel());
							}
							lblAgg.setProperty("textAlign", "right");
							((CellHandle) grRow.getCells().get(aggIndex + 1)).getContent().add(lblAgg);

							DataItemHandle dataAgg = designFactory.newDataItem("dataAgg" + indexGroup + aggreg.getIndex() + aggIndex);
							dataAgg.setResultSetColumn("aggCol" + indexGroup + aggreg.getIndex() + aggIndex);
							((CellHandle) grRow.getCells().get(aggIndex + 2)).getContent().add(dataAgg);

							aggIndex++;
							aggIndex++;
						}
					}

					cellGroup.getContent().add(aggregGrid);
				}
				indexGroup++;
			}
			else if (!component.isAutomaticGroupingList()) {
				TextItemHandle referenceClassDiv = designFactory.newTextItem("referenceClass" + new Object().hashCode());
				referenceClassDiv.setContentType("html");
				referenceClassDiv.setContent("<div class=\"classReference\" style=\"visibility:hidden;position:absolute;\">Hidden</div>");

				if (c instanceof HyperlinkColumn) {
					HyperlinkColumn hyperlink = (HyperlinkColumn) c;

					String label = "";
					if (hyperlink.keepColumnValues()) {
						label = "\" + row[\"" + c.getBusinessTableParent() + "_" + c.getTitle(datasetLanguage) + "\"] + \"";
					}
					else {
						label = hyperlink.getLabelUrl();
						if (label.contains("\"")) {
							label = label.replace("\"", "");
						}
					}

					TextDataHandle textDataTest = designFactory.newTextData("TextData_" + new Object().hashCode());
					textDataTest.setContentType("html");
					textDataTest.setProperty(StyleHandle.TEXT_ALIGN_PROP, c.getTextAlign());
					textDataTest.setValueExpr("\"<a href='" + hyperlink.getUrl() + "\" + row[\"" + c.getBusinessTableParent() + "_" + c.getTitle(datasetLanguage) + "\"] + \"' target='_blank'>" + label + "</a>\"");
					CellHandle cell = (CellHandle) tabledetail.getCells().get(indexDetail);
					cell.getContent().add(textDataTest);

				}
				else {
					CellHandle cell = (CellHandle) tabledetail.getCells().get(indexDetail);
					cell.getContent().add(data);
				}

				if (c.isSorted()) {
					SortKey sortKey = getSortKey(c, "row[\"" + c.getBusinessTableParent() + "_" + c.getTitle(selectedLanguage) + "\"]");

					PropertyHandle prop = table.getPropertyHandle(TableHandle.SORT_PROP);
					prop.addItem(sortKey);
				}
				indexDetail++;
			}
			else {
				if (table.getDetail().get(0) != null) {
					table.getDetail().get(0).dropAndClear();
				}
			}
			paddingLeftDetail = paddingLeftDetail + 15;
		}
	}

	private String findAgregateFunction(SubType type) {
		if (type == SubType.AVG) {
			return "AVE";
		}
		else if (type == SubType.COUNT) {
			return "COUNT";
		}
		else if (type == SubType.MAX) {
			return "MAX";
		}
		else if (type == SubType.MIN) {
			return "MIN";
		}
		else if (type == SubType.DISTINCT) {
			return "DISTINCT";
		}
		else {
			return "SUM";
		}
	}

	private int getNumberDetailColumn(GridComponent component) {
		int detailColumn = 0;
		for (Column col : component.getColumns()) {
			boolean isGroup = false;
			for (Column gr : component.getGroups()) {
				if (gr.getName().equals(col.getName())) {
					isGroup = true;
					break;
				}
			}
			if (!isGroup) {
				detailColumn++;
			}
		}

		if (detailColumn == 0) {
			detailColumn++;
		}

		return detailColumn;
	}

	private SortKey getSortKey(Column col, String expression) {
		String sortType = col.getSortType();

		SortKey sortKey = StructureFactory.createSortKey();
		if (sortType.equalsIgnoreCase("ASC")) {
			sortKey.setDirection(DesignChoiceConstants.SORT_DIRECTION_ASC);
		}
		else {
			sortKey.setDirection(DesignChoiceConstants.SORT_DIRECTION_DESC);
		}
		sortKey.setKey(expression);
		return sortKey;
	}
}
