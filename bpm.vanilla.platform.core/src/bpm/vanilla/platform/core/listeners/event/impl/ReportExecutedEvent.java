package bpm.vanilla.platform.core.listeners.event.impl;

import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;

/**
 * 
 * @author ludo
 *
 */
public class ReportExecutedEvent implements IVanillaEvent{
	private IVanillaComponentIdentifier sourceComponent;
	private String sessionId;
	private int groupId;
	private IRunIdentifier identifier;
	
	public ReportExecutedEvent(IVanillaComponentIdentifier sourceComponent,
			String sessionId, IRunIdentifier identifier, int groupId) {
		super();
		this.sourceComponent = sourceComponent;
		this.sessionId = sessionId;
		this.identifier = identifier;
		this.groupId = groupId;
	}


	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	

	/**
	 * @return the groupId
	 */
	public int getGroupId() {
		return groupId;
	}


	@Override
	public IVanillaComponentIdentifier getEventSourceComponent() {
		return sourceComponent;
	}

	@Override
	public String getEventTypeName() {
		return IVanillaEvent.EVENT_REPORT_GENERATED;
	}


	public IRunIdentifier getRunIdentifier() {
		return identifier;
	}
	
	
	
}
