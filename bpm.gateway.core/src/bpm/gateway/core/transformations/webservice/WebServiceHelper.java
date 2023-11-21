package bpm.gateway.core.transformations.webservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.jaxen.dom4j.Dom4jXPath;

import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.vanilla.platform.core.beans.Variable;
import bpm.vanilla.platform.core.beans.service.IService;

import com.predic8.schema.ComplexType;
import com.predic8.schema.Element;
import com.predic8.schema.Sequence;
import com.predic8.soamodel.Consts;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Operation;
import com.predic8.wsdl.PortType;
import com.predic8.wsdl.WSDLParser;

public class WebServiceHelper {

	public static HashMap<String, List<IService>> getMethodNames(String webServiceUrl) {
		WSDLParser parser = new WSDLParser();
		Definitions defs = parser.parse(webServiceUrl + "?wsdl");

		HashMap<String, List<IService>> methods = new HashMap<String, List<IService>>();
		for (PortType pt : defs.getPortTypes()) {

			for (Operation op : pt.getOperations()) {
				if (op.getInput().getMessage().getParts() != null && !op.getInput().getMessage().getParts().isEmpty() && op.getInput().getMessage().getParts().get(0).getElement() != null) {
					methods.put(op.getName(), listParameters(defs, defs.getElement(op.getInput().getMessage().getParts().get(0).getElement())));
				}
				else {
					if(!methods.containsKey(op.getName())) {
						methods.put(op.getName(), new ArrayList<IService>());
					}
				}
			}
		}
		return methods;
	}

	public static String getTargetNamespace(String webServiceUrl) {
		WSDLParser parser = new WSDLParser();
		Definitions defs = parser.parse(webServiceUrl + "?wsdl");
		return defs.getTargetNamespace();
	}

	private static List<IService> listParameters(Definitions defs, Element element) {
		ComplexType ct = (ComplexType) element.getEmbeddedType();
		if (ct == null) {
			ct = (ComplexType) element.getSchema().getType(element.getType());
		}

		List<IService> params = new ArrayList<IService>();
		if(ct.getSequence() != null) {
			for (Element e : ct.getSequence().getElements()) {
				params.add(new WebServiceParameter(e.getName(), getType(e.getType().getLocalPart())));
			}
		}
		return params;
	}

	private static List<WebServiceParameter> listParameters(Element element, List<WebServiceParameter> parameters) {

		ComplexType ct = (ComplexType) element.getEmbeddedType();
		if (ct == null) {
			ct = element.getSchema().getComplexType(element.getType().getLocalPart());
		}

		boolean found = false;
		for (Element e : ct.getSequence().getElements()) {

			// Fix for invalid schema
			if (e.getType() == null) {
				return parameters;
			}

			if (e.getType().getNamespaceURI() == Consts.SCHEMA_NS) {
				parameters.add(new WebServiceParameter(e.getName(), getType(e.getType().getLocalPart())));
				found = true;
			}
			else {
				if(!found){
					listParameters(e, parameters);
				}
			}
		}

		return parameters;
	}
	
	private static int getType(String typeName){
		if (typeName.equalsIgnoreCase("int")) {
			return Variable.TYPE_INT;
		}
		else if (typeName.equalsIgnoreCase("String")) {
			return Variable.TYPE_STRING;
		}
		else if (typeName.equalsIgnoreCase("long")) {
			return Variable.TYPE_LONG;
		}
		else if (typeName.equalsIgnoreCase("boolean")) {
			return Variable.TYPE_BOOLEAN;
		}
		else if (typeName.equalsIgnoreCase("float")) {
			return Variable.TYPE_FLOAT;
		}
		else if (typeName.equalsIgnoreCase("date")) {
			return Variable.TYPE_DATE;
		}
		else if (typeName.equalsIgnoreCase("double")) {
			return Variable.TYPE_DOUBLE;
		}
		else {
			return Variable.TYPE_OBJECT;
		}
	}

	public static StreamDescriptor getDescriptor(WebServiceInput transfo) throws Exception {
		DefaultStreamDescriptor desc = new DefaultStreamDescriptor();

		if (transfo.getWebServiceUrl() != null && !transfo.getWebServiceUrl().isEmpty() && transfo.getMethodName() != null && !transfo.getMethodName().isEmpty()) {

			WSDLParser parser = new WSDLParser();
			Definitions defs = parser.parse(transfo.getWebServiceUrl() + "?wsdl");

			for (PortType pt : defs.getPortTypes()) {

				for (Operation op : pt.getOperations()) {
					if (op.getName().equals(transfo.getMethodName())) {
						if (op.getOutput().getMessage().getParts() != null && !op.getOutput().getMessage().getParts().isEmpty() && op.getInput().getMessage().getParts().get(0).getElement() != null) {
							List<WebServiceParameter> params = listParameters(defs.getElement(op.getOutput().getMessage().getParts().get(0).getElement()), new ArrayList<WebServiceParameter>());
							for (WebServiceParameter param : params) {
								StreamElement se = new StreamElement();
								se.name = param.getName();
								se.className = Variable.JAVA_CLASSES[param.getType()];
								se.isNullable = true;
								se.originTransfo = transfo.getName();
								se.transfoName = transfo.getName();
								se.tableName = "";
								se.typeName = Variable.TYPE_NAMES[param.getType()];
								se.type = param.getType();

								desc.addColumn(se);
							}
						}
					}
				}
			}
		}

		return desc;
	}

	public static List<WebServiceRow> callWebService(String webServiceUrl, String targetNamespace, String methodName, 
			List<IService> parameters, HashMap<String, Integer> columnNames) throws MalformedURLException, IOException {

		String soapAction = targetNamespace.endsWith("/") ? targetNamespace + methodName : targetNamespace + "/" + methodName;
		String response = sendMessage(webServiceUrl, soapAction, buildMessage(targetNamespace, methodName, parameters));
		return parseXmlFile(columnNames, response, methodName + "Result");
	}
	
	private static String buildMessage(String targetNamespace, String methodName, List<IService> parameters){
		StringBuilder buf = new StringBuilder();
		buf.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"" + targetNamespace + "\">\n");
		buf.append("	<soapenv:Header/>\n");
		buf.append("	<soapenv:Body>\n");
		buf.append("		<web:" + methodName + ">\n");
		if (parameters != null) {
			for (IService param : parameters) {
				buf.append("			<web:" + param.getName() + ">" + param.getValue() + "</web:" + param.getName() + ">\n");
			}
		}
		buf.append("		</web:" + methodName + ">\n");
		buf.append("	</soapenv:Body>\n");
		buf.append("</soapenv:Envelope>");
		
//		System.out.println(buf.toString());
		
		return buf.toString();
	}
	
	private static String sendMessage(String webServiceUrl, String soapAction, String message) throws IOException {

		URL url = new URL(webServiceUrl);
		URLConnection connection = url.openConnection();
		HttpURLConnection httpConn = (HttpURLConnection) connection;
		httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		httpConn.setRequestProperty("SOAPAction", soapAction);
		httpConn.setRequestMethod("POST");
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);

		PrintWriter pw = new PrintWriter(new OutputStreamWriter(httpConn.getOutputStream(), "UTF-8"));
		pw.write(message);
		pw.close();
		
		InputStream is = httpConn.getInputStream();
	
		String result = IOUtils.toString(is, "UTF-8");
		is.close();
		httpConn.disconnect();
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private static List<WebServiceRow> parseXmlFile(HashMap<String, Integer> columnNames, String in, String methodResultName) {
		org.dom4j.Element result = null;
		try {
			Document doc = DocumentHelper.parseText(in);
			org.jaxen.XPath xpath = new Dom4jXPath("//*[local-name()='" + methodResultName + "']");
			result = (org.dom4j.Element) xpath.selectSingleNode(doc);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
//		System.out.println(in);

		List<WebServiceRow> webServiceRows = new ArrayList<WebServiceRow>();
		if (result != null) {
			if(result.elements() == null || result.elements().isEmpty()){
				String value = result.getText();
				if(value != null){

					if(columnNames != null && !columnNames.isEmpty()){
						if(columnNames.size() == 1){
							for (String columnName : columnNames.keySet()) {
								WebServiceRow webServiceRow = new WebServiceRow();
								webServiceRow.addColumnValue(columnName, value);
								webServiceRows.add(webServiceRow);
							}
						}
					}
				}
			}
			else {
				for (org.dom4j.Element element : (List<org.dom4j.Element>) result.elements()) {
					if (element.elements() == null || element.elements().isEmpty()) {
						//In that case we get the parent element
						element = element.getParent();
						
						if(columnNames != null && !columnNames.isEmpty()){
							if(columnNames.size() == 1){
								for (String columnName : columnNames.keySet()) {
									List<org.dom4j.Element> elements = (List<org.dom4j.Element>)element.elements(columnName);
									for(org.dom4j.Element el :elements){
										String value = el != null ? el.getText() : "";
										if(value != null){
											WebServiceRow webServiceRow = new WebServiceRow();
											webServiceRow.addColumnValue(columnName, value);
											webServiceRows.add(webServiceRow);
										}
									}
								}
							}
							else {
								HashMap<String, List<org.dom4j.Element>> values = new HashMap<String, List<org.dom4j.Element>>();
								for (String columnName : columnNames.keySet()) {
									List<org.dom4j.Element> elements = (List<org.dom4j.Element>)element.elements(columnName);
									if(elements != null){
										values.put(columnName, elements);
									}
								}
								
								int maxSize = -1;
								for(List<org.dom4j.Element> listElements : values.values()){
									if(maxSize <= listElements.size()){
										maxSize = listElements.size();
									}
								}
								
								if(maxSize != -1){
									for(int i=0; i<maxSize; i++){
										WebServiceRow webServiceRow = new WebServiceRow();
										for(Entry<String, List<org.dom4j.Element>> value : values.entrySet()){
											if(value.getValue().size() > i){
												String valueToAdd = value.getValue().get(i).getText();
												webServiceRow.addColumnValue(value.getKey(), valueToAdd);
											}
											else {
												webServiceRow.addColumnValue(value.getKey(), null);
											}
										}
										webServiceRows.add(webServiceRow);
									}
								}
							}
						}
						break;
					}
					else {
						WebServiceRow WebServiceRow = new WebServiceRow();
						for (String columnName : columnNames.keySet()) {
							org.dom4j.Element elementValue = element.element(columnName);
							String value = elementValue != null ? elementValue.getText() : "";
							if(value != null){
								WebServiceRow.addColumnValue(columnName, value);
							}
						}
						webServiceRows.add(WebServiceRow);
					}
				}
			}
		}
		
		return webServiceRows;
	}
}
