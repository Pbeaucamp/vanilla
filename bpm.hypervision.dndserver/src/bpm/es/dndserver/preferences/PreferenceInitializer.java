package bpm.es.dndserver.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import bpm.es.dndserver.Activator;
import bpm.es.dndserver.Messages;


public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public PreferenceInitializer() {
		
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		store.setDefault(PreferenceConstants.TARGET_VANILLA_SERVER, "http://localhost:7171/VanillaRuntime"); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.TARGET_REPOSITORY_NAME, ""); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.TARGET_VANILLA_USER, "system"); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.TARGET_VANILLA_PASSWORD, "system"); //$NON-NLS-1$

	}

}
