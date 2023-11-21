package bpm.vanilla.repository.services.parsers;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.jaxen.JaxenException;
import org.jaxen.dom4j.Dom4jXPath;

import bpm.vanilla.platform.core.beans.comments.CommentDefinition;
import bpm.vanilla.platform.core.beans.comments.CommentDefinition.TypeComment;
import bpm.vanilla.platform.core.beans.comments.CommentParameter;
import bpm.vanilla.platform.core.repository.Parameter;

public class FdDictionaryParser implements IModelParser{

	private Document document;
	private String xmlModel;
	
	private List<Integer> dependanciesId ;
	
	private List<CommentDefinition> commentsDef = new ArrayList<CommentDefinition>();
	
	public FdDictionaryParser(String xmlModel) throws Exception{
		this.xmlModel = xmlModel;
		try {
			document = DocumentHelper.parseText(xmlModel);
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new Exception("Unable to rebuild dom4j document from FdDictionary xml\n" + e.getMessage(), e);
		}
		
		parseDocument();
	}
	
	
	private void parseDocument(){
		Element root = document.getRootElement();
		
		/*
		 * parseDependancies
		 */
		dependanciesId = new ArrayList<Integer>();
		if (root.element("dependancies") != null){
			for(Object o : root.element("dependancies").elements("dependantDirectoryItemId")){
				dependanciesId.add(Integer.parseInt(((Element)o).getStringValue()));
			}
		}
		
		try {
			Dom4jXPath xpath = new Dom4jXPath("//dictionary/comment");
			
			List<Node> comments = xpath.selectNodes(document);
			
			for (Node com : comments) {
				
				String name = ((Element)com).attribute("name").getText();
				Node comment = com.selectSingleNode("comment");
				String label = comment.getText();
				Node opt = com.selectSingleNode("options");
				
				CommentDefinition def = new CommentDefinition();
				
				def.setName(name);
				def.setLabel(label);
				
				if(Boolean.parseBoolean(opt.selectSingleNode("validation").getText())) {
					def.setType(TypeComment.VALIDATION);
				}
				else {
					def.setType(TypeComment.RESTITUTION);
				}
				
				int limit = Integer.parseInt(opt.selectSingleNode("limit").getText());
				def.setNbComments(limit);
				
				def.setLimit(Boolean.parseBoolean(opt.selectSingleNode("limitComment").getText()));
				
				List<CommentParameter> parameters = new ArrayList<CommentParameter>();
				for(Node params : (List<Node>) com.selectNodes("parameter")) {
					CommentParameter p = new CommentParameter();
					
					p.setParameterIdentifier(((Element)params).attribute("name").getText());
					p.setPrompt(((Element)params).attribute("name").getText());
					
					parameters.add(p);
				}
				
				def.setParameters(parameters);

				commentsDef.add(def);
			}
			
		} catch (JaxenException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	/**
	 * @return an empty List
	 */
	public List<Parameter> getParameters() {
		return new ArrayList<Parameter>();
	}

	@Override
	public String overrideXml(Object object) {
		
		return xmlModel;
	}


	@Override
	public List<Integer> getDependanciesDirectoryItemId() {
		return dependanciesId;
	}

	@Override
	public List<Integer> getDataSourcesReferences() {
		return new ArrayList<Integer>();
	}
	
	public List<CommentDefinition> getCommentDefinitions(int itemId) {
		if (commentsDef == null) {
			return new ArrayList<CommentDefinition>();
		}

		for (CommentDefinition comDef : commentsDef) {
			comDef.setItemId(itemId);
		}
		return commentsDef;
	}
}
