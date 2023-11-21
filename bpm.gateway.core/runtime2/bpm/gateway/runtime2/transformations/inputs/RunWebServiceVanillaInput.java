package bpm.gateway.runtime2.transformations.inputs;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.server.file.AbstractFileServer;
import bpm.gateway.core.transformations.webservice.WebServiceVanillaInput;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.service.ServiceConstants;

public class RunWebServiceVanillaInput extends RuntimeStep{

	private InputStream fis;
	private Document document;
	private Iterator<Element> rowIterator;
	
	private List<String> elementColumnNames = new ArrayList<String>();
	
	public RunWebServiceVanillaInput(IRepositoryContext repCtx, WebServiceVanillaInput transformation, int bufferSize) {
		super(repCtx, transformation, bufferSize);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void init(Object adapter) throws Exception {
		WebServiceVanillaInput transfo = (WebServiceVanillaInput)this.getTransformation();

		try{
			fis =((AbstractFileServer)transfo.getServer()).getInpuStream(transfo);
		}catch(Exception e){
			error("unable to open file ", e);
			throw e;
		}
		
		try{
			document = DocumentHelper.parseText(IOUtils.toString(fis, "UTF-8"));
			info(" XML document rebuilt");
		}catch(Exception e){
			error("unable build XML Document from file ", e);
			throw e;
		}
		
		Element root = document.getRootElement();
		
		Element rows =  (!root.getName().equals(ServiceConstants.XML_ROOT))? root.element(ServiceConstants.XML_ROOT) : root;

		if (rows == null){
			error(" unable to find rows Root");
			throw new Exception("unable to find rows Root");
		}
		rowIterator = rows.elements(ServiceConstants.XML_ROW).iterator();
		
		for(StreamElement e : transfo.getDescriptor(getTransformation()).getStreamElements()){
			elementColumnNames.add(e.name);
		}
		
		isInited = true;
		info(" inited");
	}

	@Override
	public void performRow() throws Exception {
		if (!rowIterator.hasNext()){
			setEnd();
			return;
		}
		
		Element current = rowIterator.next();
		
		Row row = RowFactory.createRow(this);
		
		for(int i = 0; i < row.getMeta().getSize(); i++){
			Element e = current.element(elementColumnNames.get(i));
			if (e != null){
				row.set(i, e.getText());
			}
		}
		readedRows++;
		
		
		writeRow(row);
	}

	@Override
	public void releaseResources() {
		rowIterator = null;
		document = null;
		try{
			fis.close();
		}catch(Exception ex){
			
		}
		info(" resources released");
	}
	
}
