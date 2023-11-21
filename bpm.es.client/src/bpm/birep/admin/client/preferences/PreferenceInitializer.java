package bpm.birep.admin.client.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import adminbirep.Activator;


public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public PreferenceInitializer() {
		
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		store.setDefault(PreferenceConstants.P_BI_REPOSITORY, "default]http://localhost:8080/BIRepository]system]system"); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.P_CURRENT_REPOSITORY, "default"); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.P_BI_REPOSITIRY_PASSWORD, ""); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.P_BI_REPOSITIRY_USER, ""); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.P_BI_FREEMETRICS_URL, "jdbc:mysql://localhost/vanilla_kpi"); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.P_BI_FREEMETRICS_USER, "root"); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.P_BI_FREEMETRICS_PASSWORD, "root"); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.P_ROWS_BY_CHUNK, 20);
		store.setDefault(PreferenceConstants.P_JDBC_DRIVER, "MySQL]com.mysql.jdbc.Driver;"); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.P_BPM_PACKAGES_FOLDER, Platform.getInstallLocation().getURL().getPath().toString() + "resources/packages/"); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.P_BPM_TEMP, Platform.getInstallLocation().getURL().getPath().toString() + "temp"); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.P_BPM_HISTORIC_IMPORT_FILE, Platform.getInstallLocation().getURL().getPath().toString() + "resources/packagesImport.xml"); //$NON-NLS-1$
		
		store.setDefault(PreferenceConstants.P_BPM_VANILLA_PLACE_URL, "http://place.bpm-conseil.com:8181/VanillaPlace"); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.P_BPM_VANILLA_PLACE_LOGIN, "user"); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.P_BPM_VANILLA_PLACE_PASSWORD, "user"); //$NON-NLS-1$

	}

}
