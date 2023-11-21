package bpm.gateway.ui.views.property.sections.transformations;

import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.transformations.SurrogateKey;
import bpm.gateway.ui.composites.StreamComposite;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;

public class SurrogateKeySection  extends AbstractPropertySection{

	private Node node;
	private StreamComposite composite;
	private ICheckStateListener listener = new ICheckStateListener() {

		public void checkStateChanged(CheckStateChangedEvent event) {
			SurrogateKey tr = (SurrogateKey)node.getGatewayModel();
			
			tr.removeFieldKeyIndexAll();
			for(StreamElement e : composite.getCheckedElements()){
				tr.addFieldKeyIndex(e);
			}
			
		}

	};
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		composite = new StreamComposite(parent, SWT.NONE, true, false);
		
	}
	
	
	@Override
	public void refresh() {
		SurrogateKey tr = (SurrogateKey)node.getGatewayModel();
		
		composite.removeCheckListener(listener);
		try{
			composite.fillDatas(node.getGatewayModel().getInputs().get(0).getDescriptor(node.getGatewayModel()).getStreamElements());
			composite.setChecked(tr.getFields());
		}catch(Exception ex){
			composite.fillDatas(new ArrayList<StreamElement>());
		}
		composite.addCheckListener(listener);
	}
	

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
	}
}
