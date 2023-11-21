package bpm.vanilla.platform.core.listeners.event.impl;

import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;

public class RepositoryItemEvent implements IVanillaEvent{
	/**
	 * used for the property directoryItem.isOn
	 */
	public static final String ITEM_STATE = "bpm.vanilla.platform.core.listeners.event.impl.RepositoryItemEvent.state";
	
//	private static final String COMPONENT_TYPE_NAME = VanillaComponentType.COMPONENT_REPOSITORY;
	
	private Boolean oldValue;
	private Boolean newValue;
	private String propertyName;
	
	private int repositoryId;
	private int groupId;
	private int directoryItemId;
	private int directoryItemType;
	private int directoryItemSubType;
	private String sessionId;
	private IVanillaComponentIdentifier source;
	
	
	public RepositoryItemEvent(IVanillaComponentIdentifier source, Boolean oldValue, Boolean newValue,
			String propertyName, int repositoryId, int groupId, int directoryItemId,
			int directoryItemType, int directoryItemSubType, String sessionId) {
		super();
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.propertyName = propertyName;
		this.repositoryId = repositoryId;
		this.groupId = groupId;
		this.directoryItemId = directoryItemId;
		this.directoryItemType = directoryItemType;
		this.directoryItemSubType = directoryItemSubType;
		this.sessionId = sessionId;
		this.source = source;
	}
	


	public Object getNewValue() {
		return newValue;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public String getPropertyChangedName() {
		return propertyName;
	}

	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * @return the repositoryId
	 */
	public int getRepositoryId() {
		return repositoryId;
	}

	public int getGroupId() {
		return groupId;
	}
	
	/**
	 * @return the directoryItemId
	 */
	public int getDirectoryItemId() {
		return directoryItemId;
	}

	/**
	 * @return the directoryItemType
	 */
	public int getDirectoryItemType() {
		return directoryItemType;
	}

	/**
	 * @return the directoryItemSubType
	 */
	public int getDirectoryItemSubType() {
		return directoryItemSubType;
	}

	

	@Override
	public String getSessionId() {
		return sessionId;
	}


	@Override
	public IVanillaComponentIdentifier getEventSourceComponent() {
		return source;
	}



	@Override
	public String getEventTypeName() {
		return IVanillaEvent.EVENT_REPOSITORY_ITEM_UPDATED;
	}

}
