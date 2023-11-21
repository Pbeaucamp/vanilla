package bpm.workflow.ui.wizards;

import java.util.Properties;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.editors.WorkflowEditorInput;
import bpm.workflow.ui.editors.WorkflowMultiEditorPart;
import bpm.workflow.ui.views.VariablesViewPart;

/**
 * Wizard which displays the informations relative to the current workflow model
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class WorkflowWizard extends Wizard implements INewWizard {

	private static final String INFORMATION_PAGE_NAME = Messages.WorkflowWizard_0;
	private static final String INFORMATION_PAGE_DESCRIPTION = Messages.WorkflowWizard_1;
	private static final String INFORMATION_PAGE_TITLE = Messages.WorkflowWizard_2;

	private WorkflowInformationPage informationPage;

	protected WorkflowEditorInput input;

	public WorkflowWizard() {};

	public WorkflowWizard(WorkflowEditorInput input) {
		this.input = input;
	}

	@Override
	public boolean performFinish() {
		Properties p = informationPage.getProperties();
		WorkflowModel workflow = null;

		if(input != null) {
			workflow = input.getWorkflowModel();
			workflow.setName(p.getProperty("name")); //$NON-NLS-1$
			workflow.setDescription(p.getProperty("description")); //$NON-NLS-1$
			return true;
		}
		else {
			workflow = new WorkflowModel(p.getProperty("name")); //$NON-NLS-1$

		}
		workflow.setDescription(p.getProperty("description")); //$NON-NLS-1$

		WorkflowEditorInput input = new WorkflowEditorInput(p.getProperty("fileName"), workflow); //$NON-NLS-1$

		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			page.openEditor(input, WorkflowMultiEditorPart.ID, true);
			Activator.getDefault().getSessionSourceProvider().setModelOpened(true);
			VariablesViewPart view = (VariablesViewPart) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(VariablesViewPart.ID);
			Activator.getDefault().getSessionSourceProvider().setCheckedIn(false);
			if(view != null) {
				view.refresh();
			}

			return true;
		} catch(Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

	public void addPages() {

		informationPage = new WorkflowInformationPage(INFORMATION_PAGE_NAME);
		informationPage.setDescription(INFORMATION_PAGE_DESCRIPTION);
		informationPage.setTitle(INFORMATION_PAGE_TITLE);

		addPage(informationPage);

	}

}
