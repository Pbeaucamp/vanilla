package bpm.workflow.ui.views.property.sections;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.activities.KettleActivity;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

public class KettleSection extends AbstractPropertySection {

	private Text txtRepoName;
	private Text txtRepoUser;
	private Text txtRepoPassword;
	private Text txtJobName;
	private KettleActivity kettleActivity;
	
	private ModifyListener listener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent e) {
			if(e.getSource() == txtRepoName) {
				kettleActivity.setRepositoryName(txtRepoName.getText());
			}
			else if(e.getSource() == txtRepoUser) {
				kettleActivity.setRepositoryUser(txtRepoUser.getText());
			}
			else if(e.getSource() == txtRepoPassword) {
				kettleActivity.setRepositoryPassword(txtRepoPassword.getText());
			}
			else if(e.getSource() == txtJobName) {
				kettleActivity.setJobName(txtJobName.getText());
			}
		}
	};
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		CLabel lblName = getWidgetFactory().createCLabel(composite, Messages.KettleSection_0);
		lblName.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtRepoName = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtRepoName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtRepoName.addModifyListener(listener);

		CLabel lblUser = getWidgetFactory().createCLabel(composite, Messages.KettleSection_1);
		lblUser.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtRepoUser = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtRepoUser.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtRepoUser.addModifyListener(listener);

		CLabel lblPwd = getWidgetFactory().createCLabel(composite, Messages.KettleSection_2);
		lblPwd.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtRepoPassword = new Text(composite, SWT.BORDER | SWT.PASSWORD); //$NON-NLS-1$
		txtRepoPassword.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtRepoPassword.addModifyListener(listener);
		
		CLabel lblJob = getWidgetFactory().createCLabel(composite, Messages.KettleSection_3);
		lblJob.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtJobName = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtJobName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtJobName.addModifyListener(listener);

	}
	
	@Override
	public void refresh() {

		txtRepoName.removeModifyListener(listener);
		txtRepoUser.removeModifyListener(listener);
		txtRepoPassword.removeModifyListener(listener);
		txtJobName.removeModifyListener(listener);
		
		if(kettleActivity.getRepositoryName() != null) {
			txtRepoName.setText(kettleActivity.getRepositoryName());
		}
		if(kettleActivity.getRepositoryPassword() != null) {
			txtRepoPassword.setText(kettleActivity.getRepositoryPassword());
		}
		if(kettleActivity.getRepositoryUser() != null) {
			txtRepoUser.setText(kettleActivity.getRepositoryUser());
		}
		if(kettleActivity.getJobName() != null) {
			txtJobName.setText(kettleActivity.getJobName());
		}
		
		txtRepoName.addModifyListener(listener);
		txtRepoUser.addModifyListener(listener);
		txtRepoPassword.addModifyListener(listener);
		txtJobName.addModifyListener(listener);
	}
	
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		Node node = (Node) ((NodePart) input).getModel();
		this.kettleActivity = (KettleActivity) node.getWorkflowObject();
	}
}
