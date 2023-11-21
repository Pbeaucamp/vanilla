package bpm.gateway.ui.views.property.sections.transformations.oda;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.transformations.inputs.OdaInput;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.oda.wizard.OdaDataSetWizard;
import bpm.gateway.ui.oda.wizard.OdaDataSourceWizard;

public class OdaSection extends AbstractPropertySection {

	private Node node;
	private OdaInput odaInput;
	
	private TableViewer viewer;
	ComboBoxCellEditor editor;
	
	@Override
	public void createControls(Composite parent,TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout());
		
		Composite main = getWidgetFactory().createComposite(parent);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

	
		
		Label l = getWidgetFactory().createLabel(main, Messages.OdaSection_0);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		viewer = new TableViewer(getWidgetFactory().createTable(main, SWT.FULL_SELECTION  | SWT.BORDER));
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		viewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
			public void dispose() {
				
			}
			
			public Object[] getElements(Object inputElement) {
				Collection c = (Collection)inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		
		TableViewerColumn cName =  new TableViewerColumn(viewer, SWT.NONE);
		cName.getColumn().setWidth(200);
		cName.getColumn().setText(Messages.OdaSection_1);
		cName.setLabelProvider(new ColumnLabelProvider());
		
		
		
		TableViewerColumn cValue =  new TableViewerColumn(viewer, SWT.NONE);
		cValue.getColumn().setWidth(200);
		cValue.getColumn().setText(Messages.OdaSection_2);
		cValue.setLabelProvider(new ColumnLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				Integer i = odaInput.getParameterValue((String)element);
				if (i == null){
					return ""; //$NON-NLS-1$
				}
				
				
				try{
					StreamElement e = odaInput.getInputs().get(0).getDescriptor(odaInput).getStreamElements().get(i);
					return new String(e.name);
				}catch(Exception ex){
					ex.printStackTrace();
					return ""; //$NON-NLS-1$
				}
				
			}
			
		});
		cValue.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				odaInput.setParameter((String)element, (Integer)value);
				viewer.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				Integer i = odaInput.getParameterValue((String)element);
				if (i == null){
					return -1;
				}
				return i;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return !odaInput.getInputs().isEmpty();
			}
		});
		
		
		Button b = getWidgetFactory().createButton(parent, Messages.OdaSection_5, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		b.addSelectionListener(new SelectionAdapter() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					
					/*
					 * open DataSource edition
					 */
					OdaDataSourceWizard wiz = new OdaDataSourceWizard((OdaInput)node.getGatewayModel());
					WizardDialog dial = new WizardDialog(getPart().getSite().getShell(), wiz);
					dial.setMinimumPageSize(800, 600);
					if (dial.open() != WizardDialog.OK){
						return;
					}
					
					/*
					 * open DataSetEdition
					 */
					OdaDataSetWizard wiz2 = new OdaDataSetWizard((OdaInput)node.getGatewayModel());
					dial = new WizardDialog(getPart().getSite().getShell(), wiz2);
					dial.setMinimumPageSize(800, 600);
					if (dial.open() != WizardDialog.OK){
						return;
					}
					
					refresh();
				} catch (OdaException e1) {
					e1.printStackTrace();
				}
				
			}
			
		});
	}
	@Override
	public void refresh() {
		
		viewer.setInput(odaInput.getParameterNames());
		if (editor == null){
			editor = new ComboBoxCellEditor(viewer.getTable(), new String[]{});
		}
		
		if (odaInput.getInputs().isEmpty()){
			return;
		}
		
		try{
			List<String> l = new ArrayList<String>();
			for(StreamElement e : odaInput.getInputs().get(0).getDescriptor(odaInput).getStreamElements()){
				l.add(new String(e.name));
			}
			
			editor.setItems(l.toArray(new String[l.size()]));
	
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
       this.odaInput = (OdaInput)node.getGatewayModel();
	}
}
