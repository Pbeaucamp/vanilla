package bpm.gateway.ui.views.property.sections.d4c;

import java.util.ArrayList;
import java.util.List;

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

import bpm.gateway.core.server.d4c.D4CServer;
import bpm.gateway.core.transformations.inputs.D4CInput;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.utils.D4CHelper;

public class D4CSection extends AbstractPropertySection {

	private Node node;
	private D4CInput transfo;

	private D4CComposite d4cComposite;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		this.d4cComposite = new D4CComposite(parent, getWidgetFactory(), SWT.NONE);
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.transfo = (D4CInput) ((Node) ((NodePart) input).getModel()).getGatewayModel();
		this.node = (Node) ((NodePart) input).getModel();
		
		d4cComposite.setNode(node);
	}

	@Override
	public void refresh() {
		if (transfo != null && transfo.getServer() != null) {
			try {
				D4CHelper d4cHelper = transfo.getD4CHelper();
				
				String org = ((D4CServer) transfo.getServer()).getOrg();
				List<CkanPackage> packages = d4cHelper != null ? d4cHelper.getCkanPackagesByChunk(org, 100, 0) : new ArrayList<CkanPackage>();
				d4cComposite.refresh(packages);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void aboutToBeHidden() {
		d4cComposite.aboutToBeHidden();
		super.aboutToBeHidden();
	}
}
