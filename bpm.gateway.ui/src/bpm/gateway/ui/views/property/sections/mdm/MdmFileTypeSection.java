package bpm.gateway.ui.views.property.sections.mdm;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.mdm.MdmContractFileInput;
import bpm.gateway.ui.composites.file.FileCsvComposite;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.mdm.model.supplier.Contract;

public class MdmFileTypeSection extends AbstractPropertySection {

	private MdmContractFileInput transfo;

	private StackLayout stackedComposite;

	private FileCsvComposite csvComposite;

	// private Composite

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		// inputComposite = new FileInputComposite(parent, SWT.NONE, getWidgetFactory(), aTabbedPropertySheetPage, getPart(), this);

		Composite stack = new Composite(parent, SWT.NONE);
		stackedComposite = new StackLayout();
		stack.setLayout(stackedComposite);

		csvComposite = new FileCsvComposite(stack, SWT.NONE, getWidgetFactory());
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.transfo = (MdmContractFileInput) ((Node) ((NodePart) input).getModel()).getGatewayModel();
		showComposite(transfo.getSelectedContract());
	}

	@Override
	public void refresh() {

	}

	public void showComposite(Contract contract) {

		String format = contract.getFileVersions().getCurrentVersion(contract.getVersionId()).getFormat();
		if (format.equalsIgnoreCase("csv")) { //$NON-NLS-1$
			stackedComposite.topControl = csvComposite;
		}

	}

}
