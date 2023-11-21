package bpm.test;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.impl.RuntimeConfiguration;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteWorkflowComponent;


public class Sample {
	static final String s = "toto";
	static List<String> l;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		Pattern p = Pattern.compile("^[a-zA-Z0-9]*$");
		String myString= "fdsfdfd ' fdsf sd ' ";//"eaer fdf -- fdsf **dfs q$ fsd qds$q fds kgrep".replaceAll("[^a-zA-Z0-9]", "");
		myString = myString.replace("\'", "\\'");
			System.out.println(myString);
		
		
		
		
//		IVanillaContext ctx = new BaseVanillaContext("http://localhost:19190", "system", "system");
//	      
//		IGedComponent ged = new RemoteGedComponent(ctx);
//		ged.rebuildGedIndex();
//		
////	    IObjectIdentifier identifier = new ObjectIdentifier(1, 249);
////	      
////	    List<VanillaGroupParameter> p = new ArrayList<VanillaGroupParameter>();
////	      
////	    IRuntimeConfig runtimeConfig = new RuntimeConfiguration(1, identifier, p);
////		
////		
////		RemoteWorkflowComponent rem = new RemoteWorkflowComponent(ctx);
////		
////		try {
////			rem.undeployWorkflowModel(runtimeConfig);
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
////		rem.deployWorkflow(runtimeConfig);
////		rem.startWorkflow(runtimeConfig);
//		
////	      
////
////	    WorkflowRuntimeImpl run = new WorkflowRuntimeImpl();
////	    run.deployWorkflow(ctx, runtimeConfig);
////	    run.startWorkflow(ctx, runtimeConfig);
//
//	    System.out.println("ok");

	}

	private static void remove(String s) {
		System.out.println("ssssss " + l.size());
		l.remove(null);
		remove(s);
	}

}
