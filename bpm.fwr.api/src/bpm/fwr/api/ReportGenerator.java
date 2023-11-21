package bpm.fwr.api;

import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.eclipse.birt.report.model.api.CellHandle;
import org.eclipse.birt.report.model.api.ColorHandle;
import org.eclipse.birt.report.model.api.DataItemHandle;
import org.eclipse.birt.report.model.api.ElementFactory;
import org.eclipse.birt.report.model.api.LabelHandle;
import org.eclipse.birt.report.model.api.OdaDataSetHandle;
import org.eclipse.birt.report.model.api.OdaDataSourceHandle;
import org.eclipse.birt.report.model.api.PropertyHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.RowHandle;
import org.eclipse.birt.report.model.api.SimpleMasterPageHandle;
import org.eclipse.birt.report.model.api.StructureFactory;
import org.eclipse.birt.report.model.api.StyleHandle;
import org.eclipse.birt.report.model.api.TableHandle;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.eclipse.birt.report.model.api.elements.structures.ComputedColumn;

public class ReportGenerator {

	public static void generateReport(ReportDesignHandle designHandle, String dataSourceDriverId, String dataSetDriverId, List<String> columnNames, Properties dataSourceProp, Properties dataSourcePrivateProp, Properties dataSetProp, Properties dataSetPrivateProp, String queryTxt) throws Exception {

		ElementFactory designFactory = designHandle.getElementFactory();

		OdaDataSourceHandle dsHandle = designFactory.newOdaDataSource("Data Source", dataSourceDriverId);
		Enumeration en = dataSourceProp.propertyNames();
		while (en.hasMoreElements()) {
			String pName = (String) en.nextElement();
			dsHandle.setProperty(pName, dataSourceProp.getProperty(pName) + "");
		}

		en = dataSourcePrivateProp.propertyNames();
		while (en.hasMoreElements()) {
			String pName = (String) en.nextElement();
			dsHandle.setPrivateDriverProperty(pName, dataSourcePrivateProp.getProperty(pName) + "");
		}

		designHandle.getDataSources().add(dsHandle);

		OdaDataSetHandle datasetHandle = designFactory.newOdaDataSet("ds", dataSetDriverId);
		datasetHandle.setDataSource("Data Source");

		for (Object s : dataSetProp.keySet()) {
			datasetHandle.setProperty((String) s, (String) dataSetProp.get(s));
		}
		for (Object s : dataSetPrivateProp.keySet()) {
			datasetHandle.setPrivateDriverProperty((String) s, (String) dataSetPrivateProp.get(s));
		}

		try {
			datasetHandle.setQueryText(queryTxt);
			designHandle.getDataSets().add(datasetHandle);

		} catch (Exception e) {
			e.printStackTrace();
		}

		SimpleMasterPageHandle properties = designFactory.newSimpleMasterPage("page");
		LabelHandle title = designFactory.newLabel("title");
		title.setText("REPORT TITLE");
		title.setWidth("100%");
		title.setProperty("paddingTop", "20px");
		title.setProperty("height", 0.6);

		TableHandle table = designFactory.newTableItem("table", columnNames.size());// colnumber
		table.setWidth("100%");
		table.setDataSet(designHandle.findDataSet("ds"));

		PropertyHandle computedSet = table.getColumnBindings();
		ComputedColumn cs1 = null;

		for (String s : columnNames) {

			cs1 = StructureFactory.createComputedColumn();
			cs1.setName(s);
			cs1.setExpression("dataSetRow[\"" + s + "\"]");
			computedSet.addItem(cs1);
		}

		StyleHandle styleHandle;
		// table header (libellés)
		RowHandle tableHeader = (RowHandle) table.getHeader().get(0);

		for (int i = 0; i < columnNames.size(); i++) {

			LabelHandle label1 = designFactory.newLabel("lab_" + columnNames.get(i));
			label1.setText(columnNames.get(i));
			CellHandle cell = (CellHandle) tableHeader.getCells().get(i);
			cell.getContent().add(label1);

			// formatage du css
			styleHandle = cell.getPrivateStyle();
			ColorHandle colorHandle = styleHandle.getBackgroundColor();
			colorHandle.setValue("gray");
			styleHandle.setTextAlign(DesignChoiceConstants.TEXT_ALIGN_CENTER);
			styleHandle.setVerticalAlign(DesignChoiceConstants.VERTICAL_ALIGN_MIDDLE);
			styleHandle.setBorderBottomStyle(DesignChoiceConstants.LINE_STYLE_SOLID);
			styleHandle.setBorderTopStyle(DesignChoiceConstants.LINE_STYLE_SOLID);
			styleHandle.setBorderRightStyle(DesignChoiceConstants.LINE_STYLE_SOLID);
			styleHandle.setBorderLeftStyle(DesignChoiceConstants.LINE_STYLE_SOLID);
		}

		RowHandle tableDetail = (RowHandle) table.getDetail().get(0);
		for (int i = 0; i < columnNames.size(); i++) {

			CellHandle cell = (CellHandle) tableDetail.getCells().get(i);

			DataItemHandle data = designFactory.newDataItem(columnNames.get(i));
			data.setResultSetColumn(columnNames.get(i));
			cell.getContent().add(data);

			// formatage du css
			styleHandle = cell.getPrivateStyle();
			styleHandle.setTextAlign(DesignChoiceConstants.TEXT_ALIGN_CENTER);
			styleHandle.setBorderBottomStyle(DesignChoiceConstants.LINE_STYLE_SOLID);
			styleHandle.setBorderTopStyle(DesignChoiceConstants.LINE_STYLE_SOLID);
			styleHandle.setBorderRightStyle(DesignChoiceConstants.LINE_STYLE_SOLID);
			styleHandle.setBorderLeftStyle(DesignChoiceConstants.LINE_STYLE_SOLID);
			styleHandle.setNumberFormat("##.##");

		}

		designHandle.getBody().add(table);

	}
}
