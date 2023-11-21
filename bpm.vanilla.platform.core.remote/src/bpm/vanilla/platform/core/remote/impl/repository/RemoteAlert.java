package bpm.vanilla.platform.core.remote.impl.repository;

import java.util.List;

import bpm.vanilla.platform.core.beans.alerts.Action;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.alerts.Alert.TypeEvent;
import bpm.vanilla.platform.core.beans.alerts.Condition;
import bpm.vanilla.platform.core.beans.alerts.Smtp;
import bpm.vanilla.platform.core.beans.alerts.Subscriber;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.repository.services.IRepositoryAlertService;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteAlert implements IRepositoryAlertService {

	private HttpCommunicator httpCommunicator;
	private static XStream xstream;
	static {
		xstream = new XStream();
	}

	private static XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	private Object handleError(String responseMessage) throws Exception {
		if (responseMessage.isEmpty()) {
			return null;
		}
		Object o = xstream.fromXML(responseMessage);
		if (o != null && o instanceof VanillaException) {
			throw (VanillaException) o;
		}
		return o;
	}

	public RemoteAlert(HttpCommunicator httpCommunicator) {
		this.httpCommunicator = httpCommunicator;
	}

	@Override
	public int createAlert(Alert alert) throws Exception {
		XmlAction op = new XmlAction(createArguments(alert), IRepositoryAlertService.ActionType.ADD_ALERT);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Integer) handleError(result);
	}

//	@Override
//	public int createEvent(Event event) throws Exception {
//		XmlAction op = new XmlAction(createArguments(event), IRepositoryAlertService.ActionType.ADD_EVENT);
//		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
//		return (Integer) handleError(result);
//	}

	@Override
	public int createSmtpHost(Smtp smtp) throws Exception {
		XmlAction op = new XmlAction(createArguments(smtp), IRepositoryAlertService.ActionType.ADD_SMTP);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Integer) handleError(result);
	}

//	@Override
//	public Event getEvent(int eventId) throws Exception {
//		XmlAction op = new XmlAction(createArguments(eventId), IRepositoryAlertService.ActionType.GET_EVENT_BY_ID);
//		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
//		return (Event) handleError(result);
//	}

//	@Override
//	@SuppressWarnings("unchecked")
//	public List<Event> getEvents() throws Exception {
//		XmlAction op = new XmlAction(createArguments(), IRepositoryAlertService.ActionType.GET_EVENTS);
//		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
//		return (List<Event>) handleError(result);
//	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Alert> getAlertsByType(TypeEvent typeEvent) throws Exception {
		XmlAction op = new XmlAction(createArguments(typeEvent), IRepositoryAlertService.ActionType.GET_ALERT_BY_TYPE);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Alert>) handleError(result);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Alert> getAlertsByTypeAndDirectoryItemId(TypeEvent typeEvent, int directoryItemId, int repositoryId) throws Exception {
		XmlAction op = new XmlAction(createArguments(typeEvent, directoryItemId, repositoryId), IRepositoryAlertService.ActionType.GET_ALERT_BY_DIRECTORY_ITEM);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Alert>) handleError(result);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Smtp> getListSmtp() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IRepositoryAlertService.ActionType.GET_SMTP);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Smtp>) handleError(result);
	}

	@Override
	public void removeAction(Action ac) throws Exception {
		XmlAction op = new XmlAction(createArguments(ac), IRepositoryAlertService.ActionType.DEL_ACTION);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);
	}

	@Override
	public void removeAlert(Alert alert) throws Exception {
		XmlAction op = new XmlAction(createArguments(alert), IRepositoryAlertService.ActionType.DEL_ALERT);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);
	}

	@Override
	public void removeCondition(Condition c) throws Exception {
		XmlAction op = new XmlAction(createArguments(c), IRepositoryAlertService.ActionType.DEL_COND);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);
	}

//	@Override
//	public void removeEvent(Event ev) throws Exception {
//		XmlAction op = new XmlAction(createArguments(ev), IRepositoryAlertService.ActionType.DEL_EVENT);
//		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
//		handleError(result);
//	}

	@Override
	public void removeSmtpHost(Smtp smtp) throws Exception {
		XmlAction op = new XmlAction(createArguments(smtp), IRepositoryAlertService.ActionType.DEL_SMTP);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);
	}

	@Override
	public void removeSubscriber(Subscriber su) throws Exception {
		XmlAction op = new XmlAction(createArguments(su), IRepositoryAlertService.ActionType.DEL_SUBS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);
	}

	@Override
	public void updateAlert(Alert alert) throws Exception {
		XmlAction op = new XmlAction(createArguments(alert), IRepositoryAlertService.ActionType.UPDATE_ALERT);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);
	}

//	@Override
//	public void updateEvent(Event event) throws Exception {
//		XmlAction op = new XmlAction(createArguments(event), IRepositoryAlertService.ActionType.UPDATE_EVENT);
//		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
//		handleError(result);
//	}

	@Override
	public void updateSmtpHost(Smtp smtp) throws Exception {
		XmlAction op = new XmlAction(createArguments(smtp), IRepositoryAlertService.ActionType.UPDATE_SMTP);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);
	}

	@Override
	public Alert getAlert(int alertId) throws Exception {
		XmlAction op = new XmlAction(createArguments(alertId), IRepositoryAlertService.ActionType.GET_ALERT);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Alert) handleError(result);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Alert> getAlertsWhitoutEvent() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IRepositoryAlertService.ActionType.GET_ALERTS_WHITOUT_EVENT);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Alert>) handleError(result);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Alert> getAlerts() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IRepositoryAlertService.ActionType.GET_ALERTS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Alert>) handleError(result);
	}
}
