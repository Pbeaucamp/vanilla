package bpm.vanilla.platform.core.listeners.event.impl;

import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;

public class VanillaActionEvent implements IVanillaEvent{

	public enum VanillaActionType {
		CONNECT,
		DISCONNECT
	}

	private IVanillaComponentIdentifier sourceComponent;
	private String sessionId;
	private int userId;
	private int groupId;
	private int repositoryId;
	private VanillaActionType actionType;
	
	public VanillaActionEvent(){}
	
	public VanillaActionEvent(IVanillaComponentIdentifier source, String sessionId, int userId, int groupId, 
			int repositoryId, VanillaActionType actionType){
		this.groupId = groupId;
		this.userId = userId;
		this.sessionId = sessionId;
		this.repositoryId = repositoryId;
		this.sourceComponent = source;
		this.actionType= actionType;
	}
	
	public int getGroupId() {
		return groupId;
	}
	
	public int getUserId() {
		return userId;
	}

	public int getRepositoryId() {
		return repositoryId;
	}
	
	public VanillaActionType getActionType() {
		return actionType;
	}
	
	@Override
	public IVanillaComponentIdentifier getEventSourceComponent() {
		return sourceComponent;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}
	
	@Override
	public String getEventTypeName() {
		return IVanillaEvent.EVENT_OBJECT_EXECUTED;
	}
	
}
