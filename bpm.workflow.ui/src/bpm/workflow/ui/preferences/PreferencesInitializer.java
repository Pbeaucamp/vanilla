package bpm.workflow.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;

import bpm.workflow.ui.Activator;

/**
 * Initializer of the preferences
 * @author CHARBONNIER, CAMUS, MARTIN
 *
 */
public class PreferencesInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		IPreferenceStore global = PlatformUI.getPreferenceStore();
		
		global.setValue(IWorkbenchPreferenceConstants.DOCK_PERSPECTIVE_BAR, IWorkbenchPreferenceConstants.TOP_RIGHT);
		global.setValue(IWorkbenchPreferenceConstants.OPEN_NEW_PERSPECTIVE, IWorkbenchPreferenceConstants.OPEN_PERSPECTIVE_REPLACE);
		global.setValue(IWorkbenchPreferenceConstants.SHOW_TEXT_ON_PERSPECTIVE_BAR, false);
		global.setValue(IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS, false);
		
		store.setDefault(PreferencesConstants.P_URLPROF, "jdbc:mysql://localhost:3306/profiling"); //$NON-NLS-1$
		store.setDefault(PreferencesConstants.P_USERPROF, "biplatform"); //$NON-NLS-1$
		store.setDefault(PreferencesConstants.P_PASSWORDPROF, "biplatform"); //$NON-NLS-1$
		store.setDefault(PreferencesConstants.P_DRIVERJDBCPROF, "com.mysql.jdbc.Driver"); //$NON-NLS-1$
		store.setDefault(PreferencesConstants.P_URLREPO,"http://localhost:8080/BIRepository"); //$NON-NLS-1$
		store.setDefault(PreferencesConstants.P_USERREPO,"system"); //$NON-NLS-1$
		store.setDefault(PreferencesConstants.P_PASSWORDREPO,"system"); //$NON-NLS-1$
		store.setDefault(PreferencesConstants.P_URLVANILLA,"http://localhost:8080/vanilla"); //$NON-NLS-1$
		store.setDefault(PreferencesConstants.P_URLMAIL, "SMTP.****.****"); //$NON-NLS-1$
		store.setDefault(PreferencesConstants.P_USERMAIL, "system"); //$NON-NLS-1$
		store.setDefault(PreferencesConstants.P_PASSWORDMAIL, "system"); //$NON-NLS-1$

	}

}
