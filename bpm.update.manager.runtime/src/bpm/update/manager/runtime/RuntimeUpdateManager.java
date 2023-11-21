package bpm.update.manager.runtime;

import java.util.List;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;

import bpm.update.manager.api.IRuntimeUpdateManager;

public class RuntimeUpdateManager implements IRuntimeUpdateManager {

	private Logger logger = Logger.getLogger(RuntimeUpdateManager.class);

	private ComponentUpdateRuntime component;

	public RuntimeUpdateManager(ComponentUpdateRuntime component) {
		this.component = component;
	}

	@Override
	public void undeploy(List<String> plugins) throws Exception {
		List<Bundle> bundles = component.getBundles();

		for (String plugin : plugins) {
			for (Bundle bundle : bundles) {
				if (plugin.equals(bundle.getSymbolicName())) {
					try {
						logger.info("Uninstalling bundle '" + bundle.getSymbolicName() + "'.");
						bundle.uninstall();
						return;
					} catch (Exception e) {
						e.printStackTrace();
						throw new Exception("Unable to undeploy the bundle '" + bundle.getSymbolicName() + "' : " + e.getMessage());
					}
				}
			}
		}
	}

	@Override
	public void shutdownOsgi() throws Exception {
		component.shutdown();
	}
}
