package bpm.es.dndserver.api.fmdt.replacers.impl;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

import bpm.es.dndserver.Messages;
import bpm.es.dndserver.api.fmdt.FMDTDataSource;
import bpm.es.dndserver.api.fmdt.replacers.IFMDTReplacer;

public class BirtFmdtReplacer implements IFMDTReplacer{

	@Override
	public String replace(String modelXml, FMDTDataSource orig,	FMDTDataSource toreplace) throws Exception {
		
		boolean updated = false;
		
		
		HashMap nameSpaceMap = new HashMap();
		Document doc = DocumentHelper.parseText(modelXml);
		Element root = doc.getRootElement();
		if (root.getNamespaceURI() != null){
			nameSpaceMap.put("birt", root.getNamespaceURI()); //$NON-NLS-1$
		}
		
		XPath xpath = new Dom4jXPath( "//birt:oda-data-source[@extensionID='bpm.metadata.birt.oda.runtime']" ); //$NON-NLS-1$
		
		SimpleNamespaceContext nmsCtx = new SimpleNamespaceContext( nameSpaceMap);
		xpath.setNamespaceContext(nmsCtx);

		
		
		
		for(Element e : (List<Element>)xpath.selectNodes(doc)){
			
			Element urlE = null;
			Element loginE = null;
			Element passwordE = null;
			
			Element modelE = null;
			Element packE = null;
			Element connE = null;
			Element dirItE = null;
			
			Element groupE = null;
			Element repositoryE = null;
			
			xpath = new Dom4jXPath( "//birt:property[@name='VANILLA_URL']|//birt:property[@name='PASSWORD']|//birt:property[@name='USER']|//birt:property[@name='DIRECTORY_ITEM_ID']|//birt:property[@name='BUSINESS_MODEL']|//birt:property[@name='BUSINESS_PACKAGE']" ); //$NON-NLS-1$
			xpath.setNamespaceContext(nmsCtx);

			for(Element _id : (List<Element>)xpath.selectNodes(e)){
				if (_id.attributeValue("name").equals("BUSINESS_MODEL")){ //$NON-NLS-1$ //$NON-NLS-2$
					modelE = _id;
				}
				else if (_id.attributeValue("name").equals("BUSINESS_PACKAGE")){ //$NON-NLS-1$ //$NON-NLS-2$
					packE = _id;
				}
				else if (_id.attributeValue("name").equals("DIRECTORY_ITEM_ID")){ //$NON-NLS-1$ //$NON-NLS-2$
					dirItE = _id;
				}
				else if (_id.attributeValue("name").equals("VANILLA_URL")){ //$NON-NLS-1$ //$NON-NLS-2$
					urlE = _id;
				}
				else if (_id.attributeValue("name").equals("PASSWORD")){ //$NON-NLS-1$ //$NON-NLS-2$
					passwordE = _id;
				}
				else if (_id.attributeValue("name").equals("USER")){ //$NON-NLS-1$ //$NON-NLS-2$
					loginE = _id;
				}
				else if (_id.attributeValue("name").equals("GROUP_NAME")){ //$NON-NLS-1$ //$NON-NLS-2$
					groupE = _id;
				}
				else if (_id.attributeValue("name").equals("REPOSITORY_ID")){ //$NON-NLS-1$ //$NON-NLS-2$
					repositoryE = _id;
				}
			}
			
//			for(Element el : (List<Element>)xpath.selectNodes(doc)){
				xpath = new Dom4jXPath( "//birt:list-property[@name='privateDriverProperties']" ); //$NON-NLS-1$
				xpath.setNamespaceContext(nmsCtx);

				for(Element _id : (List<Element>)xpath.selectNodes(e)){
					xpath = new Dom4jXPath( "//birt:ex-property" ); //$NON-NLS-1$
					xpath.setNamespaceContext(nmsCtx);
					
					for(Element x : (List<Element>)xpath.selectNodes(_id)){
						try{
							if ("CONNECTION_NAME".equals(x.element("name").getStringValue())){ //$NON-NLS-1$ //$NON-NLS-2$
								connE = x.element("value"); //$NON-NLS-1$
							}
						}catch(Exception ex){
							
						}
						
					}
				}
				
				
				
				
				if (dirItE != null && modelE != null && packE != null){
					if (dirItE.getStringValue().equals(toreplace.getDirItemId() + "")){ //$NON-NLS-1$
						if (modelE.getStringValue().equals(orig.getBusinessModel()) && packE.getStringValue().equals(orig.getBusinessPackage()) &&
							urlE.getStringValue().equals(orig.getUrl()) && loginE.getStringValue().equals(orig.getUser()) &&passwordE.getStringValue().equals(orig.getPass())){
							updated = true;
							
							urlE.setText(toreplace.getUrl());
							loginE.setText(toreplace.getUser());
							passwordE.setText(toreplace.getPass());
							
							modelE.setText(toreplace.getBusinessModel());
							packE.setText(toreplace.getBusinessPackage());
							
							if(connE != null && connE.getStringValue().equals(orig.getConnectionName())){
								connE.setText(toreplace.getConnectionName());
							}
							
							if (groupE != null&& toreplace.getGroupName() != null){
								groupE.setText(toreplace.getGroupName());
							}
							
						}
					}
					
				}
		}
		
		
		
		if (!updated){
			return modelXml;
		}
		else{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			OutputFormat f = OutputFormat.createPrettyPrint();
			f.setEncoding("UTF-8"); //$NON-NLS-1$
			f.setNewlines(false);
			f.setTrimText(false);
			XMLWriter w = new XMLWriter(bos, f);
			w.write(doc.getRootElement());
			w.close();
			return bos.toString("UTF-8"); //$NON-NLS-1$
		}
	}

}
