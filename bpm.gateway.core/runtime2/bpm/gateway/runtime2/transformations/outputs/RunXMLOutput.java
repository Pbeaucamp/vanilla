package bpm.gateway.runtime2.transformations.outputs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.outputs.FileOutputXML;
import bpm.gateway.core.transformations.utils.DefinitionXSD;
import bpm.gateway.core.transformations.utils.DefinitionXsdOutput;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.StepEndedException;
import bpm.gateway.runtime2.internal.Row;
import bpm.vanilla.platform.core.utils.PGPFileProcessor;

public class RunXMLOutput extends RuntimeStep {

	private String rowStartTag;
	private String rowEndTag;
	private PrintWriter writer;
	private List<String> columnNames = new ArrayList<String>();

	private File xmlFile;
	
	private FileOutputXML transfo;
	
	private LinkedHashMap<String, List<Row>> rowByInput = new LinkedHashMap<String, List<Row>>();
	
	private Document xmlDocument;

	public RunXMLOutput(FileOutputXML transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		transfo = (FileOutputXML) getTransformation();

		String fileName = null;
		try {
			fileName = getTransformation().getDocument().getStringParser().getValue(getTransformation().getDocument(), transfo.getDefinition());
		} catch (Exception e) {
			error(" error when getting/parsing fileName", e);
			throw e;
		}
		
		if(transfo.isEncrypting() && !transfo.getDelete()) {
			error(" encryption cannot be performed: the option 'Truncate file' is not set to true. Please fix the model before.");
			throw new Exception("Encryption cannot be performed: the option 'Truncate file' is not set to true. Please fix the model before.");
		}
		
		if(transfo.isEncrypting() && (transfo.getPublicKeyPath() == null || transfo.getPublicKeyPath().isEmpty())) {
			error(" encryption cannot be performed: The Public Key is empty.");
			throw new Exception("Encryption cannot be performed: The Public Key is empty.");
		}

		xmlFile = new File(fileName);
		if (transfo.getDelete() && xmlFile.exists()) {
			xmlFile.delete();
			info(" delete file " + xmlFile.getAbsolutePath());
		}

		if (!xmlFile.exists()) {
			try {
				xmlFile.createNewFile();
				info(" file " + xmlFile.getAbsolutePath() + " created");
			} catch (Exception e) {
				error(" cannot create file " + xmlFile.getName(), e);
				throw e;
			}
		}
		writer = null;
		try {
			writer = new PrintWriter(xmlFile, transfo.getEncoding());
		} catch (FileNotFoundException e2) {
			error(" unable to create PrintWriter", e2);
			throw e2;

		} catch (UnsupportedEncodingException e) {
			error(" unable to create PrintWriter", e);
			throw e;
		}
		if(!transfo.isFromXSD()) {

			/*
			 * build the tags
			 */
			rowStartTag = "<" + ((FileOutputXML) getTransformation()).getRowTag() + ">";
			rowEndTag = "</" + ((FileOutputXML) getTransformation()).getRowTag() + ">";
	
			for (StreamElement se : transfo.getDescriptor(getTransformation()).getStreamElements()) {
				columnNames.add(new String(se.name));
			}
	
			writer.write("<" + ((FileOutputXML) getTransformation()).getRootTag() + ">\n");
	
			isInited = true;
		}
		else {
			xmlDocument = DocumentHelper.createDocument();

			isInited = true;
		}
	}

	@Override
	public void performRow() throws Exception {

		if (areInputStepAllProcessed()) {
			if (inputEmpty()) {
				setEnd();
			}
		}

		if (isEnd() && inputEmpty()) {
			return;
		}

		if (!isEnd() && inputEmpty()) {
			try {
				Thread.sleep(10);
				return;
			} catch (Exception e) {

			}
		}

		Row row = null;
		try {
			row = readRow();
		} catch (StepEndedException ex) {
			return;
		}

		if (row == null) {
			return;
		}

		if(transfo.isFromXSD()) {
			if(rowByInput.get(row.getTransformation().getName()) == null) {
				rowByInput.put(row.getTransformation().getName(), new ArrayList<Row>());
			}
			rowByInput.get(row.getTransformation().getName()).add(row);
		}
		else {
			writeRowInFile(row);
			writeRow(row);
		}

	}

	@Override
	public void releaseResources() {
		if(!transfo.isFromXSD()) {
			try {
				writer.write("</" + ((FileOutputXML) getTransformation()).getRootTag() + ">\n");
				writer.close();
				info(" closed Writer");
			} catch (Exception ex) {
				error(" unable to close writer", ex);
			}
		}
		else {	
			Node rootNode = new Node();
			rootNode.setName(transfo.getRootElement().getName());
			try {
				createNodeTree((DefinitionXsdOutput) transfo.getRootElement(), rootNode);
			} catch(IndexOutOfBoundsException e) {
				e.printStackTrace();
			} catch(ServerException e) {
				e.printStackTrace();
			}
			
			createXmlDocument(rootNode);
			
			writer.write(xmlDocument.asXML());
			writer.close();
		}

		FileOutputXML xml = (FileOutputXML) getTransformation();
		if (xml.isEncrypting() && xmlFile != null && xmlFile.exists()) {
			
			info(" encrypt file");
			try {
				String publicKeyPath = xml.getDocument().getStringParser().getValue(xml.getDocument(), xml.getPublicKeyPath());
				encryptFilePGP(publicKeyPath);
			} catch (Throwable e) {
				error(" unable to encrypt the file", e);
			}
		}
	}
	
	private void createXmlDocument(Node rootNode) {
		xmlDocument.addElement(rootNode.getName());
		createXmlChildren(rootNode, xmlDocument.getRootElement());
	}

	private void createXmlChildren(Node parentNode, Element parentElement) {
		for(Node n : parentNode.getChildren()) {
			Element elem = parentElement.addElement(n.getName());;
			if(n.getValue() != null && !n.getValue().isEmpty()) {
				elem.setText(n.getValue());
			}
			createXmlChildren(n, elem);
		}
	}

	/**
	 * @throws ServerException 
	 * @throws IndexOutOfBoundsException 
	 * 
	 */
	private void createNodeTree(DefinitionXsdOutput node, Node parentNode) throws IndexOutOfBoundsException, ServerException {
		for(DefinitionXSD child : node.getChilds()) {
			List<Node> subs = new ArrayList<Node>();

			
			if(child.getType().equals(DefinitionXSD.ITERABLE)) {	
			
				for(Row r : rowByInput.get(child.getOutputName())) {
					Node n = new Node();
					n.setName(child.getName());
					
					if(((DefinitionXsdOutput)child).getChildColumn() != null && ! ((DefinitionXsdOutput)child).getChildColumn().isEmpty()) {
						String value = String.valueOf(r.get(transfo.getDocument().getTransformation(child.getOutputName()).getDescriptor(transfo).getElementIndex(((DefinitionXsdOutput)child).getChildColumn())));
						n.setId(value);
					}
					
					for(DefinitionXSD c : child.getChilds()) {
						if(c.getType().equals(DefinitionXSD.ITERABLE)) {
							Node cn = new Node();
							cn.setName(c.getName());
							cn.setParentId(n.getId());
							n.getChildren().add(cn);
						}
						else {
							Node cn = new Node();
							cn.setName(c.getName());
							
							String value = String.valueOf(r.get(transfo.getDocument().getTransformation(child.getOutputName()).getDescriptor(transfo).getElementIndex(((DefinitionXsdOutput)c).getInputColumn())));
							cn.setValue(value);
							
							n.getChildren().add(cn);
						}
					}
					
					subs.add(n);
					createChildNodes((DefinitionXsdOutput) child, subs);
				}
			}
			
			else {
				Node n = new Node();
				n.setName(child.getName());
				
				subs.add(n);
				
				createChildNodes((DefinitionXsdOutput) child, subs);
			}
			
			
			
			parentNode.getChildren().addAll(subs);
		}
	}

	private void createChildNodes(DefinitionXsdOutput parentNode, List<Node> parents) throws IndexOutOfBoundsException, ServerException {
		if(parentNode.getChilds() != null) {
			for(DefinitionXSD child : parentNode.getChilds()) {
				List<Node> subs = new ArrayList<Node>();
				if(child.getType().equals(DefinitionXSD.ITERABLE)) {
					
					for(Row r : rowByInput.get(child.getOutputName())) {
						Node n = new Node();
						n.setName(child.getName());
						
						if(((DefinitionXsdOutput)child).getChildColumn() != null && ! ((DefinitionXsdOutput)child).getChildColumn().isEmpty()) {
							String value = String.valueOf(r.get(transfo.getDocument().getTransformation(child.getOutputName()).getDescriptor(transfo).getElementIndex(((DefinitionXsdOutput)child).getChildColumn())));
							n.setId(value);
						}
						if(((DefinitionXsdOutput)child).getParentColumn() != null && ! ((DefinitionXsdOutput)child).getParentColumn().isEmpty()) {
							String value = String.valueOf(r.get(transfo.getDocument().getTransformation(child.getOutputName()).getDescriptor(transfo).getElementIndex(((DefinitionXsdOutput)child).getParentColumn())));
							n.setParentId(value);
						}
						
						for(DefinitionXSD sub : child.getChilds()) {
							if(sub.getType().equals(DefinitionXSD.ITERABLE)) {
								Node cn = new Node();
								cn.setName(sub.getName());
								cn.setParentId(n.getId());
								n.getChildren().add(cn);
							}
							else {
								Node cn = new Node();
								cn.setName(sub.getName());
								
								String value = String.valueOf(r.get(transfo.getDocument().getTransformation(child.getOutputName()).getDescriptor(transfo).getElementIndex(((DefinitionXsdOutput)sub).getInputColumn())));
								cn.setValue(value);
								
								n.getChildren().add(cn);
							}
						}
						
						subs.add(n);
					}
					
					createChildNodes((DefinitionXsdOutput) child, subs);
					
					if(((DefinitionXsdOutput)child).getParentColumn() != null && !((DefinitionXsdOutput)child).getParentColumn().isEmpty()) {
						for(Node sub : subs) {
							for(Node parent : parents) {
								if(sub.getParentId().equals(parent.getId())) {
									for(Node n : parent.getChildren()) {
										if(n.getName().equals(sub.getName())) {
											n.getChildren().addAll(sub.getChildren());
											break;
										}
									}
									break;
								}
							}
						}
					}
					else {
						parents.get(0).getChildren().addAll(subs);
					}
					
				}
				
				else {
					Node n = new Node();
					n.setName(child.getName());
					
					subs.add(n);
					
					createChildNodes((DefinitionXsdOutput) child, subs);
				}		
			}
		}
		
	}

	private void encryptFilePGP(String publicKeyPath) throws Throwable {
		FileInputStream fis = new FileInputStream(xmlFile);
		ByteArrayOutputStream bos = PGPFileProcessor.encrypt(publicKeyPath, fis, false, true);
		
		OutputStream outputStream = new FileOutputStream (xmlFile); 
		bos.writeTo(outputStream);
		bos.close();
		outputStream.close();
	}

	/**
	 * insert a the values from the row and an EOL
	 * 
	 * @param os
	 * @param row
	 * @throws ServerException
	 * @throws IOException
	 */
	private void writeRowInFile(Row row) throws ServerException, IOException {
		writer.write("    " + rowStartTag + "\r\n");
		getTransformation().refreshDescriptor();
		List<StreamElement> columnsMetaData = getTransformation().getDescriptor(getTransformation()).getStreamElements();

		for (int i = 0; i < row.getMeta().getSize(); i++) {
			String clazz = row.getMeta().getJavaClasse(i).getName();
			String value = row.get(i) != null ? row.get(i).toString() : "";
			String colName = columnsMetaData.get(i).name.replace("(", "").replace(")", "");

			Element ele = DocumentHelper.createElement(colName);
			ele.addAttribute("javaclass", clazz);
			ele.setText(value);
			writer.write("        " + ele.asXML() + "\r\n");
		}

		writer.write("    " + rowEndTag + "\r\n");
		writer.write("\r\n");
	}

	private class Node {
		private String name;
		private String value;
		private List<Node> children;
		private String id;
		private String parentId;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public List<Node> getChildren() {
			if(children == null) {
				children = new ArrayList<Node>();
			}
			return children;
		}
		public void setChildren(List<Node> children) {
			this.children = children;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getId() {
			return id;
		}
		public void setParentId(String parentId) {
			this.parentId = parentId;
		}
		public String getParentId() {
			return parentId;
		}
		
	}
	
}
