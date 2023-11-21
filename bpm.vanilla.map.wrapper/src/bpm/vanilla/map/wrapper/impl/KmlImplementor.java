package bpm.vanilla.map.wrapper.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

import bpm.vanilla.map.core.design.fusionmap.ColorRange;
import bpm.vanilla.map.core.design.kml.IKmlManipulator;
import bpm.vanilla.map.core.design.kml.IKmlRegistry;
import bpm.vanilla.map.core.design.kml.KmlColoringDatas;
import bpm.vanilla.map.wrapper.VanillaMapComponent;

public class KmlImplementor implements IKmlManipulator{

	private VanillaMapComponent component;
	public KmlImplementor(VanillaMapComponent component){
		this.component = component;
	}
	
	@Override
	public String generateKml(String originalKmlFileUrl, KmlColoringDatas coloringDatas) throws Exception {
		IKmlRegistry kmlRegistry = component.getKmlRegistry();
		
		if (kmlRegistry == null){
			throw new Exception("Kml Registry not found");
		}
		
		
		URL url = new URL(originalKmlFileUrl);
		component.getLogger().debug("KmlOriginalFile Url : " + url.toString());
		
		SAXReader reader = new SAXReader();
	    Document document = reader.read(url);
	    component.getLogger().debug("Loaded Kml File");
	    HashMap nameSpaceMap = new HashMap();

		
		if (document.getRootElement().getNamespaceURI() != null){
		   nameSpaceMap.put("kml", document.getRootElement().getNamespaceURI());
		}
	    	    
	    List<ColorRange> ranges = coloringDatas.getColorRanges();
	    List<Object[]> datas = coloringDatas.getDatas();
		
	    for(int i = 0; i < datas.size(); i++){
	    	
	    	  XPath xpath = new Dom4jXPath( "//kml:Placemark[@id='" + datas.get(i)[0] + "']" );
			  
				SimpleNamespaceContext nmsCtx = new SimpleNamespaceContext( nameSpaceMap);
				xpath.setNamespaceContext(nmsCtx);
				List<Node>  l = xpath.selectNodes(document);
	    	
	    	for(Node n : l){
	    		Element polyStyle = ((Element)n).element("PolyStyle");
	    		Element color = null;
	    		if ( polyStyle == null){
	    			polyStyle = ((Element)n).addElement("PolyStyle");
	    			color = polyStyle.addElement("color");
	    		}
	    		else{
	    			color = polyStyle.element("color");
	    			if (color == null){
	    				color = polyStyle.addElement("color");
	    			}
	    		}
	    		boolean found = false;
	    		for(ColorRange r : ranges){
	    			if (r.getMin() <= ((Double)datas.get(i)[1]) && ((Double)datas.get(i)[1])<= r.getMax()){
	    				color.setText("7f" + r.getHex());
	    				found = true;
	    			}
	    		}
	    		if (!found){
	    			n.getParent().remove(n);
	    		}
	    		
	    		
	    	}
	    }
	    
	    File tmpFolder = new File(kmlRegistry.getKmlFolderLocation() + "/tmp");
	    if (!tmpFolder.exists()){
	    	component.getLogger().warn(tmpFolder.getAbsoluteFile() + " do not exists");
	    	tmpFolder.mkdirs();
	    	component.getLogger().info(tmpFolder.getAbsoluteFile() + " created");
	    }
	    
	    File kmlFile = new File(tmpFolder, new Object().hashCode() + "_.kml");
	    
	    OutputFormat format = OutputFormat.createCompactFormat();
	    format.setEncoding("UTF-8");
	    XMLWriter writer =  new XMLWriter(new FileOutputStream(kmlFile), format);
	    writer.write(document);
	    writer.close();
	    
	    component.getLogger().debug("generated KML in " + kmlFile.getAbsolutePath());
	    
	    
		return "tmp/" + kmlFile.getName();
	}

}
