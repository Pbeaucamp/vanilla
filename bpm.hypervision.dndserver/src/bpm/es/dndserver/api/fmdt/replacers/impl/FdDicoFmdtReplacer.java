package bpm.es.dndserver.api.fmdt.replacers.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

import bpm.es.dndserver.Messages;
import bpm.es.dndserver.api.fmdt.FMDTDataSource;
import bpm.es.dndserver.api.fmdt.replacers.IFMDTReplacer;

public class FdDicoFmdtReplacer implements IFMDTReplacer{
	private static final String BUSINESS_MODEL = "BUSINESS_MODEL"; //$NON-NLS-1$
	private static final String BUSINESS_PACKAGE = "BUSINESS_PACKAGE"; //$NON-NLS-1$
	private static final String CONNECTION = "CONNECTION_NAME"; //$NON-NLS-1$
	private static final String DIRECTORY_ITEM_ID = "DIRECTORY_ITEM_ID"; //$NON-NLS-1$
	private static final String LOGIN = "USER"; //$NON-NLS-1$
	private static final String PASSWORD = "PASSWORD"; //$NON-NLS-1$
	private static final String REPOSITORY = "URL"; //$NON-NLS-1$
	private static final String GROUP = "GROUP_NAME"; //$NON-NLS-1$

	@Override
	public String replace(String modelXml, FMDTDataSource orig,	FMDTDataSource toreplace) throws Exception {
		Document doc = DocumentHelper.parseText(modelXml);
		
		
		Element root = doc.getRootElement();
		XPath xpath = new Dom4jXPath( "//dependancies/dependantDirectoryItemId" ); //$NON-NLS-1$

		
		boolean updated = false;
		/*
		 * oda
		 */
		for(Element e : (List<Element>)root.selectNodes("//dataSource/odaExtensionDataSourceId")){ //$NON-NLS-1$

			if (e.getStringValue().equals("bpm.metadata.birt.oda.runtime") ){ //$NON-NLS-1$
				
				boolean match = true;
				
				
				Element urlE = null;
				Element userE = null;
				Element passwordE = null;
				Element groupE = null;
				
				Element modelE = null;
				Element packE = null;
				Element connE = null;
				Element dirItE = null;
				for(Element _id : (List<Element>)e.getParent().selectNodes("publicProperty|privateProperty")){ //$NON-NLS-1$
					if (_id.attributeValue("name").equals(BUSINESS_MODEL)){ //$NON-NLS-1$
						if (!_id.getStringValue().equals(orig.getBusinessModel())){
							match = false;
						}
						else{
							modelE = _id;
						}
					}
					else if (_id.attributeValue("name").equals(BUSINESS_PACKAGE)){ //$NON-NLS-1$
						if (!_id.getStringValue().equals(orig.getBusinessPackage())){
							match = false;
						}
						else{
							packE = _id;
						}
					}
					else if (_id.attributeValue("name").equals(DIRECTORY_ITEM_ID)){ //$NON-NLS-1$
						if (!_id.getStringValue().equals(toreplace.getDirItemId() + "")){ //$NON-NLS-1$
							match = false;
						}
						else{
							dirItE = _id;
						}
					}
					else if (_id.attributeValue("name").equals(CONNECTION)){ //$NON-NLS-1$
						if (!_id.getStringValue().equals(orig.getConnectionName())){
							match = false;
						}
						else{
							connE = _id;
						}
					}
					else if (_id.attributeValue("name").equals(REPOSITORY)){ //$NON-NLS-1$
						if (!_id.getStringValue().equals(orig.getUrl())){
							match = false;
						}
						else{
							urlE = _id;
						}
					}
					else if (_id.attributeValue("name").equals(LOGIN)){ //$NON-NLS-1$
						if (!_id.getStringValue().equals(orig.getUser())){
							match = false;
						}
						else{
							userE = _id;
						}
					}
					else if (_id.attributeValue("name").equals(PASSWORD)){ //$NON-NLS-1$
						if (!_id.getStringValue().equals(orig.getPass())){
							match = false;
						}
						else{
							passwordE = _id;
						}
					}
					else if (_id.attributeValue("name").equals(GROUP)){ //$NON-NLS-1$
						groupE = _id;
					}
					if (!match){
						break;
					}
				}
				
				if (match){
					passwordE.setText(toreplace.getPass());
					urlE.setText(toreplace.getUrl());
					userE.setText(toreplace.getUser());
					
					modelE.setText(toreplace.getBusinessModel());
					packE.setText(toreplace.getBusinessPackage());
					dirItE.setText(toreplace.getDirItemId() + ""); //$NON-NLS-1$
					if (connE != null){
						connE.setText(toreplace.getConnectionName());
					}
					else{
						e.addElement("privateProperty").addAttribute("name", CONNECTION).setText(toreplace.getConnectionName()); //$NON-NLS-1$ //$NON-NLS-2$
					}
					
					if (groupE != null && toreplace.getGroupName() != null){
						groupE.setText(toreplace.getGroupName());
					}
					updated = true;
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
