package bpm.vanilla.security.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import bpm.vanilla.oda.commons.Activator;
import bpm.vanilla.oda.commons.nls.Messages;


public class VanillaSecurityPreference extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	private StringFieldEditor url;
	
	public VanillaSecurityPreference() {
		super(FieldEditorPreferencePage.GRID);
		this.setDescription(Messages.getString("VanillaSecurityPreferencePage.Description")); //$NON-NLS-1$ FP IDEA
	}


	public void init(IWorkbench workbench) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(store);

	}



	@Override
	protected void createFieldEditors() {
		url = new StringFieldEditor(
				CommonPreferenceConstants.P_BPM_VANILLA_URL,
				Messages.getString("VanillaSecurityPreferencePage.RepositoryUrl"), //$NON-NLS-1$
				getFieldEditorParent());
		
		
		addField(url);
		
	}

}
