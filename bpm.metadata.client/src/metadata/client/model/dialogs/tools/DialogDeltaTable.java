package metadata.client.model.dialogs.tools;

import java.util.Collection;
import java.util.HashMap;

import metadata.client.i18n.Messages;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.sql.SQLDataStream;
import bpm.metadata.layer.logical.sql.SQLDataStreamElement;
import bpm.metadata.layer.physical.IColumn;
import bpm.metadata.layer.physical.ITable;
import bpm.metadata.layer.physical.sql.SQLColumn;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.layer.physical.sql.SQLTable;

/**
 * Dialog to check the difference between a DataStream
 * and the Physical SQL Table on which it is based
 * Allow you to remove or add Columns
 * @author ludo
 *
 */
public class DialogDeltaTable extends Dialog{
	private static final Color red = new Color(Display.getDefault(), 255, 0, 0);
	private static final String columnUsed = Messages.DialogDeltaTable_0;
	

	private CheckboxTableViewer dataStreamViewer;
	private CheckboxTableViewer tableViewer;
	private SQLDataStream dataStream;
	
	private HashMap<IColumn, String> selectedColumnNames = new HashMap<IColumn, String>();
	
	
	public DialogDeltaTable(Shell parentShell, SQLDataStream dataStream) {
		super(parentShell);
		this.dataStream = dataStream;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, true));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createViewer(main);
		
		dataStreamViewer.setInput(dataStream);
		
		try{
			SQLConnection con = (SQLConnection)dataStream.getOrigin().getConnection();
			
			ITable table = dataStream.getOrigin();
			con.getColumns((SQLTable)table);
			tableViewer.setInput(table);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
		
		return main;
	}
	
	private void createViewer(Composite composite){
		dataStreamViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		dataStreamViewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {	}
			
			public void dispose() {	}
			
			public Object[] getElements(Object inputElement) {
				IDataStream ds = (IDataStream)inputElement;
				Collection l = ds.getElements();
				return l.toArray(new Object[l.size()]);
			}
		});
		dataStreamViewer.getTable().setLinesVisible(true);
		dataStreamViewer.getTable().setHeaderVisible(true);
		dataStreamViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		TableViewerColumn fieldCol = new TableViewerColumn(dataStreamViewer, SWT.FILL);
		fieldCol.getColumn().setWidth(250);
		fieldCol.getColumn().setText(Messages.DialogDeltaTable_1);
		fieldCol.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
			
				return ((IDataStreamElement)element).getName();
			}
			
			@Override
			public Color getBackground(Object element) {
				if (!(element instanceof ICalculatedElement) && ((IDataStreamElement)element).getOrigin() == null){
					dataStreamViewer.setSelection(StructuredSelection.EMPTY);
					return red;
				}
				return super.getBackground(element);
			}
		});
			
		
		tableViewer =  CheckboxTableViewer.newCheckList(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		tableViewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
			
			public void dispose() {	}
			
			public Object[] getElements(Object inputElement) {
				SQLTable ds = (SQLTable)inputElement;
				Collection l = ds.getColumns();
				return l.toArray(new Object[l.size()]);
			}
		});
		
		
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		tableViewer.addFilter(new ViewerFilter() {
			
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				System.out.println(((SQLColumn)element).getName());
				for(IDataStreamElement c : dataStream.getElements()){
					try {
						if (element == c.getOrigin() || c.getOrigin().getName().equals(((SQLColumn)element).getName())){
							return false;
						}
					} catch (Exception e) {
					}
				}
				
				return true;
			}
		});
		tableViewer.addCheckStateListener(new ICheckStateListener() {
			
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked() && selectedColumnNames.get(event.getElement()) == null){
					IColumn col = (IColumn)event.getElement();
					selectedColumnNames.put(col, new String(col.getName()));
				}
				tableViewer.refresh();
			}
		});
		
		
		fieldCol = new TableViewerColumn(tableViewer, SWT.NONE);
		fieldCol.getColumn().setWidth(250);
		fieldCol.getColumn().setText(Messages.DialogDeltaTable_2);
		fieldCol.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((IColumn)element).getName();
			}
		});
		
		TableViewerColumn nameCol = new TableViewerColumn(tableViewer, SWT.NONE);
		nameCol.getColumn().setWidth(250);
		nameCol.getColumn().setText(Messages.DialogDeltaTable_3);
		nameCol.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (selectedColumnNames.get(element) == null){
					return ""; //$NON-NLS-1$
				}
				return selectedColumnNames.get(element);
			}
			
			@Override
			public Color getBackground(Object element) {
				String s = selectedColumnNames.get(element);
				if (s == null || dataStream.getElementNamed(s) == null){
					return super.getBackground(element);
				}
				
				tableViewer.setSelection(StructuredSelection.EMPTY);
				return red;
			}
			@Override
			public String getToolTipText(Object element) {
				if (getBackground(element) == red){
					return columnUsed;
				}
				return super.getToolTipText(element);
			}
		});

		nameCol.setEditingSupport(new EditingSupport(tableViewer) {
			TextCellEditor editor = new TextCellEditor(tableViewer.getTable());
			
			@Override
			protected void setValue(Object element, Object value) {
				selectedColumnNames.put((IColumn)element, (String)value);
				tableViewer.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				if (selectedColumnNames.get(element) == null){
					return ""; //$NON-NLS-1$
				}
				return selectedColumnNames.get(element);
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return tableViewer.getChecked(element);
			}
		});
		
		
	}

	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogDeltaTable_6);
		getShell().setSize(800, 600);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Messages.DialogDeltaTable_7, false);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
		
	}
	
	private String performChanges() throws Exception{
		StringBuffer buf = new StringBuffer();
		
		
		if (dataStreamViewer.getCheckedElements().length > 0){
			if (!MessageDialog.openQuestion(getShell(), Messages.DialogDeltaTable_8, Messages.DialogDeltaTable_9)){
				return null;
			}
		}
		
		for(Object o : dataStreamViewer.getCheckedElements()){
			dataStream.removeDataStreamElement((IDataStreamElement)o);
			buf.append(Messages.DialogDeltaTable_10 + ((IDataStreamElement)o).getName() + Messages.DialogDeltaTable_11);
		}
		
		for(Object o : tableViewer.getCheckedElements()){
			SQLDataStreamElement col = new SQLDataStreamElement((SQLColumn)o);
			if (dataStream.getElementNamed(col.getName()) != null){
				throw new Exception(Messages.DialogDeltaTable_12 + col.getName() + Messages.DialogDeltaTable_13);
			}
			dataStream.addColumn(col);
			buf.append(Messages.DialogDeltaTable_14 + col.getName() + Messages.DialogDeltaTable_15);
		}
		
		return buf.toString();
	}
	
	
	@Override
	protected void okPressed() {
		String s = ""; //$NON-NLS-1$
		try{
			s = performChanges();
			
			if (s == null){
				MessageDialog.openInformation(getShell(), Messages.DialogDeltaTable_17, Messages.DialogDeltaTable_18);
			}
			
		}catch(Exception ex){
			MessageDialog.openError(getShell(), Messages.DialogDeltaTable_19, ex.getMessage());
			return;
		}
		
		super.okPressed();
		
		MessageDialog.openInformation(getShell(), Messages.DialogDeltaTable_20, s);
	}
	
}

