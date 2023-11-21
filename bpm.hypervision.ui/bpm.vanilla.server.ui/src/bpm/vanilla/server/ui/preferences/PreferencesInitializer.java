package bpm.vanilla.server.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import bpm.vanilla.server.ui.Activator;

public class PreferencesInitializer extends AbstractPreferenceInitializer {

	public PreferencesInitializer() {
		
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferencesConstants.REPORTING_SERVER_URL, "http://localhost:7171/VanillaRuntime");
	}

}
