package bpm.gateway.runtime2.transformation.mdm;

import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.transformations.mdm.MdmInput;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;
import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.Model;
import bpm.mdm.remote.MdmRemote;
import bpm.mdm.remote.StoreReader;
import bpm.vanilla.platform.core.IRepositoryContext;

public class MdmInputRuntime extends RuntimeStep{
	private StoreReader reader = null;
	private List<Attribute> attributes;
	public MdmInputRuntime(IRepositoryContext repContext, MdmInput transformation, int bufferSize) {
		super(repContext, transformation, bufferSize);
	}
	
	@Override
	public void init(Object adapter) throws Exception{
		
		MdmInput in = (MdmInput)getTransformation();

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
		Entity entity = null;
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
		
		attributes = new ArrayList<Attribute>(entity.getAttributes());
		try{
			info("Connecting to " + entity.getName() + " entity's Datas ....");
			reader = remote.createStoreReader(entity);
			reader.open();
			info("EntityReader on " + entity.getName() + " successfully opened");
		}catch(Exception ex){
			throw new Exception("Could not open EntityReader " + ex.getMessage(),ex);
		}
		
		isInited = true;

	}
	
	
	@Override
	public void performRow() throws Exception {
		if (reader == null){
			throw new Exception("No EntityReaer defined");
		}
		
		if (reader.hasNext()){
			Row row = RowFactory.createRow(this);
			bpm.mdm.model.runtime.Row read = reader.next();
			
			int count = 0;
			for(Attribute a : attributes){
				Object o = read.getValue(a);
				row.set(count++, o);
			}
			
			writeRow(row);
		}
		else{
			if (!areInputsAlive()){
				if (areInputStepAllProcessed()){
					if (inputEmpty()){
						setEnd();
					}
				}
			}
		}
	}

	

	@Override
	public void releaseResources() {
		if (reader != null){
			
			try {
				reader.close();
				reader = null;
				info(" closed reader");
			} catch (Exception e) {
				error(" error when closing resultSet", e);
			}
		}
	}
	
	

	
}
