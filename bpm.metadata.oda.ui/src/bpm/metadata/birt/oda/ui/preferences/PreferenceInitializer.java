package bpm.metadata.birt.oda.ui.preferences;



import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import bpm.metadata.birt.oda.ui.Activator;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public PreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		store.setDefault(CommonPreferenceConstants.P_BPM_VANILLA_URL, "http://localhost:7171/VanillaRuntime"); //$NON-NLS-1$
		store.setDefault(CommonPreferenceConstants.P_BPM_REPOSITORY_LOGIN, "system"); //$NON-NLS-1$
		store.setDefault(CommonPreferenceConstants.P_BPM_REPOSITORY_PASSWORD, "system"); //$NON-NLS-1$
		
	}

}
