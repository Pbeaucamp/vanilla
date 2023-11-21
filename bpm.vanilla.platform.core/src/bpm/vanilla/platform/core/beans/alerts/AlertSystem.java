package bpm.vanilla.platform.core.beans.alerts;


public class AlertSystem implements IAlertInformation {

	private static final long serialVersionUID = -3806440683920367931L;
	
	public enum SystemEvent {
		CONNECT_TO_PORTAL("Portal Connection"),
		DECONNECT_FROM_PORTAL("Portal Deconnection");
		
		private String name;
		
		private SystemEvent(String name) {
			this.name = name;
		}
		
		public String getLabel() {
			return this.name;
		}
	}
//	public static final SystemEvent[] EVENT_SYSTEMS = {SystemEvent.CONNECT_TO_PORTAL, SystemEvent.DECONNECT_FROM_PORTAL};
	
	
	//@Column(name = "subtype_event")
	private SystemEvent subtypeEvent;
	
	public void setSubtypeEvent(SystemEvent subtypeEvent) {
		this.subtypeEvent = subtypeEvent;
	}

	public SystemEvent getSubtypeEvent() {
		return subtypeEvent;
	}
	
	@Override
	public boolean equals(Object o) {
		return subtypeEvent == ((AlertSystem)o).getSubtypeEvent();
	}

	@Override
	public String getSubtypeEventName() {
		return subtypeEvent.toString();
	}
	
	@Override
	public String getSubtypeEventLabel() {
		return subtypeEvent.getLabel();
	}
}
