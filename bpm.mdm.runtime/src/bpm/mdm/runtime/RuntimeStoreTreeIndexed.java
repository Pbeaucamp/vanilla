package bpm.mdm.runtime;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.Rule;
import bpm.mdm.model.rules.impl.EntityLinkRuleImpl;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.runtime.exception.AbstractRowException;
import bpm.mdm.model.runtime.exception.AttributeDefaultValueException;
import bpm.mdm.model.runtime.exception.AttributeTypeException;
import bpm.mdm.model.runtime.exception.NonExistingChunkFileException;
import bpm.mdm.model.runtime.exception.NullAttributeException;
import bpm.mdm.model.runtime.exception.PrimaryKeyException;
import bpm.mdm.model.runtime.exception.RuleException;
import bpm.mdm.model.runtime.exception.AbstractRowException.OperationType;
import bpm.mdm.model.storage.EntityStorageStatistics;
import bpm.mdm.model.storage.IEntityStorage;
import bpm.mdm.model.util.RowUtil;
import bpm.mdm.runtime.serializers.RuntimeSerializer;
import bpm.mdm.runtime.serializers.index.IndexNode;
import bpm.mdm.runtime.serializers.index.IndexWalker;

public class RuntimeStoreTreeIndexed implements IEntityStorage{
	private static final int autoFlushRowNumber = 2000;
	
	private static HashMap<String, Semaphore> locks = new HashMap<String, Semaphore>();
	private Entity entity;
	
	
	
	private HashMap<IndexNode, List<Row>> rowsToUpdate = new HashMap<IndexNode, List<Row>>();
	private List<Row> toAdd = new ArrayList<Row>();
	private HashMap<IndexNode, List<Row>> toDelete = new HashMap<IndexNode, List<Row>>();
	
	private boolean hasRowToFlush = false;
	
	private RuntimeSerializer runtimeSerializer;
	private RowDataChecker dataChecker;
	
	public RuntimeStoreTreeIndexed(RuntimeSerializer runtimeSerializer/*, MdmConfiguration configuration*/, Entity e) {
		this.runtimeSerializer = runtimeSerializer;
		//this.config = configuration;
		this.entity = e;
		synchronized (locks) {
			if (locks.get(e.getUuid()) == null){
				locks.put(e.getUuid(), new Semaphore(1));
			}
		}
	}
	
	public void acquire() throws Exception{
		hasRowToFlush = false;
		locks.get(entity.getUuid()).acquire();
		toAdd.clear();
		toDelete.clear();
		rowsToUpdate.clear();
		dataChecker = new RowDataChecker(entity, this);
	}
	
	public void release() throws Exception{
		dataChecker = null;
		rowsToUpdate.clear();
		toAdd.clear();
		toDelete.clear();
		locks.get(entity.getUuid()).release();
	}

	
	private List<Row> loadCurrentRows(int chunkNumber) throws Exception{
		return runtimeSerializer.getReader(entity).readStreamRows(chunkNumber);
	}
	
	
	@Override
	public void cancel() throws Exception {
		
		Logger.getLogger(getClass()).debug("Entity " + entity.getName() + " re-loaded");
	}
	
	
	private IndexNode getIndexNode(Row row){
		
		try{
			String key = RowUtil.generateUUID(entity, row);
			
			IndexNode node = IndexWalker.lookup(runtimeSerializer.getIndexTree(entity), key);
			return node;
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
		
	
	}
	


	@Override
	public void createRow(Row row) throws Exception {
		
		
		try{
			
			setDefaultAttributes(row, OperationType.CREATE);
			boolean keyMatch = getIndexNode(row) != null;
			if (keyMatch){
				throw new PrimaryKeyException(entity, row, OperationType.CREATE);
			}
			String id = RowUtil.generateUUID(entity, row) ;
			for(Row r : toAdd){
				String id1 = RowUtil.generateUUID(entity, r);
				if (id.equals(id1)){
					throw new PrimaryKeyException(entity, row, OperationType.CREATE); 
				}
			}
			
			
			//runtimeSerializer.getIndexTree(entity).dump();
//			for(Attribute a : entity.getAttributes()){
//				checkAttributeValue(a, row, OperationType.CREATE);
//			}
			dataChecker.checkRow(row, OperationType.CREATE);
		}catch(NonExistingChunkFileException ex){
			
		}finally{

		}
		toAdd.add(row);
		//Logger.getLogger(getClass()).debug("Row added for entity " + entity.getName());
		hasRowToFlush = true;
		checkAutoFlush();
	}

	@Override
	public void deleteRow(Row row) throws Exception {
	
		try{
			IndexNode node = getIndexNode(row);
			if (node == null){
				Logger.getLogger(getClass()).debug("No matching rows within the index");
				return;
			}
			
			setDefaultAttributes(row, OperationType.DELETE);

			if (toDelete.get(node) == null){
				toDelete.put(node, new ArrayList<Row>());
			}
			toDelete.get(node).add(row);
		}catch(NonExistingChunkFileException ex){
			
		}finally{

		}

		Logger.getLogger(getClass()).debug("Row added for entity " + entity.getName());
		hasRowToFlush = true;
		checkAutoFlush();
	}

	@Override
	public void flush() throws Exception {
		if (!hasRowToFlush){
			Logger.getLogger(getClass()).debug("No row to flush");
			return;
		}

		synchronized (runtimeSerializer.getIndexTree(entity)) {
			runtimeSerializer.getWriter(entity).writeRows(toAdd);
			runtimeSerializer.getWriter(entity).replaceRowsIndexed(rowsToUpdate);
			runtimeSerializer.getWriter(entity).deleteRowsTree(toDelete);
			runtimeSerializer.serializeIndex(entity);
			
		}
		
		
	}

	@Override
	public List<Row> getRows(int chunkNumber) throws NonExistingChunkFileException, Exception {
		return runtimeSerializer.getReader(entity).readStreamRows(chunkNumber);
	}

	@Override
	public void updateRow(Row row) throws Exception {
		try{
			IndexNode node = getIndexNode(row);
			if (node == null){
				Logger.getLogger(getClass()).debug("No row to update");
				return;
			}
			
			
			setDefaultAttributes(row, OperationType.UPDATE);
			
			for(Attribute a : entity.getAttributes()){
				if (!a.isId()){
					//checkAttributeValue(a, row, OperationType.UPDATE);
					dataChecker.checkAttributeValue(a, row, OperationType.UPDATE);
				}
			}
			List<Row> currentRows = loadCurrentRows(node);
			Row toUpdate = getMatchingRow(currentRows, row);
			if (toUpdate != null ){
				if (rowsToUpdate.get(node) == null){
					rowsToUpdate.put(node, new ArrayList<Row>());
					
				}
				rowsToUpdate.get(node).add(row);
				hasRowToFlush = true;
			}
		}finally{
		}
		checkAutoFlush();
	}
	
	
	private void checkAutoFlush() throws Exception{
		long sz = toAdd.size();
		for(List<Row> l : rowsToUpdate.values()){
			sz += l.size();
		}
		
		for(List<Row> l : toDelete.values()){
			sz += l.size();
		}
		
		if (sz >= autoFlushRowNumber){
			Logger.getLogger(getClass()).info("AutoFlushSize fetched, Flush launched...");
			flush();
			rowsToUpdate.clear();
			toAdd.clear();
			toDelete.clear();
		}
		
	}
	
	private void setDefaultAttributes(Row row, OperationType type)throws Exception{
		for(Attribute attribute : entity.getAttributes()){
			if (!attribute.isNullAllowed() && row.getValue(attribute)  == null){
				
				
				Class<?> cl = attribute.getDataType().getJavaClass();
				if (Date.class.isAssignableFrom(cl)){
					
				}
				else{
					try{
						//we try to set the default value from the attribute definition
						Constructor<?> ct = cl.getConstructor(String.class);
						row.setValue(attribute, ct.newInstance(attribute.getDefaultValue()));
					}catch(Exception ex){
						Logger.getLogger(getClass()).error("Unable to apply defaultAtributeValue on " + entity.getName() + " " + attribute.getName() + " because " + ex.getMessage(), ex);
						throw new AttributeDefaultValueException(entity, attribute, row, type);
					}
				}
			}
		}
		
	}

//	/**
//	 * extract the value from the row for the given attribute
//	 * if every conditions are satisfied, nothing happens,
//	 * otherwise, an exception will be thrown
//	 * 
//	 * the attribute properties are checked, then the attribte's value DataType
//	 * is verified, and finally, all the actives rules are validated
//	 * 
//	 * this method should be use for each attribute when performing a writing
//	 * operation on an entity row(create and upate operations)
//	 * 
//	 * @param attribute
//	 * @param row
//	 * @throws Exception
//	 */
//	private void checkAttributeValue(Attribute attribute, Row row, OperationType type) throws Exception{
//		Object value = row.getValue(attribute);
//		
//		//check attribute type
//		if (value != null){
//			Class<?> cl = attribute.getDataType().getJavaClass();
//			try{
//				cl.cast(value);
//			}catch(ClassCastException ex){
//				Constructor<?> constructor = cl.getConstructor(String.class);
//				if (constructor != null){
//					try{
//						constructor.newInstance(value.toString());
//					}catch(Exception ex2){
//						throw new AttributeTypeException(entity, attribute, cl, value, row, type);
//					}
//				}
//				
//			}
//			
//		}
//
//		//check attribute options
//		if (!attribute.isNullAllowed() && value == null){
//			//try to apply default value
//			if (attribute.getDefaultValue() == null){
//				throw new NullAttributeException(attribute, row,type);
//			}
//			
//		}
//		
//		//check rules
//		for(Rule r : attribute.getRules()){
//			if (r.isActive()){
//				if (!r.evaluate(value)){
//					throw new RuleException(r, row, type);
//				}
//			}
//		}
//	}

	@Override
	public Row lookup(Row row) throws Exception {

		Row looked = null;
		try{
			IndexNode node = getIndexNode(row);
			if (node == null){
				return null;
			}
			List<Row> currentRows = loadCurrentRows(node);
			Row r = getMatchingRow(currentRows, row);
			return r;
		}catch (NonExistingChunkFileException e) {
			
		}finally{
		}
		
		return looked;
	}

	@Override
	public OperationType getType(Row row) {
		
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void cancel(Row row) {
		throw new RuntimeException("Not implemented");
		
	}
	
	/**
	 * return the rows from the currentOnes  having the same primary key
	 * should only return one at maximum
	 * 
	 * @param row
	 * @return
	 */
	
	private Row getMatchingRow(List<Row> currentRows, Row row){
		List<Attribute> primaryKeyAttributes = entity.getAttributesId();
		if (primaryKeyAttributes.isEmpty()){
			return null;
		}
		
		for(Row r : currentRows){
			boolean match = true;
			for(Attribute a : primaryKeyAttributes){
				Object rowVal = row.getValue(a);
				Object curVal = r.getValue(a);
				
				if (rowVal == null){
					if (curVal != null){
						match = false;
						break;
					}
				}
				if (curVal == null && rowVal != null){
					match = false;
					break;
				}
				
				if (!rowVal.equals(curVal)){
					match = false;
					break;
				}
			}
			if (match){
				return r;
			}
		}
		return null;
	}
	
	private List<Row> loadCurrentRows(IndexNode node) throws Exception{
		return runtimeSerializer.getReader(entity).readStreamRows(node);
	}

	@Override
	public EntityStorageStatistics getStorageStatistics() throws Exception {
		return IndexWalker.gatherStatistics(runtimeSerializer.getIndexTree(entity));
	}

	@Override
	public List<Row> getInvalidRows() throws Exception {
		List<Row> l = new ArrayList<Row>();
		
		RowDataChecker checker = new RowDataChecker(entity, this);
		RuntimeEntityReader reader = new RuntimeEntityReader(this, entity);
		reader.open();
		try{
			while(reader.hasNext()){
				Row r = reader.next();
				try{
					checker.checkRow(r, null);
				}catch(AbstractRowException ex){
					l.add(r);
				}
			}
		}finally{
			reader.close();
		}
		
		return l;
	}
}
