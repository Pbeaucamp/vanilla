package bpm.gateway.runtime2.transformation.vanilla;

import java.util.Date;

import bpm.gateway.core.transformations.vanilla.VanillaGroupUser;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.UserGroup;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class RunVanillaGroup extends RuntimeStep{

	private IVanillaAPI vanillaApi;
	public RunVanillaGroup(IRepositoryContext repContext, VanillaGroupUser transformation, int bufferSize)throws Exception {
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
		User newUser = null;
		
		newGroup = new Group();
		Integer index = null; 
		
		VanillaGroupUser transfo = (VanillaGroupUser) getTransformation();

		Row newrow = RowFactory.createRow(this);
		/*
		 * Group
		 */
		if ((index = transfo.getMappingValueForThisNum(VanillaGroupUser.IX_GROUP_COMMENT)) != null){
			newGroup.setComment(row.get(index) == null ? null : "" + row.get(index));
		}
		newrow.set(VanillaGroupUser.IX_GROUP_COMMENT, newGroup.getComment());
		
		if ((index = transfo.getMappingValueForThisNum(VanillaGroupUser.IX_GROUP_IMAGE)) != null){
			newGroup.setImage(row.get(index) == null ? null : "" + row.get(index));
		}
		newrow.set(VanillaGroupUser.IX_GROUP_IMAGE, newGroup.getImage());
		
		if ((index = transfo.getMappingValueForThisNum(VanillaGroupUser.IX_GROUP_NAME)) != null){
			newGroup.setName(row.get(index) == null ? null : "" + row.get(index));
		}
		newrow.set(VanillaGroupUser.IX_GROUP_NAME, newGroup.getName());
		
		try{
			newGroup.setId(vanillaApi.getVanillaSecurityManager().addGroup(newGroup));
		}catch(Exception e){
			error(" unable to add Group ", e);
			throw e;
		}
		
		
		/*
		 * User
		 */
		newUser = new User();
		if ((index = transfo.getMappingValueForThisNum(VanillaGroupUser.IX_USER_BUSINESSMAIL)) != null){
			newUser.setBusinessMail(row.get(index) == null ? null : "" + row.get(index));
		}
		newrow.set(VanillaGroupUser.IX_USER_BUSINESSMAIL, newUser.getBusinessMail());
		
		if ((index = transfo.getMappingValueForThisNum(VanillaGroupUser.IX_USER_CELLULAR)) != null){
			newUser.setCellular(row.get(index) == null ? null : "" + row.get(index));
		}
		newrow.set(VanillaGroupUser.IX_USER_CELLULAR, newUser.getCellular());
		
		if ((index = transfo.getMappingValueForThisNum(VanillaGroupUser.IX_USER_FUNCTION)) != null){
			newUser.setFunction(row.get(index) == null ? null : "" + row.get(index));
		}
		newrow.set(VanillaGroupUser.IX_USER_FUNCTION, newUser.getFunction());
		
		if ((index = transfo.getMappingValueForThisNum(VanillaGroupUser.IX_USER_IMAGE)) != null){
			newUser.setImage(row.get(index) == null ? null : "" + row.get(index));
		}
		newrow.set(VanillaGroupUser.IX_USER_IMAGE, newUser.getImage());
		
		if ((index = transfo.getMappingValueForThisNum(VanillaGroupUser.IX_USER_ISSUPER)) != null){
			newUser.setSuperUser(row.get(index) == null ? null : "" + row.get(index));
		}
		newrow.set(VanillaGroupUser.IX_USER_ISSUPER, newUser.isSuperUser());
		
		if ((index = transfo.getMappingValueForThisNum(VanillaGroupUser.IX_USER_NAME)) != null){
			newUser.setName(row.get(index) == null ? null : "" + row.get(index));
		}
		newrow.set(VanillaGroupUser.IX_USER_NAME, newUser.getName());
		
		if ((index = transfo.getMappingValueForThisNum(VanillaGroupUser.IX_USER_PASSWORD)) != null){
			newUser.setPassword(row.get(index) == null ? null : "" + row.get(index));
			newUser.setDatePasswordModification(new Date());
		}
		newrow.set(VanillaGroupUser.IX_USER_PASSWORD, newUser.getPassword());
		
		if ((index = transfo.getMappingValueForThisNum(VanillaGroupUser.IX_USER_PHONE)) != null){
			newUser.setTelephone(row.get(index) == null ? null : "" + row.get(index));
		}
		newrow.set(VanillaGroupUser.IX_USER_PHONE, newUser.getTelephone());
		
		if ((index = transfo.getMappingValueForThisNum(VanillaGroupUser.IX_USER_PRIVATEMAIL)) != null){
			newUser.setPrivateMail(row.get(index) == null ? null : "" + row.get(index));
		}
		newrow.set(VanillaGroupUser.IX_USER_PRIVATEMAIL, newUser.getPrivateMail());
		
		if ((index = transfo.getMappingValueForThisNum(VanillaGroupUser.IX_USER_SKYPENAME)) != null){
			newUser.setSkypeName(row.get(index) == null ? null : "" + row.get(index));
		}
		newrow.set(VanillaGroupUser.IX_USER_SKYPENAME, newUser.getSkypeName());
		
		if ((index = transfo.getMappingValueForThisNum(VanillaGroupUser.IX_USER_SKYPENUMBER)) != null){
			newUser.setSkypeNumber(row.get(index) == null ? null : "" +row.get(index));
		}
		newrow.set(VanillaGroupUser.IX_USER_SKYPENUMBER, newUser.getSkypeNumber());
		
		if ((index = transfo.getMappingValueForThisNum(VanillaGroupUser.IX_USER_SURNAME)) != null){
			newUser.setSurname(row.get(index) == null ? null : "" + row.get(index));
		}
		newrow.set(VanillaGroupUser.IX_USER_SURNAME, newUser.getSurname());
		
		try{
			newUser.setId(vanillaApi.getVanillaSecurityManager().addUser(newUser));
		}catch(Exception e){
			error(" unable to add User ", e);
			throw e;
		}
		
		try{
			UserGroup ug = new UserGroup();
			ug.setUserId(newUser.getId());
			ug.setGroupId(newGroup.getId());
			vanillaApi.getVanillaSecurityManager().addUserGroup(ug);
			
		}catch(Exception e){
			error(" unable to add User in Group ", e);
			throw e;
		}
		writeRow(newrow);
	}

	@Override
	public void releaseResources() {
		info( " released");
		
	}

}
