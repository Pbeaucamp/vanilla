package bpm.vanilla.platform.core.runtime.alerts;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.fm.api.IFreeMetricsManager;
import bpm.fm.api.RemoteFreeMetricsManager;
import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.Metric;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.alerts.Action.TypeAction;
import bpm.vanilla.platform.core.beans.alerts.ActionGateway;
import bpm.vanilla.platform.core.beans.alerts.ActionMail;
import bpm.vanilla.platform.core.beans.alerts.ActionWorkflow;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.alerts.Alert.TypeEvent;
import bpm.vanilla.platform.core.beans.alerts.AlertKpi;
import bpm.vanilla.platform.core.beans.alerts.IAlertRuntime;
import bpm.vanilla.platform.core.beans.alerts.Subscriber;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.components.ReportHistoricComponent;
import bpm.vanilla.platform.core.components.system.IMailConfig;
import bpm.vanilla.platform.core.components.system.MailConfig;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;
import bpm.vanilla.platform.core.listeners.event.impl.ItemVersionEvent;
import bpm.vanilla.platform.core.listeners.event.impl.VanillaActionEvent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteHistoricReportComponent;
import bpm.vanilla.platform.core.repository.IAlertExecutor;
import bpm.vanilla.platform.core.runtime.alerts.actions.AlertToGTW;
import bpm.vanilla.platform.core.runtime.alerts.actions.AlertToWKF;
import bpm.vanilla.platform.core.runtime.tools.AlertChecker;
import bpm.vanilla.platform.core.runtime.tools.MailHelper;

public class AlertToActionRuntime implements IAlertExecutor {

	private IVanillaAPI vanillaApi;
	private Repository repository;

	private IVanillaEvent triggerEvent;
	private List<Alert> alerts;
	private int eventDirectoryItemId;
	private IRepositoryContext repositoryContext;

	public AlertToActionRuntime(IVanillaEvent triggerEvent, List<Alert> alerts, int eventDirectoryItemId, IVanillaAPI vanillaApi, Repository repository, IRepositoryContext repositoryContext) {
		this.triggerEvent = triggerEvent;
		this.alerts = alerts;
		this.eventDirectoryItemId = eventDirectoryItemId;
		this.vanillaApi = vanillaApi;
		this.repository = repository;
		this.repositoryContext = repositoryContext;
	}

	@Override
	public void execute() throws Exception {
		for (Alert alert : alerts) {
			if (alert.getTypeEvent() == TypeEvent.OBJECT_TYPE) {
				IAlertRuntime run = new AlertObjectRuntime(alert, repositoryContext);
				if (run != null && run.checkAlert()) {
					executeAction(alert);
				}
//				if(((AlertRepository)alert.getEventModel()).getSubtypeEvent().equals(ObjectEvent.GTW)){
//					IFreeMetricsManager manager = new RemoteFreeMetricsManager(vanillaApi.getVanillaContext());
//					for(bpm.fm.api.model.Metric metric : manager.getMetrics()) {
//						if(metric.getEtlId() == ((AlertRepository)alert.getEventModel()).getDirectoryItemId()) {
//							for(Alert alertkpi : metric.getAlerts()) {
//								//check the values for the previous period
//								Date date = getPreviousDate(new Date(), ((FactTable)metric.getFactTable()).getPeriodicity());
//								
//								boolean isRaised = AlertChecker.checkAlert(metric, alert, date, vanillaApi);
//								if(isRaised) {
//									executeAction(alertkpi);
//								}
//								
//							}
//						}
//					}
//				}
			}
			else if (alert.getTypeEvent() == TypeEvent.SYSTEM_TYPE) {
				IAlertRuntime run = new AlertVanillaRuntime(((VanillaActionEvent) triggerEvent).getActionType(), alert);
				if (run != null && run.checkAlert()) {
					executeAction(alert);
				}
					
				
			}
//			else if (al.getTypeEvent() == TypeEvent.UPDATE_TYPE) {
//				if (al.getAlerts() != null) {
//					for (Alert alert : al.getAlerts()) {
//						if (triggerEvent instanceof RepositoryItemEvent && (al.getSubtypeEvent() == ObjectEvent.BIRT.getType() || al.getSubtypeEvent()== ObjectEvent.GTW.getType()  || al.getSubtypeEvent() == ObjectEvent.FWR.getType())) {
//							IAlertRuntime run = new AlertUpdateRuntime((RepositoryItemEvent) triggerEvent, alert);
//							if (run != null && run.checkAlert()) {
//								executeAction(alert);
//							}
//						}
//						else if (triggerEvent instanceof ItemVersionEvent && (al.getSubtypeEvent() == ObjectEvent.BIRT_NEW_VERSION.getType() || al.getSubtypeEvent()== ObjectEvent.FWR_NEW_VERSION.getType())) {
//							IAlertRuntime run = new AlertUpdateRuntime((ItemVersionEvent) triggerEvent, alert);
//							if (run != null && run.checkAlert()) {
//								executeAction(alert);
//							}
//						}
//					}
//				}
//			}
			else if (alert.getTypeEvent() == TypeEvent.KPI_TYPE) {
				IFreeMetricsManager manager = new RemoteFreeMetricsManager(vanillaApi.getVanillaContext());
				Metric metric = manager.getMetric(((AlertKpi)alert.getEventObject()).getMetricId());
				//check the values for the previous period
				Date date = getPreviousDate(new Date(), ((FactTable)metric.getFactTable()).getPeriodicity());
				
				boolean isRaised = AlertChecker.checkAlert(metric, alert, date, vanillaApi);
				if(isRaised) {
					executeAction(alert);
				}
				
			}
			else {
				throw new Exception("This type of event (" + alert.getTypeEvent() + ") is not supported.");
			}
		}
	}

	public void executeAction(Alert alert) throws Exception {

		TypeAction typeAction = alert.getTypeAction();
		if (typeAction == TypeAction.GATEWAY) {
			
			ActionGateway act = (ActionGateway) alert.getAction().getActionObject();
			IObjectIdentifier identifier = new ObjectIdentifier(act.getRepositoryId(), act.getDirectoryItemId());

			AlertToGTW alertToGTW = new AlertToGTW(repositoryContext, identifier, new ArrayList<VanillaGroupParameter>());
			alertToGTW.runGTW(true);
		}
		else if (typeAction == TypeAction.WORKFLOW) {
			ActionWorkflow act = (ActionWorkflow) alert.getAction().getActionObject();
			IObjectIdentifier identifier = new ObjectIdentifier(act.getRepositoryId(), act.getDirectoryItemId());

			AlertToWKF alertToWKF = new AlertToWKF(repositoryContext, identifier, new ArrayList<VanillaGroupParameter>());
			alertToWKF.runWKF(true);
		}
		else if (typeAction == TypeAction.MAIL && triggerEvent instanceof ItemVersionEvent) {
			ActionMail act = (ActionMail) alert.getAction().getActionObject();
			HashMap<String, InputStream> attachements = getAttachements(vanillaApi, (ItemVersionEvent) triggerEvent);
			if (act.getSubscribers() != null) {
				for (Subscriber sub : act.getSubscribers()) {
					IMailConfig config = new MailConfig(sub.getUserMail(), "no-reply@bpm-conseil.com", act.getContent(), act.getSubject(), false);
					if(attachements != null){
						try {
							MailHelper.sendEmail(config, new HashMap<String, InputStream>());
						} catch (Throwable e) {
							e.printStackTrace();
						}
					} else {
						try {
							MailHelper.sendEmail(config, attachements);
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		else if (typeAction == TypeAction.MAIL) {
			ActionMail act = (ActionMail) alert.getAction().getActionObject();
			//HashMap<String, InputStream> attachements = getAttachements(vanillaApi, (ItemVersionEvent) triggerEvent);
			if (act.getSubscribers() != null) {
				for (Subscriber sub : act.getSubscribers()) {
					IMailConfig config = new MailConfig(sub.getUserMail(), "no-reply@bpm-conseil.com", act.getContent(), act.getSubject(), false);
					//if(attachements != null){
						try {
							MailHelper.sendEmail(config, new HashMap<String, InputStream>());
						} catch (Throwable e) {
							e.printStackTrace();
						}
					//} else {
					//	try {
					//		MailHelper.sendEmail(config, attachements);
					//	} catch (Throwable e) {
					//		e.printStackTrace();
					//	}
					//}
				}
			}
		}
	}

//	private IRepositoryContext getRepositoryContextFromSubscriber(Subscriber subscriber) throws Exception {
//		User user = vanillaApi.getVanillaSecurityManager().getUserById(subscriber.getUserId());
//		Group group = vanillaApi.getVanillaSecurityManager().getGroupById(subscriber.getGroupId());
//		IVanillaContext ctx = new BaseVanillaContext(vanillaApi.getVanillaUrl(), user.getLogin(), user.getPassword());
//		return new BaseRepositoryContext(ctx, group, repository);
//	}
	
	private HashMap<String, InputStream> getAttachements(IVanillaAPI vanillaApi, ItemVersionEvent triggerEvent) throws Exception {
		ReportHistoricComponent histoComponent = new RemoteHistoricReportComponent(vanillaApi.getVanillaContext());
		
		IObjectIdentifier identifier = new ObjectIdentifier(triggerEvent.getRepositoryId(), triggerEvent.getDirectoryItemId());

		List<GedDocument> histos = histoComponent.getReportHistoric(identifier, triggerEvent.getGroupId());
		
		DocumentVersion latest = null;
		if (histos != null && !histos.isEmpty()) {
			for (GedDocument histo : histos) {
				if (histo.getDocumentVersions() != null) {
					for (DocumentVersion doc : histo.getDocumentVersions()) {
						if ((doc.getPeremptionDate() == null || doc.getPeremptionDate().after(new Date())) && latest == null) {
							latest = doc;
						}
						else if ((doc.getPeremptionDate() == null || doc.getPeremptionDate().after(new Date())) && latest.getModificationDate().before(doc.getModificationDate())) {
							latest = doc;
						}
					}
				}
			}
		}

		if (latest == null) {
			return null;
		}
		else {
			InputStream is = histoComponent.loadHistorizedDocument(latest.getId());
			
			HashMap<String, InputStream> attachements = new HashMap<String, InputStream>();
			attachements.put(latest.getParent().getName() + "." + latest.getFormat(), is);
			return attachements;
		}
	}

	private Date getPreviousDate(Date date, String periodicity) {
		if(periodicity.equals(FactTable.PERIODICITY_YEARLY)) {
			date.setYear(date.getYear() - 1);
		}
		else if(periodicity.equals(FactTable.PERIODICITY_MONTHLY)) {
			date.setMonth(date.getMonth() - 1);
		}
		else if(periodicity.equals(FactTable.PERIODICITY_DAILY)) {
			date.setDate(date.getDate() - 1);
		}
		return date;
	}
}
