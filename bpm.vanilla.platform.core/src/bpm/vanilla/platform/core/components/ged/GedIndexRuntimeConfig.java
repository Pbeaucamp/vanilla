package bpm.vanilla.platform.core.components.ged;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.vanilla.platform.core.beans.ged.IComProperties;

/**
 * A runtime config to index a file
 * 
 * @author Marc Lanquetin
 *
 */
public class GedIndexRuntimeConfig {

	private IComProperties comProps; //ged props
	
	private int 	userId;
	private List<Integer> groupIds = new ArrayList<Integer>();
	private int 	groupId;
	private int 	repositoryId;
	private String 	format;
	private Integer existingDocId;
	private int version;
	private Date peremptionDate;
	
	private boolean isMdmAttached = false;
	
	public GedIndexRuntimeConfig() {
	}
	
	/**
	 * 
	 * @param comProps
	 * @param itemId
	 * @param userId
	 * @param groupId
	 * @param groupIds
	 * @param repositoryId
	 * @param format
	 * @param existingDocId If the document already exists, set it to null instead.
	 */
	public GedIndexRuntimeConfig(IComProperties comProps, int userId, int groupId, List<Integer> groupIds, 
			int repositoryId, String format, Integer existingDocId, int version) {
		super();
		this.comProps = comProps;
		this.userId = userId;
		this.groupId = groupId;
		if (groupIds == null){
			groupIds = new ArrayList<Integer>();
		}
		else{
			this.groupIds = groupIds;
		}
		
		this.repositoryId = repositoryId;
		this.format = format;
		this.setExistingDocId(existingDocId);
		this.version = version;
	}

	public String getFormat() {
		return format;
	}
	
	public IComProperties getComProps() {
		return comProps;
	}

	public int getUserId() {
		return userId;
	}

	public int getGroupId(){
		return groupId;
	}
	
	/**
	 * return the VanillaGroups Ids for wich we want to give access
	 */
	public List<Integer> getGroupIds() {
		if (groupIds == null){
			return new ArrayList<Integer>();
		}
		return groupIds;
	}

	public int getRepositoryId() {
		return repositoryId;
	}

	public void setExistingDocId(Integer existingDocId) {
		this.existingDocId = existingDocId;
	}

	public Integer getExistingDocId() {
		return existingDocId;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getVersion() {
		return version;
	}

	public void setPeremptionDate(Date peremptionDate) {
		this.peremptionDate = peremptionDate;
	}

	public Date getPeremptionDate() {
		return peremptionDate;
	}

	public boolean isMdmAttached() {
		return isMdmAttached;
	}

	public void setMdmAttached(boolean isMdmAttached) {
		this.isMdmAttached = isMdmAttached;
	}
}
