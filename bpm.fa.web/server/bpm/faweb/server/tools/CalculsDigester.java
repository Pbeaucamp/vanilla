package bpm.faweb.server.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester3.Digester;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import bpm.faweb.shared.infoscube.Calcul;

public class CalculsDigester {
	private List<Calcul> model;
	private Digester dig;
	
	
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
	
	@SuppressWarnings("unchecked")
	public CalculsDigester(InputStream input) throws IOException, SAXException{
		dig = new Digester();
		dig.setErrorHandler(new MyErrorHandler());
		createCallbacks(dig);
		model = (List<Calcul>) dig.parse(input);
	}
	
	@SuppressWarnings("unchecked")
	public CalculsDigester(File file) throws IOException, SAXException{
		dig = new Digester();
		dig.setErrorHandler(new MyErrorHandler());
		createCallbacks(dig);
		model = (List<Calcul>) dig.parse(file);	
	}
	
	@SuppressWarnings("unchecked")
	public CalculsDigester(String path) throws IOException, SAXException{
		dig = new Digester();
		dig.setErrorHandler(new MyErrorHandler());
		createCallbacks(dig);
		File file = new File(path);
		model = (List<Calcul>) dig.parse(file);
		
	}
	
	
	private void createCallbacks(Digester dig){
		dig.setValidating(false);

		
		String root = "view";
		
		dig.addObjectCreate(root, ArrayList.class);

		
		dig.addObjectCreate(root + "/calcul", Calcul.class);
			dig.addCallMethod(root + "/calcul/operator", "setOperator",0);
			dig.addCallMethod(root + "/calcul/orientation", "setOrientation",0);
			
			dig.addObjectCreate(root + "/calcul/fields", ArrayList.class);
				dig.addCallMethod(root + "/calcul/fields/field", "add",0);
			dig.addSetNext(root + "/calcul/fields", "setFields");
			
			dig.addCallMethod(root + "/calcul/constant", "setConstant",0);
			dig.addCallMethod(root + "/calcul/title", "setTitle",0);
		
		dig.addSetNext(root + "/calcul", "add");
		
	}
	
	
	public List<Calcul> getCalculs() {
		return model;
	}
	
}
