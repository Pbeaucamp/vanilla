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

public class GtwFmdtReplacer implements IFMDTReplacer{
	private static final String ODA_BUSINESS_MODEL = "BUSINESS_MODEL"; //$NON-NLS-1$
	private static final String ODA_BUSINESS_PACKAGE = "BUSINESS_PACKAGE"; //$NON-NLS-1$
	private static final String ODA_CONNECTION = "CONNECTION_NAME"; //$NON-NLS-1$
	private static final String ODA_DIRECTORY_ITEM_ID = "DIRECTORY_ITEM_ID"; //$NON-NLS-1$
	private static final String ODA_LOGIN = "USER"; //$NON-NLS-1$
	private static final String ODA_PASSWORD = "PASSWORD"; //$NON-NLS-1$
	private static final String ODA_REPOSITORY = "URL"; //$NON-NLS-1$
	private static final String ODA_GROUP = "GROUP_NAME"; //$NON-NLS-1$
	
	private static final String DIRECTORY_ITEM_ID = "repositoryItemId"; //$NON-NLS-1$
	private static final String CONNECTION = "connectionName"; //$NON-NLS-1$
	private static final String BUSINESS_MODEL = "businessModelName"; //$NON-NLS-1$
	private static final String BUSINESS_PACKAGE = "businessPackageName"; //$NON-NLS-1$
	private static final String REPOSITORY_SERVER_REFERENCE = "serverRef"; //$NON-NLS-1$


	@Override
	public String replace(String modelXml, FMDTDataSource orig,	FMDTDataSource toreplace) throws Exception {
		Document doc = DocumentHelper.parseText(modelXml);
		
		
		Element root = doc.getRootElement();
		boolean updated = false;
		
		/*
		 * ODA
		 */
		for(Element e : (List<Element>)root.selectNodes("//odaInputWithParameters|//odaInput")){ //$NON-NLS-1$
			boolean match = true;
			
			Element urlE = null;
			Element userE = null;
			Element passwordE = null;
			Element groupE = null;
			
			Element dirItE = null;
			Element modelE = null;
			Element packE = null;
			Element connE = null;
			for(Element _id : (List<Element>)e.selectNodes("publicDataSource/property|privateDataSource/property")){ //$NON-NLS-1$
				if (_id.element("name").getStringValue().equals(ODA_BUSINESS_MODEL)){ //$NON-NLS-1$
					if (!_id.element("value").getStringValue().equals(orig.getBusinessModel())){ //$NON-NLS-1$
						match = false;
					}
					else{
						modelE = _id.element("value"); //$NON-NLS-1$
					}
				}
				else if (_id.element("name").getStringValue().equals(ODA_BUSINESS_PACKAGE)){ //$NON-NLS-1$
					if (!_id.element("value").getStringValue().equals(orig.getBusinessPackage())){ //$NON-NLS-1$
						match = false;
					}
					else{
						packE = _id.element("value"); //$NON-NLS-1$
					}
				}
				else if (_id.element("name").getStringValue().equals(ODA_CONNECTION)){ //$NON-NLS-1$
					if (!_id.element("value").getStringValue().equals(orig.getConnectionName())){ //$NON-NLS-1$
						match = false;
					}
					else{
						connE = _id.element("value"); //$NON-NLS-1$
					}
				}
				else if (_id.element("name").getStringValue().equals(ODA_DIRECTORY_ITEM_ID)){ //$NON-NLS-1$
					if (!_id.element("value").getStringValue().equals(toreplace.getDirItemId() + "")){ //$NON-NLS-1$ //$NON-NLS-2$
						match = false;
					}
					else{
						dirItE = _id.element("value"); //$NON-NLS-1$
					}
				}
				else if (_id.element("name").getStringValue().equals(ODA_REPOSITORY)){ //$NON-NLS-1$
					if (!_id.element("value").getStringValue().equals(orig.getUrl())){ //$NON-NLS-1$
						match = false;
					}
					else{
						urlE = _id;
					}
				}
				else if (_id.element("name").getStringValue().equals(ODA_LOGIN)){ //$NON-NLS-1$
					if (!_id.element("value").getStringValue().equals(orig.getUser())){ //$NON-NLS-1$
						match = false;
					}
					else{
						userE = _id;
					}
				}
				else if (_id.element("name").getStringValue().equals(ODA_PASSWORD)){ //$NON-NLS-1$
					if (!_id.element("value").getStringValue().equals(orig.getPass())){ //$NON-NLS-1$
						match = false;
					}
					else{
						passwordE = _id;
					}
				}
				else if (_id.element("name").getStringValue().equals(ODA_GROUP)){ //$NON-NLS-1$
					groupE = _id;
				}
				if (!match){
					break;
				}
			}
				
			if (match){
				passwordE.element("value").setText(toreplace.getPass()); //$NON-NLS-1$
				urlE.element("value").setText(toreplace.getUrl()); //$NON-NLS-1$
				userE.element("value").setText(toreplace.getUser()); //$NON-NLS-1$
				
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
		
		
		/*
		 * FMDTInput
		 */
		for(Element e : (List<Element>)root.selectNodes("//fmdtInput")){ //$NON-NLS-1$
			boolean match = true;
			
			
			Element repServerRefE = null;
			
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
				else if (_id.getName().equals(REPOSITORY_SERVER_REFERENCE)){
//					if (!_id.getStringValue().equals(toreplace.getDirItemId() + "")){
//						match = false;
//					}
//					else{
						repServerRefE = _id;
//					}
				}
				if (!match){
					break;
				}
			}
				
			if (match){
				/*
				 * change RepositoryServer
				 */
				for(Element el : (List<Element>)repServerRefE.getParent().getParent().element("servers").elements("repositoryServer")){ //$NON-NLS-1$ //$NON-NLS-2$
					if (el.element("name").getStringValue().equals(repServerRefE.getStringValue())){ //$NON-NLS-1$
						el.element("repositoryConnection").element("url").setText(toreplace.getUrl()); //$NON-NLS-1$ //$NON-NLS-2$
						el.element("repositoryConnection").element("login").setText(toreplace.getUser()); //$NON-NLS-1$ //$NON-NLS-2$
						el.element("repositoryConnection").element("password").setText(toreplace.getPass()); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				
				/*
				 * update FMDTInput infos
				 */
				modelE.setText(toreplace.getBusinessModel());
				packE.setText(toreplace.getBusinessPackage());
				connE.setText(toreplace.getConnectionName());
				
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
