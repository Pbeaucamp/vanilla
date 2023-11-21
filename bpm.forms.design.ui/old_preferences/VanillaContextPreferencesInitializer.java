package bpm.forms.design.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import bpm.forms.design.ui.Activator;



public class VanillaContextPreferencesInitializer extends AbstractPreferenceInitializer {

	public static final String VANILLA_URL = "bpm.vanilla.context.vanillaUrl";
	public static final String VANILLA_LOGIN = "bpm.vanilla.context.vanillaLogin";
	public static final String VANILLA_PASSWORD = "bpm.vanilla.context.vanillaPassword";
	public static final String VANILLA_GROUP_ID = "bpm.vanilla.context.vanillaGroupId";
	
	
	public VanillaContextPreferencesInitializer() {
		
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(VANILLA_URL, "http://localhost:8080/vanilla");
		store.setDefault(VANILLA_LOGIN, "system");
		store.setDefault(VANILLA_PASSWORD, "system");
		store.setDefault(VANILLA_GROUP_ID, 1);

	}

}
