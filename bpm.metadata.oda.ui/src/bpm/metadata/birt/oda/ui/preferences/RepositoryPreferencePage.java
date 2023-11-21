package bpm.metadata.birt.oda.ui.preferences;


import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import bpm.metadata.birt.oda.ui.Activator;

public class RepositoryPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private StringFieldEditor vanillaUrl, password, login;
	
	
	public RepositoryPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		this.setDescription(Messages.getString("RepositoryPreferencePage.Description")); //$NON-NLS-1$ FP IDEA
	}

	
	@Override
	protected void createFieldEditors() {

		
		vanillaUrl = new StringFieldEditor(
				CommonPreferenceConstants.P_BPM_VANILLA_URL,
				Messages.getString("RepositoryPreferencePage.VanillaUrl"), //$NON-NLS-1$
				getFieldEditorParent());
		
		
		addField(vanillaUrl);

		
		login = new StringFieldEditor(
				CommonPreferenceConstants.P_BPM_REPOSITORY_LOGIN,
				Messages.getString("RepositoryPreferencePage.Username"), //$NON-NLS-1$
				getFieldEditorParent());
		
		
		
		addField(login);
		
		
		password = new StringFieldEditor(
				CommonPreferenceConstants.P_BPM_REPOSITORY_PASSWORD,
				Messages.getString("RepositoryPreferencePage.Password"), //$NON-NLS-1$
				getFieldEditorParent()){
			 protected void doFillIntoGrid(Composite parent, int numColumns){
				 super.doFillIntoGrid(parent, numColumns);
				 getTextControl().setEchoChar('*');

			 }

		};
		
		password.getTextControl(getFieldEditorParent()).setEchoChar('*');
		
		addField(password);
		
	}

	public void init(IWorkbench workbench) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(store);
		
	}



	

}
