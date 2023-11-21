package bpm.gateway.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import bpm.gateway.ui.Activator;

public class PreferencesInitializer extends AbstractPreferenceInitializer {

	public PreferencesInitializer() {}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		store.setDefault(PreferencesConstants.P_SHOW_MENU_AT_STARTUP, true);

	}

}
