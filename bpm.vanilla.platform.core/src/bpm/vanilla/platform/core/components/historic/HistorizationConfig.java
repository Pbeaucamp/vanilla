package bpm.vanilla.platform.core.components.historic;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.components.IRuntimeConfig;

/**
 * A configuration class to perform an Historization.
 * 
 * An Historization contains differents informations: 
 *  - ObjectIdentifier : the report model identifier for which we will create an Historic
 *  -  HistorizationTarget : the type of target that will be use to create the entry
 *  - Ids : ids of group or user that will be associated to the create entrie(s)
 * @author ludo
 *
 */
public class HistorizationConfig implements IRuntimeConfig{
	public enum HistorizationTarget{
		User, Group
	}
	
	private HistorizationTarget targetType = HistorizationTarget.Group;
	private List<Integer> targetIds = new ArrayList<Integer>();
	private IObjectIdentifier identifier;
	private Integer performerGroupId;
	private String entryName;
	private String entryFormat;
	public HistorizationConfig(HistorizationTarget targetType,
			IObjectIdentifier identifier, Integer performerGroupId, String entryName, String entryFormat) {
		super();
		this.targetType = targetType;
		this.identifier = identifier;
		this.performerGroupId = performerGroupId;
		this.entryFormat = entryFormat;
		this.entryName = entryName;
		
	}

	/**
	 * @return the entryName
	 */
	public String getEntryName() {
		return entryName;
	}

	/**
	 * @return the entryFormat
	 */
	public String getEntryFormat() {
		return entryFormat;
	}

	public void addTargetId(int id){
		targetIds.add(id);
	}
	
	@Override
	public IObjectIdentifier getObjectIdentifier() {
		return identifier;
	}

	@Override
	public List<VanillaGroupParameter> getParametersValues() {
		return new ArrayList<VanillaGroupParameter>();
	}

	@Override
	public Integer getVanillaGroupId() {
		return performerGroupId;
	}

	/**
	 * @return the targetType
	 */
	public HistorizationTarget getTargetType() {
		return targetType;
	}

	/**
	 * @return the targetIds
	 */
	public List<Integer> getTargetIds() {
		return targetIds;
	}

	/**
	 * @return the identifier
	 */
	public IObjectIdentifier getIdentifier() {
		return identifier;
	}

}
