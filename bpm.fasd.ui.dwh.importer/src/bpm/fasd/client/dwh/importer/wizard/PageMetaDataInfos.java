package bpm.fasd.client.dwh.importer.wizard;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.fasd.datasource.DataObject;

import bpm.fasd.client.dwh.importer.Messages;
import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.sqldesigner.api.model.Table;

public class PageMetaDataInfos extends WizardPage {

	
	private HashMap<Table, String> tablesType = new HashMap<Table, String>(); 
	private TreeViewer viewer;
//	private Text businessModel, businessPackage;
//	private ModifyListener txtListener = new ModifyListener() {
//		
//		@Override
//		public void modifyText(ModifyEvent e) {
//			getContainer().updateButtons();
//			
//		}
//	};
	
	
	protected PageMetaDataInfos(String pageName) {
		super(pageName);
		setDescription(Messages.PageMetaDataInfos_0);
	}

	@Override
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		
		createContent(main);
		setControl(main);
		
	}
	
	private void createContent(Composite parent){
		viewer = new TreeViewer(parent, SWT.FULL_SELECTION | SWT.VIRTUAL | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.getTree().setHeaderVisible(true);
		viewer.getTree().setLinesVisible(true);
		viewer.setContentProvider(new ITreeContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			@Override
			public void dispose() {
				
				
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				Collection<?> c = (Collection<?>)inputElement;
				return c.toArray(new Object[c.size()]);
			}

			@Override
			public Object[] getChildren(Object parentElement) {
				
				return null;
			}

			@Override
			public Object getParent(Object element) {
				
				return null;
			}

			@Override
			public boolean hasChildren(Object element) {
				
				return false;
			}
		});
		
		
		TreeViewerColumn col = new TreeViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.PageMetaDataInfos_1);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Table)element).getName();
			}
		});
		
		col = new TreeViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.PageMetaDataInfos_2);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				String s  = tablesType.get((Table)element);
				if (s != null){
					return s;
				}
				return ""; //$NON-NLS-1$
			}
		});
		
		col.setEditingSupport(new EditingSupport(viewer) {
			ComboBoxCellEditor editor = new ComboBoxCellEditor(viewer.getTree(), DataObject.TYPES, SWT.READ_ONLY);
			@Override
			protected void setValue(Object element, Object value) {
				Integer i = (Integer)value;
				if (i >= 0 && i < DataObject.TYPES.length){
					tablesType.put((Table)element, DataObject.TYPES[i]);
				
				}
				
				viewer.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				for(int i = 0; i < DataObject.TYPES.length; i++){
					if (DataObject.TYPES[i].equals(tablesType.get(element))){
						return i;
					}
				}
				return -1;
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
	
	
	protected void setTables(DocumentSnapshot snapshot){
		tablesType.clear();
		for(Table t : snapshot.getTables()){
			tablesType.put(t, ""); //$NON-NLS-1$
		}
		viewer.setInput(snapshot.getTables());
	}
	
	@Override
	public boolean isPageComplete() {
		
		return true;
	}
	
	public Object getConfigurationContext(){
//		HashMap<String, Object> configurationObject = new HashMap<String, Object>();
		
		
		
		return tablesType;
	}
}
