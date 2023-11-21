package bpm.gateway.core.tools.database;

import java.io.File;
import java.io.IOException;

import org.apache.commons.digester.Digester;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class DBConverterLoader {
	
	private static DBConverterLoader instance = new DBConverterLoader();
	
	
	private  Digester dig;
	
	private static class MyErrorHandler implements ErrorHandler{

		public void error(SAXParseException arg0) throws SAXException {
			arg0.printStackTrace();
			throw new SAXException("erreur de parse", arg0);
		}

		public void fatalError(SAXParseException arg0) throws SAXException {
			arg0.printStackTrace();
			throw new SAXException("fatal error", arg0);
		}


		public void warning(SAXParseException arg0) throws SAXException {
			arg0.printStackTrace();
			throw new SAXException("warning", arg0);
		}
		
	}
	
	private DBConverterLoader(){
		
	}
	
	private void createCallbacks(String root){
		dig.setValidating(false);
		
		dig.addObjectCreate(root, DBConverter.class);
		dig.addCallMethod(root + "/name", "setJdbcDriverName", 0);
		
		
		dig.addObjectCreate(root + "/type", TypeInfo.class);
		dig.addCallMethod(root + "/type/name", "setName", 0);
		dig.addCallMethod(root + "/type/macthing", "setMatching", 0);
		dig.addCallMethod(root + "/type/precision", "setHasPrecision", 0);
		dig.addSetNext(root + "/type", "addType");
	}
	
	public static DBConverter loadFromFile(String fileName, String xmlRootPath) throws IOException, SAXException{
		instance.dig = new Digester();
		instance.dig.setErrorHandler(new MyErrorHandler());
		instance.createCallbacks(xmlRootPath);
		
		return (DBConverter)instance.dig.parse(new File(fileName));
	}
}
