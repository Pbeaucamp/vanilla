package bpm.gateway.ui.views.property.sections.transformations;

import java.util.List;

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

import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.SimpleMappingTransformation;
import bpm.gateway.core.transformations.outputs.IOutput;
import bpm.gateway.ui.composites.CompositeVisualMapping;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;

public class VisualMappingSection extends AbstractPropertySection {

	private Node node;
	private CompositeVisualMapping compositeMapping;
	
	
	public VisualMappingSection() {
		
	}

	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		
		compositeMapping = new CompositeVisualMapping(composite, SWT.NONE);
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
		
		if (node.getGatewayModel() instanceof SimpleMappingTransformation){
			SimpleMappingTransformation transfo = (SimpleMappingTransformation)node.getGatewayModel();
			
			try{
				List<Transformation> l = transfo.getInputs();
				
				DefaultStreamDescriptor left = (DefaultStreamDescriptor)l.get(0).getDescriptor(l.get(0));
				DefaultStreamDescriptor right = (DefaultStreamDescriptor)l.get(1).getDescriptor(l.get(1));

				compositeMapping.setInput(left, right, transfo.getMappings(), node.getGatewayModel());
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		else if (node.getGatewayModel() instanceof IOutput){
			IOutput transfo = (IOutput)node.getGatewayModel();

			try{
				List<Transformation> l = transfo.getInputs();
				
				DefaultStreamDescriptor left = (DefaultStreamDescriptor)transfo.getInputs().get(0).getDescriptor(transfo);
				DefaultStreamDescriptor right = (DefaultStreamDescriptor)transfo.getDescriptor(transfo);

				compositeMapping.setInput(left, right, transfo.getMappingsFor(l.get(0)), node.getGatewayModel());
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
				
	}
}
