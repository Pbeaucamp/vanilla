package bpm.vanilla.designer.ui.common.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import bpm.vanilla.designer.ui.common.Activator;

public class Initializer extends AbstractPreferenceInitializer {

	public Initializer() {
		
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		store.setDefault(PreferencesConstants.P_VANILLA_GROUP_ID, 1);
		store.setDefault(PreferencesConstants.P_VANILLA_REP_ID, 1);
		store.setDefault(PreferencesConstants.P_VANILLA_LOGIN, "system");
		store.setDefault(PreferencesConstants.P_VANILLA_PASSWORD, "system");
		store.setDefault(PreferencesConstants.P_VANILLA_URL, "http://localhost:7171/VanillaRuntime");

	}

}
