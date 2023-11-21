package metadata.client.preferences;

import metadataclient.Activator;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public PreferenceInitializer() {}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		store.setDefault(PreferenceConstants.P_BROWSE_ROW_NUMBER, 100);
		store.setDefault(PreferenceConstants.P_GROUP_PROVIDER_URL, "http://localhost:8080/vanilla"); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.P_KETTLE_CARTE, ""); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.P_REPOSITORY, "http://localhost:8080/BIRepository"); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.P_SHOW_MENU_AT_STARTUP, true); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.P_CONFIRM_DELETION, true); //$NON-NLS-1$
	}

}
