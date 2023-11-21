package bpm.vanilla.server.ui.wizard;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import bpm.repository.api.model.Parameter;

public class DirectoryItemParameterPage extends WizardPage{
	
	private TableViewer table;
	private HashMap<Parameter, String> pValues = new HashMap<Parameter, String>();
	
	
	protected DirectoryItemParameterPage(String pageName) {
		super(pageName);
		
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		table = new TableViewer(main, SWT.BORDER);
		table.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		table.getTable().setHeaderVisible(true);
		table.getTable().setLinesVisible(true);
		table.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				Collection c = (Collection)inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		
		TableViewerColumn paramName = new TableViewerColumn(table, SWT.NONE);
		paramName.getColumn().setText("Parameter Name");
		paramName.getColumn().setWidth(200);
		
		paramName.setLabelProvider(new ColumnLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				
				return ((Parameter)element).getName();
			}
			
		});
		

		TableViewerColumn paramValue = new TableViewerColumn(table, SWT.NONE);
		paramValue.getColumn().setText("Parameter Value");
		paramValue.getColumn().setWidth(200);
		
		paramValue.setLabelProvider(new ColumnLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				
				return pValues.get((Parameter)element);
			}
			
		});
		paramValue.setEditingSupport(new EditingSupport(table) {
			
			TextCellEditor editor = new TextCellEditor(table.getTable());
			@Override
			protected void setValue(Object element, Object value) {
				pValues.put((Parameter)element, (String)value);
				table.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				return pValues.get(element);
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
		
		setControl(main);
		
	}
	
	
	public void setParameters(List<Parameter> itemParameters){
		pValues.clear();
		
		for(Parameter p : itemParameters){
			pValues.put(p, "");
		}
		
		table.setInput(itemParameters);
	}
	

	public HashMap<Parameter, String> getParametersValues(){
		return pValues;
	}
}
