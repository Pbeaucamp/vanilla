package bpm.workflow.runtime.resources.variables;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class VariablesHelper {
	private static final String VALUE_VANILLA_HOME = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_WORKFLOW_HOME);
	private static final String VALUE_GENERATED_REPORTS_HOME = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_WORKFLOW_GENERATED_REPORTS_HOME);
	private static final String VALUE_VANILLA_FILES = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_WORKFLOW_FILES);
	private static final String VALUE_VANILLA_TEMPORARY_FILES = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_WORKFLOW_TEMPORARY_FILES);
	
	
	public static IVanillaAPI getVanillaApi(){
		String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl();
		IVanillaAPI remoteAPI = new RemoteVanillaPlatform(vanillaUrl, 
				ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), 
				ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
		
		return remoteAPI;
	}
	
	public static String parseString(String toparse) {
		
		if (toparse == null || !toparse.contains("{$")) {
			return toparse;
		}
		
		List<String> varsString = new ArrayList<String>();
		for(Variable variable : ListVariable.getInstance().getVariables()){
			varsString.add(variable.getName());
		}
		for(String nomvar : varsString){
			if(nomvar.equalsIgnoreCase("{$E_TIME}")){
				Date actuelle = new Date();
				toparse = toparse.replace(nomvar, actuelle.toLocaleString());
			}
			else if(nomvar.equalsIgnoreCase("{$IP}")){
				toparse = toparse.replace(nomvar, ListVariable.getInstance().getVariable(nomvar).getValues().get(0));
			}
			else if(nomvar.equalsIgnoreCase(ListVariable.VANILLA_HOME)){

				toparse = toparse.replace(ListVariable.VANILLA_HOME, VALUE_VANILLA_HOME);
			}
			else if(nomvar.equalsIgnoreCase("{$VANILLA_TEMPORARY_FILES}")){
				toparse = toparse.replace("{$VANILLA_TEMPORARY_FILES}", VALUE_VANILLA_TEMPORARY_FILES);
			}
			else if(nomvar.equalsIgnoreCase(ListVariable.GENERATED_REPORTS_HOME)){
				toparse = toparse.replace(ListVariable.GENERATED_REPORTS_HOME, VALUE_GENERATED_REPORTS_HOME);

			}
			else if(nomvar.equalsIgnoreCase("{$VANILLA_FILES}")){
				toparse = toparse.replace("{$VANILLA_FILES}", VALUE_VANILLA_FILES);
				
			}
			else if (nomvar.equalsIgnoreCase(ListVariable.USER_DIR)) {
				toparse = toparse.replace(ListVariable.USER_DIR, System.getProperty("user.dir"));
			}
			else if (nomvar.equalsIgnoreCase("{$VANILLA_MAIL}")) {
				toparse = toparse.replace(nomvar, "vanilla@bpm-conseil.com");
			}
			else{
				try {
					toparse = toparse.replace(nomvar, ListVariable.getInstance().getVariable(nomvar).getValues().get(0));
				} catch(Exception e) {

				}
			}
			
		}
		
		return toparse;
		
	}
}
