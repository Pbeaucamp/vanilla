package bpm.forms.design.ui.preferences.pages;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import bpm.forms.design.ui.Activator;
import bpm.forms.design.ui.dialogs.DialogVanillaGroupPicker;
import bpm.forms.design.ui.preferences.VanillaContextPreferencesInitializer;
import bpm.vanilla.platform.core.beans.Group;

public class VanillaContextPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private StringFieldEditor url;
	private StringFieldEditor login;
	private StringFieldEditor password;
	private StringButtonFieldEditor group;
	
	public VanillaContextPage(){
		super(FieldEditorPreferencePage.GRID);
	}
	
	@Override
	protected void createFieldEditors() {
		
		url = new StringFieldEditor(VanillaContextPreferencesInitializer.VANILLA_URL ,
				"VanillaUrl",
				getFieldEditorParent());
				
		addField(url);
		login = new StringFieldEditor(VanillaContextPreferencesInitializer.VANILLA_LOGIN ,
				"User Login",
				getFieldEditorParent());
		
		addField(login);
		password = new StringFieldEditor(VanillaContextPreferencesInitializer.VANILLA_PASSWORD ,
				"User Password",
				getFieldEditorParent());
		addField(password);
		group = new StringButtonFieldEditor(VanillaContextPreferencesInitializer.VANILLA_GROUP_ID, "Vanilla Group", getFieldEditorParent()) {
			
			@Override
			protected String changePressed() {
				DialogVanillaGroupPicker d = new DialogVanillaGroupPicker(VanillaContextPage.this.getShell(), DialogVanillaGroupPicker.MONO_GROUPS);
				if (d.open() == DialogVanillaGroupPicker.OK){
					return d.getGroup().get(0).getId() + "";
				}
				return null;
			}
			
			@Override
			protected void doLoad() {
				super.doLoad();
				if (getTextControl() != null){
					try{
						Group g= Activator.getDefault().getVanillaContext().getVanillaApi().getVanillaSecurityManager().getGroupById(Integer.parseInt(getTextControl().getText()));
						getTextControl().setText(g.getName());
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
			}

		};
		
		addField(group);
		
		
	}

	@Override
	public void init(IWorkbench workbench) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(store);
		
	}

	@Override
	public boolean performOk() {
		Activator.getDefault().resetVanillaContext();
		return super.performOk();
	}
	
}
