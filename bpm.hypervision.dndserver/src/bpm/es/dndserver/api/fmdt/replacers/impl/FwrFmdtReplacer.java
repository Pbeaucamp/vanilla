package bpm.es.dndserver.api.fmdt.replacers.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import bpm.es.dndserver.Messages;
import bpm.es.dndserver.api.fmdt.FMDTDataSource;
import bpm.es.dndserver.api.fmdt.replacers.IFMDTReplacer;

public class FwrFmdtReplacer implements IFMDTReplacer{
	private static final String BUSINESS_MODEL = "model"; //$NON-NLS-1$
	private static final String BUSINESS_PACKAGE = "package"; //$NON-NLS-1$
	private static final String CONNECTION = "connection"; //$NON-NLS-1$
	private static final String DIRECTORY_ITEM_ID = "itemid"; //$NON-NLS-1$
	private static final String LOGIN = "user"; //$NON-NLS-1$
	private static final String PASSWORD = "password"; //$NON-NLS-1$
	private static final String REPOSITORY = "url"; //$NON-NLS-1$
	private static final String GROUP = "group"; //$NON-NLS-1$

	@Override
	public String replace(String modelXml, FMDTDataSource orig,	FMDTDataSource toreplace) throws Exception {
		Document doc = DocumentHelper.parseText(modelXml);
		
		
		Element root = doc.getRootElement();
		boolean updated = false;
		/*
		 * oda
		 */
		for(Element e : (List<Element>)root.selectNodes("//datasource")){ //$NON-NLS-1$

			
				
				boolean match = true;
				Element urlE = null;
				Element userE = null;
				Element passwordE = null;
				Element groupE = null;
				Element dirItE = null;
				Element modelE = null;
				Element packE = null;
				Element connE = null;
				for(Element _id : (List<Element>)e.elements()){
					if (_id.getName().equals(BUSINESS_MODEL)){
						if (!_id.getStringValue().equals(orig.getBusinessModel())){
							match = false;
						}
						else{
							modelE = _id;
						}
					}
					else if (_id.getName().equals(BUSINESS_PACKAGE)){
						if (!_id.getStringValue().equals(orig.getBusinessPackage())){
							match = false;
						}
						else{
							packE = _id;
						}
					}
					else if (_id.getName().equals(CONNECTION)){
						if (!_id.getStringValue().equals(orig.getConnectionName())){
							match = false;
						}
						else{
							connE = _id;
						}
					}
					else if (_id.getName().equals(DIRECTORY_ITEM_ID)){
						if (!_id.getStringValue().equals(toreplace.getDirItemId() + "")){ //$NON-NLS-1$
							match = false;
						}
						else{
							dirItE = _id;
						}
					}
					else if (_id.getName().equals(REPOSITORY)){
						if (!_id.getStringValue().equals(orig.getUrl())){
							match = false;
						}
						else{
							urlE = _id;
						}
					}
					else if (_id.getName().equals(LOGIN)){
						if (!_id.getStringValue().equals(orig.getUser())){
							match = false;
						}
						else{
							userE = _id;
						}
					}
					else if (_id.getName().equals(PASSWORD)){
						if (!_id.getStringValue().equals(orig.getPass())){
							match = false;
						}
						else{
							passwordE = _id;
						}
					}
					else if (_id.getName().equals(GROUP)){
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
					connE.setText(toreplace.getConnectionName());
					
					if (groupE != null && toreplace.getGroupName() != null){
						groupE.setText(toreplace.getGroupName());
					}
					
					updated = true;
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
