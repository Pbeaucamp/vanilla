package bpm.fm.oda.driver.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Metric;
import bpm.freemetrics.api.manager.client.FmClientAccessor;

public class QueryHelper {

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private FmClientAccessor manager;
	
	private List<Metric> metrics = new ArrayList<Metric>();
	private List<Axis> axes = new ArrayList<Axis>();
	private Date startDate;
	private Date endDate;
	private boolean dateAsParameter;

	public QueryHelper() {
		
	}
	
	public QueryHelper(FmClientAccessor manager) {
		this.manager = manager;
	}
	
	public String createQueryXml(List<bpm.fm.api.model.Metric> selectedMetric, List<Axis> selectedAxis, String strDate, String enDate, boolean dateAsParameter) {
		
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("KpiQuery");
		
		Element metrics = root.addElement("Metrics");
		for(bpm.fm.api.model.Metric metric : selectedMetric) {
			metrics.addElement("Metric").setText(metric.getId() + "");
		}
		
		Element axes = root.addElement("Axes");
		for(Axis axis : selectedAxis) {
			axes.addElement("Axis").setText(axis.getId() + "");
		}
		
		root.addElement("startDate").setText(strDate);
		root.addElement("endDate").setText(enDate);
		
		root.addElement("dateAsParameter").setText(dateAsParameter + "");
		
		return doc.asXML();
	}
	
	public void parseQueryXml(String xml) throws Exception {
		Document doc = DocumentHelper.parseText(xml);
		
		for(Element m : (List<Element>)doc.getRootElement().element("Metrics").elements("Metric")) {
			int id = Integer.parseInt(m.getText());
			
			Metric metric = manager.getRemoteFm().getMetric(id);
			metrics.add(metric);
		}
		
		for(Element m : (List<Element>)doc.getRootElement().element("Axes").elements("Axis")) {
			int id = Integer.parseInt(m.getText());
			
			Axis axis = manager.getRemoteFm().getAxis(id);
			axes.add(axis);
		}
		
		String date = doc.getRootElement().element("startDate").getText();
		startDate = dateFormat.parse(date);
		
		try {
			date = doc.getRootElement().element("endDate").getText();
			endDate = dateFormat.parse(date);
		} catch (Exception e) {
		}
		
		String asParam = doc.getRootElement().element("dateAsParameter").getText();
		try {
			dateAsParameter = Boolean.parseBoolean(asParam);
		} catch (Exception e) {
		}
	}

	public List<Metric> getMetrics() {
		return metrics;
	}

	public List<Axis> getAxes() {
		return axes;
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
