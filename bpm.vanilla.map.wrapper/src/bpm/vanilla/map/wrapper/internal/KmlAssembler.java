package bpm.vanilla.map.wrapper.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

public class KmlAssembler {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File kmlFolder = new File("C:/developpement/kml");

		
		Document main = null;
		 Element eDoc = null;
		 int count = 0;
		for(String s : kmlFolder.list()){
//			if (count == 1){
//				break;
//			}
			File kmlFile = new File(kmlFolder, s);
			
			if (!kmlFile.isFile()){
				continue;
			}
			
			
			try{
				SAXReader reader = new SAXReader();
			    Document document = reader.read(kmlFile);
			    
			    HashMap nameSpaceMap = new HashMap();

				
				if (document.getRootElement().getNamespaceURI() != null){
				   nameSpaceMap.put("kml", document.getRootElement().getNamespaceURI());
				}
			   
			    if (main == null){
			    	Element root = DocumentHelper.createElement("kml");
			    	main = DocumentHelper.createDocument(root);
			    	
			    	Namespace namespace1 = new Namespace("", "http://earth.google.com/kml/2.2");
			    	
			    	main.getRootElement().add(namespace1);
			    	
			    	
			    	
			    	eDoc = root.addElement("Document");
			    	
			    	for(Element e : (List<Element>)document.getRootElement().element("Document").elements()){
			    		if (!e.getName().equals("Placemark")){
			    			eDoc.add(e.createCopy());
			    		}
			    	}
			    				    	
			    }
			    
			    XPath xpath = new Dom4jXPath( "//kml:Placemark" );
				  
				SimpleNamespaceContext nmsCtx = new SimpleNamespaceContext( nameSpaceMap);
				xpath.setNamespaceContext(nmsCtx);
			    for(Node n : (List<Node>)xpath.selectNodes(document)){
			    	Element c = ((Element)n).createCopy(); 
			    	c.remove(c.getNamespace());
			    	eDoc.add(c);
			    }
			    
			    
			    
			    
			    
			}catch(Exception ex){
			ex.printStackTrace();	
			System.err.println("Error on " + s);
			}
			count++;
			
		}
		
		try{
			OutputFormat f = OutputFormat.createPrettyPrint();
			f.setEncoding("UTF-8");
			f.setNewlines(false);
			f.setTrimText(false);
			XMLWriter writer = new XMLWriter(new FileOutputStream("C:/developpement/kml/france.kml"), f);
			writer.write(main);
			writer.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}

}
