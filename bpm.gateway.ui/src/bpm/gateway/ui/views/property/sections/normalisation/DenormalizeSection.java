package bpm.gateway.ui.views.property.sections.normalisation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.normalisation.Denormalisation;
import bpm.gateway.ui.composites.StreamComposite;
import bpm.gateway.ui.composites.normalisation.CompositeNormalisation;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class DenormalizeSection extends AbstractPropertySection {

	private CompositeNormalisation composite;
	private StreamComposite streamComposite;
	private ComboViewer keyField;
	
	private ICheckStateListener listener = new ICheckStateListener(){

		public void checkStateChanged(CheckStateChangedEvent event) {
			Denormalisation t = (Denormalisation)node.getGatewayModel();
			Integer i;
			try {
				i = t.getInputs().get(0).getDescriptor(t).getStreamElements().indexOf(event.getElement());
			} catch (ServerException e) {
				return;
			}
			if (event.getChecked()){
				
				if (i != null){
					t.addGroupFieldindex(i);
				}
			}
			else{
				if (i != null){
					t.removeGroupFieldindex(i);
				}
				
			}
		}
		
	};
	
	private Node node;
	
	public DenormalizeSection() {
		
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayout(new GridLayout(2, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = getWidgetFactory().createLabel(parent, Messages.DenormalizeSection_0, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		keyField = new ComboViewer(getWidgetFactory().createCCombo(parent, SWT.READ_ONLY));
		keyField.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		keyField.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List l = (List)inputElement;
				return l.toArray(new Object[l.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
				
			}
			
		});
		keyField.setLabelProvider(new LabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				return ((StreamElement)element).name;
			}
			
		});
		keyField.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				Denormalisation t = (Denormalisation)node.getGatewayModel();
				if (keyField.getSelection().isEmpty()){
					return;
				}
				Object o = ((IStructuredSelection)keyField.getSelection()).getFirstElement();
				t.setInputKeyField((StreamElement)o);
				
			}
			
		});
		
		Composite main = getWidgetFactory().createComposite(parent);
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		main.setLayout(new GridLayout(2, false));
		
		composite = new CompositeNormalisation(main, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, true,1, 2));
		
		Label l2 = getWidgetFactory().createLabel(main, Messages.DenormalizeSection_1, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		streamComposite = new StreamComposite(main, SWT.NONE, true, false){
			/* (non-Javadoc)
			 * @see bpm.gateway.ui.composites.StreamComposite#desactiveListener(boolean)
			 */
			@Override
			public void desactiveListener(boolean b) {
				super.desactiveListener(b);
				if (!b){
					tableViewer.addCheckStateListener(DenormalizeSection.this.listener);
				}
				else{
					tableViewer.removeCheckStateListener(DenormalizeSection.this.listener);
				}
			}
			
			
			
		};
		streamComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, true));
	}
	
	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#aboutToBeHidden()
	 */
	@Override
	public void aboutToBeHidden() {
		streamComposite.desactiveListener(true);
		super.aboutToBeHidden();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#aboutToBeShown()
	 */
	@Override
	public void aboutToBeShown() {
		super.aboutToBeShown();
		streamComposite.desactiveListener(false);
	}

	@Override
	public void refresh() {
		Denormalisation t = (Denormalisation)node.getGatewayModel();
		composite.setTransformation(t);
	
		if (t.getInputs().isEmpty()){
			keyField.setInput(new ArrayList<StreamElement>());
			streamComposite.fillDatas(new ArrayList<StreamElement>());
			
		}
		else{
			try{
				streamComposite.fillDatas(t.getInputs().get(0).getDescriptor(t).getStreamElements());
				List<StreamElement> toCheck = new ArrayList<StreamElement>();
				for(Integer i : t.getGroupFieldIndex()){
					try{
						toCheck.add(t.getInputs().get(0).getDescriptor(t).getStreamElements().get(i));
					}catch(Exception e){
						
					}
					
				}
				streamComposite.setChecked(toCheck);
				
				
				
				keyField.setInput(t.getInputs().get(0).getDescriptor(t).getStreamElements());
				
				if (t.getInputKeyField() != null){
					StreamElement e = t.getInputs().get(0).getDescriptor(t).getStreamElements().get(t.getInputKeyField());
					
					keyField.setSelection(new StructuredSelection(e));
				}
				
			}catch(Exception e){
				
			}
		}
		
		
		
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
