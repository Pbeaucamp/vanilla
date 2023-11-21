package bpm.gateway.ui.views.property.sections.vanilla;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.vanilla.VanillaGroupUser;
import bpm.gateway.ui.composites.vanilla.CompositeUserGroup;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;

public class VanillaUserGroup extends AbstractPropertySection {

	protected Node node;
	protected CompositeUserGroup composite;
	
	
	public VanillaUserGroup() {
		
	}

	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		composite = new CompositeUserGroup(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

	}
	
	@Override
	public void refresh() {
		VanillaGroupUser t = (VanillaGroupUser)node.getGatewayModel();
		composite.setTransformation(t);
	
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
	}
}
