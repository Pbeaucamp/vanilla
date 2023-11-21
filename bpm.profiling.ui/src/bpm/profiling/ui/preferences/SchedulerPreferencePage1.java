package bpm.profiling.ui.preferences;



import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import bpm.profiling.ui.Activator;

public class SchedulerPreferencePage1 extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private StringFieldEditor schedulerServer;
	
	public SchedulerPreferencePage1() {
		
	}

	@Override
	protected void createFieldEditors() {
				
		schedulerServer = new StringFieldEditor(
				PreferenceConstants.P_SCHEDULER_SERVER_URL,
				"Bpm Scheduler Server",  //$NON-NLS-1$
				getFieldEditorParent());

		addField(schedulerServer);

		
		
	}

	public void init(IWorkbench workbench) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(store);
		
	}

}
