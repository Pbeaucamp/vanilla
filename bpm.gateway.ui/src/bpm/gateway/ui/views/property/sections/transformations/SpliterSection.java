package bpm.gateway.ui.views.property.sections.transformations;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.transformations.FieldSplitter;
import bpm.gateway.core.transformations.SplitedField;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.composites.CompositeSplitter;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;

public class SpliterSection extends AbstractPropertySection {

	private CompositeSplitter table;
	private Node node;
	private FieldSplitter transfo;
	private PropertyChangeListener listenerConnection;
	
	public SpliterSection() {
		listenerConnection = new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				
				if (table == null || table.isDisposed()){
					return;
				}
				if (evt.getPropertyName().equals(Node.TARGET_CONNECTIONS_PROP)){
					table.setInput((FieldSplitter)node.getGatewayModel());
				}
					
				else if (evt.getPropertyName().equals(Node.SOURCE_CONNECTIONS_PROP)){
					table.setInput((FieldSplitter)node.getGatewayModel());
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
		
		ToolItem add = new ToolItem(toolbar, SWT.PUSH);
		add.setToolTipText(Messages.SpliterSection_0);
		add.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.add_16));
		add.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (table.getSelection() == null || !(table.getSelection() instanceof StreamElement)){
					return;
				}
				StreamElement se = (StreamElement)table.getSelection();
				
				
				InputDialog id = new InputDialog(getPart().getSite().getShell(), Messages.SpliterSection_1, Messages.SpliterSection_2, ";", null); //$NON-NLS-1$
				id.open();
				String spliter = id.getValue();
				
				
				transfo.addSpliter(spliter, se);
				table.setInput(transfo);
			}
			
		});
		
		
		ToolItem newField = new ToolItem(toolbar, SWT.PUSH);
		newField.setToolTipText(Messages.SpliterSection_4);
		newField.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.add_16));
		newField.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (table.getSelection() == null || !(table.getSelection() instanceof SplitedField)){
					return;
				}
				
				SplitedField se = (SplitedField)table.getSelection();


				InputDialog id = new InputDialog(getPart().getSite().getShell(), Messages.SpliterSection_5, Messages.SpliterSection_6, ";", null); //$NON-NLS-1$
				id.open();
				String name = id.getValue();
				
				StreamElement newCol = se.getSplited().clone(transfo.getName(), transfo.getName());
				newCol.className = "java.lang.String"; //$NON-NLS-1$
				newCol.name =  name;
				
				transfo.addColumnFor(newCol, se);
				
				table.setInput(transfo);
				
			}
		});
		
		
		ToolItem del = new ToolItem(toolbar, SWT.PUSH);
		del.setToolTipText(Messages.SpliterSection_9);
		del.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.del_16));
		del.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object o =  table.getSelection();
				
				/*
				 * delete a SplitedField
				 */
				if (o instanceof SplitedField){
					transfo.removeSpliter((SplitedField)o);
					table.setInput(transfo);
					return;
				}
				
				/*
				 * delete a Column from a splitedField
				 */
				if (o instanceof StreamElement && !transfo.getNonSplitedStreamElements().contains(o)){
					transfo.removeNewField((StreamElement)o);
					table.setInput(transfo);
					return;
				}
			}
			
		});
		
		table = new CompositeSplitter(composite, SWT.NONE);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		

	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
        transfo = (FieldSplitter)node.getGatewayModel();
		
	}

	@Override
	public void refresh() {
				
		table.setInput((FieldSplitter)node.getGatewayModel());	
		
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
