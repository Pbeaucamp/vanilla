package bpm.fwr.api.birt;

import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ElementFactory;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.ReportItemHandle;
import org.eclipse.birt.report.model.api.StructureFactory;
import org.eclipse.birt.report.model.api.elements.structures.ComputedColumn;

import bpm.birt.fusionmaps.core.reportitem.FusionmapsItem;
import bpm.fwr.api.beans.components.VanillaMapComponent;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;
import bpm.vanilla.map.core.design.fusionmap.FusionMapProperties;

public class VanillaMapComponentCreator implements IComponentCreator<VanillaMapComponent> {

	@Override
	public DesignElementHandle createComponent(ReportDesignHandle designHandle, ElementFactory designFactory, String selectedLanguage, VanillaMapComponent component, String outputFormat) throws Exception {
		FusionmapsItem item = new FusionmapsItem(designFactory.newExtendedItem(component.getName(), FusionmapsItem.EXTENSION_NAME));
		item.setMapID(component.getId());
		item.setMapHeight(component.getHeight());
		item.setMapWidth(component.getWidth());
		item.setSwfURL(component.getSwfUrl());
		item.setMapDataXML(buildMapXml(component));
		item.setCustomProperties(FusionMapProperties.buildPropertiesFromXml(component.getCustomProperties()));

		item.getItemHandle().setDataSet(designHandle.findDataSet(component.getDataset().getName()));

		ComputedColumn bindingColumnId = StructureFactory.newComputedColumn(item.getItemHandle(), "columnId");
		bindingColumnId.setExpression("dataSetRow[\"" + component.getColumnId().getDatasetRowExpr(selectedLanguage) + "\"]");
		((ReportItemHandle) item.getItemHandle()).addColumnBinding(bindingColumnId, false);

		ComputedColumn bindingColumnValues = StructureFactory.newComputedColumn(item.getItemHandle(), "columnValues");
		bindingColumnValues.setExpression("dataSetRow[\"" + component.getColumnValues().getDatasetRowExpr(selectedLanguage) + "\"]");
		((ReportItemHandle) item.getItemHandle()).addColumnBinding(bindingColumnValues, false);

		return item.getItemHandle();
	}
	
	private String buildMapXml(VanillaMapComponent component) {
		StringBuilder buf = new StringBuilder();
		buf.append("<root>\n");
		buf.append("  <map>\n");
		buf.append("    <unit>" + component.getUnit() + "</unit>\n");
		buf.append("    <id>columnId</id>\n");
		buf.append("    <values>columnValues</values>\n");
		buf.append("    <parameters></parameters>\n");
		buf.append("    <colorRange>\n");
		for(ColorRange color : component.getColors()){
			buf.append("      <color>\n");
			buf.append("        <name>" + color.getName() + "</name>\n");
			buf.append("        <hexa>" + color.getHex() + "</hexa>\n");
			buf.append("        <min>" + color.getMin() + "</min>\n");
			buf.append("        <max>" + color.getMax() + "</max>\n");
			buf.append("      </color>\n");
		}
		buf.append("    </colorRange>\n");
		buf.append("  </map>\n");
		buf.append("</root>");
		
		return buf.toString();
	}
}
