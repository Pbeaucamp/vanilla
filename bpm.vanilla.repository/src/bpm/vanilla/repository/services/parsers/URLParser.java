package bpm.vanilla.repository.services.parsers;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import bpm.vanilla.platform.core.repository.Parameter;

public class URLParser implements IModelParser {
	protected Document document;
	private String xmlModel;

	private List<Parameter> parameters = new ArrayList<Parameter>();
	protected List<Integer> dependanciesId = new ArrayList<Integer>();

	public URLParser(String xmlModel) throws Exception {
		this.xmlModel = xmlModel;
		try {
			document = DocumentHelper.parseText(xmlModel);
		} catch(DocumentException e) {
			e.printStackTrace();
			throw new Exception("Unable to rebuild dom4j document from Fd xml\n" + e.getMessage(), e);
		}
		parseDependancies();
	}

	private void parseDependancies() {

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
		return new ArrayList<Integer>();
	}
}
