package bpm.fd.design.ui.rcp.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import bpm.fd.design.ui.rcp.Activator;



public class PreferenceInitializers extends AbstractPreferenceInitializer {

	public PreferenceInitializers() {
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(Preferences.SHOW_WELCOME_AT_STARTUP, true);

	}

}
