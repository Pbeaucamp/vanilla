package bpm.es.pack.manager.wizard.exp;
//package bpm.es.pack.manager.exp;
//
//import java.util.Collection;
//
//import org.eclipse.jface.viewers.CellEditor;
//import org.eclipse.jface.viewers.ColumnLabelProvider;
//import org.eclipse.jface.viewers.EditingSupport;
//import org.eclipse.jface.viewers.ISelectionChangedListener;
//import org.eclipse.jface.viewers.IStructuredSelection;
//import org.eclipse.jface.viewers.ITreeContentProvider;
//import org.eclipse.jface.viewers.SelectionChangedEvent;
//import org.eclipse.jface.viewers.TextCellEditor;
//import org.eclipse.jface.viewers.TreeViewer;
//import org.eclipse.jface.viewers.TreeViewerColumn;
//import org.eclipse.jface.viewers.Viewer;
//import org.eclipse.jface.wizard.WizardPage;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.events.SelectionAdapter;
//import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.ToolBar;
//import org.eclipse.swt.widgets.ToolItem;
//
//import adminbirep.Activator;
//import adminbirep.Messages;
//import bpm.vanilla.platform.core.beans.pack.old.ImportItem;
//import bpm.vanilla.platform.core.beans.pack.old.Replacement;
//
//public class MappingPage extends WizardPage{
//
//	private TreeViewer content;
//	private ToolItem add, del;
//	private CompositeMapping2 mappingComposite;
//
//	protected MappingPage(String pageName) {
//		super(pageName);
//	}
//
//	public void createControl(Composite parent) {
//		Composite main = new Composite(parent, SWT.NONE);
//		main.setLayout(new GridLayout());
//		main.setLayoutData(new GridData(GridData.FILL_BOTH));
//		
//		createToolbar(main);
//		createTree(main);
//		
//		setControl(main);
//		refreshInput();
//		
//		mappingComposite = new CompositeMapping2(main, SWT.NONE);
//		mappingComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
//	}
//	
//	public void refreshInput(){
//		Vanillap
//		content.setInput();
//	}
//	
//	private void createTree(Composite parent){
//		content = new TreeViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
//		content.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
//		content.getTree().setLinesVisible(true);
//		content.getTree().setHeaderVisible(true);
//		content.setContentProvider(new ITreeContentProvider() {
//			
//			@Override
//			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
//			
//			@Override
//			public void dispose() { }
//			
//			@Override
//			public Object[] getElements(Object inputElement) {
//				Collection c = ((Collection)inputElement);
//				return c.toArray(new Object[c.size()]);
//			}
//			
//			@Override
//			public boolean hasChildren(Object element) {
//				if (element instanceof ImportItem){
//					return !((ImportItem)element).getAutoReplacements().isEmpty();
//				}
//				return false;
//			}
//			
//			@Override
//			public Object getParent(Object element) {
//				if (element instanceof ImportItem){
//					return content.getInput();
//				}
//				else{
//					return ((Replacement)element).getItem();
//				}
//			}
//			
//			@Override
//			public Object[] getChildren(Object parentElement) {
//				Collection c = ((ImportItem)parentElement).getAutoReplacements();
//				return c.toArray(new Object[c.size()]);
//			}
//		});
//		content.addSelectionChangedListener(new ISelectionChangedListener() {
//			
//			@Override
//			public void selectionChanged(SelectionChangedEvent event) {
//				add.setEnabled(!content.getSelection().isEmpty());
//				del.setEnabled(!content.getSelection().isEmpty());
//				
//				Object o = ((IStructuredSelection)content.getSelection()).getFirstElement();
//				if (o instanceof ImportItem){
//					mappingComposite.setImportItem(((ExportWizard)getWizard()).getRepository(), (ImportItem)o);
//				}
//				
//			}
//		});
//		
//		TreeViewerColumn colName = new TreeViewerColumn(content, SWT.NONE);
//		colName.getColumn().setWidth(100);
//		colName.getColumn().setText("Object Name"); //$NON-NLS-1$
//		colName.setLabelProvider(new ColumnLabelProvider(){
//			@Override
//			public String getText(Object element) {
//				if (element instanceof ImportItem){
//					return ((ImportItem)element).getName();
//				}
//				return ""; //$NON-NLS-1$
//			}
//		});
//		
//		TreeViewerColumn oldValue = new TreeViewerColumn(content, SWT.NONE);
//		oldValue.getColumn().setWidth(200);
//		oldValue.getColumn().setText("Replace"); //$NON-NLS-1$
//		oldValue.setLabelProvider(new ColumnLabelProvider(){
//			@Override
//			public String getText(Object element) {
//				if (element instanceof Replacement){
//					return ((Replacement)element).getOriginalString();
//				}
//				return ""; //$NON-NLS-1$
//			}
//		});
//		oldValue.setEditingSupport(new EditingSupport(content) {
//			
//			TextCellEditor editor = new TextCellEditor(content.getTree());
//			
//			@Override
//			protected void setValue(Object element, Object value) {
//				((Replacement)element).setOriginalString((String)value);
//				content.refresh();
//			}
//			
//			@Override
//			protected Object getValue(Object element) {
//				if (element instanceof Replacement){
//				return ((Replacement)element).getOriginalString();	
//				}
//				return ""; //$NON-NLS-1$
//			}
//			
//			@Override
//			protected CellEditor getCellEditor(Object element) {
//				return editor;
//			}
//			
//			@Override
//			protected boolean canEdit(Object element) {
//				return element instanceof Replacement;
//			}
//		});
//		
//		
//		TreeViewerColumn newValue = new TreeViewerColumn(content, SWT.NONE);
//		newValue.getColumn().setWidth(200);
//		newValue.getColumn().setText("By"); //$NON-NLS-1$
//		newValue.setLabelProvider(new ColumnLabelProvider(){
//			
//			@Override
//			public String getText(Object element) {
//				if (element instanceof Replacement){
//					return ((Replacement)element).getReplacementString();
//				}
//				return ""; //$NON-NLS-1$
//			}
//		});
//		newValue.setEditingSupport(new EditingSupport(content) {
//			
//			TextCellEditor editor = new TextCellEditor(content.getTree());
//			
//			@Override
//			protected void setValue(Object element, Object value) {
//				((Replacement)element).setReplacementString((String)value);
//				content.refresh();
//			}
//			
//			@Override
//			protected Object getValue(Object element) {
//				if (element instanceof Replacement){
//				return ((Replacement)element).getReplacementString();	
//				}
//				return ""; //$NON-NLS-1$
//			}
//			
//			@Override
//			protected CellEditor getCellEditor(Object element) {
//				return editor;
//			}
//			
//			@Override
//			protected boolean canEdit(Object element) {
//				return element instanceof Replacement;
//			}
//		});
//	}
//
//	private void createToolbar(Composite parent){
//		ToolBar bar = new ToolBar(parent, SWT.HORIZONTAL);
//		bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		
//		add = new ToolItem(bar, SWT.PUSH);
//		add.setToolTipText(Messages.MappingPage_8);
//		add.setImage(Activator.getDefault().getImageRegistry().get("add")); //$NON-NLS-1$
//		add.setEnabled(false);
//		add.addSelectionListener(new SelectionAdapter() {
//
//			/* (non-Javadoc)
//			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
//			 */
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				for(Object o : ((IStructuredSelection)content.getSelection()).toList()){
//					((ImportItem)o).addAutoReplacement("oldValue", "newValue"); //$NON-NLS-1$ //$NON-NLS-2$
//				}
//				content.refresh();
//			}
//			
//		});
//		
//		
//		del = new ToolItem(bar, SWT.PUSH);
//		del.setToolTipText(Messages.MappingPage_12);
//		del.setImage(Activator.getDefault().getImageRegistry().get("del")); //$NON-NLS-1$
//		del.setEnabled(false);
//		del.addSelectionListener(new SelectionAdapter() {
//			
//			@Override
//			public void widgetSelected(SelectionEvent e) {	
//				for(Object o : ((IStructuredSelection)content.getSelection()).toList()){
//					if (o instanceof Replacement){
//						((Replacement)o).getItem().removeAutoReplacement((Replacement)o);
//					}
//				}
//				content.refresh();
//			}
//		});
//	}
//
//	@Override
//	public boolean isPageComplete() {
//		return true;
//	}
//
//	@Override
//	protected boolean isCurrentPage() {
//		return super.isCurrentPage();
//	}
//}
