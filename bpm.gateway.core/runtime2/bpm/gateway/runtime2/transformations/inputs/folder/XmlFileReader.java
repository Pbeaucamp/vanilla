package bpm.gateway.runtime2.transformations.inputs.folder;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.StreamElement;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class XmlFileReader implements IFileReader{
	private Document document;
	private Iterator<Element> rowIterator;
	private String[] elementColumnNames;
	private RuntimeStep runtimeStep;
	
	public XmlFileReader(RuntimeStep runtimeStep, File file, String encoding, String rootTag, String rowTag) throws Exception{
		this.runtimeStep = runtimeStep;
		try{
			document = DocumentHelper.parseText(IOUtils.toString(new FileInputStream(file), encoding));
		}catch(Exception ex){
			throw new Exception("Error when building Dom4j Document from file : " + ex.getMessage(), ex);
		}
		
		Element root = document.getRootElement();
		Element rows =  (!root.getName().equals(rootTag))? root.element(rootTag) : root;
		rowIterator = rows.elements(rowTag).iterator();
		
		
		elementColumnNames = new String[runtimeStep.getTransformation().getDescriptor(runtimeStep.getTransformation()).getColumnCount()];
		int i = 0;
		for(StreamElement e : runtimeStep.getTransformation().getDescriptor(runtimeStep.getTransformation()).getStreamElements()){
			elementColumnNames[i++] = new String(e.name);
		}
		
	}

	public boolean isAllRead() {
		return !rowIterator.hasNext();
	}

	public Row readRow() throws Exception {
		Row row = RowFactory.createRow(runtimeStep);
		Element current = rowIterator.next();
		for(int i = 0; i < row.getMeta().getSize(); i++){
			Element e = current.element(elementColumnNames[i]);
			if (e != null){
				row.set(i, e.getText());
			}
		}
		return row;
	}

	public void releaseResources() {
		rowIterator = null;
		document = null;
		
	}
	
}
