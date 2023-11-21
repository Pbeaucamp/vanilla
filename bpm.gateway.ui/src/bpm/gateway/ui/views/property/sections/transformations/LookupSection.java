package bpm.gateway.ui.views.property.sections.transformations;

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

import bpm.gateway.core.transformations.Lookup;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class LookupSection extends AbstractPropertySection {

	
	protected Node node;
	protected NodePart nodePart;
	
	private Button removeNotMatchingRows, trashNotMatching;
	
	
	
	public LookupSection() {
		
	}

	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout());
		
		removeNotMatchingRows = getWidgetFactory().createButton(composite, Messages.LookupSection_0, SWT.CHECK);
		removeNotMatchingRows.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		removeNotMatchingRows.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				((Lookup)node.getGatewayModel()).setRemoveRowsWithoutLookupMatching(removeNotMatchingRows.getSelection());
			}
			
		});
		
		trashNotMatching = getWidgetFactory().createButton(composite, Messages.LookupSection_1, SWT.CHECK);
		trashNotMatching.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		trashNotMatching.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				((Lookup)node.getGatewayModel()).setTrashRowsWithoutLookupMatching(trashNotMatching.getSelection());
			}
			
		});

	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
        this.nodePart = (NodePart) input;

	}
	
	@Override
	public void refresh() {
		Lookup transfo = (Lookup)node.getGatewayModel();
		
		removeNotMatchingRows.setSelection(transfo.isRemoveRowsWithoutLookupMatching());
		trashNotMatching.setSelection(transfo.isTrashRowsWithoutLookupMatching());
		
		
		
	}


	@Override
	public void aboutToBeShown() {
		
		super.aboutToBeShown();
	}


	@Override
	public void aboutToBeHidden() {
		
		super.aboutToBeHidden();
	}
	
	

	
	

}
