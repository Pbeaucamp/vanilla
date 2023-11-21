package bpm.workflow.runtime.resources.variables;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.digester3.Digester;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Digester for the variables
 * @author CHARBONNIER, MARTIN
 *
 */
public class ListVariableDigester {
	
	private Digester dig = new Digester();
	private StringBuffer error=   new StringBuffer();
	
	
	private ListVariableDigester(){
		dig.setValidating(false);
		dig.setErrorHandler(new MyErrorHandler());
		createCallbacks();
	}
	
	
	private ListVariableDigester(ClassLoader classLoader){
		if (classLoader != null){
			dig.setClassLoader(classLoader);
		}
		
		dig.setValidating(false);
		dig.setErrorHandler(new MyErrorHandler());
		createCallbacks();
	}
	
	private void createCallbacks(){
		String root = "listVariable";
		
		dig.addObjectCreate(root, ListVariable.class);
			dig.addCallMethod(root + "/name", "setName", 0);
			dig.addCallMethod(root + "/id", "setId", 0);
			
			
			
			dig.addObjectCreate(root + "/variable", Variable.class);
				dig.addCallMethod(root + "/variable/id", "setId", 0);
				dig.addCallMethod(root + "/variable/name", "setName", 0);
				dig.addCallMethod(root + "/variable/value", "setValue", 0);
				dig.addCallMethod(root + "/variable/type", "setType", 0);
			dig.addSetNext(root + "/variable", "addVariable");
			
					
	}
	
	
	public static ListVariable getListVariable(ClassLoader classLoader, String filePath) throws IOException, SAXException{
		ListVariableDigester dig = new ListVariableDigester(classLoader);
		ListVariable m = (ListVariable)dig.dig.parse(new File(filePath));

		return m;
	}
	
	public static ListVariable getModel(ClassLoader classLoader, InputStream inputStream) throws IOException, SAXException{
		ListVariableDigester dig = new ListVariableDigester(classLoader);
		
		ListVariable m = (ListVariable)dig.dig.parse(inputStream);

		return m;
	}
	
	

	
	private class MyErrorHandler implements ErrorHandler{

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

}
