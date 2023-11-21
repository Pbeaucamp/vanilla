package bpm.gateway.ui.views.property.sections.files.kml;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.kml.Kml;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.views.property.sections.files.FileSelectionSection;

public class KmlGeneralSection extends AbstractPropertySection {
	
	private Kml kml;
	
	private FileSelectionSection fileSelectionComposite;
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout());

		this.fileSelectionComposite = new FileSelectionSection(composite, getWidgetFactory(), SWT.NONE);
	}
	
	@Override
	public void refresh() {
		try {
			fileSelectionComposite.refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void aboutToBeHidden() {
		fileSelectionComposite.aboutToBeHidden();
		super.aboutToBeHidden();
	}

	@Override
	public void aboutToBeShown() {
		fileSelectionComposite.aboutToBeShown();
		super.aboutToBeShown();
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        Node node = (Node)((NodePart) input).getModel();
        this.kml = (Kml)(node).getGatewayModel();
		
        fileSelectionComposite.setNode(node);
	}
}
