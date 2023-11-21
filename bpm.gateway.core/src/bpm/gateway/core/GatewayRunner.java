package bpm.gateway.core;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

import bpm.gateway.runtime2.RuntimeEngine;

public class GatewayRunner {

	public static void main(String[] args) throws Exception {
		System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
		
		String filePath = null;
		String propertyFile = null;
		for(String file : args) {
			if(file.endsWith(".gateway")) {
				filePath = file;
			}
			else if(file.endsWith(".properties")) {
				propertyFile = file;
			}
		}
		
		Properties props = new Properties();
		props.load(new FileInputStream(propertyFile));
		
		GatewayDigester dig = new GatewayDigester(new File(filePath), new ArrayList<AbrstractDigesterTransformation>());
		DocumentGateway big = dig.getDocument(null);
		
		for(Object p : props.keySet()) {
			String prop = p.toString();
			if(prop.startsWith("parameter.")) {
				String paramName = prop.substring("parameter.".length(), prop.length());
				big.getParameter(paramName).setValue(props.getProperty(prop));
			}
		}
		
		RuntimeEngine engine = new RuntimeEngine();
		engine.init(null, big, null, System.out);
		engine.run(1);
	}

}
