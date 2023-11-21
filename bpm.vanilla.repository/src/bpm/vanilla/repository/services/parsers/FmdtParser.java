package bpm.vanilla.repository.services.parsers;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class FmdtParser extends AbstractGeneralParser{

	public FmdtParser(String xmlModel) throws Exception {
		super(xmlModel);
		
	}

	@Override
	public String overrideXml(Object object) {
		HashMap<String, String> p = (HashMap<String, String>)object;
		

		
		for(String k : p.keySet()){
			boolean found = false;
			for(Object o : document.selectNodes(k)){
				found = true;
				((Element)o).setText(p.get(k));
			}
			
			if (! found){
				int pos = k.lastIndexOf("/");
				Element n = (Element)document.selectSingleNode(k.substring(0, pos));
				n.addElement(k.substring(pos+1)).setText(p.get(k));
			}
		}
		
		try{
			
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			OutputFormat form = OutputFormat.createPrettyPrint();
//			form.setEncoding("UTF-8");
			
			form.setTrimText(false);
			XMLWriter writer = new XMLWriter(os, form);
			writer.write(document.getRootElement());
			writer.close();
			
			return os.toString("UTF-8");
		}catch(Exception ex){
			
		}
		
		return document.getRootElement().asXML();
	}
	
	@Override
	protected void parseDependancies() {
		for(Element e : (List<Element>)document.getRootElement().elements("olapDataSource")){
			if (e.element("olapConnection") != null){
				
				Element d = e.element("olapConnection").element("directoryItemId");
				
				if (d != null){
					dependanciesId.add(Integer.parseInt(d.getStringValue()));
				}
				
			}
		}
		
	}
	
	@Override
	public List<Integer> getDataSourcesReferences() {
		return new ArrayList<Integer>();
	}

	@Override
	protected void parseParameters() throws Exception {
		
	}

}
