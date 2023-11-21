package bpm.forms.design.ui.composite.celledtitors;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

import bpm.forms.core.design.ITargetTable;
import bpm.forms.design.ui.Activator;
import bpm.forms.design.ui.Messages;
import bpm.forms.design.ui.composite.CompositeTargetTable;

public class TargetTableDialogCellEditor extends DialogCellEditor{
	private FormToolkit toolkit;
	public TargetTableDialogCellEditor(Composite parent, FormToolkit toolkit){
		super(parent);
		this.toolkit = toolkit;
	}
	
	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		DialogTargetTablePicker d = new DialogTargetTablePicker(cellEditorWindow.getShell(), toolkit, (Long)getValue());
		
		if (d.open() == DialogTargetTablePicker.OK){
			return d.getSelectedTargetTable();
		}
		return null;
	}

	
	
	public class DialogTargetTablePicker extends Dialog{
		
		private ComboViewer tables;
		private CompositeTargetTable composite;
		private FormToolkit toolkit;
		
		private ITargetTable selected;
		
		private Long selectionId;
		
		
		public DialogTargetTablePicker(Shell parentShell, FormToolkit toolkit) {
			super(parentShell);
			setShellStyle(getShellStyle() | SWT.RESIZE);
			this.toolkit = toolkit;
		}
		
		public DialogTargetTablePicker(Shell parentShell, FormToolkit toolkit, Long selectionId) {
			this(parentShell, toolkit);
			this.selectionId = selectionId;
		
		}
		
		@Override
		protected Control createDialogArea(Composite parent) {
			
			Composite main = toolkit.createComposite(parent);
			main.setLayout(new GridLayout(2, false));
			
			main.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			Label l = toolkit.createLabel(main, Messages.TargetTableDialogCellEditor_0);
			l.setLayoutData(new GridData());
			
			tables = new ComboViewer(main, SWT.READ_ONLY);
			tables.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			tables.setContentProvider(new ObservableListContentProvider());
			tables.setLabelProvider(new LabelProvider(){
				@Override
				public String getText(Object element) {
					return ((ITargetTable)element).getName();
				}
			});
			
			
			
			composite = new CompositeTargetTable(toolkit);
			composite.createContent(main);
			composite.getClient().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
			composite.getClient().setEnabled(false);
			
			
			return main;
		}
		
		@Override
		protected void createButtonsForButtonBar(Composite parent) {
			Button b = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
			b.setEnabled(false);
			createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
		}
		
		@Override
		protected void okPressed() {
			selected = (ITargetTable)((IStructuredSelection)tables.getSelection()).getFirstElement();
			super.okPressed();
		}
		
		
		public ITargetTable getSelectedTargetTable(){
			return selected;
		}
		
		@Override
		protected void initializeBounds() {
			getShell().setText(Messages.TargetTableDialogCellEditor_1);
			getShell().setSize(400, 300);
			
			WritableList l = new WritableList(Activator.getDefault().getServiceProvider().getDefinitionService().getTargetTables(), ITargetTable.class);
			tables.setInput(l);
			
			
			
			tables.addSelectionChangedListener(new ISelectionChangedListener() {
				
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					composite.setInput((ITargetTable)((IStructuredSelection)tables.getSelection()).getFirstElement());
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				}
			});
			
			if (selectionId != null){
				for(Object o : l){
					if (((ITargetTable)o).getId() == selectionId){
						tables.setSelection(new StructuredSelection(o));
					}
				}
			}
			
		}
	}
	
	
	
}
