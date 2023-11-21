package bpm.vanilla.server.ui.wizard.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import bpm.repository.api.model.IDirectoryItem;
import bpm.repository.api.model.IRepositoryConnection;
import bpm.repository.api.model.Parameter;
import bpm.vanilla.repository.ui.viewers.RepositoryLabelProvider;

public class ListPage extends WizardPage{

	private ListViewer objectViewer;
	private TableViewer parameterViewer;
	private IRepositoryConnection repSocket;
	
	private HashMap<IDirectoryItem, HashMap<Parameter, String>> params = new HashMap<IDirectoryItem, HashMap<Parameter, String>> ();
	
	
	public ListPage(String pageName) {
		super(pageName);
		
	}


	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Select Object");
		
		objectViewer = new ListViewer(main, SWT.BORDER);
		objectViewer.setLabelProvider(new RepositoryLabelProvider());
		objectViewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				Collection c = (Collection)inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		objectViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				if (objectViewer.getSelection().isEmpty()){
					parameterViewer.setInput(new ArrayList<String>());
				}
				else{
					IDirectoryItem it = (IDirectoryItem)((IStructuredSelection)objectViewer.getSelection()).getFirstElement();
					if (params.get(it) == null){
						HashMap<Parameter, String> h = new HashMap<Parameter, String>();
						try{
							for(Parameter p : repSocket.getParametersFor(it)){
								h.put(p, "");
							}
						}catch(Exception ex){
							ex.printStackTrace();
							MessageDialog.openError(getShell(), "Problem Occured", "Unable to get Object Parameters : \n\t-" + ex.getMessage());
						}
						
						params.put(it, h);
					}
					
					parameterViewer.setInput(params.get(it).keySet());
				}
				
			}
		});
		objectViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Define Object Parameters Values");
		
		parameterViewer = new TableViewer(main, SWT.BORDER);
		parameterViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		parameterViewer.getTable().setHeaderVisible(true);
		parameterViewer.getTable().setLinesVisible(true);
		parameterViewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				Collection c = (Collection)inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		
		TableViewerColumn paramName = new TableViewerColumn(parameterViewer, SWT.NONE);
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
		

		TableViewerColumn paramValue = new TableViewerColumn(parameterViewer, SWT.NONE);
		paramValue.getColumn().setText("Parameter Value");
		paramValue.getColumn().setWidth(200);
		
		paramValue.setLabelProvider(new ColumnLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				IDirectoryItem it = (IDirectoryItem)((IStructuredSelection)objectViewer.getSelection()).getFirstElement();
				
				return params.get(it).get((Parameter)element);

			}
			
		});
		paramValue.setEditingSupport(new EditingSupport(parameterViewer) {
			
			TextCellEditor editor = new TextCellEditor(parameterViewer.getTable());
			@Override
			protected void setValue(Object element, Object value) {
				IDirectoryItem it = (IDirectoryItem)((IStructuredSelection)objectViewer.getSelection()).getFirstElement();
				
				params.get(it).put((Parameter)element, (String)value);
				parameterViewer.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				IDirectoryItem it = (IDirectoryItem)((IStructuredSelection)objectViewer.getSelection()).getFirstElement();
				return params.get(it).get(element);
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

	public void setInput(Collection<IDirectoryItem> items, IRepositoryConnection sock){
		this.repSocket = sock;
		
		params.clear();
//		for(IDirectoryItem it : items){
//			params.put(it, new HashMap<Parameter, String>());
//		}
		
		objectViewer.setInput(items);
	}
	
	public HashMap<IDirectoryItem, HashMap<Parameter, String>> getParameters(){
		return params;
	}
	
}
