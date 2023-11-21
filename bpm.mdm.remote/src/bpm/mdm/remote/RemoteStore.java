package bpm.mdm.remote;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.runtime.exception.AbstractRowException;
import bpm.mdm.model.runtime.exception.NonExistingChunkFileException;
import bpm.mdm.model.runtime.exception.RowsExceptionHolder;
import bpm.mdm.model.runtime.exception.AbstractRowException.OperationType;
import bpm.mdm.model.serialisation.DatasSerializer;
import bpm.mdm.model.serialisation.MdmConfiguration;
import bpm.mdm.model.storage.EntityStorageStatistics;
import bpm.mdm.model.storage.IEntityStorage;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteStore implements IEntityStorage{
	private List<Row> toAdd = new ArrayList<Row>();
	private List<Row> toUpdate= new ArrayList<Row>();
	private List<Row> toDelete= new ArrayList<Row>();
	
	private HttpRemote http;
	private Entity entity;
	private XStream xstream;
	
	protected RemoteStore(Entity entity, HttpRemote http, XStream xstream){
		this.entity = entity;
		this.http = http;
		this.xstream = xstream;

	}
	
	@Override
	public void cancel() throws Exception {
		toDelete.clear();
		toUpdate.clear();
		toAdd.clear();
		
	}

	@Override
	public void createRow(Row row) throws Exception {
		toAdd.add(row);
		
	}

	@Override
	public void deleteRow(Row row) throws Exception {
		toDelete.add(row);
		
	}

	private List<AbstractRowException> getExceptions(String xmlResult) throws Exception{
		List<AbstractRowException> rowsErrors = new ArrayList<AbstractRowException>();
		if (xmlResult != null && !xmlResult.isEmpty()){
			Object o = xstream.fromXML(xmlResult);
			if (o instanceof List){
				
				for(Object e : (List)o){
					if (e instanceof AbstractRowException){
						rowsErrors.add((AbstractRowException)e);
					}
				}
				
			}
		}
		return rowsErrors;
	}
	
	
	@Override
	public void flush() throws Exception {
		
		List<AbstractRowException> rowsErrors = new ArrayList<AbstractRowException>();
		
//		remote.addRows(toAdd);
//		remote.deleteRows(toDelete);
//		remote.updateRows(toUpdate);
		
		//save
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DatasSerializer s = new DatasSerializer(new MdmConfiguration());
		
		if (!toAdd.isEmpty()){
			s.saveDatas(entity, s.convertToSerializable(entity, toAdd), bos);
			byte[] bytes = Base64.encodeBase64(bos.toByteArray());
			
			XmlAction op = new XmlAction(createArguments(entity.getName(), bytes), 
					IMdmProvider.ActionType.SAVE_DATAS);
			
			String res = null;
			try{
				res = http.executeDatasAction(xstream.toXML(op));
				
			}catch(Exception ex){
				throw new Exception("Communication error : " + ex.getMessage(), ex);
			}
			
			List<AbstractRowException> rowExceptions = getExceptions(res);			
			rowsErrors.addAll(rowExceptions);
			
			List<Row> failedRows = retrieveFailedRows(toAdd, rowExceptions);
			
			toAdd.clear();
			toAdd.addAll(failedRows);
		}
		
		
		//update
		if (!toUpdate.isEmpty()){
			bos = new ByteArrayOutputStream();
			s.saveDatas(entity, s.convertToSerializable(entity, toUpdate), bos);
			byte[] bytes = Base64.encodeBase64(bos.toByteArray());
			
			XmlAction op = new XmlAction(createArguments(entity.getName(), bytes), 
					IMdmProvider.ActionType.UPDATE_DATAS);
			String res = null;
			try{
				res = http.executeDatasAction(xstream.toXML(op));
			}catch(Exception ex){
				throw new Exception("Communication error : " + ex.getMessage(), ex);
			}
			
			List<AbstractRowException> rowExceptions = getExceptions(res);			
			rowsErrors.addAll(rowExceptions);
			
			List<Row> failedRows = retrieveFailedRows(toUpdate, rowExceptions);
			toUpdate.clear();
			toUpdate.addAll(failedRows);
		}
		
		
		//delete
		if (!toDelete.isEmpty()){
			bos = new ByteArrayOutputStream();
			s.saveDatas(entity, s.convertToSerializable(entity, toDelete), bos);
			byte[] bytes = Base64.encodeBase64(bos.toByteArray());
			
			XmlAction op = new XmlAction(createArguments(entity.getName(), bytes), 
					IMdmProvider.ActionType.DELETE_DATAS);
			String res = null;
			try{
				res = http.executeDatasAction(xstream.toXML(op));
			}catch(Exception ex){
				throw new Exception("Communication error : " + ex.getMessage(), ex);
			}
			
			List<AbstractRowException> rowExceptions = getExceptions(res);			
			rowsErrors.addAll(rowExceptions);
			
			List<Row> failedRows = retrieveFailedRows(toDelete, rowExceptions);
			
			toDelete.clear();
			toDelete.addAll(failedRows);
		}
		if (!rowsErrors.isEmpty()){
			throw new RowsExceptionHolder(rowsErrors);
		}
	}

	/**
	 * 
	 * @param flushed : flushed rows (rows given to toAdd,toUpdate or toDelete
	 * @param rowExceptions : each rows that flush failed
	 * @return retrieve the rows from the flushed list that match to all rowExeptions
	 * 
	 * it is done to retrieve the failed from the input after having been 
	 * sent back by the server
	 * 
	 */
	// TODO  : a better way be to associate a identifier to each sent rows
	// then retore them for their id, it should be quicker
	private List<Row> retrieveFailedRows(List<Row> flushed,
			List<AbstractRowException> rowExceptions) {
		
		List<Row> failedRows = new ArrayList<Row>();
		for(Row r : flushed){
			for(AbstractRowException e : rowExceptions){
				boolean equals = true;
				for(Attribute a : entity.getAttributes()){
					if (e.getRow().getValue(a) == null && r.getValue(a) != null){
						equals = false;
						break;
					}
					else if (r.getValue(a) == null && e.getRow().getValue(a) != null){
						if (!a.isNullAllowed()){
							continue;
						}
						equals = false;
						break;
						
					}
					else if (r.getValue(a) != null && e.getRow().getValue(a) != null && !r.getValue(a).equals(e.getRow().getValue(a))){
						
						equals = false;
						break;
					}
				}
				if (equals){
					failedRows.add(r);
					e.setRow(r);
				}
			}
		}
		return failedRows;
	}

	@Override
	public List<Row> getRows(int chunkNumber) throws NonExistingChunkFileException, Exception {
		
		
		XmlAction op = new XmlAction(createArguments(entity.getName(), chunkNumber), 
				IMdmProvider.ActionType.GET_ENTITY_DATAS);
		
		String xml = http.executeDatasAction(xstream.toXML(op));
		
		if (xml.isEmpty()){
			return new ArrayList<Row>();
		}
		
		
		byte[] bytes = (byte[])xml.getBytes();
		
		if (bytes == null || bytes.length == 0){
			throw new NonExistingChunkFileException("Chunk " + chunkNumber + " does not exist on " + entity.getName());
		}
		
		DatasSerializer s = new DatasSerializer(new MdmConfiguration());
		List<HashMap<String,Serializable>> l = s.loadDatas(entity, new ByteArrayInputStream(Base64.decodeBase64(bytes)));
		
		
		
		return s.convert(entity, l);
	}


	@Override
	public void updateRow(Row row) throws Exception {
		toUpdate.add(row);
		
	}
	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	@Override
	public Row lookup(Row row) throws Exception {
		HashMap<String, Object> primaryKey = new HashMap<String, Object>();
		
		for(Attribute a : entity.getAttributesId()){
			primaryKey.put(a.getUuid(), row.getValue(a));
		}
		
		if (primaryKey.size() == 0){
			throw new Exception("Cannot perform a lookup on the Entity " + entity.getName() + " because it has no Identifier Attribute");
		}
		
		XmlAction op = new XmlAction(createArguments(entity.getUuid(), primaryKey), 
				IMdmProvider.ActionType.LOOKUP);
		
		String xml = http.executeDatasAction(xstream.toXML(op));
		
		if (xml.isEmpty()){
			return null;
		}
		
		
		byte[] bytes = (byte[])xml.getBytes();
		
		DatasSerializer s = new DatasSerializer(new MdmConfiguration());
		List<HashMap<String,Serializable>> l = s.loadDatas(entity, new ByteArrayInputStream(Base64.decodeBase64(bytes)));
		
		
		
		List<Row> r =  s.convert(entity, l);
		return r.get(0);
	}

	@Override
	public OperationType getType(Row row) {
		if (toAdd.contains(row)){
			return OperationType.CREATE;
		}
		if (toDelete.contains(row)){
			return OperationType.DELETE;
		}
		if (toUpdate.contains(row)){
			return OperationType.UPDATE;
		}
		return null;
	}

	@Override
	public void cancel(Row row) {
		toAdd.remove(row);
		toDelete.remove(row);
		toUpdate.remove(row);
		
	}

	@Override
	public EntityStorageStatistics getStorageStatistics() throws Exception {
		XmlAction op = new XmlAction(createArguments(entity.getName()), 
				IMdmProvider.ActionType.GET_STORE_STATISTICS);
		
		String res = null;
		try{
			res = http.executeDatasAction(xstream.toXML(op));
			
		}catch(Exception ex){
			throw new Exception("Communication error : " + ex.getMessage(), ex);
		}
		Object o = xstream.fromXML(res);
		if (o instanceof Exception){
			throw (Exception)o;
		}
		
		return (EntityStorageStatistics)o;
	}

	@Override
	public List<Row> getInvalidRows() throws Exception {
		XmlAction op = new XmlAction(createArguments(entity.getUuid()), 
				IMdmProvider.ActionType.GET_INVALID_ROWS);
		
		String xml = http.executeDatasAction(xstream.toXML(op));
		
		if (xml.isEmpty()){
			return null;
		}
		
		
		byte[] bytes = (byte[])xml.getBytes();
		
		DatasSerializer s = new DatasSerializer(new MdmConfiguration());
		List<HashMap<String,Serializable>> l = s.loadDatas(entity, new ByteArrayInputStream(Base64.decodeBase64(bytes)));
		List<Row> r =  s.convert(entity, l);
		return r;
	}
	
}
