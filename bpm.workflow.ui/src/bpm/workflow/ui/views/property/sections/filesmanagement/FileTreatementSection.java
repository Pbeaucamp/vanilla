package bpm.workflow.ui.views.property.sections.filesmanagement;

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

import bpm.workflow.runtime.model.activities.filemanagement.IFileTreatement;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

/**
 * Section for the selection of an item on a repository
 * 
 * @author CHARBONNIER
 * 
 */
public class FileTreatementSection extends AbstractPropertySection {
	private Node node;
	private Text filesName;

	public FileTreatementSection() {}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		final Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(1, false));

		CLabel itemLabel = getWidgetFactory().createCLabel(composite, Messages.FileTreatementSection_0);
		itemLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, true, false));

		filesName = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		filesName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

	}

	@Override
	public void refresh() {
		if(((IFileTreatement) node.getWorkflowObject()).getFilesToTreat() != null)
			filesName.setText(((IFileTreatement) node.getWorkflowObject()).getFilesToTreat());
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
	}

	@Override
	public void aboutToBeShown() {
		filesName.addModifyListener(listener);
		super.aboutToBeShown();
	}

	@Override
	public void aboutToBeHidden() {
		try {
			filesName.removeModifyListener(listener);
		} catch(Exception e) {}
		super.aboutToBeHidden();
	}

	private ModifyListener listener = new ModifyListener() {

		public void modifyText(ModifyEvent evt) {
			((IFileTreatement) node.getWorkflowObject()).setFilesToTreat(filesName.getText());
		}
	};

}
