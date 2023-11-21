package bpm.gateway.ui.preferences.pages;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import bpm.gateway.ui.Activator;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.preferences.PreferencesConstants;

public class GeneralPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private IntegerFieldEditor maxRowPreference;
	private BooleanFieldEditor showMenuAtStartup;

	public GeneralPage() {
		super(FieldEditorPreferencePage.GRID);
	}

	@Override
	protected void createFieldEditors() {

		maxRowPreference = new IntegerFieldEditor(PreferencesConstants.P_MAX_ROW_PER_CHUNK, Messages.GeneralPage_1, getFieldEditorParent());

		addField(maxRowPreference);

		showMenuAtStartup = new BooleanFieldEditor(PreferencesConstants.P_SHOW_MENU_AT_STARTUP, Messages.GeneralPage_2, getFieldEditorParent());

		addField(showMenuAtStartup);

	}

	public void init(IWorkbench workbench) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(store);

	}

	@Override
	protected void performApply() {
		super.performApply();
	}

}
