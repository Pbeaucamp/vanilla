package bpm.vanilla.repository.services.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

import bpm.vanilla.platform.core.repository.Parameter;

public class JASPERParser implements IModelParser {
	protected Document document;
	private String xmlModel;

	private List<Parameter> parameters = new ArrayList<Parameter>();
	protected List<Integer> dependanciesId = new ArrayList<Integer>();
	protected List<Integer> dataSourceReferences = new ArrayList<Integer>();

	public JASPERParser(String xmlModel) throws Exception {
		this.xmlModel = xmlModel;
		try {
			document = DocumentHelper.parseText(xmlModel);
		} catch(DocumentException e) {
			e.printStackTrace();
			throw new Exception("Unable to rebuild dom4j document from Fd xml\n" + e.getMessage(), e);
		}

		parseDocument();
		parseDependancies();
	}

	private void parseDocument() throws Exception {

		/*
		 * parse parameters
		 */
		List<Element> lst = new ArrayList<Element>();

		HashMap nameSpaceMap = new HashMap();

		Element root = document.getRootElement();
		if(root.getNamespaceURI() != null) {
			nameSpaceMap.put("jasper", root.element("xml").element("jasperReport").getNamespaceURI());
		}

		try {
			XPath xpath = new Dom4jXPath("//jasper:parameter");

			SimpleNamespaceContext nmsCtx = new SimpleNamespaceContext(nameSpaceMap);
			xpath.setNamespaceContext(nmsCtx);

			lst.addAll(xpath.selectNodes(document));
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new Exception("Unable to parse Jasper Model parameters : " + ex.getMessage(), ex);
		}

		lst.addAll(document.getRootElement().selectNodes("//prompt"));

		for(Element e : lst) {
			String s = null;
			if(e.attribute("name") != null) {
				s = e.attribute("name").getText();
			}
			else {
				s = e.getStringValue();
			}
			Parameter param = new Parameter();
			param.setName(s);
			parameters.add(param);
		}
	}

	private void parseDependancies() {
		Element e = document.getRootElement().element("datasourceid");;

		if(e != null) {
			try {
				int dataSourceId = Integer.parseInt(e.getStringValue());
				dataSourceReferences.add(dataSourceId);
			} catch(Exception ex) {

			}
		}
	}

	@Override
	public List<Integer> getDependanciesDirectoryItemId() {
		return dependanciesId;
	}

	@Override
	public List<Parameter> getParameters() {
		return parameters;
	}

	@Override
	public String overrideXml(Object object) {
		return xmlModel;
	}

	@Override
	public List<Integer> getDataSourcesReferences() {
		return dataSourceReferences;
	}
}
