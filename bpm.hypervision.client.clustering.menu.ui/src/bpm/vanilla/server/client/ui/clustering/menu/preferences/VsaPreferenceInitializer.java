package bpm.vanilla.server.client.ui.clustering.menu.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;


import bpm.vanilla.server.client.ui.clustering.menu.Activator;
import bpm.vanilla.server.client.ui.clustering.menu.Messages;


public class VsaPreferenceInitializer extends AbstractPreferenceInitializer {

	public static final String VANILLA_URL = "bpm.vanilla.server.client.ui.clustering.menu.preferences.vanillaUrl"; //$NON-NLS-1$
	public static final String VANILLA_LOGIN = "bpm.vanilla.server.client.ui.clustering.menu.preferences.vanillaLogin"; //$NON-NLS-1$
	public static final String VANILLA_PASSWORD = "bpm.vanilla.server.client.ui.clustering.menu.preferences.vanillaPasword"; //$NON-NLS-1$
	public static final String SAVE_CONNECTION_INFOS = "bpm.vanilla.server.client.ui.clustering.menu.preferences.saveConnectionInfo"; //$NON-NLS-1$
	
	public VsaPreferenceInitializer() {
		
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(VANILLA_URL, "http://localhost:7171/VanillaRuntime"); //$NON-NLS-1$
		store.setDefault(VANILLA_LOGIN, "system"); //$NON-NLS-1$
		store.setDefault(VANILLA_PASSWORD, "system"); //$NON-NLS-1$
		store.setDefault(SAVE_CONNECTION_INFOS, true);

	}

}
