package bpm.es.pack.manager.digester;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXParseException;

import bpm.vanilla.workplace.core.model.ImportHistoric;



public class ImportHistoricDigester {

	private List model;
	private Digester dig = new Digester();
	
	private void createCallBacks(){
		String root = "import"; //$NON-NLS-1$
		
		dig.addObjectCreate(root, ArrayList.class);
		dig.addObjectCreate(root + "/importHistoric", ImportHistoric.class); //$NON-NLS-1$
		dig.addCallMethod(root + "/importHistoric/date", "setDate", 0); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addCallMethod(root + "/importHistoric/fileName", "setFileName", 0); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addCallMethod(root + "/importHistoric/packageName", "setPackageName", 0); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addCallMethod(root + "/importHistoric/logs", "setLogs", 0); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addSetNext(root + "/importHistoric", "add"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public ImportHistoricDigester(InputStream is)throws Exception{
		dig.setValidating(false);
		dig.setErrorHandler(new ErrorHandler());
		createCallBacks();
//		model = new ExportDetails();
		Object o = dig.parse(is);
		model = (List)o;
		
	}
	
	public ImportHistoricDigester(String fileName)throws Exception{
		dig.setValidating(false);
		dig.setErrorHandler(new ErrorHandler());
		createCallBacks();
//		model = new ExportDetails();
		Object o = dig.parse(new File(fileName));
		model = (List)o;
		
	}
	
	public List getModel(){
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
