package bpm.gateway.ui.views.property.sections.transformations;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.SimpleMappingTransformation;
import bpm.gateway.ui.composites.MapperComposite;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;

public class MapperSection extends AbstractPropertySection {

	private MapperComposite compositeMapping;
	private Node node;
	private PropertyChangeListener listenerConnection;
	
	public MapperSection() {
		listenerConnection = new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(Node.TARGET_CONNECTIONS_PROP)){
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
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		
		compositeMapping = new MapperComposite(composite, SWT.NONE);
//		compositeMapping.setLayoutData(new GridData(GridData.FILL_BOTH));
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

		SimpleMappingTransformation transfo = (SimpleMappingTransformation)node.getGatewayModel();
		
			
		compositeMapping.fillDatas(transfo);
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
