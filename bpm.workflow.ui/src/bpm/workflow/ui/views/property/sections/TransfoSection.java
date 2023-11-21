package bpm.workflow.ui.views.property.sections;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.activities.vanilla.GatewayActivity;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

/**
 * Section for the selection of a gateway object on a repository server
 * 
 * @author CHARBONNIER, CAMUS, MARTIN
 * 
 */
public class TransfoSection extends AbstractPropertySection {
	private Node node;
	private Text txtItemName;
	private Composite composite;

	public TransfoSection() {

	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		CLabel lblItem = getWidgetFactory().createCLabel(composite, Messages.TransfoSection_0);
		lblItem.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtItemName = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtItemName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtItemName.setEnabled(false);

		getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$

	}

	@Override
	public void refresh() {

		GatewayActivity a = (GatewayActivity) node.getWorkflowObject();
		if(a.getBiObject() != null) {
			txtItemName.setText(a.getBiObject().getItemName());
		}

	}

	@Override
	public void aboutToBeShown() {
		super.aboutToBeShown();

	}

	@Override
	public void aboutToBeHidden() {
		super.aboutToBeHidden();
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
	}

}
