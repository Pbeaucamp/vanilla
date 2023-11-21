package bpm.vanilla.workplace.server.helper;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXParseException;

import bpm.vanilla.workplace.core.model.PlaceImportItem;
import bpm.vanilla.workplace.shared.model.ListOfImportItem;

public class ExportDescriptorDigester {

	private ListOfImportItem model;
	private Digester dig = new Digester();
	
	private void createCallBacks(){
		String root = "exportDescriptor";
		
		dig.addObjectCreate(root, ListOfImportItem.class);

		//Items
		dig.addObjectCreate(root + "/item", PlaceImportItem.class);
			dig.addCallMethod(root + "/item/id", "setId", 0);
			dig.addCallMethod(root + "/item/directoryId", "setDirectoryId", 0);
			dig.addCallMethod(root + "/item/name", "setName", 0);
			dig.addCallMethod(root + "/item/type", "setType", 0);
			dig.addCallMethod(root + "/item/subtype", "setSubtype", 0);
			dig.addCallMethod(root + "/item/path", "setPath", 0);
			dig.addCallMethod(root + "/item/need", "addNeeded", 0);
//			dig.addObjectCreate(root + "/item", Integer.class);
//			dig.addSetNext(root + "/item", "addNeeded");
		dig.addSetNext(root + "/item", "addImportItem");
	}

	public ExportDescriptorDigester(InputStream is)throws Exception{
		dig.setValidating(false);
		dig.setErrorHandler(new ErrorHandler());
		createCallBacks();
//		model = new ExportDetails();
		Object o = dig.parse(is);
		model = (ListOfImportItem)o;
		
	}
	
	public ExportDescriptorDigester(String fileName)throws Exception{
		dig.setValidating(false);
		dig.setErrorHandler(new ErrorHandler());
		createCallBacks();
//		model = new ExportDetails();
		Object o = dig.parse(new File(fileName));
		model = (ListOfImportItem)o;
	}
	
	public ListOfImportItem getModel(){
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
