package bpm.dataprovider.odainput;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.digester3.Digester;
import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import bpm.vanilla.platform.core.beans.data.OdaInput;


public class OdaInputDigester {
	private OdaInput o;
	
//	public OdaInputDigester(InputStream is) {
//		Digester dig = new Digester();
//		dig.setValidating(false);
//		dig.setErrorHandler(new ErrorHandler());
//		
//		String root = "odaInput";
//		
//		dig.addObjectCreate(root, OdaInput.class);
//		dig.addCallMethod(root + "/name", "setName", 0);
//		dig.addCallMethod(root + "/description", "setDescription", 0);
//
//		
//		dig.addCallMethod(root + "/odaExtensionId", "setOdaExtensionId", 0);
//		dig.addCallMethod(root + "/odaExtensionDataSourceId", "setOdaExtensionDataSourceId", 0);
//		
//		
//		dig.addCallMethod(root + "/privateDataSource/property", "setDatasourcePrivateProperty", 2);
//		dig.addCallParam(root + "/privateDataSource/property/name", 0);
//		dig.addCallParam(root + "/privateDataSource/property/value", 1);
//
//		
//		dig.addCallMethod(root + "/publicDataSource/property", "setDatasourcePublicProperty", 2);
//		dig.addCallParam(root + "/publicDataSource/property/name", 0);
//		dig.addCallParam(root + "/publicDataSource/property/value", 1);
//		
//
//		
//		dig.addCallMethod(root + "/privateDataSet/property", "setDatasetPrivateProperty", 2);
//		dig.addCallParam(root + "/privateDataSet/property/name", 0);
//		dig.addCallParam(root + "/privateDataSet/property/value", 1);
//		
//		
//		dig.addCallMethod(root + "/publicDataSet/property", "setDatasetPublicProperty", 2);
//		dig.addCallParam(root + "/publicDataSet/property/name", 0);
//		dig.addCallParam(root + "/publicDataSet/property/value", 1);	
//		
//		
//		dig.addCallMethod(root + "/queryText", "setQueryText", 0);
//		
//		dig.addCallMethod(root + "/parameter", "setParameter", 2);
//		dig.addCallParam(root + "/parameter/name", 0);
//		dig.addCallParam(root + "/parameter/value", 1);
//		
//		try {
//			o = (OdaInput) dig.parse(is);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		
//	}
	
	public OdaInputDigester(String odainput) throws IOException, SAXException {

		Digester dig = new Digester();
		dig.setValidating(false);
		dig.setErrorHandler(new ErrorHandler());
		dig.setClassLoader(OdaInputDigester.class.getClassLoader());
		
		createCallBack(dig);

		o = (OdaInput) dig.parse(IOUtils.toInputStream(odainput, "UTF-8"));
	
	}
	
	public OdaInputDigester(InputStream stream) throws IOException, SAXException {

		Digester dig = new Digester();
		dig.setValidating(false);
		dig.setErrorHandler(new ErrorHandler());
		dig.setClassLoader(OdaInputDigester.class.getClassLoader());
		
		createCallBack(dig);

		o = (OdaInput) dig.parse(stream);
	
	}
	
	
	private void createCallBack(Digester dig){
		String root = "odaInput";
		
		dig.addObjectCreate(root, OdaInput.class);
		dig.addCallMethod(root + "/name", "setName", 0);
		dig.addCallMethod(root + "/description", "setDescription", 0);

		
		dig.addCallMethod(root + "/odaExtensionId", "setOdaExtensionId", 0);
		dig.addCallMethod(root + "/odaExtensionDataSourceId", "setOdaExtensionDataSourceId", 0);
		
		
		dig.addCallMethod(root + "/privateDataSource/property", "setDatasourcePrivateProperty", 2);
		dig.addCallParam(root + "/privateDataSource/property/name", 0);
		dig.addCallParam(root + "/privateDataSource/property/value", 1);

		
		dig.addCallMethod(root + "/publicDataSource/property", "setDatasourcePublicProperty", 2);
		dig.addCallParam(root + "/publicDataSource/property/name", 0);
		dig.addCallParam(root + "/publicDataSource/property/value", 1);
		

		
		dig.addCallMethod(root + "/privateDataSet/property", "setDatasetPrivateProperty", 2);
		dig.addCallParam(root + "/privateDataSet/property/name", 0);
		dig.addCallParam(root + "/privateDataSet/property/value", 1);
		
		
		dig.addCallMethod(root + "/publicDataSet/property", "setDatasetPublicProperty", 2);
		dig.addCallParam(root + "/publicDataSet/property/name", 0);
		dig.addCallParam(root + "/publicDataSet/property/value", 1);	
		
		
		dig.addCallMethod(root + "/queryText", "setQueryText", 0);
		
		dig.addCallMethod(root + "/parameter", "setParameter", 2);
		dig.addCallParam(root + "/parameter/name", 0);
		dig.addCallParam(root + "/parameter/value", 1);	
	}

	public OdaInput getOdaInput() {
		return o;
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
