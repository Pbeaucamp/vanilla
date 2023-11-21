package bpm.es.datasource.analyzer.remapper.internal;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import bpm.es.datasource.analyzer.remapper.IRemapperPerformer;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class FavRemap implements IRemapperPerformer{

	
	private Document sourceXmlDocument; 
	private RepositoryItem orginialItem;
	private RepositoryItem newItem;
	
	public FavRemap(Document sourceXmlDocument, RepositoryItem orginialItem, RepositoryItem newItem){
		this.sourceXmlDocument = sourceXmlDocument;
		this.orginialItem = orginialItem;
		this.newItem = newItem;
	}

	public boolean checkModification() throws Exception {
		// TODO check if the is new FMDT model can replace the old one
		return true;
	}

	public void performModification() throws Exception {
		

		Element root = sourceXmlDocument.getRootElement();

		
		for(Element e : (List<Element>)root.selectNodes("//fav/fasdid")){
			if (e.getStringValue().equals(orginialItem.getId() + "")){
				e.setText(newItem.getId() + "");
			}
		}

	}

	public String getTaskPerfomed() {
		return "Replace FASD Items References  " + orginialItem.getItemName() + " by " + newItem.getItemName() + " in FAV model";
	}
}
