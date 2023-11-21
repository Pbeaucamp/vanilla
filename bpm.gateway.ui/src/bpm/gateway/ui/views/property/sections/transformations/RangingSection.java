package bpm.gateway.ui.views.property.sections.transformations;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.core.runtime.Assert;
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
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.core.transformations.calcul.Range;
import bpm.gateway.core.transformations.calcul.RangingTransformation;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.composites.Ranging;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;

public class RangingSection extends AbstractPropertySection {

	private Ranging table;
	private Node node;
	private RangingTransformation transfo;
	
	private PropertyChangeListener listenerConnection;
	private ComboViewer target;
	private Text outputFieldname;
	
	private CCombo type;
	private ToolItem add, del;
	
	private Range selectedRange = null;
	
	private ISelectionChangedListener selectionListener;
	
	public RangingSection() {
		listenerConnection = new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				
				if (table == null || table.isDisposed()){
					return;
				}
				if (evt.getPropertyName().equals(Node.TARGET_CONNECTIONS_PROP)){
					table.setInput(transfo);
				}
				
				
			}
			
		};
		
		selectionListener = new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()){
					del.setEnabled(false);
					selectedRange = null;
				}
				else{
					del.setEnabled(true);
					selectedRange = (Range)((IStructuredSelection)event.getSelection()).getFirstElement();
					
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
		
		ToolBar toolbar = new ToolBar(composite, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		
		add = new ToolItem(toolbar, SWT.PUSH);
		add.setToolTipText(Messages.RangingSection_0);
		add.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.add_16));
		add.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				Range r = new Range();
				transfo.addRange(r);
				table.setInput(transfo);
				
				
			}
			
		});
		add.setEnabled(true);
		
		del = new ToolItem(toolbar, SWT.PUSH);
		del.setToolTipText(Messages.RangingSection_1);
		del.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.del_16));
		del.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				transfo.removeRange(selectedRange);
				selectedRange = null;
				table.setInput(transfo);
				
			}
			
		});
		del.setEnabled(false);
		
	
		Label l = getWidgetFactory().createLabel(composite, Messages.RangingSection_2);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		target = new ComboViewer(composite, SWT.READ_ONLY);
		target.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		target.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((StreamElement)element).name;
			}
			
		});
		target.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<StreamElement> l = (List<StreamElement>)inputElement;
				return l.toArray(new StreamElement[l.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
				
			}
			
		});
		target.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				if (target.getSelection().isEmpty()){
					return;
				}
				
				transfo.setTarget((StreamElement)((IStructuredSelection)target.getSelection()).getFirstElement());
				
			}
			
		});
		
		Label l2 = getWidgetFactory().createLabel(composite, Messages.RangingSection_3);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		type =  getWidgetFactory().createCCombo(composite, SWT.READ_ONLY);
		type.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		type.setItems(Variable.VARIABLES_TYPES);
		type.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				transfo.setType(type.getSelectionIndex());
			}
			
		});

		
		Label l3 = getWidgetFactory().createLabel(composite, Messages.RangingSection_4);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		outputFieldname = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		outputFieldname.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		outputFieldname.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				if (transfo != null){
					transfo.setOutputFieldName(outputFieldname.getText());
				}
				
			}
			
		});
		
		
		table = new Ranging(composite, SWT.NONE);
		table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		table.addSelectionListener(selectionListener);
		

	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
        transfo = (RangingTransformation)node.getGatewayModel();
		
        
	}

	@Override
	public void refresh() {
		table.setInput(transfo);
		
		if (transfo.getOutputFieldName() != null){
			outputFieldname.setText(transfo.getOutputFieldName());
		}
		
		try{
			target.setInput(transfo.getDescriptor(transfo).getStreamElements());
			if (transfo.getTarget() != null){
				target.setSelection(new StructuredSelection(transfo.getTarget()));
			}
			type.select(transfo.getType());
		}catch(Exception e){}
		
	}

	@Override
	public void aboutToBeShown() {
		if (node != null){
			node.addPropertyChangeListener(listenerConnection);	
		}
		if(table != null && !table.isDisposed()){
			table.addSelectionListener(selectionListener);
		}
		
		super.aboutToBeShown();
	}


	@Override
	public void aboutToBeHidden() {
		if (node != null){
			node.removePropertyChangeListener(listenerConnection);
		}
		if (table!=null && !table.isDisposed()){
			table.removeSelectionListener(selectionListener);
		}
		
		super.aboutToBeHidden();
	}

	@Override
	public void dispose() {
		if (table!=null && table.isDisposed()){
			table.removeSelectionListener(selectionListener);
		}
		
		super.dispose();
	}
	
	
}
