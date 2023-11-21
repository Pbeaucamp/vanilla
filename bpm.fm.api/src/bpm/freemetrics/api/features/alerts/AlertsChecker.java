/**
 * 
 */
package bpm.freemetrics.api.features.alerts;

import java.io.File;
import java.util.Date;
import java.util.List;

import bpm.freemetrics.api.digester.beans.FmDigAction;
import bpm.freemetrics.api.features.actions.Action;
import bpm.freemetrics.api.features.actions.engine.MailSender;
import bpm.freemetrics.api.manager.FactoryManager;
import bpm.freemetrics.api.manager.FactoryManagerException;
import bpm.freemetrics.api.manager.IManager;
import bpm.freemetrics.api.organisation.metrics.Metric;
import bpm.freemetrics.api.organisation.metrics.MetricValues;
import bpm.freemetrics.api.utils.IConstants;
import bpm.freemetrics.api.utils.Tools;

/**
 * @author ansybelgarde
 *
 */
public class AlertsChecker {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//Le chemin vers le fichier de configuration spring pour fm
		String ressourcePath ="C:"+File.separator+"Documents and Settings"+File.separator+"Belgarde"+File.separator+"Bureau"+File.separator+"eclipse 3.3"+File.separator;// "/Users/ansybelgarde/Desktop/gwt-mac-1.5.3/WKSP_FM_WEB_STD_V2/freeMetricWeb_E/tomcat/webapps/ROOT/";

		//La key datasources dans le cas ou plusieurs sources ont ete configure
		String key = "MySQL";

		//Les elements d'authentification pour l'envois de mails
//		String senderName = "FmRegisteredUser@gmail.com";
//		String senderPassWord = "FmRegisteredUser20070207";


		String propertiesPath = ressourcePath+File.separator+"Ressources"+File.separator+"FM_Mail_Settings.properties";
		try {
			// 1 : initialiser l'api pour Spring
			initDS(ressourcePath,Tools.OS_TYPE_WINDOWS,key);
			IManager man = FactoryManager.getManager();

			// 2 : Analyser les données et extraire les alertes programmé sur les metrics
			List<MetricWithAlert> lstMwal = man.getMetricWithAlerts();

			for (MetricWithAlert mwal  : lstMwal) {

				if(mwal != null && mwal.getMaAlertId() != null 
						&& isActiveAlert(mwal.getMaAlertId(),man)
						&& mwal.getMaBeginDate().before(new Date())
						&& isValidCondition(mwal, man)){

					if(mwal.getMaEndDate() == null){
						boolean resultat = performAction(mwal,man,propertiesPath);
						System.out.println("Alerte check result is " +resultat);
					}else if(mwal.getMaEndDate() != null && mwal.getMaEndDate().after(new Date()) ){
						// 3 : Executer l'action si les conditions sont remplis
						boolean resultat = performAction(mwal,man,propertiesPath);
						System.out.println("Alerte check result is " +resultat);
					}
				}
			}
		} catch (FactoryManagerException e) {
			e.printStackTrace();
		}
	}

	public static boolean performAction(MetricWithAlert mwal,IManager man, String propertiesPath) {
		boolean succes = false;

		if(mwal.getMaAlertId() != null){

			Alert al = man.getAlertById(mwal.getMaAlertId());

			if(al != null && al.getAlActionId() != null){

				Action ac = man.getActionById( al.getAlActionId());

				if(ac != null){

					for (FmDigAction task : ac.getActionsToDo()) {

						if(task.getComponent() != null){

							if(task.getComponent().trim().equalsIgnoreCase(""+IConstants.ACTIONTYPE_SEND_TO_MAIL)){

								succes = new MailSender().execute(task,propertiesPath);

							}else if(task.getComponent().trim().equalsIgnoreCase(""+IConstants.ACTIONTYPE_SEND_TO_POPUP)){

							}else if(task.getComponent().trim().equalsIgnoreCase(""+IConstants.ACTIONTYPE_SEND_TO_SMS)){

							}else if(task.getComponent().trim().equalsIgnoreCase(""+IConstants.ACTIONTYPE_SEND_TO_SKYPE)){

							}
						}
					}

					//XXX ensuite en fonction de l'intervalle creer 1 acction scheuduled 
					//2 Mettre a jour alert pour sa prochaine execution

				}
			}
		}

		return succes;
	}

	public static boolean isActiveAlert(int maAlertId, IManager man) {
		boolean isActive = false;

		Alert al = man.getAlertById(maAlertId);

		if(al != null){
			isActive = al.getAlIsActive();
		}

		return isActive;
	}

	public static boolean isValidCondition(MetricWithAlert mwal, IManager man) {
		boolean condIsOk = false;

		AlertCondition cond = man.getAlertConditionForMWAId(mwal.getId());
		if(cond != null){
			String test = cond.getAcDefinition();
			if(Tools.isValid(test)){
				Metric m = man.getMetricById(mwal.getMaMetricsId());
				MetricValues val = null;


				if(m != null && mwal.getMaApplicationId() != null)
					val = man.getLastValueForAppIdAndMetrId(mwal.getMaApplicationId(), m.getId());

				if(val != null){
					MetricValues obj = man.getObjectifsForValueAndPeriod(val, val.getMvPeriodDate());

					if(obj != null){

						float actual = val.getMvValue();
						float min = obj.getMvMinValue();
						float max = obj.getMvMaxValue();
						float target = obj.getMvGlObjectif();

						if(test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_MAXIMUM.trim()+" < "+IConstants.VALUE_KEY_MINIMUM.trim()) 
								|| test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_MINIMUM.trim()+" > "+IConstants.VALUE_KEY_MAXIMUM.trim())){
							return min > max;
						}else if(test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_MAXIMUM.trim()+" = "+IConstants.VALUE_KEY_MINIMUM.trim()) 
								|| test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_MINIMUM.trim()+" = "+IConstants.VALUE_KEY_MAXIMUM.trim())){
							return Float.compare(min,max) == 0;
						}else if(test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_MAXIMUM.trim()+" > "+IConstants.VALUE_KEY_MINIMUM.trim()) 
								|| test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_MINIMUM.trim()+" < "+IConstants.VALUE_KEY_MAXIMUM.trim())){
							return	min < max;
						}
						
						else if(test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_MAXIMUM.trim()+" < "+IConstants.VALUE_KEY_ACTUAL.trim())  
								|| test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_ACTUAL.trim()+" > "+IConstants.VALUE_KEY_MAXIMUM.trim())){
							return actual > max;
						}else if(test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_MAXIMUM.trim()+" = "+IConstants.VALUE_KEY_ACTUAL.trim()) 
								|| test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_ACTUAL.trim()+" = "+IConstants.VALUE_KEY_MAXIMUM.trim())){
							return Float.compare(actual,max) == 0;
						}else if(test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_MAXIMUM.trim()+" > "+IConstants.VALUE_KEY_ACTUAL.trim()) 
								|| test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_ACTUAL.trim()+" > "+IConstants.VALUE_KEY_MAXIMUM.trim())){
							return max > actual;
						}
						
						else if(test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_ACTUAL.trim()+" < "+IConstants.VALUE_KEY_MINIMUM.trim()) 
								|| test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_MINIMUM.trim()+" > "+IConstants.VALUE_KEY_ACTUAL.trim())){
							return min > actual;
						}else if(test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_ACTUAL.trim()+" = "+IConstants.VALUE_KEY_MINIMUM.trim()) 
								|| test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_MINIMUM.trim()+" = "+IConstants.VALUE_KEY_ACTUAL.trim())){
							return Float.compare(min,actual) == 0;
						}else if(test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_ACTUAL.trim()+" > "+IConstants.VALUE_KEY_MINIMUM.trim()) 
								|| test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_MINIMUM.trim()+" < "+IConstants.VALUE_KEY_ACTUAL.trim()) ){
							return actual > min;
						}

						else if(test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_MAXIMUM.trim()+" > "+IConstants.VALUE_KEY_TARGET.trim()) 
								|| test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_TARGET.trim()+" < "+IConstants.VALUE_KEY_MAXIMUM.trim())){
							return max > target;
						}else if(test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_TARGET.trim()+" > "+IConstants.VALUE_KEY_MAXIMUM.trim()) 
								|| test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_MAXIMUM.trim()+" < "+IConstants.VALUE_KEY_TARGET.trim())){
							return max < target;
						}else if(test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_MAXIMUM.trim()+" = "+IConstants.VALUE_KEY_TARGET.trim()) 
								|| test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_TARGET.trim()+" = "+IConstants.VALUE_KEY_MAXIMUM.trim())){
							return Float.compare(target,max) == 0;
						}

						else if(test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_MINIMUM.trim()+" > "+IConstants.VALUE_KEY_TARGET.trim()) 
								|| test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_TARGET.trim()+" < "+IConstants.VALUE_KEY_MINIMUM.trim())){
							return min > target;
						}else if(test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_TARGET.trim()+" > "+IConstants.VALUE_KEY_MINIMUM.trim()) 
								|| test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_MINIMUM.trim()+" < "+IConstants.VALUE_KEY_TARGET.trim())){
							return min < target;
						}else if(test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_TARGET.trim()+" = "+IConstants.VALUE_KEY_MINIMUM.trim())
								|| test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_MINIMUM.trim()+" = "+IConstants.VALUE_KEY_TARGET.trim())){
							return Float.compare(target,min) == 0;
						}

						else if(test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_ACTUAL.trim()+" > "+IConstants.VALUE_KEY_TARGET.trim()) 
								|| test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_TARGET.trim()+" < "+IConstants.VALUE_KEY_ACTUAL.trim())){
							return actual > target;
						}else if(test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_TARGET.trim()+" > "+IConstants.VALUE_KEY_ACTUAL.trim()) 
								|| test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_ACTUAL.trim()+" < "+IConstants.VALUE_KEY_TARGET.trim())){
							return actual < target;
						}else if(test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_ACTUAL.trim()+" = "+IConstants.VALUE_KEY_TARGET.trim())
								|| test.trim().equalsIgnoreCase(IConstants.VALUE_KEY_TARGET.trim()+" = "+IConstants.VALUE_KEY_ACTUAL.trim())){
							return Float.compare(target,actual) == 0;
						}

					}
				}
			}
		}

		return condIsOk;
	}

	public static void initDS(String ressourcePath,int osType,String key) throws FactoryManagerException{
		FactoryManager.init(ressourcePath,osType);
	}

}
