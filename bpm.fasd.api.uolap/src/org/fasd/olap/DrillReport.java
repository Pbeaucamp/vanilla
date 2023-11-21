package org.fasd.olap;

import java.util.ArrayList;
import java.util.List;

public class DrillReport extends Drill {

	private String name;
	private int itemId;
	private List<DrillReportParameter> parameters;
	
	public DrillReport() {}
	
	public DrillReport(int itemId, String name) {
		this.itemId = itemId;
		this.name = name;
	}
	
	public DrillReport(int itemId, String name, List<DrillReportParameter> parameters) {
		this.itemId = itemId;
		this.name = name;
		this.parameters = parameters;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public List<DrillReportParameter> getParameters() {
		if(parameters == null) {
			parameters = new ArrayList<DrillReportParameter>();
		}
		return parameters;
	}

	public void setParameters(List<DrillReportParameter> parameters) {
		this.parameters = parameters;
	}
	
	public void addParameter(DrillReportParameter parameter) {
		if(parameters == null) {
			parameters = new ArrayList<DrillReportParameter>();
		}
		parameters.add(parameter);
	}

	@Override
	public String getXml() {
		StringBuilder buf = new StringBuilder();
		buf.append("<drillreport>\n");
		
		buf.append("	<name>"+drillName+"</name>\n");
		buf.append("	<item>"+itemId+"</item>\n");
		buf.append("	<reportname>"+name+"</reportname>\n");
		buf.append("	<parameters>\n");
		
		try {
			for(DrillReportParameter dim : parameters) {
				buf.append("	<parameter>\n");
				buf.append("		<name>"+dim.getName()+"</name>\n");
				buf.append("		<dimension>"+dim.getDimension()+"</dimension>\n");
				buf.append("		<level>"+dim.getLevel()+"</level>\n");
				
				buf.append("	</parameter>\n");
			}
		} catch (Exception e) {

		}
		
		buf.append("	</parameters>\n");
		
		buf.append("</drillreport>\n");
		return buf.toString();
	}
	
	public void setItemId(String id) {
		itemId = Integer.parseInt(id);
	}
	
}
