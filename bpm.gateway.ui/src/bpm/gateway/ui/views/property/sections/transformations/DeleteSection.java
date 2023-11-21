package bpm.gateway.ui.views.property.sections.transformations;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.DeleteRows;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class DeleteSection extends AbstractPropertySection {

	
	protected Node node;
	private PropertyChangeListener listenerConnection;
	
	private Button keepAll, keepDistinct;
	
	public DeleteSection() {
		listenerConnection = new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(Node.SOURCE_CONNECTIONS_PROP)){
					refresh();
				}
				
			}
			
		};
	}

	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout());
		
		Group g = getWidgetFactory().createGroup(composite, ""); //$NON-NLS-1$
		g.setLayout(new GridLayout());
		g.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		keepDistinct = getWidgetFactory().createButton(g, Messages.DeleteSection_1, SWT.RADIO);
		keepDistinct.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		keepDistinct.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				 ((DeleteRows)node.getGatewayModel()).setKeepDistinctRows(keepDistinct.getSelection());
				 
				 
			}
			
		});
		
		
		keepAll = getWidgetFactory().createButton(g, Messages.DeleteSection_2, SWT.RADIO);
		keepAll.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		keepAll.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				 ((DeleteRows)node.getGatewayModel()).setKeepDistinctRows(!keepAll.getSelection());
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

	}
	
	@Override
	public void refresh() {
		DeleteRows transfo = (DeleteRows)node.getGatewayModel();
		
		if (transfo.isKeepDistinctRows()){
			keepDistinct.setSelection(true);
			keepAll.setSelection(false);
		}
		else{
			keepDistinct.setSelection(false);
			keepAll.setSelection(true);
		}
		
	}


	@Override
	public void aboutToBeShown() {
		node.addPropertyChangeListener(listenerConnection);
		super.aboutToBeShown();
	}


	@Override
	public void aboutToBeHidden() {
		if (node != null){
			node.removePropertyChangeListener(listenerConnection);
		}
		super.aboutToBeHidden();
	}
	
	

	
	

}
