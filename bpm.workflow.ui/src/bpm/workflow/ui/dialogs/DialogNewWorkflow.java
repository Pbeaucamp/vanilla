package bpm.workflow.ui.dialogs;

import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.composites.CompositeWorkflowInformations;
import bpm.workflow.ui.editors.WorkflowEditorInput;
import bpm.workflow.ui.editors.WorkflowMultiEditorPart;

/**
 * Dialog for the creation of a new workflow
 * 
 * @author Charles MARTIN
 * 
 */
public class DialogNewWorkflow extends Dialog {

	private CompositeWorkflowInformations compWorkflowInfos;

	public DialogNewWorkflow(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(400, 300);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText(Messages.DialogNewWorkflow_0);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));

		compWorkflowInfos = new CompositeWorkflowInformations(null, composite, SWT.NONE, null);

		return composite;
	}

	public Properties getProperties() {
		return compWorkflowInfos.getProperties();
	}

	@Override
	protected void okPressed() {
		Properties props = getProperties();
		if (props.getProperty("fileName") == null) { //$NON-NLS-1$
			MessageDialog.openInformation(getShell(), Messages.DialogNewWorkflow_13, Messages.DialogNewWorkflow_14);
		}
		else if (props.getProperty("name") == null) { //$NON-NLS-1$
			MessageDialog.openInformation(getShell(), Messages.DialogNewWorkflow_16, Messages.DialogNewWorkflow_17);
		}
		else {
			WorkflowModel workflow = new WorkflowModel(props.getProperty("name")); //$NON-NLS-1$
			workflow.setDescription(props.getProperty("description")); //$NON-NLS-1$
			WorkflowEditorInput input = new WorkflowEditorInput(props.getProperty("fileName"), workflow); //$NON-NLS-1$
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {
				page.openEditor(input, WorkflowMultiEditorPart.ID, true);
			} catch (PartInitException e1) {
				e1.printStackTrace();
			}
			super.okPressed();
		}
	}

}
