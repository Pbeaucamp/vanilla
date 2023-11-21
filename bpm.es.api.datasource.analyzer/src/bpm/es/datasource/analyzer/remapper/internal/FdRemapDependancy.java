package bpm.es.datasource.analyzer.remapper.internal;

import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

import bpm.es.datasource.analyzer.remapper.IRemapperPerformer;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class FdRemapDependancy implements IRemapperPerformer{

	
	private Document sourceXmlDocument; 
	private RepositoryItem orginialItem;
	private RepositoryItem newItem;
	
	public FdRemapDependancy(Document sourceXmlDocument, RepositoryItem orginialItem, RepositoryItem newItem){
		this.sourceXmlDocument = sourceXmlDocument;
		this.orginialItem = orginialItem;
		this.newItem = newItem;
	}

	public boolean checkModification() throws Exception {
		// TODO check if the is new FMDT model can replace the old one
		return true;
	}

	public void performModification() throws Exception {
		
		
		HashMap nameSpaceMap = new HashMap();

		Element root = sourceXmlDocument.getRootElement();
		if (root.getNamespaceURI() != null){
			nameSpaceMap.put("fd", root.getNamespaceURI());
		}
		 
		XPath xpath = new Dom4jXPath( "//dependancies/dependantDirectoryItemId" );
		
		SimpleNamespaceContext nmsCtx = new SimpleNamespaceContext( nameSpaceMap);
		xpath.setNamespaceContext(nmsCtx);

		
		for(Element e : (List<Element>)xpath.selectNodes(sourceXmlDocument)){
			
			if (e.getStringValue().equals(orginialItem.getId() + "")){
				e.setText(newItem.getId() + "");
			}

		}

	}

	public String getTaskPerfomed() {
		return "Replace Items References(Fd,Dico, ExtDoc)  " + orginialItem.getItemName() + " by " + newItem.getItemName() + " in FD model";
	}
}
