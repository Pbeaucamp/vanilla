package bpm.faweb.server.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.digester3.Digester;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import bpm.faweb.shared.infoscube.Calcul;

public class CustomSizesDigester {
	
	private HashMap model;
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
	public CustomSizesDigester(InputStream input) throws IOException, SAXException{
		dig = new Digester();
		dig.setErrorHandler(new MyErrorHandler());
		createCallbacks(dig);
		model = (HashMap) dig.parse(input);
	}
	
	@SuppressWarnings("unchecked")
	public CustomSizesDigester(File file) throws IOException, SAXException{
		dig = new Digester();
		dig.setErrorHandler(new MyErrorHandler());
		createCallbacks(dig);
		model = (HashMap) dig.parse(file);	
	}
	
	@SuppressWarnings("unchecked")
	public CustomSizesDigester(String path) throws IOException, SAXException{
		dig = new Digester();
		dig.setErrorHandler(new MyErrorHandler());
		createCallbacks(dig);
		File file = new File(path);
		model = (HashMap) dig.parse(file);
		
	}
	
	private void createCallbacks(Digester dig){
		dig.setValidating(false);

		
		String root = "view/sizes";
		
		dig.addObjectCreate(root, HashMap.class);

		
		dig.addCallMethod(root + "/size", "put", 2);
			dig.addCallParam(root + "/size/sizeCol", 0);
			dig.addCallParam(root + "/size/sizeWidth", 1);
		
	}
	
	public HashMap<Integer, Integer> getSizes() {
		
		try {
			HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();
			for(Object i : model.keySet()) {
				result.put(Integer.parseInt((String) i), Integer.parseInt((String) model.get(i)));
			}
			
			return result;
		} catch(Exception e) {
			return new HashMap<Integer, Integer>();
		}
	}
}
