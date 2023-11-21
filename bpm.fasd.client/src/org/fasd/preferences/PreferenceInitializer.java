package org.fasd.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.fasd.i18N.LanguageText;
import org.freeolap.FreemetricsPlugin;


public class PreferenceInitializer extends AbstractPreferenceInitializer {
	public void initializeDefaultPreferences() {
		IPreferenceStore store = FreemetricsPlugin.getDefault().getPreferenceStore();
		IPreferenceStore global = PlatformUI.getPreferenceStore();
		
		global.setValue(IWorkbenchPreferenceConstants.DOCK_PERSPECTIVE_BAR, IWorkbenchPreferenceConstants.TOP_RIGHT);
		global.setValue(IWorkbenchPreferenceConstants.OPEN_NEW_PERSPECTIVE, IWorkbenchPreferenceConstants.OPEN_PERSPECTIVE_REPLACE);
		global.setValue(IWorkbenchPreferenceConstants.SHOW_TEXT_ON_PERSPECTIVE_BAR, false);
		global.setValue(IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS, false);
		
		store.setDefault(PreferenceConstants.P_SHOWPERSPECTIVEBAR, true);
		store.setDefault(PreferenceConstants.P_SHOWFASTVIEWBAR, false);
		store.setDefault(PreferenceConstants.P_SHOWCOOLBAR, true);
		store.setDefault(PreferenceConstants.P_SHOWSTATUSLINE, false);
		store.setDefault(PreferenceConstants.P_SHOWNEWATSTARTUP, true);
		store.setDefault(PreferenceConstants.P_RECENTFILE1,""); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.P_RECENTFILE2,""); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.P_RECENTFILE3,""); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.P_RECENTFILE4,""); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.P_RECENTFILE5,""); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.P_SCHEMAFOLDER,"C:\\Documents and Settings\\FRED\\Bureau"); //$NON-NLS-1$
		
		store.setDefault(PreferenceConstants.P_BROWSERFIRSTXLINES,"100"); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.P_BROWSEDIMENSIONXLINES,""); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.P_CUBEEXPLORERXLINES,""); //$NON-NLS-1$

		store.setDefault(PreferenceConstants.P_LANG, LanguageText.getCurrentLanguage());
		
	}
	
	
	
}
