package bpm.gateway.core.server.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.core.transformations.calcul.Script;
import bpm.gateway.core.transformations.inputs.FileInputXML;
import bpm.gateway.core.transformations.utils.DefinitionXSD;
import bpm.gateway.core.transformations.utils.DefinitionXsdOutput;
import bpm.vanilla.platform.core.utils.PGPFileProcessor;

import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.parser.XSOMParser;

public class FileXMLHelper {

	public static List<DefinitionXSD> getXPaths(FileXML transfo) throws Exception {
		List<DefinitionXSD> xPaths = new ArrayList<DefinitionXSD>();

		if (transfo.getXsdFilePath() != null) {
			FileInputStream xsdInputStream = new FileInputStream(transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getXsdFilePath()));

			XSOMParser parser = new XSOMParser();
			parser.parse(xsdInputStream);

			parser.setErrorHandler(new ErrorHandler() {
				
				@Override
				public void warning(SAXParseException exception) throws SAXException {
					exception.printStackTrace();
				}
				
				@Override
				public void fatalError(SAXParseException exception) throws SAXException {
					exception.printStackTrace();
				}
				
				@Override
				public void error(SAXParseException exception) throws SAXException {
					exception.printStackTrace();
				}
			});
			XSSchemaSet sset = parser.getResult();

			XSSchema globalSchema = sset.getSchema("");
			// =========================================================
			// the main entity of this file is in the Elements
			// there should only be one!
//			if (globalSchema.getElementDecls().size() != 1) {
//				throw new Exception("Should be only 1 element type per file.");
//			}

			XSElementDecl ed = globalSchema.getElementDecls().values().toArray(new XSElementDecl[0])[0];
			XSContentType xsContentType = ed.getType().asComplexType().getContentType();
			XSParticle particle = xsContentType.asParticle();
			if (particle != null) {

				XSTerm term = particle.getTerm();
				if (term.isModelGroup()) {
					XSModelGroup xsModelGroup = term.asModelGroup();
					term.asElementDecl();
					XSParticle[] particles = xsModelGroup.getChildren();
					for (XSParticle p : particles) {
						XSTerm pterm = p.getTerm();
						if (pterm.isElementDecl()) {
							String propertyName = "/" + pterm.asElementDecl().getName();

							DefinitionXSD def = new DefinitionXSD(null, pterm.asElementDecl().getName(), propertyName, false);

							xPaths.add(def);

							XSComplexType xsComplexType = pterm.asElementDecl().getType().asComplexType();
							if (xsComplexType != null) {
								XSContentType content = xsComplexType.getContentType();
								XSParticle partic = content.asParticle();
								getOptionalElements(propertyName, xPaths, partic);
							}
						}
					}
				}
			}
		}
		return xPaths;
	}

	public static DefinitionXSD[] getRootXSD(FileXML transfo, boolean isFromOutput) throws Exception {
		FileInputStream xsdInputStream = new FileInputStream(transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getXsdFilePath()));

		XSOMParser parser = new XSOMParser();
		parser.parse(xsdInputStream);

		XSSchemaSet sset = parser.getResult();
		XSSchema globalSchema = sset.getSchema("");
		// =========================================================
		// the main entity of this file is in the Elements
		// there should only be one!
		if (globalSchema.getElementDecls().size() != 1) {
			throw new Exception("Should be only 1 element type per file.");
		}

		if (isFromOutput) {
			XSElementDecl ed = globalSchema.getElementDecls().values().toArray(new XSElementDecl[0])[0];
			String propertyName = "/" + ed.asElementDecl().getName();

			DefinitionXsdOutput def = new DefinitionXsdOutput(null, ed.asElementDecl().getName(), propertyName);
			if(transfo.getRootElement() == null || transfo.getRootElement().getElementPath() == null || !transfo.getRootElement().getElementPath().equals(propertyName)) {
				transfo.setRootElement(def);
			}
		}

		List<DefinitionXSD> xsdTree = new ArrayList<DefinitionXSD>();
		if (transfo.getRootElement() != null) {
			XSParticle particle = findElement(transfo.getRootElement().getElementPath(), globalSchema);
			if (particle != null) {
				buildTree(transfo.getRootElement(), particle, true, isFromOutput);
				xsdTree.add(transfo.getRootElement());
			}
		}
		return xsdTree.toArray(new DefinitionXSD[xsdTree.size()]);
	}

	private static void buildTree(DefinitionXSD parentElement, XSParticle particle, boolean first, boolean isFromOutput) {
		XSTerm pterm = particle.getTerm();
		if (pterm.isElementDecl()) {

			if (first) {
				XSComplexType xsComplexType = (pterm.asElementDecl()).getType().asComplexType();
				if (xsComplexType != null) {
					Collection<? extends XSAttributeUse> c = xsComplexType.getAttributeUses();
					if(c != null) {
						for(XSAttributeUse att : c) {
						      XSAttributeDecl attributeDecl = att.getDecl();
						      XSSimpleType type = attributeDecl.getType();
						      parentElement.addChilds(new DefinitionXSD(parentElement, attributeDecl.getName(), "", true));
//						      System.out.println("type: " + attributeDecl.getType());
						}
					}
					
					XSContentType content = xsComplexType.getContentType();
					XSParticle partic = content.asParticle();
					if (partic != null) {
						buildTree(parentElement, partic, false, isFromOutput);
					}
				}
			}
			else {
				String particlePath = parentElement.getElementPath() + "/" + pterm.asElementDecl().getName();
				DefinitionXSD definition = findDefinitionXSD(parentElement, particlePath);
				if (definition == null) {
					if(isFromOutput) {
						definition = new DefinitionXsdOutput(parentElement, pterm.asElementDecl().getName(), particlePath);
					}
					else {
						definition = new DefinitionXSD(parentElement, pterm.asElementDecl().getName(), particlePath, false);
					}
					parentElement.addChilds(definition);
				}

				XSComplexType xsComplexType = (pterm.asElementDecl()).getType().asComplexType();
				if (xsComplexType != null) {
					Collection<? extends XSAttributeUse> c = xsComplexType.getAttributeUses();
					if(c != null) {
						for(XSAttributeUse att : c) {
						      XSAttributeDecl attributeDecl = att.getDecl();
						      definition.addChilds(new DefinitionXSD(definition, attributeDecl.getName(), "", true));
						}
					}
					
					XSContentType content = xsComplexType.getContentType();
					XSParticle partic = content.asParticle();
					if (partic != null) {
						buildTree(definition, partic, false, isFromOutput);
					}
				}
			}
		}
		else if (pterm.isModelGroup()) {
			XSModelGroup xsModelGroup2 = pterm.asModelGroup();
			if (xsModelGroup2.getChildren() != null) {
				for (XSParticle childParticle : xsModelGroup2.getChildren()) {
					if (childParticle != null) {
						buildTree(parentElement, childParticle, false, isFromOutput);
					}
				}
			}
		}
	}

	private static DefinitionXSD findDefinitionXSD(DefinitionXSD parentElement, String particlePath) {
		if (parentElement.getChilds() != null && !parentElement.getChilds().isEmpty()) {
			for (DefinitionXSD child : parentElement.getChilds()) {
				if (child.getElementPath().equals(particlePath)) {
					return child;
				}
			}
		}
		return null;
	}

	private static XSParticle findElement(String xPath, XSSchema globalSchema) {
		String[] elements = xPath.split("/");

		XSElementDecl ed = globalSchema.getElementDecls().values().toArray(new XSElementDecl[0])[0];
		XSContentType xsContentType = ed.getType().asComplexType().getContentType();
		XSParticle particle = xsContentType.asParticle();

		for (String element : elements) {
			if(ed.asElementDecl() != null && ed.asElementDecl().getName().equals(element)) {
				return particle;
			}
		}
		
		for (String element : elements) {
			if (!element.isEmpty()) {
				particle = recursFindElement(particle, element);
			}
		}

		return particle;
	}

	private static XSParticle recursFindElement(XSParticle particle, String element) {
		XSTerm pterm = particle.getTerm();
		if (pterm.isElementDecl()) {
			if (pterm.asElementDecl().getName().equals(element)) {
				return particle;
			}
			else {
				XSComplexType xsComplexType = pterm.asElementDecl().getType().asComplexType();
				if (xsComplexType != null) {
					XSContentType content = xsComplexType.getContentType();
					XSParticle partic = content.asParticle();
					return recursFindElement(partic, element);
				}
			}
		}
		else if (pterm.isModelGroup()) {
			XSModelGroup xsModelGroup2 = pterm.asModelGroup();
			XSParticle[] xsParticleArray = xsModelGroup2.getChildren();
			for (XSParticle xsParticleTemp : xsParticleArray) {
				XSTerm pTermTmp = xsParticleTemp.getTerm();
				if (pTermTmp.isElementDecl()) {

					if (pTermTmp.asElementDecl().getName().equals(element)) {
						return xsParticleTemp;
					}
				}
				else if (pTermTmp.isModelGroup()) {
					xsModelGroup2 = pTermTmp.asModelGroup();
					xsParticleArray = xsModelGroup2.getChildren();
					for (XSParticle xsParticleTemp2 : xsParticleArray) {
						particle = recursFindElement(xsParticleTemp2, element);
					}

					if (particle != null) {
						return particle;
					}
				}
			}
		}

		return null;
	}

	private static void getOptionalElements(String prefix, List<DefinitionXSD> list, XSParticle xsParticle) {
		if (xsParticle != null) {

			XSTerm pterm = xsParticle.getTerm();

			if (pterm.isElementDecl()) {

				String name = prefix + "/" + pterm.asElementDecl().getName();

				DefinitionXSD def = new DefinitionXSD(null, pterm.asElementDecl().getName(), name, false);
				list.add(def);

				XSComplexType xsComplexType = (pterm.asElementDecl()).getType().asComplexType();
				if (xsComplexType != null && !(pterm.asElementDecl().getType()).toString().contains("Enumeration")) {

					XSContentType xsContentType = xsComplexType.getContentType();

					XSParticle xsParticleInside = xsContentType.asParticle();
					getOptionalElements(name, list, xsParticleInside);
				}

			}
			else if (pterm.isModelGroup()) {

				XSModelGroup xsModelGroup2 = pterm.asModelGroup();
				XSParticle[] xsParticleArray = xsModelGroup2.getChildren();
				for (XSParticle xsParticleTemp : xsParticleArray) {
					getOptionalElements(prefix, list, xsParticleTemp);
				}
			}
		}
	}

	public static void createStreamDescriptor(FileInputXML transfo, int rowNumberToCheck) throws Throwable {
		if (transfo.isFromXSD()) {
			if (transfo.getRootElement() == null) {
				throw new Exception("You need to choose the Root Element of the XSD");
			}
			else if (transfo.getRootElement().getChilds() == null || transfo.getRootElement().getChilds().isEmpty()) {
				throw new Exception("This Root Element has no child. Please, choose an other one.");
			}

			HashMap<Transformation, DefaultStreamDescriptor> descriptors = new HashMap<Transformation, DefaultStreamDescriptor>();
			buildXsdDescriptors(descriptors, transfo, transfo.getRootElement(), null);
			transfo.setDescriptor(descriptors);
		}
		else {
			InputStream is = null;
			if(transfo.isFromUrl()) {
				String fileUrl = transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getDefinition());
				is = new URL(fileUrl).openStream();
			}
			else {
				is = getFileStream(transfo, ((AbstractFileServer) transfo.getServer()).getInpuStream(transfo));
			}

			Document xmlDoc = DocumentHelper.parseText(IOUtils.toString(is, transfo.getEncoding()));
			Element root = xmlDoc.getRootElement();

			Element rows = transfo.getRootTag() != null && !transfo.getRootTag().isEmpty() ? ((!root.getName().equals(transfo.getRootTag())) ? root.element(transfo.getRootTag()) : root) : root;

			HashMap<Element, List<StreamElement>> map = new HashMap<Element, List<StreamElement>>();
			if (rows.getName().equals(transfo.getRowTag())) {
				List<StreamElement> l = new ArrayList<StreamElement>();
				for (Element e : (List<Element>) rows.elements()) {
					StreamElement se = new StreamElement();
					se.name = e.getName();
					se.transfoName = transfo.getName();
					se.originTransfo = transfo.getName();
					se.className = "java.lang.String";
					l.add(se);
				}
				map.put(rows, l);
			}
			else {
				for (Element row : (List<Element>) rows.elements(transfo.getRowTag())) {
	
					List<StreamElement> l = new ArrayList<StreamElement>();
					for (Element e : (List<Element>) row.elements()) {
						StreamElement se = new StreamElement();
						se.name = e.getName();
						se.transfoName = transfo.getName();
						se.originTransfo = transfo.getName();
						se.className = "java.lang.String";
						l.add(se);
					}
					map.put(row, l);
				}
			}

			Element maxLengthKey = null;
			for (Element e : map.keySet()) {
				if (maxLengthKey == null || map.get(maxLengthKey).size() < map.get(e).size()) {
					maxLengthKey = e;
				}
			}

			DefaultStreamDescriptor des = new DefaultStreamDescriptor();
			for (StreamElement el : map.get(maxLengthKey)) {
				des.addColumn(el);
			}

			HashMap<Transformation, DefaultStreamDescriptor> descriptors = new HashMap<Transformation, DefaultStreamDescriptor>();
			descriptors.put(transfo, des);
			transfo.setDescriptor(descriptors);
		}
	}

	private static void buildXsdDescriptors(HashMap<Transformation, DefaultStreamDescriptor> descriptors, FileXML fileXml, DefinitionXSD definition, DefaultStreamDescriptor des) {

		if (!definition.getType().equals(DefinitionXSD.EXCLUDE)) {

			if (definition.getType().equals(DefinitionXSD.ITERABLE)) {

				des = new DefaultStreamDescriptor();
				Transformation transfo = findTransfo(fileXml, definition.getOutputName());

				descriptors.put(transfo, des);

				if (definition.getColumnId() != null) {
					Script s = definition.getColumnId();

					StreamElement se = new StreamElement();
					se.name = s.getName();
					se.transfoName = transfo.getName();
					se.originTransfo = transfo.getName();
					se.type = s.getType();
					se.typeName = Variable.VARIABLES_TYPES[se.type];

					switch (se.type) {
						case Variable.BOOLEAN:
							se.className = Boolean.class.getName();
							break;
						case Variable.DATE:
							se.className = Date.class.getName();
							break;
						case Variable.FLOAT:
							se.className = Double.class.getName();
							break;
						case Variable.INTEGER:
							se.className = Integer.class.getName();
							break;
						case Variable.STRING:
							se.className = String.class.getName();
							break;
						default:
							se.className = Object.class.getName();
					}
					des.addColumn(se);
				}

				if (definition.getChilds() != null) {
					for (DefinitionXSD child : definition.getChilds()) {
						buildXsdDescriptors(descriptors, fileXml, child, des);
					}
				}

//				if (definition.getAttributes() != null) {
//					for (AttributeXSD child : definition.getAttributes()) {
//						StreamElement se = new StreamElement();
//						se.name = child.getName();
//						se.transfoName = fileXml.getName();
//						se.originTransfo = fileXml.getName();
//						se.className = "java.lang.String";
//						des.addColumn(se);
//					}
//				}
			}
			else {
				StreamElement se = new StreamElement();
				se.name = definition.getName();
				se.transfoName = fileXml.getName();
				se.originTransfo = fileXml.getName();
				se.className = "java.lang.String";
				des.addColumn(se);

				if (definition.getChilds() != null) {
					for (DefinitionXSD child : definition.getChilds()) {
						buildXsdDescriptors(descriptors, fileXml, child, des);
					}
				}

//				if (definition.getAttributes() != null) {
//					for (AttributeXSD child : definition.getAttributes()) {
//						StreamElement attse = new StreamElement();
//						attse.name = child.getName();
//						attse.transfoName = fileXml.getName();
//						attse.originTransfo = fileXml.getName();
//						attse.className = "java.lang.String";
//						des.addColumn(attse);
//					}
//				}
			}
		}
	}

	private static Transformation findTransfo(FileXML fileXml, String outputName) {
		if (fileXml.getOutputs() != null) {
			for (Transformation output : fileXml.getOutputs()) {
				if (output.getName().equals(outputName)) {
					return output;
				}
			}
		}
		return null;
	}

	private static InputStream getFileStream(FileXML transfo, InputStream inpuStream) throws Throwable {
		if (transfo.isEncrypting()) {
			if (transfo instanceof FileInputXML) {
				String privateKeyPath = transfo.getDocument().getStringParser().getValue(transfo.getDocument(), ((FileInputXML) transfo).getPrivateKeyPath());
				return decryptFilePGP(inpuStream, privateKeyPath, ((FileInputXML) transfo).getPassword());
			}
			else {
				return inpuStream;
			}
		}
		else {
			return inpuStream;
		}
	}

	private static InputStream decryptFilePGP(InputStream encodedFile, String secretKeyPath, String password) throws Throwable {
		ByteArrayOutputStream bos = PGPFileProcessor.decrypt(secretKeyPath, encodedFile, password);

		ByteArrayInputStream is = new ByteArrayInputStream(bos.toByteArray());
		bos.close();
		return is;
	}

	public static List<List<Object>> getValues(FileXML transfo, int firstRow, int maxRows) throws Throwable {
		int readedRow = 0;
		int currentRow = 0;
		
		if(transfo instanceof FileInputXML && ((FileInputXML) transfo).isFromUrl()) {
			throw new Exception("This method is not available for file from URL.");
		}

		InputStream is = getFileStream(transfo, ((AbstractFileServer) transfo.getServer()).getInpuStream(transfo));

		List<List<Object>> values = new ArrayList<List<Object>>();
		Document doc = DocumentHelper.parseText(IOUtils.toString(is, transfo.getEncoding()));

		if (transfo.isFromXSD()) {
			if (transfo.getRootElement() == null) {
				throw new Exception("You need to choose the Root Element of the XSD");
			}
			else if (transfo.getRootElement().getChilds() == null || transfo.getRootElement().getChilds().isEmpty()) {
				throw new Exception("This Root Element has no child. Please, choose an other one.");
			}

			List<Element> elements = (List<Element>) doc.selectNodes("/" + transfo.getRootElement().getElementPath());

			for (Element el : elements) {
				readedRow++;
				List<Object> line = new ArrayList<Object>();
				if (readedRow <= firstRow) {
					continue;
				}

				for (StreamElement se : transfo.getDescriptor(transfo).getStreamElements()) {
					if (el.element(se.name) != null) {
						line.add(el.element(se.name).getText());
					}
					else {
						line.add(null);
					}
				}

				values.add(line);
				currentRow++;
				if (currentRow >= maxRows) {
					break;
				}
			}
		}
		else {
			Element root = doc.getRootElement();
			Element rows = transfo.getRootTag() != null && !transfo.getRootTag().isEmpty() ? ((!root.getName().equals(transfo.getRootTag())) ? root.element(transfo.getRootTag()) : root) : root;

			if (rows.getName().equals(transfo.getRowTag())) {
				List<Object> line = new ArrayList<Object>();
				for (StreamElement se : transfo.getDescriptor(transfo).getStreamElements()) {
					if (rows.element(se.name) != null) {
						line.add(rows.element(se.name).getText());
					}
					else {
						line.add(null);
					}
				}
				values.add(line);
			}
			else {
				for (Element el : (List<Element>) rows.elements(transfo.getRowTag())) {
					readedRow++;
					List<Object> line = new ArrayList<Object>();
					if (readedRow <= firstRow) {
						continue;
					}

					for (StreamElement se : transfo.getDescriptor(transfo).getStreamElements()) {

						if (el.element(se.name) != null) {
							line.add(el.element(se.name).getText());
						}
						else {
							line.add(null);
						}

					}

					values.add(line);
					currentRow++;
					if (currentRow >= maxRows) {
						break;
					}
				}
			}
		}
		return values;
	}

	public static List<List<Object>> getCountDistinctFieldValues(StreamElement field, FileXML stream) throws Throwable {
		if(stream instanceof FileInputXML && ((FileInputXML) stream).isFromUrl()) {
			throw new Exception("This method is not available for file from URL.");
		}
		
		InputStream is = getFileStream(stream, ((AbstractFileServer) stream.getServer()).getInpuStream(stream));

		List<List<Object>> values = new ArrayList<List<Object>>();
		Document doc = DocumentHelper.parseText(IOUtils.toString(is, stream.getEncoding()));

		Element root = doc.getRootElement();

		if (stream.isFromXSD()) {
			if (stream.getRootElement() == null) {
				throw new Exception("You need to choose the Root Element of the XSD");
			}
			else if (stream.getRootElement().getChilds() == null || stream.getRootElement().getChilds().isEmpty()) {
				throw new Exception("This Root Element has no child. Please, choose an other one.");
			}

			List<Element> elements = (List<Element>) doc.selectNodes("/" + stream.getRootElement().getElementPath());

			for (Element el : elements) {
				boolean present = false;
				String value = null;
				if (el.element(field.name) != null) {
					value = el.element(field.name).getText();
				}

				for (List<Object> l : values) {
					if (l.get(0).equals(value)) {
						l.set(1, (Integer) l.get(1) + 1);
						present = true;
						break;
					}
				}

				if (!present) {
					List<Object> l = new ArrayList<Object>();
					l.add(value);
					l.add(1);
					values.add(l);
				}
			}
		}
		else {
			Element rows = stream.getRootTag() != null && !stream.getRootTag().isEmpty() ? ((!root.getName().equals(stream.getRootTag())) ? root.element(stream.getRootTag()) : root) : root;

			if (rows.getName().equals(stream.getRowTag())) {
				boolean present = false;
				String value = null;
				if (rows.element(field.name) != null) {
					value = rows.element(field.name).getText();
				}

				for (List<Object> l : values) {
					if (l.get(0).equals(value)) {
						l.set(1, (Integer) l.get(1) + 1);
						present = true;
						break;
					}
				}

				if (!present) {
					List<Object> l = new ArrayList<Object>();
					l.add(value);
					l.add(1);
					values.add(l);
				}
			}
			else {
				for (Element el : (List<Element>) rows.elements(stream.getRowTag())) {
					boolean present = false;
					String value = null;
					if (el.element(field.name) != null) {
						value = el.element(field.name).getText();
					}

					for (List<Object> l : values) {
						if (l.get(0).equals(value)) {
							l.set(1, (Integer) l.get(1) + 1);
							present = true;
							break;
						}
					}

					if (!present) {
						List<Object> l = new ArrayList<Object>();
						l.add(value);
						l.add(1);
						values.add(l);
					}
				}
			}
		}

		return values;
	}
}
