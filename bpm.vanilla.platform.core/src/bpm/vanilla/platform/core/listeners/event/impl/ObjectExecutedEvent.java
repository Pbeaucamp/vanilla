package bpm.vanilla.platform.core.listeners.event.impl;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.tasks.ITaskState;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;

public class ObjectExecutedEvent implements IVanillaEvent {

	private IVanillaComponentIdentifier sourceComponent;
	private String sessionId;
	private IObjectIdentifier objectIdentifier;
	private int groupId;
	private ITaskState state;

	public ObjectExecutedEvent() {
	}

	public ObjectExecutedEvent(IVanillaComponentIdentifier source, String sessionId, IObjectIdentifier objectId, int groupId, ITaskState state) {
		this.groupId = groupId;
		this.objectIdentifier = objectId;
		this.sessionId = sessionId;
		this.sourceComponent = source;
		this.state = state;
	}

	public int getGroupId() {
		return groupId;
	}

	@Override
	public IVanillaComponentIdentifier getEventSourceComponent() {
		return sourceComponent;
	}

	public IObjectIdentifier getObjectIdentifier() {
		return objectIdentifier;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}
	
	public ITaskState getState() {
		return state;
	}

	@Override
	public String getEventTypeName() {
		return IVanillaEvent.EVENT_OBJECT_EXECUTED;
	}

}
