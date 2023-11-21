package bpm.profiling.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import bpm.profiling.ui.Activator;

public class RepositoryPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private StringFieldEditor repositoryUrl;
	private StringFieldEditor repositoryLogin;
	private StringFieldEditor repositoryPassword;
	
	public RepositoryPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
	}

	@Override
	protected void createFieldEditors() {
		
		repositoryUrl = new StringFieldEditor(
				PreferenceConstants.P_BPM_REPOSITORY_URL,
				"Vanilla Url",  //$NON-NLS-1$
				getFieldEditorParent());

		addField(repositoryUrl);
		
		
		
		repositoryLogin = new StringFieldEditor(
				PreferenceConstants.P_BPM_REPOSITORY_LOGIN,
				"Login",  //$NON-NLS-1$
				getFieldEditorParent());

		addField(repositoryLogin);
		
		
		repositoryPassword = new StringFieldEditor(
				PreferenceConstants.P_BPM_REPOSITORY_PASSWORD,
				"Password",  //$NON-NLS-1$
				getFieldEditorParent());

		addField(repositoryPassword);
		
	

	}

	public void init(IWorkbench workbench) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(store);
		
	}

	
}

