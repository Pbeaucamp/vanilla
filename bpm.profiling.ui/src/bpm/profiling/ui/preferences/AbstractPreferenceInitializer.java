package bpm.profiling.ui.preferences;



import org.eclipse.jface.preference.IPreferenceStore;

import bpm.profiling.ui.Activator;

public class AbstractPreferenceInitializer extends
		org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer {

	public AbstractPreferenceInitializer() {
		
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		store.setDefault(PreferenceConstants.P_SCHEDULER_SERVER_URL, "http://localhost:8080/scheduler-server");
		store.setDefault(PreferenceConstants.P_BPM_REPOSITORY_URL, "http://localhost:7171/VanillaRuntime");
		store.setDefault(PreferenceConstants.P_BPM_REPOSITORY_LOGIN, "system");
		store.setDefault(PreferenceConstants.P_BPM_REPOSITORY_PASSWORD, "system");

	}

}
