package bpm.norparena.ui.menu.client.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import bpm.norparena.ui.menu.Activator;



public class Initializer extends AbstractPreferenceInitializer {

	public Initializer() {
		
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		store.setDefault(PreferenceConstants.P_BI_REPOSITIRY_USER, "system"); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.P_BI_REPOSITIRY_PASSWORD, "system"); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.p_BI_SECURITY_SERVER, "http://localhost:7171/VanillaRuntime"); //$NON-NLS-1$

		store.setDefault(PreferenceConstants.P_ROWS_BY_CHUNK, 5);
	}

}
