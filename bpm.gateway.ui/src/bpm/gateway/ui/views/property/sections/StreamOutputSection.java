package bpm.gateway.ui.views.property.sections;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.ui.composites.StreamComposite;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;

public class StreamOutputSection extends AbstractPropertySection {
	private Node node;
	private StreamComposite streamComposite;
	private PropertyChangeListener listener = new PropertyChangeListener(){

		/* (non-Javadoc)
		 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent evt) {
			refresh();
			
		}
		
	};
	
	
	public StreamOutputSection() {
		
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
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		
		streamComposite = new StreamComposite(composite, SWT.NONE, false, true);
		streamComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
         
		

	}

	
	@Override
	public void refresh() {
		try {
			streamComposite.fillDatas(node.getGatewayModel().getDescriptor(node.getGatewayModel()).getStreamElements());
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#aboutToBeHidden()
	 */
	@Override
	public void aboutToBeHidden() {
		((AbstractTransformation)node.getGatewayModel()).removePropertyChangeListener(listener);
		super.aboutToBeHidden();
	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#aboutToBeShown()
	 */
	@Override
	public void aboutToBeShown() {
		((AbstractTransformation)node.getGatewayModel()).addPropertyChangeListener(listener);
		super.aboutToBeShown();
	}
	
}
