package bpm.vanilla.repository.services.parsers;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.platform.core.repository.Parameter;

public abstract class AbstractGeneralParser implements IModelParser{

	protected Document document;
	private String xmlModel;
	
	protected List<Integer> dependanciesId = new ArrayList<Integer>();
	protected List<Parameter> parameters = new ArrayList<Parameter>();
	
	protected void addParameters(Parameter parameter){
		this.parameters.add(parameter);
	}
	
	public AbstractGeneralParser() {}
	
	public AbstractGeneralParser(String xmlModel) throws Exception{
		this.xmlModel = xmlModel;
		try {
			document = DocumentHelper.parseText(xmlModel);
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new Exception("Unable to rebuild dom4j document from Fd xml\n" + e.getMessage(), e);
		}
		
		parseDependancies();
		parseParameters();
	}
	
	abstract protected void parseDependancies() throws Exception;
	abstract protected void parseParameters() throws Exception;

	@Override
	public List<Parameter> getParameters() {
		return new ArrayList<Parameter>(parameters);
	}


	public abstract String overrideXml(Object object) throws Exception;


	@Override
	public List<Integer> getDependanciesDirectoryItemId() {
		return new ArrayList<Integer>(dependanciesId);
	}

	
	public Element getRootElement(){
		return document.getRootElement();
	}
	
	protected String getXmlModelDefinition(){
		return xmlModel;
	}
}
