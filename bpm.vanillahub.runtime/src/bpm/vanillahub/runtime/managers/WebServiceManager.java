package bpm.vanillahub.runtime.managers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.beans.resources.attributes.WebServiceDefinition;
import bpm.vanillahub.core.beans.resources.attributes.WebServiceMethodDefinition;
import bpm.vanillahub.core.beans.resources.attributes.WebServiceParameter;
import bpm.vanillahub.core.beans.resources.attributes.WebServiceParameter.TypeParameter;

import com.predic8.schema.ComplexType;
import com.predic8.schema.Element;
import com.predic8.schema.SimpleType;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Operation;
import com.predic8.wsdl.Part;
import com.predic8.wsdl.Port;
import com.predic8.wsdl.PortType;
import com.predic8.wsdl.Service;
import com.predic8.wsdl.WSDLParser;

public class WebServiceManager {

	public static List<WebServiceMethodDefinition> getMethodNames(IVanillaLogger logger, String webServiceUrl) {
		List<WebServiceMethodDefinition> methods = new ArrayList<WebServiceMethodDefinition>();

		WSDLParser parser = new WSDLParser();
		Definitions defs = parser.parse(webServiceUrl + "?wsdl");

		logger.info("PortTypes: ");
		for (PortType pt : defs.getPortTypes()) {
			logger.info("  PortType Name: " + pt.getName());
			logger.info("  PortType Operations: ");
			
			WebServiceDefinition webServiceDefinition = getWebServiceDefinition(logger, defs, pt);
			
			for (Operation op : pt.getOperations()) {
				logger.info("    Operation Name: " + op.getName());
				String messageNameInput = op.getInput().getMessage().getName();
				String messageNameOutput = op.getOutput() != null && op.getOutput().getMessage() != null ? op.getOutput().getMessage().getName() : "";
				WebServiceMethodDefinition method = new WebServiceMethodDefinition(webServiceDefinition, op.getName(), messageNameInput, messageNameOutput);

				List<WebServiceParameter> params = new ArrayList<WebServiceParameter>();
				if (op.getInput().getMessage().getParts() != null) {
					for (Part part : op.getInput().getMessage().getParts()) {

						if (part.getElement() != null) {
							listParameters(params, part.getElement(), "");
						}
					}
				}
				method.setParameters(params);
				methods.add(method);
			}
		}

		return methods;
	}
	
	

	private static WebServiceDefinition getWebServiceDefinition(IVanillaLogger logger, Definitions defs, PortType pt) {
		for (Service service : defs.getServices()) {
			logger.info("  Service Name: " + service.getName());
			if(service.getName().equals(pt.getName())) {
				for (Port port : service.getPorts()) {
					return new WebServiceDefinition(pt.getName(), port.getBinding().getName(), port.getAddress().getLocation());
				}
			}
		}
		return null;
	}

	public static String getTargetNamespace(String webServiceUrl) {
		WSDLParser parser = new WSDLParser();
		Definitions defs = parser.parse(webServiceUrl + "?wsdl");
		return defs.getTargetNamespace();
	}

	private static void listParameters(List<WebServiceParameter> parameters, Element element, String parentPath) {
		ComplexType ct = (ComplexType) element.getEmbeddedType();
		if (ct == null)
			ct = element.getSchema().getComplexType(element.getType().getLocalPart());

		for (Element e : ct.getSequence().getElements()) {
			if (e.getEmbeddedType() instanceof ComplexType) {
				parentPath += "/" + e.getName();
				listParameters(parameters, e, parentPath);
			}
			else if (e.getType() == null) {
				String documentation = getDocumentation(e);
				parameters.add(new WebServiceParameter(e.getName(), parentPath, TypeParameter.TYPE_OBJECT, documentation));
			}
			else {
				String documentation = getDocumentation(e);
				parameters.add(new WebServiceParameter(e.getName(), parentPath, getType(e.getType().getLocalPart()), documentation));
			}
		}
	}

	private static String getDocumentation(Element e) {
		if (e.getAnnotation() != null && e.getAnnotation().getDocumentations() != null && !e.getAnnotation().getDocumentations().isEmpty()) {
			String desc = e.getAnnotation().getDocumentations().get(0).getContent();
			try {
				return IOUtils.toString(desc.getBytes(), "UTF-8");
			} catch (IOException e1) {
				e1.printStackTrace();
				return desc;
			}
		}
		else if (e.getEmbeddedType() instanceof SimpleType) {
			SimpleType st = (SimpleType) e.getEmbeddedType();
			if (st.getAnnotation() != null && st.getAnnotation().getDocumentations() != null && !st.getAnnotation().getDocumentations().isEmpty()) {
				String desc = st.getAnnotation().getDocumentations().get(0).getContent();
				try {
					return IOUtils.toString(desc.getBytes(), "UTF-8");
				} catch (IOException e1) {
					e1.printStackTrace();
					return desc;
				}
			}
		}
		return "";
	}

	private static TypeParameter getType(String typeName) {
		if (typeName.equalsIgnoreCase("int")) {
			return TypeParameter.TYPE_INT;
		}
		else if (typeName.equalsIgnoreCase("String")) {
			return TypeParameter.TYPE_STRING;
		}
		else if (typeName.equalsIgnoreCase("long")) {
			return TypeParameter.TYPE_LONG;
		}
		else if (typeName.equalsIgnoreCase("boolean")) {
			return TypeParameter.TYPE_BOOLEAN;
		}
		else if (typeName.equalsIgnoreCase("float")) {
			return TypeParameter.TYPE_FLOAT;
		}
		else if (typeName.equalsIgnoreCase("date")) {
			return TypeParameter.TYPE_DATE;
		}
		else if (typeName.equalsIgnoreCase("datetime")) {
			return TypeParameter.TYPE_DATE;
		}
		else if (typeName.equalsIgnoreCase("double")) {
			return TypeParameter.TYPE_DOUBLE;
		}
		else {
			return TypeParameter.TYPE_OBJECT;
		}
	}
}
