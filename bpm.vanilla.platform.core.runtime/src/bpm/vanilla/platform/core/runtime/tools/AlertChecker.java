package bpm.vanilla.platform.core.runtime.tools;

import java.util.Date;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import bpm.fm.api.IFreeMetricsManager;
import bpm.fm.api.RemoteFreeMetricsManager;
import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.utils.LevelMember;
import bpm.fm.api.model.utils.MetricValue;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.alerts.AlertKpi;
import bpm.vanilla.platform.core.beans.alerts.Condition;
import bpm.vanilla.platform.core.beans.alerts.ConditionKpi;

public class AlertChecker {
	
	private static ScriptEngine mgr;
	
	static {
		mgr = new ScriptEngineManager().getEngineByName("JavaScript");
	}

	public static boolean checkAlert(Metric metric, Alert alert, Date date, IVanillaAPI vanillaApi) throws Exception {
		IFreeMetricsManager component = new RemoteFreeMetricsManager(vanillaApi.getVanillaContext());
		boolean fired = false;
		
		//get the value
		//check if the alert is filtered on an axis
		MetricValue value = null;
		if(alert.getEventObject() instanceof AlertKpi && alert.getEventModel() != null && ((AlertKpi)alert.getEventObject()).getAxisId() > 0 && ((AlertKpi)alert.getEventObject()).getLevelValue() != null && !((AlertKpi)alert.getEventObject()).getLevelValue().isEmpty()) {
			Axis axis = component.getAxis(((AlertKpi)alert.getEventObject()).getAxisId());
			List<MetricValue> vals = component.getValuesByDateAndLevelAndMetric(date, axis.getChildren().get(((AlertKpi)alert.getEventObject()).getLevelIndex()).getId(), metric.getId(), -1);
			
			LOOK:for(MetricValue val : vals) {
				for(LevelMember mem : val.getAxis()) {
					if(mem.getLabel().equals(((AlertKpi)alert.getEventObject()).getLevelValue())) {
						value = val;
						break LOOK;
					}
				}
			}
			
			
		}
		else {
			value = component.getValueByMetricAndDate(date, metric.getId(), -1);
		}
		
		boolean firedFinal = false;
		String operator = alert.getOperator();
		int operatorType = -1;
		if (operator.equalsIgnoreCase("and")) {
			operatorType = 0;
			firedFinal = true; //init
		}
		else if (operator.equalsIgnoreCase("or")) {
			operatorType = 1;
			firedFinal = false; //init
		}
		
		for(Condition condition : alert.getConditions()){
			//check the condition
			if(condition.getConditionObject() instanceof ConditionKpi){
				ConditionKpi model = (ConditionKpi) condition.getConditionObject();
				if(model.getType().equals(ConditionKpi.ALERT_TYPES[ConditionKpi.MISSING_TYPE])) {
					fired = checkMissingCondition(condition, value);
				}
				else if(model.getType().equals(ConditionKpi.ALERT_TYPES[ConditionKpi.STATE_TYPE])) {
					fired = checkStateCondition(condition, value);
				}
				else if(model.getType().equals(ConditionKpi.ALERT_TYPES[ConditionKpi.VALUE_TYPE])) {
					if(value != null){
						fired = checkValueCondition(condition, value);
					} else {
						fired = false;
					}
					
				}
				if(operatorType == 0 && fired == false){
					firedFinal = false; //one is false
					break;
				}
				if(operatorType == 1 && fired == true){
					firedFinal = true; //at least one is true
					break;
				}
			}
		}
//		if(firedFinal) {
//			//fire the event
//			fireEvent(alert, metric);
//		}
		
		return firedFinal;
	}

	private static boolean checkValueCondition(Condition condition, MetricValue value) throws ScriptException {
		
		double left = getFieldValue(value, condition.getLeftOperand());
		double right = getFieldValue(value, condition.getRightOperand());
		
		
		
		String formula = left + condition.getOperator() + right;
		
		
		String res = mgr.eval(formula).toString();
		if(res.equalsIgnoreCase("true")) {
			return true;
		}
		
		return false;
	}

	private static double getFieldValue(MetricValue value, String field) {
		if(field.equals(ConditionKpi.FIELDS[ConditionKpi.FIELD_VAL])) {
			return value.getValue();
		}
		else if(field.equals(ConditionKpi.FIELDS[ConditionKpi.FIELD_MAX])) {
			return value.getMaximum();
		}
		else if(field.equals(ConditionKpi.FIELDS[ConditionKpi.FIELD_MIN])) {
			return value.getMinimum();
		}
		else if(field.equals(ConditionKpi.FIELDS[ConditionKpi.FIELD_OBJ])) {
			return value.getObjective();
		}
		return 0;
	}

	private static boolean checkStateCondition(Condition condition, MetricValue value) {
		if(((ConditionKpi)condition.getConditionObject()).getStateType().equals(ConditionKpi.STATE_TYPES[ConditionKpi.STATE_ABOVE])) {
			return value.getHealth() > 0;
		}
		else if(((ConditionKpi)condition.getConditionObject()).getStateType().equals(ConditionKpi.STATE_TYPES[ConditionKpi.STATE_EQUAL])) {
			return value.getHealth() == 0;
		}
		else if(((ConditionKpi)condition.getConditionObject()).getStateType().equals(ConditionKpi.STATE_TYPES[ConditionKpi.STATE_UNDER])) {
			return value.getHealth() < 0;
		}
		return false;
	}

	private static boolean checkMissingCondition(Condition condition, MetricValue value) {
		
		if(value == null) {
			return true;
		}
		
		if(((ConditionKpi)condition.getConditionObject()).getMissingType().equals(ConditionKpi.MISSING_TYPES[ConditionKpi.MISSING_OBJ_ONLY])) {
			return value.getObjective() == 0.0;
		}
		else if(((ConditionKpi)condition.getConditionObject()).getMissingType().equals(ConditionKpi.MISSING_TYPES[ConditionKpi.MISSING_VAL_OBJ])) {
			return value.getValue() == 0.0 && value.getObjective() == 0.0;
		}
		else if(((ConditionKpi)condition.getConditionObject()).getMissingType().equals(ConditionKpi.MISSING_TYPES[ConditionKpi.MISSING_VAL_ONLY])) {
			return value.getValue() == 0.0;
		}
		return false;
	}

//	private static void fireEvent(Alert alert, Metric metric) throws Exception {
//		VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
//		IVanillaContext vanillaContext = new BaseVanillaContext(conf.getVanillaServerUrl(), conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
//		IVanillaAPI api = new RemoteVanillaPlatform(vanillaContext);
//		
//		if(event.getType().equals(MetricAlertEvent.TYPES.get(MetricAlertEvent.TYPE_BIG))) {
//			runObject(event.getItemId(), vanillaContext, api);
//		}
//		else if(event.getType().equals(MetricAlertEvent.TYPES.get(MetricAlertEvent.TYPE_MAIL))) {
//			
//			
//			String[] recipients = event.getRecipients().split(";");
//			for(String recipient : recipients) {
//				IMailConfig config = new MailConfig(recipient, metric.getResponsible(), event.getMessage(), "Alert on metric " + metric.getName(), true);
//				api.getVanillaSystemManager().sendEmail(config, new HashMap<String, InputStream>());
//			}
//			
//		}
//		//well... do nothing I guess....
//	}
//
//	private static void runObject(int itemId, IVanillaContext vanillaContext, IVanillaAPI api) throws Exception {
//		
//		String repositoryId = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_PUBLIC_REPOSITORY_ID);
//		String groupId = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_PUBLIC_GROUP_ID);
//		
//		Group group = api.getVanillaSecurityManager().getGroupById(Integer.parseInt(groupId));
//		Repository repository = api.getVanillaRepositoryManager().getRepositoryById(Integer.parseInt(repositoryId));
//		
//		IRepositoryContext ctx = new BaseRepositoryContext(vanillaContext, group, repository);
//		
//		User user = api.getVanillaSecurityManager().getUserByLogin(vanillaContext.getLogin());
//		
//		IRepositoryApi sock = new RemoteRepositoryApi(ctx);
//		RepositoryItem item = sock.getRepositoryService().getDirectoryItem(itemId);
//		if(item.getType() == IRepositoryApi.BIW_TYPE) {
//			RemoteWorkflowComponent workflow = new RemoteWorkflowComponent(vanillaContext);
//			
//			IObjectIdentifier ident = new ObjectIdentifier(repository.getId(), itemId);
//			
//			IRuntimeConfig config = new RuntimeConfiguration(group.getId(), ident, new ArrayList<VanillaGroupParameter>());
//			workflow.startWorkflowAsync(config);
//		}
//		else if(item.getType() == IRepositoryApi.GTW_TYPE) {
//			RemoteGatewayComponent big = new RemoteGatewayComponent(vanillaContext);
//			
//			IObjectIdentifier ident = new ObjectIdentifier(repository.getId(), itemId);
//			
//			GatewayRuntimeConfiguration config = new GatewayRuntimeConfiguration(ident, new ArrayList<VanillaGroupParameter>(), group.getId());
//			big.runGatewayAsynch(config, user);
//		}
//		
//	}
	
}
