package bpm.gateway.ui.views.property.sections.files.vcl;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.file.FileVCL;
import bpm.gateway.core.transformations.outputs.FileOutputVCL;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.viewer.StreamDescriptorStructuredContentProvider;

public class FileOutputVCLSection extends AbstractPropertySection {

	private Node node;
	private Button append;
	private Button writeHeaders;
	private Button deleteFirst;
	private Button truncateField;
	
	private TableViewer viewer;
	
	private SelectionListener lst = new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (e.widget == append){
				writeHeaders.setEnabled(!append.getSelection());
				((FileOutputVCL)node.getGatewayModel()).setAppend(append.getSelection());
			}
			else if (e.widget == writeHeaders){
				((FileOutputVCL)node.getGatewayModel()).setContainHeaders(writeHeaders.getSelection());
			}
			else if (e.widget == deleteFirst){
				((FileOutputVCL)node.getGatewayModel()).setDelete(deleteFirst.getSelection());
			}
			else if (e.widget == truncateField){
				((FileOutputVCL)node.getGatewayModel()).setTruncateField(truncateField.getSelection());
			}
			
		}
	};
	
	public FileOutputVCLSection() {
		
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		
				
		writeHeaders = getWidgetFactory().createButton(composite, Messages.FileOutputVCLSection_0, SWT.CHECK);
		writeHeaders.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		append = getWidgetFactory().createButton(composite, Messages.FileOutputVCLSection_1, SWT.CHECK);
		append.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		append.setVisible(false);
		
		deleteFirst = getWidgetFactory().createButton(composite, Messages.FileOutputVCLSection_2, SWT.CHECK);
		deleteFirst.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		truncateField = getWidgetFactory().createButton(composite, Messages.FileOutputVCLSection_3, SWT.CHECK);
		truncateField.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label l = getWidgetFactory().createLabel(composite, Messages.FileOutputVCLSection_4);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));

		
		viewer = new TableViewer(getWidgetFactory().createTable(composite, SWT.BORDER | SWT.FULL_SELECTION));
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		viewer.setContentProvider(new StreamDescriptorStructuredContentProvider() );
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		
		TableViewerColumn fieldName = new TableViewerColumn(viewer, SWT.LEFT);
		fieldName.getColumn().setText(Messages.FileOutputVCLSection_5);
		fieldName.getColumn().setWidth(200);
		fieldName.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((StreamElement)element).name;
			}
		});
		
		TableViewerColumn fieldSize = new TableViewerColumn(viewer, SWT.LEFT);
		fieldSize.getColumn().setText(Messages.FileOutputVCLSection_6);
		fieldSize.getColumn().setWidth(200);
		fieldSize.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				
				try{
					int pos = node.getGatewayModel().getDescriptor(node.getGatewayModel()).getStreamElements().indexOf(element);
					return "" + ((FileOutputVCL)node.getGatewayModel()).getColumnSizes().get(pos); //$NON-NLS-1$
				}catch(Exception ex){
					return "10"; //$NON-NLS-1$
				}
				
				
			}
		});
	
	
		fieldSize.setEditingSupport(new EditingSupport(viewer) {
			TextCellEditor editor = new TextCellEditor(viewer.getTable());
			@Override
			protected void setValue(Object element, Object value) {
				try{
					int pos = node.getGatewayModel().getDescriptor(node.getGatewayModel()).getStreamElements().indexOf(element);
					((FileOutputVCL)node.getGatewayModel()).setColumnSize(pos, Integer.parseInt((String)value));
				}catch(Exception ex){
					ex.printStackTrace();
				}
				viewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				try{
					int pos = node.getGatewayModel().getDescriptor(node.getGatewayModel()).getStreamElements().indexOf(element);
					return "" + ((FileOutputVCL)node.getGatewayModel()).getColumnSizes().get(pos); //$NON-NLS-1$
				}catch(Exception ex){
					ex.printStackTrace();
					
					return ""; //$NON-NLS-1$
				}
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
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
		writeHeaders.removeSelectionListener(lst);
		append.removeSelectionListener(lst);
		deleteFirst.removeSelectionListener(lst);
		truncateField.removeSelectionListener(lst);
		FileOutputVCL tr = (FileOutputVCL) node.getGatewayModel();

		writeHeaders.setSelection(tr.isContainHeader());
		append.setSelection(tr.isAppend());
		deleteFirst.setSelection(tr.getDelete());
		truncateField.setSelection(tr.getTruncateField());
		writeHeaders.setEnabled(!tr.isAppend());
		
		try {
			viewer.setInput(tr.getDescriptor(tr));
		} catch (ServerException e) {
			e.printStackTrace();
			viewer.setInput(null);
		}
		
		
		writeHeaders.addSelectionListener(lst);
		append.addSelectionListener(lst);
		deleteFirst.addSelectionListener(lst);
		truncateField.addSelectionListener(lst);
	}
	
	
	
}
