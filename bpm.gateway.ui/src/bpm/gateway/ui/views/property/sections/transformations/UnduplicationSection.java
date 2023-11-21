package bpm.gateway.ui.views.property.sections.transformations;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.UnduplicateRows;
import bpm.gateway.ui.composites.StreamComposite;
import bpm.gateway.ui.composites.labelproviders.DefaultStreamLabelProvider;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class UnduplicationSection extends AbstractPropertySection {

	private static Color RED = new Color(Display.getCurrent(), 255, 0, 75);
	
	protected Node node;
	protected StreamComposite streamComposite;
	private PropertyChangeListener listenerConnection;
	
	public UnduplicationSection() {
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
		composite.setLayout(new GridLayout());
		
		ToolBar toolbar = new ToolBar(composite, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		toolbar.setBackground(composite.getBackground());
		
		ToolItem addSelected = new ToolItem(toolbar, SWT.PUSH);
		addSelected.setText(Messages.UnduplicationSection_0);
		addSelected.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				performAddColumns(streamComposite.getCheckedElements());
				streamComposite.refresh();
				streamComposite.clearCheck();
				refresh();
			}
			
		});
		
		ToolItem delSelected = new ToolItem(toolbar, SWT.PUSH);
		delSelected.setText(Messages.UnduplicationSection_1);
		delSelected.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				performDelColumns(streamComposite.getCheckedElements());
				streamComposite.refresh();
				streamComposite.clearCheck();

			}
		});
		
		streamComposite = new StreamComposite(composite, SWT.NONE, true, false);
		streamComposite.setLabelProvider(new StreamLabelProvider(new LabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
		streamComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
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
		UnduplicateRows transfo = (UnduplicateRows)node.getGatewayModel();
		
			
		try {
			List<StreamElement> l = new ArrayList<StreamElement>();
			
//			l.addAll(transfo.getDescriptor().getStreamElements());
			

			l.addAll(transfo.getDescriptor(transfo).getStreamElements());

			streamComposite.fillDatas(l);
			
		} catch (ServerException e) {
			
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
	
	
	protected void performAddColumns(List<StreamElement>  elements){
		
		UnduplicateRows tr = (UnduplicateRows)node.getGatewayModel();
		
		for(StreamElement i : elements){
			
			tr.addField(i);
			
		}
		
	}
	
	
	protected void performDelColumns(List<StreamElement>  elements){
		UnduplicateRows tr = (UnduplicateRows)node.getGatewayModel();
		
		for(StreamElement i : elements){
			
			tr.removeField(i);
			
		}
		
	}
	
	
	public class StreamLabelProvider extends DefaultStreamLabelProvider{

		public StreamLabelProvider(ILabelProvider provider,
				ILabelDecorator decorator) {
			super(provider, decorator);
			
		}

		@Override
		public Color getBackground(Object element) {
			
			UnduplicateRows tr = null;
			
			if (node.getGatewayModel() instanceof UnduplicateRows){
				tr = (UnduplicateRows)node.getGatewayModel();
			}
			else{
				return null;
			}
			
			
			if (!tr.isChecked((StreamElement)element)){
				return RED;
			}
			
			return super.getBackground(element);
		}
		
	}
}
