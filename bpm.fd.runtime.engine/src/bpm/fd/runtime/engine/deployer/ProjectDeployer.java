package bpm.fd.runtime.engine.deployer;

import java.util.HashMap;

import bpm.fd.api.core.model.FdProject;
import bpm.fd.runtime.model.DashBoard;
import bpm.fd.runtime.model.DashInstance;
import bpm.fd.runtime.model.controler.Controler;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;

/**
 * Helper to create the JSP and generate all needed resources files
 * @author LCA
 *
 */
public class ProjectDeployer {
	
//	public static String deploy(IObjectIdentifier identifier, Group group, User user, FdProject project, String localeLanguage, 
//			boolean override, HashMap<String, String> parameters) throws Exception{
//		DashBoard d = Controler.getInstance().deployDashBoard(identifier, project, override, null);
//		
//		DashInstance instance = Controler.getInstance().createInstance(identifier, group, user, localeLanguage, d);
//		
//		setParameters(parameters, instance);
//		
//		return instance.getRelativeUrl();
//	}
	
	public static String deploy(IObjectIdentifier identifier, IRepositoryContext ctx, User user, FdProject project, String localeLanguage, 
			boolean override, HashMap<String, String> parameters) throws Exception{
		DashBoard d = Controler.getInstance().deployDashBoard(identifier, project, override, ctx);
		
		DashInstance instance = Controler.getInstance().createInstance(identifier, ctx.getGroup(), user, localeLanguage, d);
		
		setParameters(parameters, instance);
		
		return instance.getRelativeUrl();
	}

	private static void setParameters(HashMap<String, String> parameters, DashInstance instance) {
		try {
			for(String p : parameters.keySet()) {
				instance.setParameter(p, parameters.get(p));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String deployForm(boolean isValidation, IObjectIdentifier identifier, Group group, User user, FdProject project, String localeLanguage, 
			boolean override, HashMap<String, String> hiddenParameterMap) throws Exception{
		DashBoard d = Controler.getInstance().deployForm(isValidation, identifier, project, override, hiddenParameterMap);
		
		DashInstance instance = Controler.getInstance().createInstance(identifier, group, user, localeLanguage, d);
		return instance.getRelativeUrl();
	}
}
