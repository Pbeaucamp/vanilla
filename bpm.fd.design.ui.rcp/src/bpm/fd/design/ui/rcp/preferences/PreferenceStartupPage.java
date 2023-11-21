package bpm.fd.design.ui.rcp.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import bpm.fd.design.ui.rcp.Activator;
import bpm.fd.design.ui.rcp.Messages;

public class PreferenceStartupPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private BooleanFieldEditor showWelcome;
	
	public PreferenceStartupPage() {
		super(FieldEditorPreferencePage.GRID);
	}
	@Override
	protected void createFieldEditors() {
		showWelcome = new BooleanFieldEditor(
				Preferences.SHOW_WELCOME_AT_STARTUP,
				Messages.PreferenceStartupPage_0,  
				getFieldEditorParent());
		
		
		addField(showWelcome);
		
	}

	public void init(IWorkbench workbench) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(store);
		
	}

	
}
