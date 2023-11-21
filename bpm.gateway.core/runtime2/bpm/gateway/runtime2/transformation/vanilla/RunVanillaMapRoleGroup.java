package bpm.gateway.runtime2.transformation.vanilla;

import java.util.List;

import bpm.gateway.core.transformations.vanilla.VanillaRoleGroupAssocaition;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Role;
import bpm.vanilla.platform.core.beans.RoleGroup;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class RunVanillaMapRoleGroup extends RuntimeStep{

	private IVanillaAPI vanillaApi;
	private Integer groupIndex;
	private List<Integer> roles;
	
	public RunVanillaMapRoleGroup(IRepositoryContext repContext,VanillaRoleGroupAssocaition transformation, int bufferSize) throws Exception{
		super(repContext, transformation, bufferSize);
		if (getRepositoryContext() == null){
			throw new Exception("Cannot use a VanillaSecurity step without a VanillaContext. You must be connected to Vanilla.");
		}
	}

	@Override
	public void init(Object adapter) throws Exception {
		VanillaRoleGroupAssocaition transfo = (VanillaRoleGroupAssocaition) getTransformation();
	
		vanillaApi = new RemoteVanillaPlatform(getRepositoryContext().getVanillaContext());
		
		groupIndex = transfo.getGroupIdIndex();
		
		if (groupIndex == null){
			error("GroupIndex not defined");
			throw new Exception("GroupIndex not defined");
		}
		roles = transfo.getRolesId();
		
		
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
		
		
		Integer groupId = Integer.valueOf(row.get(groupIndex ).toString());
		
		Group group = vanillaApi.getVanillaSecurityManager().getGroupById(groupId);
		if (group == null){
			warn("No group with id=" + groupId);
			return;
		}
		for(Integer i : roles){
			Role role = vanillaApi.getVanillaSecurityManager().getRoleById(i);
			if (role == null){
				warn("No Role with id=" + i);
				continue;
			}
			RoleGroup rg = new RoleGroup();
			rg.setRoleId(role.getId());
			rg.setGroupId(group.getId());
			vanillaApi.getVanillaSecurityManager().addRoleGroup(rg);
			
			Row newrow = RowFactory.createRow(this);
			newrow.set(0, role.getId());
			newrow.set(1, group.getId());
			
			writeRow(newrow);
		}
		
		
		
		
		
	}

	@Override
	public void releaseResources() {
		info( " released");
		
	}

}
