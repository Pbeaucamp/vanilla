package bpm.workflow.ui.wizards;

import java.util.Properties;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import bpm.workflow.ui.composites.CompositeWorkflowInformations;

/**
 * Page which displays the informations relative to the current workflow model
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class WorkflowInformationPage extends WizardPage {

	private CompositeWorkflowInformations compWorkflowInfos;

	protected WorkflowInformationPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		// create main composite & set layout
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		compWorkflowInfos = new CompositeWorkflowInformations(this, mainComposite, SWT.NONE, ((WorkflowWizard) getWizard()).input);

		// page setting
		setControl(mainComposite);
		setPageComplete(false);
	}

	@Override
	public boolean isPageComplete() {
		return compWorkflowInfos != null && compWorkflowInfos.isComplete();
	}

	public void updateButtons() {
		getContainer().updateButtons();
	}

	public Properties getProperties() {
		return compWorkflowInfos != null ? compWorkflowInfos.getProperties() : null;
	}

}
