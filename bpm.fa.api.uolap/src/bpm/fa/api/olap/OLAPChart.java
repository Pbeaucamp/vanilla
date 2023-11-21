package bpm.fa.api.olap;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester3.Digester;

public class OLAPChart {
	private String title;
	
	private String type;
	
	public String defaultRenderer;
	
	private List<String> selectedGroup = new ArrayList<String>();
	private List<String> selectedData = new ArrayList<String>();
	private List<String> selectedChartFilters = new ArrayList<String>();
	private String axeX = "";
	private String measure = "";
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDefaultRenderer() {
		return defaultRenderer;
	}

	public void setDefaultRenderer(String defaultRenderer) {
		this.defaultRenderer = defaultRenderer;
	}

	public List<String> getSelectedGroup() {
		return selectedGroup;
	}

	public void setSelectedGroup(List<String> selectedGroup) {
		this.selectedGroup = selectedGroup;
	}
	
	public void addSelectedGroup(String group) {
		this.selectedGroup.add(group);
	}

	public List<String> getSelectedData() {
		return selectedData;
	}

	public void setSelectedData(List<String> selectedData) {
		this.selectedData = selectedData;
	}

	public void addSelectedData(String data) {
		this.selectedData.add(data);
	}
	
	public List<String> getSelectedChartFilters() {
		return selectedChartFilters;
	}

	public void setSelectedChartFilters(List<String> selectedChartFilters) {
		this.selectedChartFilters = selectedChartFilters;
	}
	
	public void addSelectedChartFilter(String filter) {
		this.selectedChartFilters.add(filter);
	}

	public String getMeasure() {
		return measure;
	}

	public void setMeasure(String measure) {
		this.measure = measure;
	}
	
	public String getAxeX() {
		return axeX;
	}
	public void setAxeX(String axeX) {
		this.axeX = axeX;
	}
	
	public String loadXML() {
		String chartXml = "";

		chartXml += "<chart>\n";

		chartXml += "	<title>" + title + "</title>\n";

		chartXml += "	<groups>\n";
		for (String chartGroup : selectedGroup) {
			chartXml += "		<group>" + chartGroup + "</group>\n";
		}
		chartXml += "	</groups>\n";

		chartXml += "	<datas>\n";
		for (String chartData : selectedData) {
			chartXml += "		<data>" + chartData + "</data>\n";
		}
		chartXml += "	</datas>\n";

		chartXml += "	<filters>\n";
		for (String filter : selectedChartFilters) {
			chartXml += "		<filter>" + filter + "</filter>\n";
		}
		chartXml += "	</filters>\n";

		chartXml += "	<measure>" + measure + "</measure>\n";

		chartXml += "	<type>" + type + "</type>\n";

		chartXml += "	<renderer>" + defaultRenderer + "</renderer>\n";
		
		chartXml += "	<axex>" + axeX + "</axex>\n";

		chartXml += "</chart>\n";
		
		return chartXml;
	}
	
	public static void digest(Digester dig,String node) {
		dig.addObjectCreate(node, OLAPChart.class);
			dig.addCallMethod(node + "/title", "setTitle", 0);
			dig.addCallMethod(node + "/groups/group", "addSelectedGroup", 0);
			dig.addCallMethod(node + "/datas/data", "addSelectedData", 0);
			dig.addCallMethod(node + "/filters/filter", "addSelectedChartFilter", 0);
			dig.addCallMethod(node + "/measure", "setMeasure", 0);
			dig.addCallMethod(node + "/type", "setType", 0);
			dig.addCallMethod(node + "/renderer", "setDefaultRenderer", 0);
			dig.addCallMethod(node + "/axex", "setAxeX", 0);
	}
}
