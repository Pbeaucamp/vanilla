package bpm.vanilla.repository.services;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.alerts.Action;
import bpm.vanilla.platform.core.beans.alerts.ActionMail;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.alerts.Alert.TypeEvent;
import bpm.vanilla.platform.core.beans.alerts.AlertRepository;
import bpm.vanilla.platform.core.beans.alerts.Condition;
import bpm.vanilla.platform.core.beans.alerts.IActionInformation;
import bpm.vanilla.platform.core.beans.alerts.IAlertInformation;
import bpm.vanilla.platform.core.beans.alerts.IConditionInformation;
import bpm.vanilla.platform.core.beans.alerts.Smtp;
import bpm.vanilla.platform.core.beans.alerts.Subscriber;
import bpm.vanilla.platform.core.repository.services.IRepositoryAlertService;
import bpm.vanilla.repository.beans.RepositoryDaoComponent;
import bpm.vanilla.repository.beans.RepositoryRuntimeComponent;

import com.thoughtworks.xstream.XStream;

public class ServiceAlert implements IRepositoryAlertService {

	private RepositoryDaoComponent repositoryDaoComponent;
	
	private XStream xstream = new XStream();

	public ServiceAlert(RepositoryRuntimeComponent repositoryRuntimeComponent, int repositoryId) {
		this.repositoryDaoComponent = repositoryRuntimeComponent.getRepositoryDao(repositoryId);
	}

	@Override
	public int createAlert(Alert alert) {
		
		alert.setEventModel(xstream.toXML(alert.getEventObject()));
		int alertId = repositoryDaoComponent.getAlertDao().save(alert);

		for (Condition c : alert.getConditions()) {
			c.setAlertId(alertId);
			c.setConditionModel(xstream.toXML(c.getConditionObject()));
			repositoryDaoComponent.getConditionDao().save(c);
		}

		if (alert.getAction() != null) {
			Action act = alert.getAction();
			act.setAlertId(alertId);
			act.setActionModel(xstream.toXML(act.getActionObject()));
			if(act.getActionObject() instanceof ActionMail){
				ActionMail actmail = (ActionMail)act.getActionObject();
				if (actmail.getSubscribers() != null && !actmail.getSubscribers().isEmpty()) {
					for (Subscriber s : actmail.getSubscribers()) {
						s.setAlertId(alertId);
						repositoryDaoComponent.getSubscriberDao().save(s);
					}
				}
			}
			repositoryDaoComponent.getActionDao().save(act);
		}

		return alertId;
	}

//	@Override
//	public int createEvent(Event event) throws Exception {
//		return repositoryDaoComponent.getEventDao().save(event);
//	}

	@Override
	public int createSmtpHost(Smtp smtp) {
		return repositoryDaoComponent.getSmtpDao().save(smtp);
	}

//	@Override
//	public Event getEvent(int eventId) throws Exception {
//		Event event = repositoryDaoComponent.getEventDao().findByPrimaryKey(eventId);
//
//		List<Alert> alerts = getAlertsByEventId(eventId);
//		event.setAlerts(alerts);
//
//		return event;
//	}

//	@Override
//	public List<Event> getEvents() throws Exception {
//		List<Event> events = repositoryDaoComponent.getEventDao().findAll();
//
//		for (Event event : events) {
//			List<Alert> alerts = getAlertsByEventId(event.getId());
//			event.setAlerts(alerts);
//		}
//
//		return events;
//	}

	private List<Alert> getAlertsByEventId(int eventId) throws Exception {
		List<Alert> alerts = repositoryDaoComponent.getAlertDao().findByEventId(eventId);

		for (Alert alert : alerts) {
			buildAlert(alert);
		}
		return alerts;
	}

	// @Override
	// public List<Event> getEventsByOwnerId(int userId) throws Exception {
	// List<Alert> alerts = new ArrayList<Alert>();
	//
	// for (Alert alert :
	// repositoryDaoComponent.getAlertDao().findByUserId(userId)) {
	// int alertId = alert.getId();
	//
	// //We set the alert's conditions
	// List<Condition> conditions =
	// repositoryDaoComponent.getConditionDao().findByAlertId(alertId);
	// alert.setConditions(conditions);
	//
	// //We set the alert's event
	// List<Event> events =
	// repositoryDaoComponent.getEventDao().findByAlertId(alertId);
	// for(Event event : events){
	// EventProperties eventProps =
	// eventPropertiesDao.findByEventId(event.getId());
	// event.setProperties(eventProps);
	// }
	// alert.setEvents(events);
	//
	// //We set the alert's event
	// List<Subscriber> subscribers =
	// repositoryDaoComponent.getSubscriberDao().findByAlertId(alertId);
	// alert.setSubscribers(subscribers);
	//
	// //We set the alert action
	// Action action =
	// repositoryDaoComponent.getActionDao().findByAlertId(alertId);
	// alert.setAction(action);
	//
	// alerts.add(alert);
	// }
	//
	// return alerts.toArray(new Alert[alerts.size()]);
	// }

	@Override
	public List<Alert> getAlertsByType(TypeEvent typeEvent) throws Exception {
		List<Alert> alerts = repositoryDaoComponent.getAlertDao().findByTypeEvent(typeEvent);

		if (alerts != null) {
			for (Alert alert : alerts) {
				buildAlert(alert);
			}
		}

		return alerts;
	}

	@Override
	public List<Alert> getAlertsByTypeAndDirectoryItemId(TypeEvent typeEvent, int directoryItemId, int repositoryId) throws Exception {
		List<Alert> alertsTemp = new ArrayList<Alert>();

		// We check if there are events associated with this type of event
		List<Alert> alertsFind = getAlertsByType(typeEvent);
		if (alertsFind != null) {
			for (Alert alert : alertsFind) {
				if(alert.getEventObject() instanceof AlertRepository){
					AlertRepository model = (AlertRepository) alert.getEventObject();
					if (model.getDirectoryItemId() == directoryItemId && model.getRepositoryId() == repositoryId) {
						alertsTemp.add(alert);
					}
				}
			}
		}

		return alertsTemp;
	}

	@Override
	public List<Smtp> getListSmtp() throws Exception {
		List<Smtp> smtps = new ArrayList<Smtp>();
		for (Smtp smtp : repositoryDaoComponent.getSmtpDao().findAll()) {
			smtps.add(smtp);
		}
		return smtps;
	}

	@Override
	public void removeAction(Action action) {
		repositoryDaoComponent.getActionDao().delete(action);
	}

	@Override
	public void removeAlert(Alert alert) {
		for (Condition c : alert.getConditions()) {
			if (c.getId() > 0) {
				repositoryDaoComponent.getConditionDao().delete(c);
			}
		}

		

		Action action = alert.getAction();
		if (action != null && action.getId() > 0) {
			if(action.getActionObject() instanceof ActionMail){
				ActionMail model = (ActionMail) action.getActionObject();
				for (Subscriber s : model.getSubscribers()) {
					if (s.getId() > 0) {
						repositoryDaoComponent.getSubscriberDao().delete(s);
					}
				}
			}
			repositoryDaoComponent.getActionDao().delete(action);
		}

		repositoryDaoComponent.getAlertDao().delete(alert);
	}

	@Override
	public void removeCondition(Condition condition) {
		repositoryDaoComponent.getConditionDao().delete(condition);
	}

//	@Override
//	public void removeEvent(Event event) {
//		if (event.getAlerts() != null) {
//			for (Alert alert : event.getAlerts()) {
//				removeAlert(alert);
//			}
//		}
//
//		repositoryDaoComponent.getEventDao().delete(event);
//	}

	@Override
	public void removeSmtpHost(Smtp smtp) {
		repositoryDaoComponent.getSmtpDao().delete(smtp);
	}

	@Override
	public void removeSubscriber(Subscriber subscriber) {
		repositoryDaoComponent.getSubscriberDao().delete(subscriber);
	}

	@Override
	public void updateAlert(Alert alert) {
		alert.setEventModel(xstream.toXML(alert.getEventObject()));
		repositoryDaoComponent.getAlertDao().update(alert);
		int alertId = alert.getId();
		
		List<Condition> oldConds = repositoryDaoComponent.getConditionDao().findByAlertId(alertId);

		// We remove all subscribers before to add the new ones
		if (oldConds != null) {
			for (Condition cond : oldConds) {
				repositoryDaoComponent.getConditionDao().delete(cond);
			}
		}
		for (Condition c : alert.getConditions()) {
			c.setAlertId(alertId);
			c.setConditionModel(xstream.toXML(c.getConditionObject()));
			repositoryDaoComponent.getConditionDao().save(c);
		}

		
		List<Subscriber> subscribers = repositoryDaoComponent.getSubscriberDao().findByAlertId(alertId);

		// We remove all subscribers before to add the new ones
		if (subscribers != null) {
			for (Subscriber sub : subscribers) {
				repositoryDaoComponent.getSubscriberDao().delete(sub);
			}
		}

		Action action = alert.getAction();
		if (action != null) {
			action.setActionModel(xstream.toXML(action.getActionObject()));
			if (action.getId() > 0) {
				repositoryDaoComponent.getActionDao().update(action);
			}
			else {
				action.setAlertId(alertId);
				repositoryDaoComponent.getActionDao().save(action);
			}
			if(action.getActionObject() instanceof ActionMail){
				ActionMail model = (ActionMail) action.getActionObject();
				for (Subscriber s : model.getSubscribers()) {
					s.setAlertId(alertId);
					repositoryDaoComponent.getSubscriberDao().save(s);
				}
			}
		}
	}

//	@Override
//	public void updateEvent(Event event) throws Exception {
//		repositoryDaoComponent.getEventDao().update(event);
//	}

	@Override
	public void updateSmtpHost(Smtp smtp) {
		repositoryDaoComponent.getSmtpDao().update(smtp);
	}

	@Override
	public Alert getAlert(int alertId) throws Exception {
		Alert alert = repositoryDaoComponent.getAlertDao().findByPrimaryKey(alertId);
		if (alert != null) {
			buildAlert(alert);
		}
		return alert;
	}

	@Override
	public List<Alert> getAlertsWhitoutEvent() throws Exception {
		List<Alert> alerts = repositoryDaoComponent.getAlertDao().getAlertsWhitoutEvent();
		if (alerts != null) {
			for (Alert alert : alerts) {
				buildAlert(alert);
			}
		}
		return alerts;
	}

	private void buildAlert(Alert alert) {
		if(alert.getEventModel()!= null && alert.getEventModel()!= ""){
			alert.setEventObject((IAlertInformation) xstream.fromXML(alert.getEventModel()));
		}
		// We set the alert's conditions
		List<Condition> conditions = repositoryDaoComponent.getConditionDao().findByAlertId(alert.getId());
		for(Condition cond : conditions){
			if(cond.getConditionModel()!= null && cond.getConditionModel()!= ""){
				cond.setConditionObject((IConditionInformation) xstream.fromXML(cond.getConditionModel()));
			}
		}
		alert.setConditions(conditions);

		// We set the alert action
		Action action = repositoryDaoComponent.getActionDao().findByAlertId(alert.getId());
		
		alert.setAction(action);
		if(action.getActionModel()!= null && action.getActionModel()!= ""){
			action.setActionObject((IActionInformation) xstream.fromXML(action.getActionModel()));
			if(action.getActionObject() instanceof ActionMail){
				ActionMail model = (ActionMail) action.getActionObject();
				// We set the alert's event
				List<Subscriber> subscribers = repositoryDaoComponent.getSubscriberDao().findByAlertId(alert.getId());
				model.setSubscribers(subscribers);
			}
		}
		
		
	}
	
	@Override
	public List<Alert> getAlerts() throws Exception {
		List<Alert> alerts = repositoryDaoComponent.getAlertDao().findAll();

		for (Alert alert : alerts) {
			buildAlert(alert);
		}

		return alerts;
	}
}
