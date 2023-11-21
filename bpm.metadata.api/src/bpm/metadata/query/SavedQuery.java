package bpm.metadata.query;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

import bpm.metadata.digester.SqlQueryDigester;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.vanilla.platform.core.beans.chart.SavedChart;

import com.thoughtworks.xstream.XStream;

public class SavedQuery {
	
	private String name;
	private String description;
	private QuerySql query;
	
	private List<SavedChart> charts;
	
	private String queryXml;
	private String chartXml;

	public SavedQuery() {}
	
	public SavedQuery(String name, String description, QuerySql query) {
		this.name = name;
		this.description = description;
		this.query = query;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description != null ? description : "";
	}

	public String getXml() {
		String queryTmp = query != null ? query.getXml() : queryXml;
		String chartXml = null;
		if (charts != null && !charts.isEmpty()) {
			XStream xstream = new XStream();
			chartXml = xstream.toXML(charts);
		}
		else if (this.chartXml != null && !this.chartXml.isEmpty()) {
			chartXml = this.chartXml;
		}
		
		StringBuffer buf = new StringBuffer();
		buf.append("<savedQuery>\n");
		buf.append("    <name>" + name + "</name>\n");
		buf.append("    <description>" + description + "</description>\n");
		buf.append("    <queryXml><![CDATA[" + queryTmp + "]]></queryXml>\n");
		if (chartXml != null && !chartXml.isEmpty()) {
			buf.append("    <chartXml><![CDATA[" + chartXml + "]]></chartXml>\n");
		}
		buf.append("</savedQuery>\n");
		return buf.toString();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setQuery(String queryXml) {
		this.queryXml = queryXml;
	}
	
	public QuerySql loadQuery(String groupName, IBusinessPackage pack) throws Exception {
		if(query != null) {
			return query;
		}
		
		if(queryXml != null) {
			InputStream is = IOUtils.toInputStream(queryXml, "UTF-8");
			
			SqlQueryDigester dig = new SqlQueryDigester(is, groupName, pack);
			query = dig.getModel();
			return query;
		}
		
		return null;
	}
	
	
	public List<SavedChart> getCharts() {
		return charts;
	}
	
	public void setCharts(List<SavedChart> charts) {
		this.charts = charts;
	}
	
//	public void addChart(SavedChart chart) {
//		if (charts == null) {
//			this.charts = new ArrayList<>();
//		}
//		this.charts.add(chart);
//	}
	
	public List<SavedChart> loadChart() {
		if (charts != null && !charts.isEmpty()) {
			return charts;
		}
		
		if (chartXml == null || chartXml.isEmpty()) {
			return null;
		}
		
		XStream xstream = new XStream();
		this.charts = (List<SavedChart>) xstream.fromXML(chartXml);
		return charts;
	}
	
	public void setChartXml(String chartXml) {
		this.chartXml = chartXml;
	}

	public boolean hasChart() {
		return (charts != null && !charts.isEmpty()) || (chartXml != null && !chartXml.isEmpty());
	}
}
