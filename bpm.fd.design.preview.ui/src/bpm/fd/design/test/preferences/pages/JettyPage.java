package bpm.fd.design.test.preferences.pages;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import bpm.fd.design.test.Activator;
import bpm.fd.design.test.Messages;
import bpm.fd.design.test.preferences.PreferencesConstants;

public class JettyPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private IntegerFieldEditor port;
	
	public JettyPage() {
		super(FieldEditorPreferencePage.GRID);
	}

	@Override
	protected void createFieldEditors() {
		port = new IntegerFieldEditor(
				PreferencesConstants.P_JETTY_PORT,
				Messages.JettyPage_0,  
				getFieldEditorParent());
		
		
		addField(port);
		
	}

	public void init(IWorkbench workbench) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(store);
		
	}

}
