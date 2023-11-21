package bpm.vanilla.server.reporting;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import bpm.metadata.birt.oda.runtime.ConnectionPool;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public class Activator implements BundleActivator {

	private Thread loadThread;
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		try {
			loadThread = new Thread() {
				@Override
				public void run() {
					
					try {
						Thread.sleep(100000);
						String metadataIds = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.metadata.preload.ids");
						String groupNames = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.metadata.preload.group.names");
						if (metadataIds != null && groupNames != null) {
							String[] arrayIds = metadataIds.split(",");
							String[] arrayGroupNames = groupNames.split(",");
							
							String url = ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl();
							String user = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
							String pass = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
							String rep = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_PUBLIC_REPOSITORY_ID);
							
							for(String group : arrayGroupNames) {
								for(String metadata : arrayIds) {
									Properties props = new Properties();
									props.put("URL", url);
									props.put("USER", user);
									props.put("PASSWORD", pass);
									props.put("DIRECTORY_ITEM_ID", metadata);
									props.put("GROUP_NAME", group);
									props.put("VANILLA_URL", url);
									props.put("REPOSITORY_ID", rep);
									ConnectionPool.getConnection(props);
									
									Logger.getLogger(getClass()).debug("loaded metadata " + metadata + " for group " + group);
								}
							}
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			};
			loadThread.start();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}

}
