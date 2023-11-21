package bpm.oda.driver.reader.wizards.dialog;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.PShelfItem;
import org.eclipse.nebula.widgets.pshelf.RedmondShelfRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import bpm.oda.driver.reader.Activator;
import bpm.oda.driver.reader.icons.IconsName;
import bpm.oda.driver.reader.model.dataset.ParameterDescriptor;
import bpm.oda.driver.reader.viewer.ListContentProvider;

public class DialogParameterValues extends Dialog{

	private HashMap<ParameterDescriptor, String> desc = new HashMap<ParameterDescriptor, String>();
	private TableViewer viewer;
	
	public DialogParameterValues(Shell parentShell, List<ParameterDescriptor> desc) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		for(ParameterDescriptor d : desc){
			this.desc.put(d, "");
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		
		parent.setLayout(new GridLayout(1, false));
		
		Label l = new Label(parent, SWT.NONE);
		l.setText("Define parameters values for each paramater in the list.");
		l.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		l.setFont(new Font (Display.getCurrent(), "Arial", 10, SWT.BOLD));
		
		l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));

		PShelf shelf = new PShelf(parent, SWT.SMOOTH | SWT.BORDER);
		shelf.setLayoutData(new GridData(GridData.FILL, GridData.FILL,true,true,1,1));
		shelf.setRenderer(new RedmondShelfRenderer());
		
		PShelfItem item = new PShelfItem(shelf, SWT.NONE);
		item.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_WRENCH));
		item.setText("Parameter's List");
		
		item.getBody().setLayout(new GridLayout(1, false));
		
		
		viewer = new TableViewer(item.getBody(), SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
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
		pName.getColumn().setText("Parameter Label");
		pName.getColumn().setWidth(200);
		
		TableViewerColumn pValue = new TableViewerColumn(viewer, SWT.NONE);
		pValue.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
			
				return 	((ParameterDescriptor)element).getLabel();
			}
			
		});
		pValue.getColumn().setText("Parameter Value");
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
		getShell().setText("Define Parameters Values");
	}

}
