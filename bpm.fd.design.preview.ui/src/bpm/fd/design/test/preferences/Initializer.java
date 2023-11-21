package bpm.fd.design.test.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import bpm.fd.design.test.Activator;

public class Initializer extends AbstractPreferenceInitializer {

	public Initializer() {
		
	}

	@Override
	public void initializeDefaultPreferences() {
		Activator.getDefault().getPreferenceStore().setDefault(PreferencesConstants.P_JETTY_PORT, 9191);
		Activator.getDefault().getPreferenceStore().setDefault(PreferencesConstants.P_DEFAULT_REPOSITORY_URL, "http://localhost:8080/BIRepository"); //$NON-NLS-1$
		Activator.getDefault().getPreferenceStore().setDefault(PreferencesConstants.P_DEFAULT_VANILLA_URL, "http://localhost:7171/VanillaRuntime"); //$NON-NLS-1$
		Activator.getDefault().getPreferenceStore().setDefault(PreferencesConstants.P_DEFAULT_VANILLA_LOGIN, "system"); //$NON-NLS-1$
		Activator.getDefault().getPreferenceStore().setDefault(PreferencesConstants.P_DEFAULT_VANILLA_PASSWORD, "system"); //$NON-NLS-1$
		Activator.getDefault().getPreferenceStore().setDefault(PreferencesConstants.P_DEFAULT_VANILLA_GROUP, "System"); //$NON-NLS-1$

	}

}
