package bpm.fd.design.ui.wizard.dialogs;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.fd.api.core.model.datas.ParameterDescriptor;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.viewer.contentproviders.ListContentProvider;

public class DialogParameterValues extends Dialog{

	private HashMap<ParameterDescriptor, String> desc = new HashMap<ParameterDescriptor, String>();
	private TableViewer viewer;
	
	public DialogParameterValues(Shell parentShell, List<ParameterDescriptor> desc) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		for(ParameterDescriptor d : desc){
			this.desc.put(d, ""); //$NON-NLS-1$
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		viewer = new TableViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new ListContentProvider<ParameterDescriptor>());
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		
		
		TableViewerColumn pName = new TableViewerColumn(viewer, SWT.NONE);
		pName.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
			
				return 	((ParameterDescriptor)element).getLabel();
			}
			
		});
		pName.getColumn().setText(Messages.DialogParameterValues_1);
		pName.getColumn().setWidth(200);
		
		TableViewerColumn pValue = new TableViewerColumn(viewer, SWT.NONE);
		pValue.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				String s = desc.get((ParameterDescriptor)element);
				if (s == null){
					return "";
				}
				return s;
			}
			
		});
		pValue.getColumn().setText(Messages.DialogParameterValues_2);
		pValue.getColumn().setWidth(200);
		pValue.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				desc.put((ParameterDescriptor)element, (String)value);
				viewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				return desc.get((ParameterDescriptor)element);
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return new TextCellEditor((Composite)viewer.getControl());
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
		
		viewer.setInput(desc.keySet());
		return viewer.getControl();
	}
	
	public HashMap<ParameterDescriptor, String> getValues(){
		return desc;
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
		getShell().setText(Messages.DialogParameterValues_3);
	}

}
