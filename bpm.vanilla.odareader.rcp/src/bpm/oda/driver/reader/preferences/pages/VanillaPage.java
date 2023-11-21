package bpm.oda.driver.reader.preferences.pages;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import bpm.oda.driver.reader.Activator;
import bpm.oda.driver.reader.preferences.PreferencesConstants;

public class VanillaPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private StringFieldEditor vanillaUrl, vanillaLogin, vanillaPassword;
	
	public VanillaPage() {
		super(FieldEditorPreferencePage.GRID);
	}
	
	@Override
	protected void createFieldEditors() {
		vanillaUrl = new StringFieldEditor(
				PreferencesConstants.PREF_VANILLA_URL,
				"Vanilla Url",
				getFieldEditorParent());
		
		
		addField(vanillaUrl);
		
		vanillaLogin = new StringFieldEditor(
				PreferencesConstants.PREF_VANILLA_LOGIN,
				"Vanilla Login",
				getFieldEditorParent());
		
		
		addField(vanillaLogin);
		
		vanillaPassword = new StringFieldEditor(
				PreferencesConstants.PREF_VANILLA_PASSWORD,
				"Vanilla Password",
				getFieldEditorParent());
		
		
		addField(vanillaPassword);
		vanillaPassword.getTextControl(getFieldEditorParent()).setEchoChar('*');
	}

	public void init(IWorkbench workbench) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(store);
		
	}


}
