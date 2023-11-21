package bpm.mdm.remote;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import bpm.mdm.model.Entity;
import bpm.mdm.model.api.IEntityReader;
import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.serialisation.DatasSerializer;
import bpm.mdm.model.serialisation.MdmConfiguration;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class StoreReader implements IEntityReader{
	private int chunkNumber = -1;
	private List<Row> rows;
	private Iterator<Row> iterator;
	
	private HttpRemote http;
	private Entity entity;
	private XStream xstream;
	
	protected StoreReader(Entity entity, HttpRemote http, XStream xstream){
		this.entity = entity;
		this.http = http;
		this.xstream = xstream;

	}
	
	public void open() throws Exception{
		if (chunkNumber >= 0){
			throw new Exception("Cannot open a StoreReader more tahn once");
		}
		readNextChunk();
	}
	
	private void isOpen() throws Exception{
		if (rows == null || iterator == null){
			throw new Exception("The reader has not been opened");
		}
	}
	
	public boolean hasNext() throws Exception{
		isOpen();
		if (chunkNumber < 0){
			return false;
		}
		if (!iterator.hasNext()){
			try {
				return readNextChunk();
			} catch (Exception e) {
				Logger.getLogger(getClass()).warn(e.getMessage(), e);
				releaseResources();
				return false;
			}
		}
		
		return true;
		
		
	}
	
	public Row next() throws Exception{
		isOpen();
		return iterator.next();
	}
	
	private void releaseResources(){
		chunkNumber = -1;
		
		if (rows != null){
			rows.clear();
		}
		rows = null;
		iterator = null;
	}
	
	private boolean readNextChunk() throws Exception{
		chunkNumber++;
		XmlAction op = new XmlAction(createArguments(entity.getName(),chunkNumber), 
				IMdmProvider.ActionType.GET_ENTITY_DATAS);
		
		String xml = http.executeDatasAction(xstream.toXML(op));
		
		if (xml.isEmpty()){
			//releaseResources();
			
			return false;
		}
		
		
		byte[] bytes = (byte[])xml.getBytes();
		
		DatasSerializer s = new DatasSerializer(new MdmConfiguration());
		List<HashMap<String,Serializable>> l = s.loadDatas(entity, new ByteArrayInputStream(Base64.decodeBase64(bytes)));
		
		
		
		rows =  s.convert(entity, l);
		iterator = rows.iterator();
		return true;

	}
	
	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	@Override
	public void close() throws Exception {
		isOpen();
		releaseResources();
		
	}
}
