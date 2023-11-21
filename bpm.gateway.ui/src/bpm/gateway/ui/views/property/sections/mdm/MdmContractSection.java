package bpm.gateway.ui.views.property.sections.mdm;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.mdm.MdmContractFileInput;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.mdm.model.supplier.Supplier;

public class MdmContractSection extends AbstractPropertySection {

	private Node node;
	private MdmContractFileInput transfo;

	private MdmContractComposite mdmComposite;

	private boolean warnConnectedToVanilla = true;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		this.mdmComposite = new MdmContractComposite(parent, getWidgetFactory(), SWT.NONE, false);
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.transfo = (MdmContractFileInput) ((Node) ((NodePart) input).getModel()).getGatewayModel();
		this.node = (Node) ((NodePart) input).getModel();
		
		mdmComposite.setNode(node);
	}

	@Override
	public void refresh() {
		if (transfo != null) {
			try {
				List<Supplier> suppliers = transfo.getDocument().getMdmHelper().getMdmSuppliers();
				mdmComposite.refresh(suppliers);
			} catch(Exception e) {
				e.printStackTrace();
				if (warnConnectedToVanilla) {
					MessageDialog.openInformation(getPart().getSite().getShell(), Messages.MdmContractSection_0, Messages.MdmContractSection_1);
					
					warnConnectedToVanilla = false;
				}
			}
		}
	}

	@Override
	public void aboutToBeHidden() {
		mdmComposite.aboutToBeHidden();
		super.aboutToBeHidden();
	}
}
