package bpm.fa.api.olap.projection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class ProjectionParser {

	public static Projection parse(String xml) throws Exception {
		Projection model = new Projection();
		Document doc = DocumentHelper.parseText(xml);
		
		Element root = doc.getRootElement();
		
		Element projection = root;
		
		int fasdId = Integer.parseInt(projection.element("fasd").getText());
		String cubeName = projection.element("cubename").getText();
		String type = projection.element("type").getText();
		
		List<ProjectionMeasure> measures = new ArrayList<ProjectionMeasure>();
		for(Element mes : (List<Element>)root.element("projectionmeasures").elements("projectionmeasure")) {
			ProjectionMeasure projMes = new ProjectionMeasure();
			String uname = mes.element("uname").getText();
			String formula = mes.element("formula").getText();
			projMes.setFormula(formula);
			projMes.setUname(uname);
			
			Element conds = mes.element("conditions");
			if(conds.elements("condition") != null) {
				for(Element cond : (List<Element>) conds.elements("condition")) {
					ProjectionMeasureCondition condition = new ProjectionMeasureCondition();
					condition.setFormula(cond.elementText("formula"));
					for(Element unameElem : (List<Element>) cond.element("unames").elements("uname")) {
						condition.addMemberUname(unameElem.getText());
					}
					projMes.addCondition(condition);
				}
			}
			
			
			measures.add(projMes);
		}
		
		model.setFasdId(fasdId);
		model.setCubeName(cubeName);
		model.setProjectionMeasures(measures);
		model.setType(type);
		
		if(type.equals(Projection.TYPE_EXTRAPOLATION)) {
			String endDateString = projection.element("enddate").getText();
			String startDateString = projection.element("startdate").getText();
			String projectionLevel = projection.element("projectionlevel").getText();
			model.setProjectionLevel(projectionLevel);
			model.setEndDateString(endDateString);
			model.setStartDateString(startDateString);
		}
		
		return model;
	}
	
	public static String getXml(Projection proj) {
		Document doc = DocumentHelper.createDocument();
		
		Element root = doc.addElement("projection");
		
		root.addElement("fasd").setText(proj.getFasdId()+"");
		root.addElement("cubename").setText(proj.getCubeName());
		
		if(proj.getType().equals(Projection.TYPE_EXTRAPOLATION)) {
			root.addElement("enddate").setText(proj.getEndDateString());
			root.addElement("startdate").setText(proj.getStartDateString());
			root.addElement("projectionlevel").setText(proj.getProjectionLevel());
		}
		
		root.addElement("type").setText(proj.getType());
		
		Element measures = root.addElement("projectionmeasures");
		for(ProjectionMeasure mes : proj.getProjectionMeasures()) {
			Element measure = measures.addElement("projectionmeasure");
			measure.addElement("uname").setText(mes.getUname());
			measure.addElement("formula").setText(mes.getFormula());
			
			Element elemConds = measure.addElement("conditions");
			for(ProjectionMeasureCondition cond : mes.getConditions()) {
				Element elemCond = elemConds.addElement("condition");
				elemCond.addElement("formula").setText(cond.getFormula());
				Element condUnames = elemCond.addElement("unames");
				for(String uname : cond.getMemberUnames()) {
					condUnames.addElement("uname").setText(uname);
				}
			}
		}
		
		try {
			Element xml = doc.getRootElement();
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			OutputFormat form = OutputFormat.createPrettyPrint();
			form.setTrimText(false);
			XMLWriter writer = new XMLWriter(bos, form);
			writer.write(xml);
			writer.close();
			
			return bos.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return doc.asXML();
	}
}
