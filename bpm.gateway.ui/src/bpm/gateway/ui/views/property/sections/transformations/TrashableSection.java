package bpm.gateway.ui.views.property.sections.transformations;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.Trashable;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class TrashableSection extends AbstractPropertySection {

	
	protected Node node;
	protected NodePart nodePart;
	private CCombo trashes;
	
	private PropertyChangeListener listenerConnection;
	
	public TrashableSection() {
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
		
		parent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		
		Label l = getWidgetFactory().createLabel(composite, Messages.TrashableSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		trashes = getWidgetFactory().createCCombo(composite, SWT.READ_ONLY);
		trashes.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		trashes.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean isNull = true;
				for(Transformation t : node.getGatewayModel().getOutputs()){
					if (t.getName().equals(trashes.getText())){
						((Trashable)node.getGatewayModel()).setTrashTransformation(t);
						
						
						for (Object o : nodePart.getSourceConnections()){
							if (o instanceof EditPart){
								((EditPart)o).refresh();
							}
						}
						
						for (Object o : nodePart.getTargetConnections()){
							if (o instanceof EditPart){
								((EditPart)o).refresh();
							}
						}
						isNull = false;
						break;
					}
				}
				
				if (isNull){
					((Trashable)node.getGatewayModel()).setTrashTransformation(null);
					for (Object o : nodePart.getSourceConnections()){
						if (o instanceof EditPart){
							((EditPart)o).refresh();
						}
					}
					
					for (Object o : nodePart.getTargetConnections()){
						if (o instanceof EditPart){
							((EditPart)o).refresh();
						}
					}
				}
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
		Transformation transfo = (Transformation)node.getGatewayModel();
		List<String> l = new ArrayList<String>();
			
		
		int i = 0;
		int index = -1;
		for(Transformation t : transfo.getOutputs()){
			l.add(t.getName());
			
			if (((Trashable)transfo).getTrashTransformation() != null){
				if (((Trashable)transfo).getTrashTransformation().getName().equals(t.getName())){
					index = i;
				}
				else{
					i++;
				}
			}
			
		}
		l.add(0, "----- None -----"); //$NON-NLS-1$
		if (trashes != null && !trashes.isDisposed()){
			trashes.setItems(l.toArray(new String[l.size()]));
		}
		
		if (((Trashable)transfo).getTrashTransformation() == null){
			trashes.select(0);
		}else if (index != -1){
			trashes.select(index + 1);
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
