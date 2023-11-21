package metadata.client.preferences.pages;

import metadata.client.i18n.Messages;
import metadata.client.preferences.PreferenceConstants;
import metadataclient.Activator;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class GeneralPage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	private BooleanFieldEditor showMenuAtStartup, confirmDeletion;
	public GeneralPage() {
		super(FieldEditorPreferencePage.GRID);
	}

	@Override
	protected void createFieldEditors() {
		
		showMenuAtStartup = new BooleanFieldEditor(
				PreferenceConstants.P_SHOW_MENU_AT_STARTUP,
				Messages.GeneralPage_3,  
				getFieldEditorParent());
		
		
		
		addField(showMenuAtStartup);
		
		confirmDeletion = new BooleanFieldEditor(
				PreferenceConstants.P_CONFIRM_DELETION,
				Messages.GeneralPage_4,  
				getFieldEditorParent());
		
		
		
		addField(confirmDeletion);
	}

	public void init(IWorkbench workbench) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(store);
		
	}

	
}
