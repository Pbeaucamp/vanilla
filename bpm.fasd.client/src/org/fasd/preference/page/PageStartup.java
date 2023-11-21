package org.fasd.preference.page;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.fasd.i18N.LanguageText;
import org.fasd.preferences.PreferenceConstants;
import org.freeolap.FreemetricsPlugin;

public class PageStartup extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PageStartup() {
		super(FieldEditorPreferencePage.GRID);
	}

	@Override
	protected void createFieldEditors() {
		BooleanFieldEditor showNewAtStartup = new BooleanFieldEditor(PreferenceConstants.P_SHOWNEWATSTARTUP, LanguageText.PageStartup_ShowNewDialogAtStartUp, getFieldEditorParent());
		addField(showNewAtStartup);

	}

	public void init(IWorkbench workbench) {
		IPreferenceStore store = FreemetricsPlugin.getDefault().getPreferenceStore();
		setPreferenceStore(store);
	}

}
