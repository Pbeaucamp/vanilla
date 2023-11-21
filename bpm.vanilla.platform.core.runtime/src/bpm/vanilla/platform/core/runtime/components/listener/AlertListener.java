package bpm.vanilla.platform.core.runtime.components.listener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.fm.api.IFreeMetricsManager;
import bpm.fm.api.RemoteFreeMetricsManager;
import bpm.fm.api.model.FactTable;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.VanillaSession;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.alerts.AlertRepository;
import bpm.vanilla.platform.core.beans.alerts.Alert.TypeEvent;
import bpm.vanilla.platform.core.beans.alerts.AlertRepository.ObjectEvent;
import bpm.vanilla.platform.core.beans.validation.Validation;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;
import bpm.vanilla.platform.core.listeners.IVanillaListener;
import bpm.vanilla.platform.core.listeners.event.impl.ItemVersionEvent;
import bpm.vanilla.platform.core.listeners.event.impl.ObjectExecutedEvent;
import bpm.vanilla.platform.core.listeners.event.impl.ReportExecutedEvent;
import bpm.vanilla.platform.core.listeners.event.impl.RepositoryItemEvent;
import bpm.vanilla.platform.core.listeners.event.impl.VanillaActionEvent;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.IAlertExecutor;
import bpm.vanilla.platform.core.runtime.alerts.AlertToActionRuntime;
import bpm.vanilla.platform.core.runtime.alerts.ValidationRuntime;
import bpm.vanilla.platform.core.runtime.tools.AlertChecker;
import bpm.vanilla.platform.core.runtime.tools.OSGIHelper;

/**
 * 
 * Listener for Alerts, once an Object is Run, if an alert is attached to this
 * object the alert is checked and its action is run if the condition is
 * satisfied
 * 
 * @author ludo
 * 
 */
public class AlertListener implements IVanillaListener {
	private static final String[] EVENT_TYPES = new String[] { ObjectExecutedEvent.class.getName(), VanillaActionEvent.class.getName(), RepositoryItemEvent.class.getName(), ItemVersionEvent.class.getName(), ReportExecutedEvent.class.getName() };

	@Override
	public String[] getListenedEventTypes() {
		return EVENT_TYPES;
	}

	@Override
	public void handleEvent(IVanillaEvent event) {
		if (event instanceof ObjectExecutedEvent || event instanceof VanillaActionEvent || event instanceof RepositoryItemEvent || event instanceof ItemVersionEvent || event instanceof ReportExecutedEvent) {
			checkAndRunAlerts(event);
		}
		else {
			Logger.getLogger(getClass()).warn("Received an Event of unsupported class " + event.getClass().getName() + " -> event ignored");
		}
	}

	private void checkAndRunAlerts(IVanillaEvent event) {
		IAlertExecutor runner = null;
		if (event instanceof ObjectExecutedEvent) {
			runner = getAlertRunner((ObjectExecutedEvent) event);
		}
		else if (event instanceof VanillaActionEvent) {
			runner = getAlertRunner((VanillaActionEvent) event);
		}
		else if (event instanceof RepositoryItemEvent) {
			runner = getAlertRunner((RepositoryItemEvent) event);
		}
		else if (event instanceof ItemVersionEvent) {
			runner = getAlertRunner((ItemVersionEvent) event);
		}

		if (runner != null) {
			try {
				runner.execute();
			} catch (Exception e) {
				Logger.getLogger(getClass()).error("Error when running alert - " + e.getMessage(), e);
			}
		}
		
//		try {
//			runFreeMetricsAlert(event);
//		} catch (Exception e) {
//			Logger.getLogger(getClass()).error("Error when running alert - " + e.getMessage(), e);
//		}
		
		
		//Handle validation
		runner = null;
		if (event instanceof ObjectExecutedEvent) {
			runner = checkValidation((ObjectExecutedEvent) event);
		}

		if (runner != null) {
			try {
				runner.execute();
			} catch (Exception e) {
				Logger.getLogger(getClass()).error("Error when running validation - " + e.getMessage(), e);
			}
		}
	}

//	private void runFreeMetricsAlert(IVanillaEvent event) throws Exception {
//		if(event instanceof ObjectExecutedEvent) {
//			ObjectExecutedEvent execEvent = (ObjectExecutedEvent) event;
//			
//			VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
//			IVanillaContext vanillaContext = new BaseVanillaContext(conf.getVanillaServerUrl(), conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
//			RemoteFreeMetricsManager fmapi = new RemoteFreeMetricsManager(vanillaContext);
//			
//			fmapi.checkAlerts(execEvent.getObjectIdentifier().getDirectoryItemId());
//		}
//		
//	}

	private AlertToActionRuntime getAlertRunner(VanillaActionEvent event) {
		Repository repository = null;
		try {
			repository = OSGIHelper.getRepositoryManager().getRepositoryById(event.getRepositoryId());
			if (repository == null) {
				throw new Exception("Event thrown by a non registered Repository... this is weird....");
			}
		} catch (Exception ex) {
			return null;
		}

		try {
			IVanillaContext vanillaContext = null;
			VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
			if (event.getSessionId() != null) {
				VanillaSession session = OSGIHelper.getSystemManager().getSession(event.getSessionId());
				vanillaContext = new BaseVanillaContext(conf.getVanillaServerUrl(), session.getUser().getLogin(), session.getUser().getPassword());
			}
			if (vanillaContext == null) {
				Logger.getLogger(getClass()).warn("Unable to extract the user that triggered the event.The Root use will be used to perform handle the event");
				vanillaContext = new BaseVanillaContext(conf.getVanillaServerUrl(), conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
			}
			
			Group grp = OSGIHelper.getSecurityManager().getGroupById(event.getGroupId());
			if(grp == null) {
				grp = new Group();
				grp.setId(-1);
			}

			IRepositoryContext repositoryContext = new BaseRepositoryContext(vanillaContext, grp, repository);

			IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaContext);
			RemoteRepositoryApi repositoryApi = new RemoteRepositoryApi(repositoryContext);

			List<Alert> alerts = repositoryApi.getAlertService().getAlertsByType(TypeEvent.SYSTEM_TYPE);

			if (alerts != null && !alerts.isEmpty()) {
				AlertToActionRuntime alertRunner = new AlertToActionRuntime(event, alerts, -1, vanillaApi, repository, repositoryContext);
				return alertRunner;
			}
		} catch (Exception ex) {
			Logger.getLogger(getClass()).error(ex.getMessage(), ex);
		}

		return null;
	}

	private AlertToActionRuntime getAlertRunner(RepositoryItemEvent event) {
		Repository repository = null;
		try {
			repository = OSGIHelper.getRepositoryManager().getRepositoryById(event.getRepositoryId());
			if (repository == null) {
				throw new Exception("Event thrown by a non registered Repository... this is weird....");
			}
		} catch (Exception ex) {
			return null;
		}

		try {
			IVanillaContext vanillaContext = null;
			VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
			if (event.getSessionId() != null) {
				VanillaSession session = OSGIHelper.getSystemManager().getSession(event.getSessionId());
				vanillaContext = new BaseVanillaContext(conf.getVanillaServerUrl(), session.getUser().getLogin(), session.getUser().getPassword());
			}
			if (vanillaContext == null) {
				Logger.getLogger(getClass()).warn("Unable to extract the user that triggered the event.The Root use will be used to perform handle the event");
				vanillaContext = new BaseVanillaContext(conf.getVanillaServerUrl(), conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
			}
			
			Group grp = OSGIHelper.getSecurityManager().getGroupById(event.getGroupId());
			if(grp == null) {
				grp = new Group();
				grp.setId(-1);
			}

			IRepositoryContext repositoryContext = new BaseRepositoryContext(vanillaContext, grp, repository);

			IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaContext);
			RemoteRepositoryApi repositoryApi = new RemoteRepositoryApi(repositoryContext);

			List<Alert> events = repositoryApi.getAlertService().getAlertsByTypeAndDirectoryItemId(TypeEvent.OBJECT_TYPE, event.getDirectoryItemId(), event.getRepositoryId());

			if (events != null && !events.isEmpty()) {
				AlertToActionRuntime alertRunner = new AlertToActionRuntime(event, events, -1, vanillaApi, repository, repositoryContext);
				return alertRunner;
			}
		} catch (Exception ex) {
			Logger.getLogger(getClass()).error(ex.getMessage(), ex);
		}

		return null;
	}

	private AlertToActionRuntime getAlertRunner(ObjectExecutedEvent event) {

		Repository repository = null;
		try {
			repository = OSGIHelper.getRepositoryManager().getRepositoryById(event.getObjectIdentifier().getRepositoryId());
			if (repository == null) {
				throw new Exception("Event thrown by a non registered Repository... this is weird....");
			}
		} catch (Exception ex) {
			return null;
		}

		try {
			IVanillaContext vanillaContext = null;
			VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
			if (event.getSessionId() != null) {
				VanillaSession session = OSGIHelper.getSystemManager().getSession(event.getSessionId());
				vanillaContext = new BaseVanillaContext(conf.getVanillaServerUrl(), session.getUser().getLogin(), session.getUser().getPassword());
			}
			if (vanillaContext == null) {
				Logger.getLogger(getClass()).warn("Unable to extract the user that triggered the event.The Root use will be used to perform handle the event");
				vanillaContext = new BaseVanillaContext(conf.getVanillaServerUrl(), conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
			}
			
			Group grp = OSGIHelper.getSecurityManager().getGroupById(event.getGroupId());
			if(grp == null) {
				grp = new Group();
				grp.setId(-1);
			}
			
			IRepositoryContext repositoryContext = new BaseRepositoryContext(vanillaContext, grp, repository);

			IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaContext);
			RemoteRepositoryApi repositoryApi = new RemoteRepositoryApi(repositoryContext);

			List<Alert> events = repositoryApi.getAlertService().getAlertsByTypeAndDirectoryItemId(TypeEvent.OBJECT_TYPE, event.getObjectIdentifier().getDirectoryItemId(), event.getObjectIdentifier().getRepositoryId());
			
			IFreeMetricsManager manager = new RemoteFreeMetricsManager(vanillaApi.getVanillaContext());
			List<Alert> kpis = new ArrayList<Alert>();
			
			for(bpm.fm.api.model.Metric metric : manager.getMetrics()) {
				if(metric.getEtlId() == event.getObjectIdentifier().getDirectoryItemId()) {
					for(Alert alertkpi : metric.getAlerts()) {
						kpis.add(alertkpi);
					}
				}
			}
			
			events.addAll(kpis);
			
			if (!events.isEmpty()) {
				AlertToActionRuntime alertRunner = new AlertToActionRuntime(null, events, event.getObjectIdentifier().getDirectoryItemId(), vanillaApi, repository, repositoryContext);
				return alertRunner;
			}
		} catch (Exception ex) {
			Logger.getLogger(getClass()).error(ex.getMessage(), ex);

		}
		return null;
	}
	
	private AlertToActionRuntime getAlertRunner(ItemVersionEvent event) {
		Repository repository = null;
		try {
			repository = OSGIHelper.getRepositoryManager().getRepositoryById(event.getRepositoryId());
			if (repository == null) {
				throw new Exception("Event thrown by a non registered Repository... this is weird....");
			}
		} catch (Exception ex) {
			return null;
		}

		try {
			IVanillaContext vanillaContext = null;
			VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
			if (event.getSessionId() != null) {
				VanillaSession session = OSGIHelper.getSystemManager().getSession(event.getSessionId());
				vanillaContext = new BaseVanillaContext(conf.getVanillaServerUrl(), session.getUser().getLogin(), session.getUser().getPassword());
			}
			if (vanillaContext == null) {
				Logger.getLogger(getClass()).warn("Unable to extract the user that triggered the event.The Root use will be used to perform handle the event");
				vanillaContext = new BaseVanillaContext(conf.getVanillaServerUrl(), conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
			}
			
			Group grp = OSGIHelper.getSecurityManager().getGroupById(event.getGroupId());
			if(grp == null) {
				grp = new Group();
				grp.setId(-1);
			}

			IRepositoryContext repositoryContext = new BaseRepositoryContext(vanillaContext, grp, repository);

			IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaContext);
			RemoteRepositoryApi repositoryApi = new RemoteRepositoryApi(repositoryContext);

			List<Alert> events = repositoryApi.getAlertService().getAlertsByTypeAndDirectoryItemId(TypeEvent.OBJECT_TYPE, event.getDirectoryItemId(), event.getRepositoryId());

			if (events != null && !events.isEmpty()) {
				AlertToActionRuntime alertRunner = new AlertToActionRuntime(event, events, -1, vanillaApi, repository, repositoryContext);
				return alertRunner;
			}
		} catch (Exception ex) {
			Logger.getLogger(getClass()).error(ex.getMessage(), ex);
		}

		return null;
	}

	private IAlertExecutor checkValidation(ObjectExecutedEvent event) {
		Repository repository = null;
		try {
			repository = OSGIHelper.getRepositoryManager().getRepositoryById(event.getObjectIdentifier().getRepositoryId());
			if (repository == null) {
				throw new Exception("Event thrown by a non registered Repository... this is weird....");
			}
		} catch (Exception ex) { }
	
		try {
			IVanillaContext vanillaContext = null;
			VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
			if (event.getSessionId() != null) {
				VanillaSession session = OSGIHelper.getSystemManager().getSession(event.getSessionId());
				vanillaContext = new BaseVanillaContext(conf.getVanillaServerUrl(), session.getUser().getLogin(), session.getUser().getPassword());
			}
			if (vanillaContext == null) {
				Logger.getLogger(getClass()).warn("Unable to extract the user that triggered the event.The Root use will be used to perform handle the event");
				vanillaContext = new BaseVanillaContext(conf.getVanillaServerUrl(), conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
			}
			
			Group grp = OSGIHelper.getSecurityManager().getGroupById(event.getGroupId());
			if(grp == null) {
				grp = new Group();
				grp.setId(-1);
			}
			
			IRepositoryContext repositoryContext = new BaseRepositoryContext(vanillaContext, grp, repository);
	
			IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaContext);
			IRepositoryApi repositoryApi = new RemoteRepositoryApi(repositoryContext);
	
			List<Validation> validations = repositoryApi.getRepositoryService().getValidationByStartEtl(event.getObjectIdentifier().getDirectoryItemId());
			if (validations != null && !validations.isEmpty()) {
				return new ValidationRuntime(vanillaApi, repositoryApi, validations, event);
			}
		} catch (Exception ex) {
			Logger.getLogger(getClass()).error(ex.getMessage(), ex);
		}
		return null;
	}

}
