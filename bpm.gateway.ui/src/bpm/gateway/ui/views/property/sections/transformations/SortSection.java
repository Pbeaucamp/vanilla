package bpm.gateway.ui.views.property.sections.transformations;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.SortTransformation;
import bpm.gateway.ui.composites.SortComposite;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;

public class SortSection extends AbstractPropertySection {

	private SortComposite table;
	private Node node;
	private SortTransformation transfo;
	
	private PropertyChangeListener listenerConnection;

	
	
	
	private ISelectionChangedListener selectionListener;
	
	public SortSection() {
		listenerConnection = new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				
				if (table == null || table.isDisposed()){
					return;
				}
				if (evt.getPropertyName().equals(Node.TARGET_CONNECTIONS_PROP)){
					try {
						table.setInput(transfo);
					} catch (Exception e) {
						
						e.printStackTrace();
					}
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
		composite.setLayout(new GridLayout(2, false));
		


		table = new SortComposite(composite, SWT.NONE);
		table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
//		table.addSelectionListener(selectionListener);
		

	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
        transfo = (SortTransformation)node.getGatewayModel();
		
        
	}

	@Override
	public void refresh() {
		try {
			table.setInput(transfo);
		} catch (Exception e) {
			
			e.printStackTrace();
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

	@Override
	public void dispose() {

		super.dispose();
	}
	
	
}
