package bpm.es.pack.manager.digester;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXParseException;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Role;
import bpm.vanilla.platform.core.repository.RepositoryLog;
import bpm.vanilla.workplace.core.model.ExportDetails;
import bpm.vanilla.workplace.core.model.ImportItem;
import bpm.vanilla.workplace.core.model.ReportBean;

public class ExportDescriptorDigester {

	private ExportDetails model;
	private Digester dig = new Digester();
	
	private void createCallBacks(){
		String root = "exportDescriptor"; //$NON-NLS-1$
		
		dig.addObjectCreate(root,ExportDetails.class);
		dig.addCallMethod(root + "/exporter", "setExporterId", 0); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addCallMethod(root + "/date", "setDate", 0); //$NON-NLS-1$ //$NON-NLS-2$
		
		dig.addCallMethod(root + "/options/name", "setName", 0); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addCallMethod(root + "/options/desc", "setDesc", 0); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addCallMethod(root + "/options/location", "setLocation", 0); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addCallMethod(root + "/options/allContent", "setAllContent", 0); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addCallMethod(root + "/options/includeReports", "setIncludeReports", 0); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addCallMethod(root + "/options/includeLogs", "setIncludeLogs", 0); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addCallMethod(root + "/options/includeCalendar", "setIncludeCalendar", 0); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addCallMethod(root + "/options/includeGroups", "setIncludeGroups", 0); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addCallMethod(root + "/options/includeRoles", "setIncludeRoles", 0); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addCallMethod(root + "/options/includeGrants", "setIncludeGrants", 0); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addCallMethod(root + "/options/includeDataSources", "setIncludeDataSources", 0); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addCallMethod(root + "/options/replaceOld", "setReplaceOld", 0); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addCallMethod(root + "/options/ownerExporter", "setOwnerExporter", 0); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addCallMethod(root + "/options/applyOwnerToOld", "setApplyOwnerToOld", 0); //$NON-NLS-1$ //$NON-NLS-2$
		
		
		

//		
		//Items
		dig.addObjectCreate(root + "/item", ImportItem.class); //$NON-NLS-1$
			dig.addCallMethod(root + "/item/id", "setId", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/item/directoryId", "setDirectoryId", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/item/name", "setName", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/item/type", "setType", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/item/subtype", "setSubtype", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/item/path", "setPath", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/item/need", "addNeeded", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/item/auto-replacement", "addAutoReplacement", 2); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallParam(root + "/item/auto-replacement/old", 0); //$NON-NLS-1$
			dig.addCallParam(root + "/item/auto-replacement/new", 1); //$NON-NLS-1$
		dig.addSetNext(root + "/item", "addImportItem"); //$NON-NLS-1$ //$NON-NLS-2$
//			
//
		//Groups
		dig.addObjectCreate(root + "/group", Group.class); //$NON-NLS-1$
			dig.addCallMethod(root + "/group/id", "setId", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/group/name", "setName", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/group/comment", "setComment", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/group/custom1", "setCustom1", 0); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addSetNext(root + "/group", "addGroup"); //$NON-NLS-1$ //$NON-NLS-2$
		
		//group-items
		dig.addCallMethod(root + "/group-item", "addGroupItem", 2); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addCallParam(root + "/group-item/group", 0); //$NON-NLS-1$
		dig.addCallParam(root + "/group-item/item", 1); //$NON-NLS-1$
//		
		//Role
		dig.addObjectCreate(root + "/role", Role.class); //$NON-NLS-1$
			dig.addCallMethod(root + "/role/id", "setId", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/role/name", "setName", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/role/comment", "setComment", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/role/image", "setImage", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/role/type", "setType", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/role/grant", "setGrants", 0); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addSetNext(root + "/role", "addRole"); //$NON-NLS-1$ //$NON-NLS-2$
//		
//		
		//group-role
		dig.addCallMethod(root + "/group-role", "addGroupRole", 2); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addCallParam(root + "/group-role/group", 0); //$NON-NLS-1$
		dig.addCallParam(root + "/group-role/role", 1); //$NON-NLS-1$
//		
//		
		//reports-mapping
		dig.addObjectCreate(root + "/reports", ReportBean.class); //$NON-NLS-1$
			dig.addCallMethod(root + "/reports/dirItId", "setDirectoryItemId" , 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/reports/itemName", "setItemName", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/reports/files/file", "addFile", 0); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addSetNext(root + "/reports", "addReportBean"); //$NON-NLS-1$ //$NON-NLS-2$
//		
		//logs
		dig.addObjectCreate(root + "/log", RepositoryLog.class); //$NON-NLS-1$
			dig.addCallMethod(root + "/log/id", "setId", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/log/appName", "setAppName", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/log/operation", "setOperation", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/log/objectId", "setObjectId", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/log/userId", "setUserId", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/log/clientIp", "setClientIp", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/log/groupId", "setGroupId", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/log/time", "setTime", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/log/delay", "setDelay", 0); //$NON-NLS-1$ //$NON-NLS-2$
			dig.addCallMethod(root + "/log/groupName", "setGroupName", 0); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addSetNext(root + "/log", "addLog"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public ExportDescriptorDigester(InputStream is)throws Exception{
		dig.setValidating(false);
		dig.setErrorHandler(new ErrorHandler());
		createCallBacks();
//		model = new ExportDetails();
		Object o = dig.parse(is);
		model = (ExportDetails)o;
		
	}
	
	public ExportDescriptorDigester(String fileName)throws Exception{
		dig.setValidating(false);
		dig.setErrorHandler(new ErrorHandler());
		createCallBacks();
//		model = new ExportDetails();
		Object o = dig.parse(new File(fileName));
		model = (ExportDetails)o;
		
	}
	
	public ExportDetails getModel(){
		return model;
	}
	
	public class ErrorHandler implements org.xml.sax.ErrorHandler {
		public void warning(SAXParseException ex) throws SAXParseException {
			throw ex;
		}

		public void error(SAXParseException ex) throws SAXParseException {
			throw ex;
		}

		public void fatalError(SAXParseException ex) throws SAXParseException {
			throw ex;
		}
	}
}
