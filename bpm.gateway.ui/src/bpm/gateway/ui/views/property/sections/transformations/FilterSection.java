package bpm.gateway.ui.views.property.sections.transformations;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.Filter;
import bpm.gateway.core.transformations.utils.Condition;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.composites.FilterTable2;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;

public class FilterSection extends AbstractPropertySection {

	private FilterTable2 table;
	private Button exclusive;
	private Node node;
	private PropertyChangeListener listenerConnection;
	
	private ToolItem add, del;
	
	private ISelectionChangedListener selectionListener;
	
	public FilterSection() {
		listenerConnection = new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				
				if (table == null || table.isDisposed()){
					return;
				}
				if (evt.getPropertyName().equals(Node.TARGET_CONNECTIONS_PROP)){
					table.refreshFields();
				}
					
//				else if (evt.getPropertyName().equals(Node.SOURCE_CONNECTIONS_PROP)){
//					table.refreshOutputs();
//				}
				
			}
			
		};
		
		selectionListener = new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				if (table.getSelection().getFirstElement() instanceof Transformation){
					add.setEnabled(true);
					del.setEnabled(false);
				}
				else if (table.getSelection().getFirstElement() instanceof Condition){
					add.setEnabled(false);
					del.setEnabled(true);
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
		
		add = new ToolItem(toolbar, SWT.PUSH);
		add.setToolTipText(Messages.FilterSection_0);
		add.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.add_16));
		add.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (table.getSelection().getFirstElement() instanceof Transformation){
					Condition c = new Condition();
					c.setOutput((Transformation)table.getSelection().getFirstElement());
					((Filter)node.getGatewayModel()).addCondition(c);
					table.refreshViewer();
					table.expand((Transformation)table.getSelection().getFirstElement());
				}
				
				
			}
			
		});
		add.setEnabled(false);
		
		del = new ToolItem(toolbar, SWT.PUSH);
		del.setToolTipText(Messages.FilterSection_1);
		del.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.del_16));
		del.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				Transformation t = null;
				
				for(Object o : table.getSelection().toList()){
					t = ((Condition)o).getOutput();
					((Filter)node.getGatewayModel()).removeCondition(((Condition)o));
				}

				table.refreshViewer();
				table.expand(t);
			}
			
		});
		del.setEnabled(false);
		
		exclusive = getWidgetFactory().createButton(composite, Messages.FilterSection_2, SWT.CHECK);
		exclusive.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		exclusive.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				((Filter)node.getGatewayModel()).setExclusive(exclusive.getSelection());
			}
			
		});
		
		
		table = new FilterTable2(composite, SWT.NONE);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.addSelectionListener(selectionListener);
		

	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
        Filter transfo = (Filter)node.getGatewayModel();
		table.setFilter(transfo);	
	}

	@Override
	public void refresh() {
		exclusive.setSelection(((Filter)node.getGatewayModel()).isExclusive());		

		
	}

	@Override
	public void aboutToBeShown() {
		node.addPropertyChangeListener(listenerConnection);
		table.addSelectionListener(selectionListener);
		super.aboutToBeShown();
	}


	@Override
	public void aboutToBeHidden() {
		if (node != null){
			node.removePropertyChangeListener(listenerConnection);
		}
		if (table != null && !table.isDisposed()){
			table.removeSelectionListener(selectionListener);
		}
		
		super.aboutToBeHidden();
	}

	@Override
	public void dispose() {
		if (table != null && !table.isDisposed()){
			table.removeSelectionListener(selectionListener);
		}
		super.dispose();
	}
	
	
}
