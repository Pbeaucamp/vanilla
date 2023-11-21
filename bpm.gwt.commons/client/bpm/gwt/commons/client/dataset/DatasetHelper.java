package bpm.gwt.commons.client.dataset;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Metric;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class DatasetHelper {

	private static DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy-MM-dd");

	private List<Metric> metrics = new ArrayList<Metric>();
	private Axis axis;
	private Date startDate;
	private Date endDate;
	private boolean dateAsParameter;

	public String generateKpiDatasetXml(List<Metric> metrics, Axis axes, Date startDate, Date endDate, boolean dateAsParameter) {
		StringBuilder buf = new StringBuilder();

		buf.append("<KpiQuery>\n");
		buf.append("	<Metrics>\n");
		for (Metric m : metrics) {
			buf.append("		<Metric>" + m.getId() + "</Metric>\n");
		}
		buf.append("	</Metrics>\n");

		buf.append("	<Axes>\n");

		buf.append("		<Axis>" + axes.getId() + "</Axis>\n");

		buf.append("	</Axes>\n");

		buf.append("	<startDate>" + dateFormat.format(startDate) + "</startDate>\n");
		buf.append("	<endDate>" + dateFormat.format(endDate) + "</endDate>\n");
		buf.append("	<dateAsParameter>" + dateAsParameter + "</dateAsParameter>\n");

		buf.append("</KpiQuery>\n");

		return buf.toString();
	}

	public void parseKpiDatasetXml(String xml, List<Metric> existingMetrics, List<Axis> existingAxis) {
		Document doc = XMLParser.parse(xml);
		//find metrics
		NodeList metricList = doc.getElementsByTagName("Metric");
		for(int i = 0 ; i < metricList.getLength() ; i++) {
			String mIdString = metricList.item(i).getFirstChild().getNodeValue();
			int mId = Integer.parseInt(mIdString.trim());
			for(Metric m : existingMetrics) {
				if(mId == m.getId()) {
					metrics.add(m);
					break;
				}
			}
		}
		//find axis
		NodeList axisList = doc.getElementsByTagName("Axis");
		String axisIdString = axisList.item(0).getFirstChild().getNodeValue();
		int axisId = Integer.parseInt(axisIdString.trim());
		for(Axis m : existingAxis) {
			if(axisId == m.getId()) {
				axis = m;
				break;
			}
		}
		//find dates
		NodeList startDateList = doc.getElementsByTagName("startDate");
		String startDateString = startDateList.item(0).getFirstChild().getNodeValue();
		startDate = dateFormat.parse(startDateString);
		
		NodeList endDateList = doc.getElementsByTagName("endDate");
		String endDateString = endDateList.item(0).getFirstChild().getNodeValue();
		endDate = dateFormat.parse(endDateString);
		
		NodeList paramDateList = doc.getElementsByTagName("dateAsParameter");
		String paramDateString = paramDateList.item(0).getFirstChild().getNodeValue();
		dateAsParameter = Boolean.parseBoolean(paramDateString);
	}

	public List<Metric> getMetrics() {
		return metrics;
	}

	public Axis getAxis() {
		return axis;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public boolean isDateAsParameter() {
		return dateAsParameter;
	}

}
