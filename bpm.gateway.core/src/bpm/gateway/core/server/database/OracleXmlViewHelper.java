package bpm.gateway.core.server.database;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.xdb.XMLType;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.core.transformations.calcul.Script;
import bpm.gateway.core.transformations.inputs.OracleXmlView;
import bpm.gateway.core.transformations.utils.DefinitionXSD;

public class OracleXmlViewHelper {

	public static void createStreamDescriptor(OracleXmlView transfo, int rowNumberToCheck) throws Exception {
		if (transfo.getRootElement() == null) {
			throw new Exception("You need to choose the Root Element of the XSD");
		}
		else if (transfo.getRootElement().getChilds() == null || transfo.getRootElement().getChilds().isEmpty()) {
			throw new Exception("This Root Element has no child. Please, choose an other one.");
		}

		HashMap<Transformation, DefaultStreamDescriptor> descriptors = new HashMap<Transformation, DefaultStreamDescriptor>();
		buildViewDescriptors(descriptors, transfo, transfo.getRootElement(), null);
		transfo.setDescriptor(descriptors);
	}

	private static void buildViewDescriptors(HashMap<Transformation, DefaultStreamDescriptor> descriptors, OracleXmlView oracleView, DefinitionXSD definition, DefaultStreamDescriptor des) {

		if (!definition.getType().equals(DefinitionXSD.EXCLUDE)) {

			if (definition.getType().equals(DefinitionXSD.ITERABLE)) {

				des = new DefaultStreamDescriptor();
				Transformation transfo = findTransfo(oracleView, definition.getOutputName());

				descriptors.put(transfo, des);

				if (definition.getColumnId() != null) {
					Script s = definition.getColumnId();

					StreamElement se = new StreamElement();
					se.name = s.getName();
					se.transfoName = oracleView.getName();
					se.originTransfo = oracleView.getName();
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
						buildViewDescriptors(descriptors, oracleView, child, des);
					}
				}
			}
			else {
				StreamElement se = new StreamElement();
				se.name = definition.getName();
				se.transfoName = oracleView.getName();
				se.originTransfo = oracleView.getName();
				se.className = "java.lang.String";
				des.addColumn(se);

				if (definition.getChilds() != null) {
					for (DefinitionXSD child : definition.getChilds()) {
						buildViewDescriptors(descriptors, oracleView, child, des);
					}
				}
			}
		}
	}

	public static List<DefinitionXSD> getXPaths(OracleXmlView transfo) throws Exception {
		List<DefinitionXSD> xPaths = new ArrayList<DefinitionXSD>();

		if (transfo.getDefinition() != null) {
			List<Document> docs = getDocument(transfo, 2);
			if (docs != null && !docs.isEmpty()) {
				Element root = docs.get(0).getRootElement();
				// if (root != null && root.elements() != null &&
				// !root.elements().isEmpty()) {
				//
				// for (Element child : (List<Element>) root.elements()) {
				if (root != null) {
					String propertyName = "/" + root.getName();
					DefinitionXSD def = new DefinitionXSD(null, root.getName(), propertyName, false);

					xPaths.add(def);

					getOptionalElements(propertyName, xPaths, root);
					// }
				}
			}
		}
		return xPaths;
	}

	private static List<Document> getDocument(OracleXmlView view, Integer maxRows) throws Exception {
		if (!(view instanceof OracleXmlView)) {
			throw new ServerException(view.getClass().getName() + " not valid type for a DataBase Server", view.getServer());
		}

		if (view.getServer() == null) {
			throw new Exception("Server need to be define.");
		}

		DataBaseConnection con = (DataBaseConnection) ((DataBaseServer) view.getServer()).getCurrentConnection(null);
		boolean conWasConnected = con.isOpened();
		if (!conWasConnected) {
			con.connect(view.getDocument());
		}

		String query;
		try {
			if (view.getDocument() != null) {
				query = new String(view.getDocument().getStringParser().getValue(view.getDocument(), view.getDefinition()));
			}
			else {
				// Means that the document is not initialized yet
				return null;
				// return view.getDescriptor(null);
			}
		} catch (Exception e1) {
			throw new Exception("Unable to parse query:" + view.getDefinition() + ", reason : " + e1.getMessage(), e1);
		}

		Connection sock = con.getSocket(view.getDocument());

		OraclePreparedStatement statement = null;
		try {
			// statement = (OraclePreparedStatement)
			// sock.prepareStatement("SELECT SYS_NC_ROWINFO$ FROM " + query +
			// " where rownum < " + maxRows);
			if (!query.toLowerCase().contains("rownum") && maxRows != null) {
				statement = (OraclePreparedStatement) sock.prepareStatement(query + " where rownum < " + maxRows);
			}
			else {
				statement = (OraclePreparedStatement) sock.prepareStatement(query);
			}
			OracleResultSet resultSet = (OracleResultSet) statement.executeQuery();

			List<Document> docs = new ArrayList<Document>();

			while (resultSet.next()) {
				XMLType xml = (XMLType) resultSet.getObject(1);
				String xmlContent = xml.getString();
//				String xmlContent = resultSet.getCLOB(1).stringValue();
//				System.out.println(xmlContent);

				Document doc = DocumentHelper.parseText(xmlContent);
				docs.add(doc);

				xml.close();
			}
			resultSet.close();

			return docs;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (conWasConnected) {
				con.disconnect();
			}
		}

		return null;
	}

	public static DefinitionXSD[] getRootXSD(OracleXmlView transfo) throws Exception {
		List<DefinitionXSD> xsdTree = new ArrayList<DefinitionXSD>();

		List<Document> docs = getDocument(transfo, null);
		if (docs != null && !docs.isEmpty()) {
			Element root = docs.get(0).getRootElement();

			if (transfo.getRootElement() != null) {
				Element particle = findElement(transfo.getRootElement().getElementPath(), root);
				if (particle != null) {
					buildTree(transfo.getRootElement(), particle, true);

					updateTree(transfo.getRootElement(), transfo.getRootElement(), docs);

					xsdTree.add(transfo.getRootElement());
				}
			}
		}
		return xsdTree.toArray(new DefinitionXSD[xsdTree.size()]);
	}

	private static void buildTree(DefinitionXSD parentElement, Element particle, boolean first) {
		if (first) {
			if (particle.attributes() != null && !particle.attributes().isEmpty()) {
				for (Attribute att : (List<Attribute>) particle.attributes()) {
					if (!alreadyExist(parentElement, att.getName())) {
						parentElement.addChilds(new DefinitionXSD(parentElement, att.getName(), "", true));
					}
				}
			}

			if (particle.elements() != null && !particle.elements().isEmpty()) {
				for (Element childParticle : (List<Element>) particle.elements()) {
					if (childParticle != null) {
						buildTree(parentElement, childParticle, false);
					}
				}
			}
		}
		else {

			String particlePath = parentElement.getElementPath() + "/" + particle.getName();
			DefinitionXSD definition = findDefinitionXSD(parentElement, particlePath);
			if (definition == null) {
				definition = new DefinitionXSD(parentElement, particle.getName(), particlePath, false);
				parentElement.addChilds(definition);
			}

			if (particle.attributes() != null && !particle.attributes().isEmpty()) {
				for (Attribute att : (List<Attribute>) particle.attributes()) {
					if (!alreadyExist(parentElement, att.getName())) {
						definition.addChilds(new DefinitionXSD(parentElement, att.getName(), "", true));
					}
				}
			}

			if (particle.elements() != null && !particle.elements().isEmpty()) {
				for (Element childParticle : (List<Element>) particle.elements()) {
					if (childParticle != null) {
						buildTree(definition, childParticle, false);
					}
				}
			}
		}
	}

	private static void updateTree(DefinitionXSD rootElement, DefinitionXSD parentElement, List<Document> docs) {
		if (docs.size() > 1) {
			for (int i = 1; i < docs.size(); i++) {
				Element root = docs.get(i).getRootElement();
				Element particle = findElement(rootElement.getElementPath(), root);

				buildTree(parentElement, particle, true);
			}
		}
	}

	private static boolean alreadyExist(DefinitionXSD parentElement, String name) {
		if (parentElement.getChilds() != null) {
			for (DefinitionXSD child : parentElement.getChilds()) {
				if (child.getName().equals(name)) {
					return true;
				}
			}
		}
		return false;
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

	private static Element findElement(String xPath, Element root) {
		String[] elements = xPath.split("/");

		Element particle = root;

		// for (String el : elements) {
		// if (particle != null && particle.getName().equals(el)) {
		// return particle;
		// }
		// }

		for (String el : elements) {
			if (!el.isEmpty()) {
				particle = recursFindElement(particle, el);
			}
		}

		return particle;
	}

	private static Element recursFindElement(Element particle, String element) {
		if (particle.getName().equals(element)) {
			return particle;
		}
		else {

			if (particle.elements() != null && !particle.elements().isEmpty()) {
				for (Element xsParticleTemp : (List<Element>) particle.elements()) {
					particle = recursFindElement(xsParticleTemp, element);

					if (particle != null) {
						return particle;
					}
				}
			}
		}

		return null;
	}

	private static void getOptionalElements(String prefix, List<DefinitionXSD> xPaths, Element element) {
		if (element.elements() != null && !element.elements().isEmpty()) {
			for (Element el : (List<Element>) element.elements()) {
				// String propertyName = "/" + el.getName();
				String name = prefix + "/" + el.getName();

				DefinitionXSD def = new DefinitionXSD(null, el.getName(), name, false);

				xPaths.add(def);

				if (el.elements() != null && !el.elements().isEmpty()) {
					getOptionalElements(name, xPaths, el);
				}
			}
		}
	}

	private static Transformation findTransfo(OracleXmlView oracleView, String outputName) {
		if (oracleView.getOutputs() != null) {
			for (Transformation output : oracleView.getOutputs()) {
				if (output.getName().equals(outputName)) {
					return output;
				}
			}
		}
		return null;
	}

	public static List<List<Object>> getValues(OracleXmlView transfo, int firstRow, int maxRows) throws Exception {

		List<Document> documents = getDocument(transfo, maxRows);

		List<List<Object>> values = new ArrayList<List<Object>>();

		if (documents != null && !documents.isEmpty()) {
			for (Document doc : documents) {
				List<Object> line = getValues(transfo, doc, firstRow);
				values.add(line);
			}
		}

		return values;
	}

	private static List<Object> getValues(OracleXmlView transfo, Document doc, int firstRow) throws Exception {
		int readedRow = 0;

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

			return line;
		}

		return null;
	}

	public static List<List<Object>> getCountDistinctFieldValues(StreamElement field, OracleXmlView stream) throws FileNotFoundException, DocumentException, IOException, Exception {

		// InputStream is = getFileStream(stream, ((AbstractFileServer)
		// stream.getServer()).getInpuStream(stream));
		//
		List<List<Object>> values = new ArrayList<List<Object>>();
		// Document doc = DocumentHelper.parseText(IOUtils.toString(is,
		// stream.getEncoding()));
		//
		// Element root = doc.getRootElement();
		//
		// if (stream.getRootElement() == null) {
		// throw new
		// Exception("You need to choose the Root Element of the XSD");
		// }
		// else if (stream.getRootElement().getChilds() == null ||
		// stream.getRootElement().getChilds().isEmpty()) {
		// throw new
		// Exception("This Root Element has no child. Please, choose an other one.");
		// }
		//
		// List<Element> elements = (List<Element>) doc.selectNodes("/" +
		// stream.getRootElement().getElementPath());
		//
		// for (Element el : elements) {
		// boolean present = false;
		// String value = null;
		// if (el.element(field.name) != null) {
		// value = el.element(field.name).getText();
		// }
		//
		// for (List<Object> l : values) {
		// if (l.get(0).equals(value)) {
		// l.set(1, (Integer) l.get(1) + 1);
		// present = true;
		// break;
		// }
		// }
		//
		// if (!present) {
		// List<Object> l = new ArrayList<Object>();
		// l.add(value);
		// l.add(1);
		// values.add(l);
		// }
		// }

		return values;
	}
}
