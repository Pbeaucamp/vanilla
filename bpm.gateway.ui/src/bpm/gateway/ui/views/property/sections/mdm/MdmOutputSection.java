package bpm.gateway.ui.views.property.sections.mdm;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.mdm.MdmOutput;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class MdmOutputSection extends AbstractPropertySection {
	private Button updateExisting;
	private MdmOutput transfo;
	
	private class Listener extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			transfo.setUpdateExisting(updateExisting.getSelection());
		}
	}
	private Listener btLst = new Listener();
	
	public MdmOutputSection() {
		
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		
		
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout());
		
		updateExisting = getWidgetFactory().createButton(composite, Messages.MdmOutputSection_0, SWT.CHECK);
		updateExisting.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.transfo = (MdmOutput)((Node)((NodePart) input).getModel()).getGatewayModel();
	}
	
	@Override
	public void refresh() {
		updateExisting.removeSelectionListener(btLst);
		updateExisting.setSelection(this.transfo.isUpdateExisting());
		updateExisting.addSelectionListener(btLst);
	}

}
