package bpm.workflow.ui.views.property.sections;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
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

import bpm.workflow.runtime.model.activities.TalendActivity;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

public class TalendSection extends AbstractPropertySection {

	private Text txtFilePath;
	private TalendActivity talendActivity;
	
	private ModifyListener listener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent e) {
			if(e.getSource() == txtFilePath) {
				talendActivity.setFileName(txtFilePath.getText());
			}
		}
	};
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		CLabel lblName = getWidgetFactory().createCLabel(composite, Messages.TalendSection_0);
		lblName.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtFilePath = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtFilePath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtFilePath.addModifyListener(listener);

	}
	
	@Override
	public void refresh() {

		txtFilePath.removeModifyListener(listener);
		
		if(talendActivity.getFileName() != null) {
			txtFilePath.setText(talendActivity.getFileName());
		}
		
		txtFilePath.addModifyListener(listener);
	}
	
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		Node node = (Node) ((NodePart) input).getModel();
		this.talendActivity = (TalendActivity) node.getWorkflowObject();
	}
}
