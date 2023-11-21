package bpm.es.datasource.analyzer.remapper;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import bpm.vanilla.platform.core.repository.RepositoryItem;



public class ModelRemapper {

	private Document modelXmlDocument;
	private List<IRemapperPerformer> remappers = new ArrayList<IRemapperPerformer>();
	private RepositoryItem item;
	
	
	public ModelRemapper(Document modelXmlDocument, RepositoryItem item){
		this.modelXmlDocument = modelXmlDocument;
		this.item = item;
	}
	
	public void addPerformer(IRemapperPerformer performer){
		remappers.add(performer);
	}
	
	public List<IRemapperPerformer> getPerformers(){
		return new ArrayList<IRemapperPerformer>(remappers);
	}
	
	public Document getModelXmlDocument(){
		return modelXmlDocument;
	}
	
	public String getModelXmlDocument(String encoding) throws Exception{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		OutputFormat f = OutputFormat.createPrettyPrint();
		f.setEncoding(encoding);
		f.setTrimText(false);
		f.setNewlines(false);
		XMLWriter writer = new XMLWriter(bos, f);
		writer.write(getModelXmlDocument().getRootElement());
		writer.close();
		
		return bos.toString(encoding);
	}

	public String getItemName() {
		return item.getItemName();
	}
	
	public RepositoryItem getItem(){
		return item;
	}
}
