package bpm.fwr.api.birt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.birt.report.item.crosstab.core.ICrosstabConstants;
import org.eclipse.birt.report.item.crosstab.core.de.CrosstabCellHandle;
import org.eclipse.birt.report.item.crosstab.core.de.CrosstabReportItemHandle;
import org.eclipse.birt.report.item.crosstab.core.de.DimensionViewHandle;
import org.eclipse.birt.report.item.crosstab.core.de.LevelViewHandle;
import org.eclipse.birt.report.item.crosstab.core.de.MeasureViewHandle;
import org.eclipse.birt.report.item.crosstab.core.util.CrosstabExtendedItemFactory;
import org.eclipse.birt.report.model.api.ComputedColumnHandle;
import org.eclipse.birt.report.model.api.DataItemHandle;
import org.eclipse.birt.report.model.api.DataSetHandle;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ElementFactory;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.LabelHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.ReportItemHandle;
import org.eclipse.birt.report.model.api.StructureFactory;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.eclipse.birt.report.model.api.elements.structures.ComputedColumn;
import org.eclipse.birt.report.model.api.extension.IReportItem;
import org.eclipse.birt.report.model.api.olap.MeasureGroupHandle;
import org.eclipse.birt.report.model.api.olap.MeasureHandle;
import org.eclipse.birt.report.model.api.olap.TabularCubeHandle;
import org.eclipse.birt.report.model.api.olap.TabularDimensionHandle;
import org.eclipse.birt.report.model.api.olap.TabularHierarchyHandle;
import org.eclipse.birt.report.model.api.olap.TabularLevelHandle;
import org.eclipse.birt.report.model.api.olap.TabularMeasureHandle;
import org.eclipse.birt.report.model.elements.interfaces.ICubeModel;

import bpm.fwr.api.beans.components.CrossComponent;
import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.Column.SubType;

public class CrossComponentCreator implements IComponentCreator<CrossComponent> {

	@Override
	public DesignElementHandle createComponent(ReportDesignHandle designHandle, ElementFactory designFactory, String selectedLanguage, CrossComponent component, String outputFormat) throws Exception {
		TabularCubeHandle cubeHandle = designHandle.getElementFactory().newTabularCube("MyCube");

		DataSetHandle repDs = null;
		for (int i = 0; i < designHandle.getDataSets().getCount(); i++) {
			DataSetHandle ds = (DataSetHandle) designHandle.getDataSets().get(i);
			if (ds.getName().equalsIgnoreCase(component.getDataset().getName())) {
				repDs = ds;
			}
		}

		cubeHandle.setDataSet(repDs);
		designHandle.getCubes().add(cubeHandle);

		// measure group
		MeasureGroupHandle measureGroupHandle = designHandle.getElementFactory().newTabularMeasureGroup("testMeasureGroup");
		cubeHandle.setProperty(ICubeModel.MEASURE_GROUPS_PROP, measureGroupHandle);
		// measure
		for (int i = 0; i < component.getCrossCells().size(); i++) {
			TabularMeasureHandle temp = designFactory.newTabularMeasure(null);
			measureGroupHandle.add(MeasureGroupHandle.MEASURES_PROP, temp);
		}

		// some measures
		List<MeasureHandle> measuresAdded = new ArrayList<MeasureHandle>();
		int mesindex = 0;
		for (Column mes : component.getCrossCells()) {
			MeasureHandle measure = (MeasureHandle) measureGroupHandle.getContent(MeasureGroupHandle.MEASURES_PROP, mesindex);
			if (mes.getName().contains(".")) {
				String[] ch = mes.getName().split("\\.");
				String na = ch[0] + "_" + ch[1];
				measure.setName(na);
			}
			else {
				measure.setName(mes.getName());
			}
			if (mes.isCalculated()) {
				measure.setMeasureExpression(mes.getExpression());
			}
			else {
				measure.setMeasureExpression("dataSetRow['" + mes.getName() + "']");
			}

//			if (mes.getMeasureBehavior() == IDataStreamElement.BEHAVIOR_AVG) {
//				measure.setFunction(DesignChoiceConstants.MEASURE_FUNCTION_AVERAGE);
//			}
//			else if (mes.getMeasureBehavior() == IDataStreamElement.BEHAVIOR_COUNT) {
//				measure.setFunction(DesignChoiceConstants.MEASURE_FUNCTION_COUNT);
//			}
//			else if (mes.getMeasureBehavior() == IDataStreamElement.BEHAVIOR_MAX) {
//				measure.setFunction(DesignChoiceConstants.MEASURE_FUNCTION_MAX);
//			}
//			else if (mes.getMeasureBehavior() == IDataStreamElement.BEHAVIOR_MIN) {
//				measure.setFunction(DesignChoiceConstants.MEASURE_FUNCTION_MIN);
//			}
//			else {
//				measure.setFunction(DesignChoiceConstants.MEASURE_FUNCTION_SUM);
//			}
			if (mes.getType() == SubType.AVG) {
				measure.setFunction(DesignChoiceConstants.MEASURE_FUNCTION_AVERAGE);
			}
			else if (mes.getType() == SubType.COUNT) {
				measure.setFunction(DesignChoiceConstants.MEASURE_FUNCTION_COUNT);
			}
			else if (mes.getType() == SubType.MAX) {
				measure.setFunction(DesignChoiceConstants.MEASURE_FUNCTION_MAX);
			}
			else if (mes.getType() == SubType.MIN) {
				measure.setFunction(DesignChoiceConstants.MEASURE_FUNCTION_MIN);
			}
			else if (mes.getType() == SubType.DISTINCT) {
				measure.setFunction(DesignChoiceConstants.MEASURE_FUNCTION_COUNTDISTINCT);
			}
			else {
				measure.setFunction(DesignChoiceConstants.MEASURE_FUNCTION_SUM);
			}
			measure.setCalculated(false);
			measure.setDataType(DesignChoiceConstants.COLUMN_DATA_TYPE_FLOAT);

			measuresAdded.add(measure);
			mesindex++;
		}

		TabularDimensionHandle dimension = designFactory.newTabularDimension(null);
		cubeHandle.add(TabularCubeHandle.DIMENSIONS_PROP, dimension);
		dimension.add(TabularDimensionHandle.HIERARCHIES_PROP, designFactory.newTabularHierarchy(null));
		TabularHierarchyHandle hierarchy = (TabularHierarchyHandle) dimension.getContent(TabularDimensionHandle.HIERARCHIES_PROP, 0);

		int numcol = 0;
		ArrayList<TabularLevelHandle> listlvl = new ArrayList<TabularLevelHandle>();

		for (Column c : component.getCrossCols()) {
			numcol++;
			hierarchy.add(TabularHierarchyHandle.LEVELS_PROP, designFactory.newTabularLevel(dimension, "" + (numcol - 1)));
			TabularLevelHandle level = (TabularLevelHandle) hierarchy.getContent(TabularHierarchyHandle.LEVELS_PROP, numcol - 1);
			if (c.getBusinessTableParent().contains(".") || c.getName().contains(".")) {
				String name = c.getName();
				if (c.getName().contains(".")) {
					String[] ch = c.getName().split("\\.");
					name = ch[0] + "_" + ch[1];
				}

				String parent = c.getBusinessTableParent();
				if (c.getBusinessTableParent().contains(".")) {
					String[] ch = c.getBusinessTableParent().split("\\.");
					parent = ch[0] + "_" + ch[1];
				}

				try {
					level.setName(parent + "_" + name);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				level.setName(c.getBusinessTableParent() + "_" + c.getName());
			}
			level.setColumnName(c.getName());
			level.setDataType(DesignChoiceConstants.COLUMN_DATA_TYPE_STRING);
			listlvl.add(level);
		}

		TabularDimensionHandle dimension2 = designFactory.newTabularDimension(null);
		cubeHandle.add(TabularCubeHandle.DIMENSIONS_PROP, dimension2);
		dimension2.add(TabularDimensionHandle.HIERARCHIES_PROP, designFactory.newTabularHierarchy(null));
		TabularHierarchyHandle hierarchy2 = (TabularHierarchyHandle) dimension2.getContent(TabularDimensionHandle.HIERARCHIES_PROP, 0);

		int numrow = 0;
		for (Column c : component.getCrossRows()) {
			numcol++;
			numrow++;
			hierarchy2.add(TabularHierarchyHandle.LEVELS_PROP, designFactory.newTabularLevel(dimension, "" + (numrow - 1)));
			TabularLevelHandle level = (TabularLevelHandle) hierarchy2.getContent(TabularHierarchyHandle.LEVELS_PROP, numrow - 1);
			if (c.getBusinessTableParent().contains(".") || c.getName().contains(".")) {
				String name = c.getName();
				if (c.getName().contains(".")) {
					String[] ch = c.getName().split("\\.");
					name = ch[0] + "_" + ch[1];
				}

				String parent = c.getBusinessTableParent();
				if (c.getBusinessTableParent().contains(".")) {
					String[] ch = c.getBusinessTableParent().split("\\.");
					parent = ch[0] + "_" + ch[1];
				}

				level.setName(parent + "_" + name);
			}
			else {
				level.setName(c.getBusinessTableParent() + "_" + c.getName());
			}
			level.setColumnName(c.getName());
			level.setDataType(DesignChoiceConstants.COLUMN_DATA_TYPE_STRING);
			listlvl.add(level);
		}

		ExtendedItemHandle xtab = CrosstabExtendedItemFactory.createCrosstabReportItem(designHandle, cubeHandle, "MyCrosstab");

		IReportItem reportItem = xtab.getReportItem();
		CrosstabReportItemHandle xtabHandle = (CrosstabReportItemHandle) reportItem;

		DimensionViewHandle dvh = xtabHandle.insertDimension(dimension, ICrosstabConstants.COLUMN_AXIS_TYPE, 0);

		int numcoldim = 0;

		for (int i = 0; i < component.getCrossCols().size(); i++) {
			LevelViewHandle levelViewHandle = dvh.insertLevel(listlvl.get(numcoldim), numcoldim);
			levelViewHandle.getModelHandle().setName("Level" + new Object().hashCode());

			CrosstabCellHandle cellHandle = levelViewHandle.getCell();
			DesignElementHandle eii = xtabHandle.getModelHandle();

			String newColName = listlvl.get(numcoldim).getName() + "_" + new Object().hashCode();
			
			ComputedColumn bindingColumn = StructureFactory.newComputedColumn(eii, newColName);
			ComputedColumnHandle bindingHandle = ((ReportItemHandle) eii).addColumnBinding(bindingColumn, false);
			bindingColumn.setDataType(listlvl.get(numcoldim).getDataType());
			String exp = "dimension['" + dimension.getName() + "']['" + listlvl.get(numcoldim).getName() + "']";
			bindingColumn.setExpression(exp);
			DataItemHandle dataHandle = designFactory.newDataItem(newColName);
			dataHandle.setResultSetColumn(bindingHandle.getName());
			dataHandle.setProperties(addListStyle(component.getXStyle(), 2, false));
			cellHandle.getModelHandle().setProperties(addListStyle(component.getXStyle(), 2, true));
			cellHandle.addContent(dataHandle);
			numcoldim++;
		}

		DimensionViewHandle dvh2 = xtabHandle.insertDimension(dimension2, ICrosstabConstants.ROW_AXIS_TYPE, 0);
		xtab.setProperty("hideMeasureHeader", false);
		xtab.setProperty("emptyCellValue", "0");

		for (int i = 0; i < component.getCrossRows().size(); i++) {
			LevelViewHandle levelViewHandle2 = dvh2.insertLevel(listlvl.get(numcoldim), numcoldim);
			levelViewHandle2.getModelHandle().setName("Level" + new Object().hashCode());

			CrosstabCellHandle cellHandle2 = levelViewHandle2.getCell();
			DesignElementHandle eii2 = xtabHandle.getModelHandle();
			
			String newColName = listlvl.get(numcoldim).getName() + "_" + new Object().hashCode();

			ComputedColumn bindingColumn2 = StructureFactory.newComputedColumn(eii2, newColName);
			ComputedColumnHandle bindingHandle2 = ((ReportItemHandle) eii2).addColumnBinding(bindingColumn2, false);
			bindingColumn2.setDataType(listlvl.get(numcoldim).getDataType());
			String exp2 = "dimension['" + dimension2.getName() + "']['" + listlvl.get(numcoldim).getName() + "']";
			bindingColumn2.setExpression(exp2);
			DataItemHandle dataHandle2 = designFactory.newDataItem(newColName);
			dataHandle2.setResultSetColumn(bindingHandle2.getName());
			cellHandle2.addContent(dataHandle2);
			dataHandle2.setProperties(addListStyle(component.getXStyle(), 1, false));
			cellHandle2.getModelHandle().setProperties(addListStyle(component.getXStyle(), 1, true));
			numcoldim++;
		}

		// some measures
		int mesIndex = 0;
		for (MeasureHandle mes : measuresAdded) {
			MeasureViewHandle mvh = xtabHandle.insertMeasure(mes, mesIndex);
			mvh.addHeader();

			CrosstabCellHandle headerCell = mvh.getHeader();
			LabelHandle lblHead = designFactory.newLabel(mes.getName() + "lblhead");

			lblHead.setText(component.getCrossCells().get(mesIndex).getTitle(selectedLanguage));
			headerCell.addContent(lblHead);

			mvh.getCell().getModelHandle().setProperties(addListStyle(component.getXStyle(), 0, true));
			
			CrosstabCellHandle cellMeasure = mvh.getCell();
			DesignElementHandle eii2 = xtabHandle.getModelHandle();
			
			String newColName = listlvl.get(mesIndex).getName() + "_" + new Object().hashCode();
			
			ComputedColumn bindingColumn2 = StructureFactory.newComputedColumn(eii2, newColName);
			ComputedColumnHandle bindingHandle2 = ((ReportItemHandle) eii2).addColumnBinding(bindingColumn2, false);
			bindingColumn2.setDataType(listlvl.get(mesIndex).getDataType());
			String exp2 = "measure['" + mes.getName() + "']";
			bindingColumn2.setExpression(exp2);
			DataItemHandle dataHandle2 = designFactory.newDataItem(newColName);
			dataHandle2.setResultSetColumn(bindingHandle2.getName());
			cellMeasure.addContent(dataHandle2);
			dataHandle2.setProperties(addListStyle(component.getXStyle(), 0, false));
			cellMeasure.getModelHandle().setProperties(addListStyle(component.getXStyle(), 0, true));

			// add grand total to row or/and column
			LabelHandle tcol = designFactory.newLabel("grandcol");
			tcol.setText("Grand Total");
			tcol.setProperties(addListStyle(component.getXStyle(), 0, false));

			LabelHandle trow = designFactory.newLabel("grandrow");
			trow.setText("Grand Total");
			trow.setProperties(addListStyle(component.getXStyle(), 0, false));

			int nbrgt = 0;
			Column xgtrow = component.getCrossRows().get(0);
			if (xgtrow.isAgregate()) {
				CrosstabCellHandle gt = xtabHandle.addGrandTotal(0);
				gt.addContent(trow);
				trow.getContainer().setProperties(addListStyle(component.getXStyle(), 0, true));
				DataItemHandle de = (DataItemHandle) mvh.getAggregationCell(nbrgt).getContents().get(mesIndex);
				de.setProperties(addListStyle(component.getXStyle(), 2, false));
				de.getContainer().setProperties(addListStyle(component.getXStyle(), 2, true));
				nbrgt++;
			}
			Column xgtcol = component.getCrossCols().get(0);
			if (xgtcol.isAgregate()) {
				CrosstabCellHandle gt2 = xtabHandle.addGrandTotal(1);
				gt2.addContent(tcol);
				tcol.getContainer().setProperties(addListStyle(component.getXStyle(), 0, true));
				DataItemHandle de = (DataItemHandle) mvh.getAggregationCell(nbrgt).getContents().get(mesIndex);
				de.setProperties(addListStyle(component.getXStyle(), 2, false));
				de.getContainer().setProperties(addListStyle(component.getXStyle(), 2, true));
			}
			mesIndex++;
		}

		return xtab;
	}

	private HashMap<String, String> addListStyle(String style, int niveau, boolean iscell) {
		String[] backgrounds = { "white", "white", "white" };

		if (style.equalsIgnoreCase("COLOR_SCHEMA_PINK")) {
			backgrounds[0] = "#F2D0DC";
			backgrounds[1] = "#E7A7B9";
			backgrounds[2] = "#E78796";
		}

		if (style.equalsIgnoreCase("COLOR_SCHEMA_BLUE")) {
			backgrounds[0] = "#D0DCF2";
			backgrounds[1] = "#A7B9E7";
			backgrounds[2] = "#9FB9DC";
		}
		if (style.equalsIgnoreCase("COLOR_SCHEMA_GRAY")) {
			backgrounds[0] = "#ECECEC";
			backgrounds[1] = "#DADADA";
			backgrounds[2] = "#D1D1D1";
		}
		if (style.equalsIgnoreCase("COLOR_SCHEMA_PINK_AND_BROWN")) {
			backgrounds[0] = "#F2DCD0";
			backgrounds[1] = "#E7B9A7";
			backgrounds[2] = "#E79687";
		}
		if (style.equalsIgnoreCase("COLOR_SCHEMA_LIGHT_GREEN")) {
			backgrounds[0] = "#D0F2DC";
			backgrounds[1] = "#A7E7B9";
			backgrounds[2] = "#87E796";
		}

		HashMap<String, String> LstProperties = new HashMap<String, String>();
		LstProperties.put("backgroundColor", backgrounds[niveau]);
		if (iscell) {
			LstProperties.put("borderTopWidth", "Thin");
			LstProperties.put("borderLeftWidth", "Thin");
			LstProperties.put("borderBottomWidth", "Thin");
			LstProperties.put("borderRightWidth", "Thin");

			LstProperties.put("borderTopColor", "#000000");
			LstProperties.put("borderLeftColor", "#000000");
			LstProperties.put("borderBottomColor", "#000000");
			LstProperties.put("borderRightColor", "#000000");

			LstProperties.put("borderTopStyle", "solid");
			LstProperties.put("borderLeftStyle", "solid");
			LstProperties.put("borderBottomStyle", "solid");
			LstProperties.put("borderRightStyle", "solid");
		}
		return LstProperties;
	}

}
