package bpm.vanilla.platform.core.listeners.event.impl;

import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;

public class ItemVersionEvent implements IVanillaEvent {
	/**
	 * used for the property directoryItem.isOn
	 */
	public static final String ITEM_STATE = "bpm.vanilla.platform.core.listeners.event.impl.ItemVersionEvent.state";

	private String propertyName;

	private int repositoryId;
	private int groupId;
	private int directoryItemId;
	private int documentVersionId;
	private String sessionId;
	private IVanillaComponentIdentifier source;

	public ItemVersionEvent(IVanillaComponentIdentifier source, String propertyName, int repositoryId, int groupId, int directoryItemId, int documentVersionId, String sessionId) {
		super();
		this.propertyName = propertyName;
		this.repositoryId = repositoryId;
		this.groupId = groupId;
		this.directoryItemId = directoryItemId;
		this.documentVersionId = documentVersionId;
		this.sessionId = sessionId;
		this.source = source;
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
		return 0;
	}

	/**
	 * @return the directoryItemSubType
	 */
	public int getDirectoryItemSubType() {
		return 0;
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
		return IVanillaEvent.EVENT_ITEM_VERSION_UPDATED;
	}

	public int getDocumentVersionId() {
		return documentVersionId;
	}

}
