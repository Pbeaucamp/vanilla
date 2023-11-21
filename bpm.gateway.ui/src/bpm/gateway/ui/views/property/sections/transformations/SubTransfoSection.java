package bpm.gateway.ui.views.property.sections.transformations;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.core.transformations.SubTransformation;
import bpm.gateway.core.transformations.outputs.SubTransformationFinalStep;
import bpm.gateway.ui.composites.CompositeSubTransfo;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class SubTransfoSection extends AbstractPropertySection {


	private CompositeSubTransfo compositeSub;
	private ComboViewer subSteps;
	private Node node;
	private RepositoryItem di;
	private DocumentGateway gtw ;
	
	private PropertyChangeListener lst = new PropertyChangeListener(){

		public void propertyChange(PropertyChangeEvent evt) {
			refresh();
		}
		
	};
	
	private ISelectionChangedListener finalLst = new ISelectionChangedListener() {
		
		public void selectionChanged(SelectionChangedEvent event) {
		
			
			if (!subSteps.getSelection().isEmpty()){
				((SubTransformation)node.getGatewayModel()).setFinalStep((Transformation)((IStructuredSelection)subSteps.getSelection()).getFirstElement());
			}
			
			
			
		}
	};
	
	public SubTransfoSection() {
		
	}
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(3, false));
		
		Label l2 = getWidgetFactory().createLabel(composite, Messages.SubTransfoSection_0);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
	
		subSteps = new ComboViewer(getWidgetFactory().createCCombo(composite, SWT.READ_ONLY));
		subSteps.getCCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		subSteps.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				DocumentGateway g = (DocumentGateway)inputElement;
				
				List<Transformation> l = new ArrayList<Transformation>();
				
				for(Transformation t : g.getTransformations()){
					if (t instanceof SubTransformationFinalStep){
						l.add(t);
					}
				}
				return l.toArray(new Object[l.size()]);
			}
		});
		subSteps.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				
				return ((Transformation)element).getName();
			}
		});
		
		Button reload = getWidgetFactory().createButton(composite, Messages.SubTransfoSection_1, SWT.PUSH);
		reload.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		reload.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				gtw = null;
				refresh();
			}
		});
		
		l2 = getWidgetFactory().createLabel(composite, Messages.SubTransfoSection_2);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));
		
		
		compositeSub = new CompositeSubTransfo(composite, SWT.NONE);
		compositeSub.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		
		
		
	}
	
	@Override
	public void refresh() {
		subSteps.removeSelectionChangedListener(finalLst);
		BusyIndicator.showWhile(Display.getDefault(), new Runnable(){
			public void run(){
				try{
					loadGateway();
				}catch(FileNotFoundException e){
					e.printStackTrace();
				}catch(Exception e){
					e.printStackTrace();
					MessageDialog.openWarning(getPart().getSite().getShell(), Messages.SubTransfoSection_3, e.getMessage());
				}
			}
		});
		
		subSteps.addSelectionChangedListener(finalLst);
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
	}

	
	private void loadGateway() throws Exception{
		SubTransformation tr = (SubTransformation)node.getGatewayModel();
		
		StreamDescriptor desc = null;
		try{
			desc = node.getGatewayModel().getInputs().get(0).getDescriptor(node.getGatewayModel());
		}catch(Exception ex){
			if (desc == null){
				desc = new DefaultStreamDescriptor();
			}
		}
		
		if (tr.getDefinition() == null || tr.getDefinition().equals("")){ //$NON-NLS-1$
			
			compositeSub.fillDatas((SubTransformation)node.getGatewayModel(), desc,new ArrayList<String>());
			return;
		}

		
		if (gtw == null){
			gtw = tr.getDocument().getSubTransformationHelper().modelLoader(tr);
		}
		
	
		List<String> l = new ArrayList<String>();

		for(Parameter p : gtw.getParameters()){
			l.add(p.getName());
		}
		
		
		compositeSub.fillDatas((SubTransformation)node.getGatewayModel(), desc,l);
		
		subSteps.setInput(gtw);
		
		if (tr.getFinalStep() != null){
			subSteps.setSelection(new StructuredSelection(gtw.getTransformation(tr.getFinalStep())));
		}
	}

	@Override
	public void aboutToBeHidden() {
		((SubTransformation)node.getGatewayModel()).removePropertyChangeListener(lst);
		super.aboutToBeHidden();
	}

	@Override
	public void aboutToBeShown() {
		((SubTransformation)node.getGatewayModel()).addPropertyChangeListener(lst);
		super.aboutToBeShown();
	}
	
	
	
}
