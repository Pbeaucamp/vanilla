package bpm.vanilla.platform.core.repository.services;

import java.util.List;

import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.beans.alerts.Action;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.alerts.Alert.TypeEvent;
import bpm.vanilla.platform.core.beans.alerts.Condition;
import bpm.vanilla.platform.core.beans.alerts.Smtp;
import bpm.vanilla.platform.core.beans.alerts.Subscriber;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface IRepositoryAlertService {
	public static enum ActionType implements IXmlActionType{
		ADD_ALERT(Level.INFO), ADD_SMTP(Level.INFO),
		GET_ALERTS(Level.DEBUG), GET_ALERT_BY_TYPE(Level.DEBUG), GET_ALERT_BY_DIRECTORY_ITEM(Level.DEBUG), GET_SMTP(Level.DEBUG), 
		GET_ALERT(Level.DEBUG), GET_ALERTS_WHITOUT_EVENT(Level.DEBUG), UPDATE_ALERT(Level.INFO), UPDATE_SMTP(Level.INFO),
		DEL_ACTION(Level.INFO), DEL_ALERT(Level.INFO), DEL_COND(Level.INFO), DEL_SMTP(Level.INFO), DEL_SUBS(Level.INFO);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	
//	public int createEvent(Event event) throws Exception;

	public int createAlert(Alert alert) throws Exception;
	
	public int createSmtpHost(Smtp smtp) throws Exception;
	
//	public List<Event> getEvents() throws Exception;
	
//	public Event getEvent(int eventId) throws Exception;
	
	public List<Alert> getAlertsByType(TypeEvent typeEvent) throws Exception;
	
	public List<Alert> getAlertsByTypeAndDirectoryItemId(TypeEvent typeEvent, int directoryItemId, int repositoryId) throws Exception;
	
	public List<Smtp> getListSmtp() throws Exception;
	
//	public void updateEvent(Event event) throws Exception;
	
	public void updateAlert(Alert alert) throws Exception;
	
	public void updateSmtpHost(Smtp smtp) throws Exception;
	
//	public void removeEvent(Event ev) throws Exception;
	
	public void removeSubscriber(Subscriber su) throws Exception;

	public void removeCondition(Condition c) throws Exception;
	
	public void removeAction(Action ac) throws Exception;
	
	public void removeAlert(Alert alert) throws Exception;
	
	public void removeSmtpHost(Smtp smtp) throws Exception;

	public List<Alert> getAlertsWhitoutEvent() throws Exception;

	public Alert getAlert(int alertId) throws Exception;
	
	public List<Alert> getAlerts() throws Exception;
}
