package bpm.fd.design.test.preferences.pages;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import bpm.fd.design.test.Activator;
import bpm.fd.design.test.Messages;
import bpm.fd.design.test.preferences.PreferencesConstants;

public class VanillaProfilePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage{

	private StringFieldEditor vanillaLogin;
	private StringFieldEditor vanillaPassword;
	private StringFieldEditor vanillaUrl;
	private StringFieldEditor vanillaGroup;
	private StringFieldEditor vanillaRepository;
	
	public VanillaProfilePage(){
		super(FieldEditorPreferencePage.GRID);
	}

	@Override
	protected void createFieldEditors() {
		vanillaUrl = new StringFieldEditor(
				PreferencesConstants.P_DEFAULT_VANILLA_URL,
				Messages.VanillaProfilePage_0,  
				getFieldEditorParent());
		addField(vanillaUrl);
		
		vanillaLogin = new StringFieldEditor(
				PreferencesConstants.P_DEFAULT_VANILLA_LOGIN,
				Messages.VanillaProfilePage_1,  
				getFieldEditorParent());
		addField(vanillaLogin);
		
		vanillaPassword = new StringFieldEditor(
				PreferencesConstants.P_DEFAULT_VANILLA_PASSWORD,
				Messages.VanillaProfilePage_2,  
				getFieldEditorParent()){
			 protected void doFillIntoGrid(Composite parent, int numColumns){
				 super.doFillIntoGrid(parent, numColumns);
				 getTextControl().setEchoChar('*');

			 }

		};
		
		addField(vanillaPassword);
		
		vanillaGroup = new StringFieldEditor(
				PreferencesConstants.P_DEFAULT_VANILLA_GROUP,
				Messages.VanillaProfilePage_3,  
				getFieldEditorParent());
		addField(vanillaGroup);
		
		vanillaRepository = new StringFieldEditor(
				PreferencesConstants.P_DEFAULT_REPOSITORY_URL,
				Messages.VanillaProfilePage_4,  
				getFieldEditorParent());
		addField(vanillaRepository);
		
		
		
	}

	public void init(IWorkbench workbench) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(store);
		
	}
}
