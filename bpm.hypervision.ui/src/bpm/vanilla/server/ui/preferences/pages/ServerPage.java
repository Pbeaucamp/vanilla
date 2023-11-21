package bpm.vanilla.server.ui.preferences.pages;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.Messages;
import bpm.vanilla.server.ui.preferences.PreferencesConstants;


public class ServerPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage{

	private StringFieldEditor reporting;
	
	/**
	 * 
	 */
	public ServerPage() {
		super(FieldEditorPreferencePage.GRID);
	
	}

	@Override
	protected void createFieldEditors() {

		reporting = new StringFieldEditor(
				PreferencesConstants.REPORTING_SERVER_URL,
				Messages.ServerPage_0,  
				getFieldEditorParent());
		
		
		addField(reporting);
		
	}

	public void init(IWorkbench workbench) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(store);
		
	}

	

}
