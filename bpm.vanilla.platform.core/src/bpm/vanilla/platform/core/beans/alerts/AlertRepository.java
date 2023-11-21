package bpm.vanilla.platform.core.beans.alerts;


public class AlertRepository implements IAlertInformation {

	private static final long serialVersionUID = -3806440683920367931L;
	
	public enum ObjectEvent {
		FWR("Vanilla Web Report"),
		BIRT("Birt Report"),
		GTW("Gateway"),
		BIRT_NEW_VERSION("Birt Report Edition"),
		FWR_NEW_VERSION("Vanilla Web Report Edition");
//		FM(4);
		
		private String name;
		
		private ObjectEvent(String name) {
			this.name = name;
		}
		
		public String getLabel() {
			return this.name;
		}
	}
	
	//@Column(name = "subtype_event")
	private ObjectEvent subtypeEvent;
	
	//@Column(name = "item_id")
	private int directoryItemId;
	
	//@Column(name = "repository_id")
	private int repositoryId;
	
	public void setSubtypeEvent(ObjectEvent subtypeEvent) {
		this.subtypeEvent = subtypeEvent;
	}

	public ObjectEvent getSubtypeEvent() {
		return subtypeEvent;
	}
	
	public int getDirectoryItemId() {
		return directoryItemId;
	}
	
	public void setDirectoryItemId(int directoryItemId) {
		this.directoryItemId = directoryItemId;
	}
	
	public int getRepositoryId() {
		return repositoryId;
	}
	
	public void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	@Override
	public boolean equals(Object o) {
		return directoryItemId == ((AlertRepository)o).getDirectoryItemId() && repositoryId == ((AlertRepository)o).getRepositoryId() && subtypeEvent == ((AlertRepository)o).getSubtypeEvent();
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
