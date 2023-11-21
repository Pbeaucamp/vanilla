package bpm.oda.driver.reader.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import bpm.oda.driver.reader.Activator;


public class PreferencesInitializer extends AbstractPreferenceInitializer {

	public PreferencesInitializer() {
		
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		store.setDefault(PreferencesConstants.PREF_VANILLA_URL, "http://localhost:8080/vanilla"); //$NON-NLS-1$
		store.setDefault(PreferencesConstants.PREF_VANILLA_LOGIN, "system"); //$NON-NLS-1$
		store.setDefault(PreferencesConstants.PREF_VANILLA_PASSWORD, "system"); //$NON-NLS-1$


	}

}
