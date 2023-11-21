package bpm.vanilla.platform.core.runtime.alerts;

import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.alerts.AlertConstants;
import bpm.vanilla.platform.core.beans.alerts.AlertSystem;
import bpm.vanilla.platform.core.beans.alerts.IAlertRuntime;
import bpm.vanilla.platform.core.listeners.event.impl.VanillaActionEvent.VanillaActionType;

public class AlertVanillaRuntime implements IAlertRuntime {
	
	private VanillaActionType type;
//	private Event event;
	private Alert alert;

	public AlertVanillaRuntime(VanillaActionType type, Alert alert) {
		this.type = type;
//		this.event = event;
		this.alert = alert;
	}
	
	@Override
	public boolean checkAlert() throws Exception {
		if(alert.getState() != AlertConstants.ACTIVE){
			return false;
		}
		
		if(type == VanillaActionType.CONNECT){
			return ((AlertSystem)alert.getEventObject()).getSubtypeEvent() == AlertSystem.SystemEvent.CONNECT_TO_PORTAL;
		}
		else if(type == VanillaActionType.DISCONNECT){
			return ((AlertSystem)alert.getEventObject()).getSubtypeEvent() == AlertSystem.SystemEvent.DECONNECT_FROM_PORTAL;
		}
		else {
			throw new Exception("Evenement with type = " + type + " is not supported. Alert cancelled.");
		}
	}
}
