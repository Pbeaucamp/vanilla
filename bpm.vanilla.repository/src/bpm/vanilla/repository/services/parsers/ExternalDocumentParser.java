package bpm.vanilla.repository.services.parsers;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import bpm.vanilla.platform.core.repository.Parameter;

public class ExternalDocumentParser implements IModelParser{
	protected Document document;
	private String xmlModel;
	
	protected List<Integer> dependanciesId = new ArrayList<Integer>();
	
	public ExternalDocumentParser(String xmlModel) throws Exception{
		this.xmlModel = xmlModel;
		try {
			document = DocumentHelper.parseText(xmlModel);
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new Exception("Unable to rebuild dom4j document from Fd xml\n" + e.getMessage(), e);
		}
		
		parseDependancies();
	}
	
	public String getRelativePath() {
		return document.getRootElement().element("path").getText();
	}
	
	private void parseDependancies(){
		
	}
	
	@Override
	public List<Integer> getDependanciesDirectoryItemId() {
		return new ArrayList<Integer>();
	}
	
	@Override
	public List<Parameter> getParameters() {
		return new ArrayList<Parameter>();
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
