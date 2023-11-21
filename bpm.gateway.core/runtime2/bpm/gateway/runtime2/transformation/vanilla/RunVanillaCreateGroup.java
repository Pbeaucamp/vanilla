package bpm.gateway.runtime2.transformation.vanilla;

import bpm.gateway.core.transformations.vanilla.VanillaCreateGroup;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class RunVanillaCreateGroup extends RuntimeStep{

	private IVanillaAPI vanillaApi;
	public RunVanillaCreateGroup(IRepositoryContext repContext, VanillaCreateGroup transformation, int bufferSize) throws Exception{
		super(repContext, transformation, bufferSize);
		if (getRepositoryContext() == null){
			throw new Exception("Cannot use a VanillaSecurity step without a VanillaContext. You must be connected to Vanilla.");
		}
	}

	@Override
	public void init(Object adapter) throws Exception {
		vanillaApi = new RemoteVanillaPlatform(getRepositoryContext().getVanillaContext());		
		info(" inited");
		
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
		
		Row row = readRow();
		
		
		Group newGroup = null;
		
		Integer index = null; 
		
		VanillaCreateGroup transfo = (VanillaCreateGroup) getTransformation();
		
		
		Row newrow = RowFactory.createRow(this);
		
		/*
		 * Group
		 */
		newGroup = new Group();
		if ((index = transfo.getMappingValueForThisNum(VanillaCreateGroup.IX_GROUP_COMMENT)) != null){
			newGroup.setComment(row.get(index) == null ? null : "" + row.get(index));
		}
		newrow.set(VanillaCreateGroup.IX_GROUP_COMMENT, newGroup.getComment());
		
		if ((index = transfo.getMappingValueForThisNum(VanillaCreateGroup.IX_GROUP_CUSTOM_1)) != null){
			newGroup.setCustom1(row.get(index) == null ? null : "" + row.get(index));
		}
		newrow.set(VanillaCreateGroup.IX_GROUP_CUSTOM_1, newGroup.getCustom1());
		
		if ((index = transfo.getMappingValueForThisNum(VanillaCreateGroup.IX_GROUP_IMAGE)) != null){
			newGroup.setImage(row.get(index) == null ? null : "" + row.get(index));
		}
		newrow.set(VanillaCreateGroup.IX_GROUP_IMAGE, newGroup.getImage());
		
		if ((index = transfo.getMappingValueForThisNum(VanillaCreateGroup.IX_GROUP_NAME)) != null){
			newGroup.setName(row.get(index) == null ? null : "" + row.get(index));
		}
		newrow.set(VanillaCreateGroup.IX_GROUP_NAME, newGroup.getName());
		
		if ((index = transfo.getMappingValueForThisNum(VanillaCreateGroup.IX_GROUP_PARENT_ID)) != null){
			newGroup.setParentId(row.get(index) == null ? null : "" + row.get(index));
		}
		newrow.set(VanillaCreateGroup.IX_GROUP_PARENT_ID, newGroup.getParentId());
		
		
		
		try{
			newGroup.setId(vanillaApi.getVanillaSecurityManager().addGroup(newGroup));
		}catch(Exception e){
			warn(" unable to add Group :" + e.getMessage() + " inputDatas=" + row.dump());
//			throw e;
		}
		
		
		writeRow(newrow);
	}

	@Override
	public void releaseResources() {
		info( " released");
		
	}

}
