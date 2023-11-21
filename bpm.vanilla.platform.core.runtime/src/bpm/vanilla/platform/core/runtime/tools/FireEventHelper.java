package bpm.vanilla.platform.core.runtime.tools;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class FireEventHelper {

	public static void fireEvent(IVanillaEvent event) throws Exception {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String vanillaUrl = config.getVanillaServerUrl();
		String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);

		IVanillaContext vanillaCtx = new BaseVanillaContext(vanillaUrl, login, password);

		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx);
		vanillaApi.getListenerService().fireEvent(event);
	}
}
