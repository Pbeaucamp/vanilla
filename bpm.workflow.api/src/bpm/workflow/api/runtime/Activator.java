package bpm.workflow.api.runtime;

import java.util.Locale;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import bpm.smart.core.xstream.RemoteAdminManager;
import bpm.smart.core.xstream.RemoteSmartManager;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.User;
import bpm.workflow.runtime.IReportListenerProvider;
import bpm.workflow.runtime.ReportListener;


public class Activator implements  BundleActivator {
	private static Bundle bundle;
	
	@Override
	public void start(BundleContext context) throws Exception {
		bundle = context.getBundle();
		
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		bundle = null;
		
	}

	public static ReportListener getReportListener(){
		
		if (bundle == null){
			return null;
		}
		
		ServiceReference ref = bundle.getBundleContext().getServiceReference(IReportListenerProvider.class.getName());
		if (ref != null){
			ReportListener res = ((IReportListenerProvider)bundle.getBundleContext().getService(ref)).getReportListener();
			
			bundle.getBundleContext().ungetService(ref);
			return res;
		}
		return null;
	}
	
	
}
