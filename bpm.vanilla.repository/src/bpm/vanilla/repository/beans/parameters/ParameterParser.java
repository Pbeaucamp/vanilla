package bpm.vanilla.repository.beans.parameters;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.platform.core.repository.Parameter;

public class ParameterParser {
	public static final int ORBEON = 1;
	public static final int JASPER = 2;
	public static final int BIRT = 3;
	public static final int FWR = 4;
	public static final int GTW = 5;
	public static final int FD = 6;
	public static final int FAV = 7;

	public static final List<Parameter> parse(int modelType, Element e) {
		switch (modelType) {
			case ORBEON:
				return parseOrbeon(e);
			case JASPER:
				return parseJasper(e);
			case BIRT:
				return parseBIRT(e);
			case FWR:
				return parseFWR(e);
			case GTW:
				return parseGTW(e);
			case FD:
				return parseFd(e);
			case FAV:
				return parseFAV(e);

		}
		return new ArrayList<Parameter>();

	}

	private static List<Parameter> parseFAV(Element e) {
		List<Parameter> l = new ArrayList<Parameter>();
		if (e.element("report").element("fav").element("view").element("parameters").elements("parameter") != null) {
			for (Element elem : (List<Element>) e.element("report").element("fav").element("view").element("parameters").elements("parameter")) {
				Parameter p = new Parameter();
				p.setName(elem.element("name").getStringValue());
				l.add(p);
			}
		}
		return l;
	}

	private static List<Parameter> parseFd(Element xmlElement) {
		List<Parameter> l = new ArrayList<Parameter>();
		if (xmlElement.element("formsOutputs") != null) {

			for (Element _p : (List<Element>) xmlElement.element("formsOutputs").elements("output")) {
				Parameter p = new Parameter();
				p.setName(_p.getStringValue());
				l.add(p);
			}

		}
		return l;
	}

	private static List<Parameter> parseGTW(Element xmlElement) {

		List<Parameter> l = new ArrayList<Parameter>();

		List<Element> lst = new ArrayList<Element>();

		lst.addAll(xmlElement.selectNodes("//parameter"));

		for (Element e : lst) {
			String s = null;
			if (e.element("name") != null) {
				s = e.element("name").getStringValue();
				Parameter param = new Parameter();
				if (e.element("defaultValue") != null) {
					param.setDefaultValue(e.element("defaultValue").getStringValue());
				}
				param.setName(s);
				l.add(param);
			}

		}

		return l;

	}

	private static List<Parameter> parseFWR(Element xmlElement) {

		List<Parameter> l = new ArrayList<Parameter>();

		List<Element> lst = new ArrayList<Element>();

		lst.addAll(xmlElement.selectNodes("//fwrprompt"));

		for (Element e : lst) {
			String s = null;
			if (e.element("name") != null) {
				s = e.element("name").getStringValue();
				Parameter param = new Parameter();
				param.setName(s);
				l.add(param);
			}

		}

		return l;

	}

	private static List<Parameter> parseBIRT(Element xmlElement) {
		List<Parameter> l = new ArrayList<Parameter>();

		List<Element> lst = new ArrayList<Element>();

		if (xmlElement.element("parameters") != null) {
			lst.addAll(xmlElement.element("parameters").elements("scalar-parameter"));

			for (Object o : xmlElement.element("parameters").elements("parameter-group")) {
				lst.addAll(((Element) o).element("parameters").elements("scalar-parameter"));
			}
			// if (xmlElement.element("parameters").element("parameter-group")
			// != null) {
			// lst.addAll(xmlElement.element("parameters").element("parameter-group").element("parameters").elements("scalar-parameter"));
			// }

			for (Object o : xmlElement.element("parameters").elements("cascading-parameter-group")) {
				lst.addAll(((Element) o).element("parameters").elements("scalar-parameter"));
			}
		}

		for (Element e : lst) {
			String s = null;
			if (e.attribute("name") != null) {
				boolean skip = false;
				for (Object o : e.elements("property")) {
					if (((Element) o).attribute("name").getText().equals("hidden")) {
						skip = true;
						break;
					}
				}

				if (skip) {
					continue;
				}
				s = e.attribute("name").getText();
				Parameter param = new Parameter();
				param.setName(s);
				l.add(param);
			}

		}

		return l;
	}

	private static List<Parameter> parseJasper(Element xmlElement) {
		List<Parameter> l = new ArrayList<Parameter>();

		List<Element> lst = new ArrayList<Element>();

		lst.addAll(xmlElement.selectNodes("//parameter[@isForPrompting='true']"));
		lst.addAll(xmlElement.selectNodes("//prompt"));

		for (Element e : lst) {
			String s = null;
			if (e.attribute("name") != null) {
				s = e.attribute("name").getText();
			}
			else {
				s = e.getStringValue();
			}
			Parameter param = new Parameter();
			param.setName(s);
			l.add(param);
		}

		return l;
	}

	private static List<Parameter> parseOrbeon(Element xmlElement) {
		List<Parameter> l = new ArrayList<Parameter>();

		List<Element> lst = new ArrayList<Element>();

		lst.addAll(xmlElement.selectNodes("//xforms:input[@ref]"));
		lst.addAll(xmlElement.selectNodes("//xforms:select1[@ref]"));
		lst.addAll(xmlElement.selectNodes("//xforms:select[@ref]"));
		lst.addAll(xmlElement.selectNodes("//xforms:secret[@ref]"));
		lst.addAll(xmlElement.selectNodes("//xforms:textarea[@ref]"));
		lst.addAll(xmlElement.selectNodes("//xforms:range[@ref]"));

		for (Element e : lst) {
			String s = e.attribute("ref").getText();

			Parameter param = new Parameter();
			param.setName(s);
			param.setInstanceName(e.attributeValue("ref"));
			l.add(param);
		}

		return l;
	}

	public static void main(String[] args) {
		try {
			Document doc = DocumentHelper.parseText(IOUtils.toString(new FileInputStream("D:\\doc - Workflow\\tot.gateway"), "UTF-8"));
			parseGTW(doc.getRootElement());
		} catch (Exception e) { }
	}
}
