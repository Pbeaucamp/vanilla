package bpm.metadata.birt.contribution.preferences;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import bpm.metadata.birt.contribution.Activator;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class VanillaItemsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private FieldEditor fieldUrl, fieldUser, fieldPassword;
	
	public VanillaItemsPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
//		setDescription("A demonstration of a preference page implementation");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		fieldUrl = new StringFieldEditor(PreferenceConstants.P_VANILLA_RUNTIME, "Vanilla Runtime URL: ", getFieldEditorParent());
		addField(fieldUrl);
		
		fieldUser = new StringFieldEditor(PreferenceConstants.P_VANILLA_USER, "User : ", getFieldEditorParent());
		addField(fieldUser);
		
		fieldPassword = new StringFieldEditor(PreferenceConstants.P_VANILLA_PASSWORD, "Password : ", getFieldEditorParent());
		addField(fieldPassword);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	@Override
	public boolean performOk() {
		IPreferenceStore store = getPreferenceStore();
		
		String vanillaUrl = store.getString(PreferenceConstants.P_VANILLA_RUNTIME);
		ConfigurationManager.getInstance().getVanillaConfiguration().setProperty(VanillaConfiguration.P_VANILLA_URL, vanillaUrl);
		
		String user = store.getString(PreferenceConstants.P_VANILLA_USER);
		ConfigurationManager.getInstance().getVanillaConfiguration().setProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN, user);
		
		String password = store.getString(PreferenceConstants.P_VANILLA_PASSWORD);
		ConfigurationManager.getInstance().getVanillaConfiguration().setProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD, password);
		
		return super.performOk();
	}
}