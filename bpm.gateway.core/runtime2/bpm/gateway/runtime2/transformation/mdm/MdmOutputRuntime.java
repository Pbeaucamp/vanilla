package bpm.gateway.runtime2.transformation.mdm;

import java.util.HashMap;

import bpm.gateway.core.Trashable;
import bpm.gateway.core.transformations.mdm.MdmOutput;
import bpm.gateway.core.transformations.outputs.IOutput;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;
import bpm.mdm.model.Entity;
import bpm.mdm.model.Model;
import bpm.mdm.model.runtime.RuntimeFactory;
import bpm.mdm.model.runtime.exception.AbstractRowException;
import bpm.mdm.model.runtime.exception.RowsExceptionHolder;
import bpm.mdm.model.storage.IEntityStorage;
import bpm.mdm.remote.MdmRemote;
import bpm.vanilla.platform.core.IRepositoryContext;

public class MdmOutputRuntime extends RuntimeStep{
	private IEntityStorage store;
	private Entity entity;
	protected RuntimeStep errorHandler;
	private boolean updateExisting = false; 
	/*
	 * matching mdmRows with gatewayRow to know where to write flushed/error rows
	 */
	private HashMap<bpm.mdm.model.runtime.Row, Row> currentBatchedRows = new HashMap<bpm.mdm.model.runtime.Row, Row>();
	
	public MdmOutputRuntime(IRepositoryContext repContext, MdmOutput transformation, int bufferSize) {
		super(repContext, transformation, bufferSize);
	}
	
	@Override
	public void init(Object adapter) throws Exception{
		
		MdmOutput in = (MdmOutput)getTransformation();
		updateExisting = in.isUpdateExisting();
		
		//detect errorHandler transformation
		if (in.getTrashTransformation() != null){
			for(RuntimeStep rs : getOutputs()){
				if (rs.getTransformation() == in.getTrashTransformation()){
					errorHandler = rs;
					break;
				}
			}
		}
		
		//get IEntityStore
		
		MdmRemote remote = new MdmRemote(
				getRepositoryContext().getVanillaContext().getLogin(), 
				getRepositoryContext().getVanillaContext().getPassword(), 
				getRepositoryContext().getVanillaContext().getVanillaUrl(), null, null);
		
		
		Model model = null;
		try{
			info("Loading Mdm model....");
			model = remote.loadModel();
			info("Mdm model successfully loaded");
		}catch(Exception ex){
			throw new Exception("Unable to load Mdm model " + ex.getMessage(), ex);
		}
		info("Looking for Mdm's Entity " + in.getEntityUuid() + "...");
		for(Entity e : model.getEntities()){
			if (e.getUuid().equals(in.getEntityUuid())){
				entity = e;
				break;
			}
		}
		
		if (entity == null){
			throw new Exception("Unable to find the entity with uuid=" + in.getEntityUuid());
		}
		info("Found Mdm's Entity " + entity.getName() );
		
		
		try{
			info("Connecting to " + entity.getName() + " entity's Datas ....");
			
			store = remote.getStore(entity);
			info("EntityStore on " + entity.getName() + " successfully opened");
		}catch(Exception ex){
			throw new Exception("Could not open EntityReader " + ex.getMessage(),ex);
		}
		
		isInited = true;

	}
	
	@Override
	protected void writeRow(Row row) throws InterruptedException{
		for(RuntimeStep r : getOutputs()){
			if (r != errorHandler){
				r.insertRow(row, this);
				
			}
		}
		writedRows++;
	}
	
	protected void writeErrorRow(Row row) throws InterruptedException{
		if (errorHandler != null){
			errorHandler.insertRow(row, this);
			writedRows++;
		}
	}
	@Override
	public void performRow() throws Exception {
		if (areInputStepAllProcessed()){
			if (inputEmpty()){
				setEnd();
			}
		}
		
		if (isEnd() && inputEmpty()){
			return;
		}
		
		if (!isEnd() && inputEmpty()){
			try{
				Thread.sleep(10);
				return;
			}catch(Exception e){
				
			}
		}
		
		
		IOutput mapper = (IOutput)getTransformation();
		
		
		Row currentRow = null;
		try{
			currentRow = readRow();
		}catch(Exception e){
			return;
		}
		
		Row newRow = RowFactory.createRow(this);
		bpm.mdm.model.runtime.Row mdmRow = RuntimeFactory.eINSTANCE.createRow();
		for(RuntimeStep rs : inputs){
			//setting the actual rowValue
			for(int i = 0; i < entity.getAttributes().size(); i++){
				Integer rowIndice = mapper.getMappingValueForThisNum(rs.getTransformation(), i);
				if (rowIndice != null){
					newRow.set(i, currentRow.get(rowIndice));
					mdmRow.setValue(entity.getAttributes().get(i), newRow.get(i));
				}
			}
		}
		//check update requirement
		if (updateExisting){
			if (store.lookup(mdmRow) != null){
				store.updateRow(mdmRow);
			}
			else{
				store.createRow(mdmRow);
			}
		}
		else{
			store.createRow(mdmRow);
		}
		
		
		
		currentBatchedRows.put(mdmRow, newRow);
		
		if (currentBatchedRows.size() >= getBufferSize()){
			flushStore();
		}
	}
	
	private void flushStore() throws Exception{
		try{
			store.flush();
		}catch(RowsExceptionHolder ex){
			if (errorHandler == null){
				throw new Exception("Some rows have not been flushed correctly and there is no errorHandler attached to this step", ex);
			}
			
			for(AbstractRowException e : ex.getErrors()){
				Row r = currentBatchedRows.get(e.getRow());
				writeErrorRow(r);
				currentBatchedRows.remove(e.getRow());
				
			}
						
		}catch(Exception ex){
			throw ex;
		}finally{
			for(Row r : currentBatchedRows.values()){
				writeRow(r);
			}
			currentBatchedRows.clear();
		}
	}

	@Override
	public void releaseResources() {
		currentBatchedRows.clear();
		currentBatchedRows = null;
		
	}

	@Override
	protected synchronized void setEnd() {
		try {
			flushStore();
		} catch (Exception e) {
			e.printStackTrace();
			error(" error on last batch execution ", e);
		}
		super.setEnd();
	}
}
