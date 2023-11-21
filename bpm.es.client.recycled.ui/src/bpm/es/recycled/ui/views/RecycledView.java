package bpm.es.recycled.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import bpm.birep.admin.client.content.views.ViewTree;
import bpm.birep.admin.client.dialog.DialogXmlViewer;
import bpm.birep.admin.client.trees.TreeDirectory;
import bpm.es.recycled.ui.Messages;
import bpm.es.recycled.ui.icons.Icons;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class RecycledView extends ViewPart implements ISelectionListener{
	
	public static final String ID = "bpm.es.recycled.ui.views.RecycledView"; //$NON-NLS-1$

	protected TreeViewer viewer;
	protected TableViewer tableViewer;
	
	private Composite container;
	private int directory_id;
	
	protected ViewerFilter deletedFilter;
	private IRepository repository;
	
	public RecycledView(){
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().addSelectionListener(this);
		repository = new Repository(Activator.getDefault().getRepositoryApi());
	}
	
	@Override
	public void dispose() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().removeSelectionListener(this);
		super.dispose();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		container.setLayout(new GridLayout());
		
		createFilter();
		createToolbar(container);
		createRecycle(container);
		
		IToolBarManager mngr = this.getViewSite().getActionBars().getToolBarManager();
		mngr.add(new ViewActionRefresh(this));	
	}
	
	private void createRecycle(Composite container) {
		
		viewer = new TreeViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.VIRTUAL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		viewer.setContentProvider(new ITreeContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
			
			@Override
			public void dispose() { }
			
			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List l = (List) inputElement;
				return l.toArray(new Object[l.size()]);
			}
			
			@Override
			public boolean hasChildren(Object element) {
				if (element instanceof RepositoryDirectory){
					try {
						if (repository.getChildDirectories((RepositoryDirectory)element).size() == 0 && repository.getItems((RepositoryDirectory)element).size() == 0){
							return false;
						}
						else{
							return true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return false;
			}
			
			@Override
			public Object getParent(Object element) {
				return null;
			}
			
			@Override
			public Object[] getChildren(Object parentElement) {
				List<Object> l = new ArrayList<Object>();
				if (parentElement instanceof RepositoryDirectory){
					try{
						l.addAll(repository.getChildDirectories((RepositoryDirectory)parentElement));
						l.addAll(repository.getItems((RepositoryDirectory)parentElement));
					}catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				return l.toArray(new Object[l.size()]);
			}
		});
		
		loadData();
		viewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof RepositoryDirectory)
				{
					RepositoryDirectory dir = (RepositoryDirectory) element;
					return dir.getName();
				}
				else if(element instanceof RepositoryItem){
					RepositoryItem item = (RepositoryItem) element;
					return item.getItemName();
				}
				else{
					RepositoryItem item = (RepositoryItem) element;
					return item.getItemName();
				}
			}
			
			@Override
			public Image getImage(Object element) {
				if(element instanceof RepositoryItem){
					RepositoryItem rep = (RepositoryItem)element;
					ImageRegistry reg = Activator.getDefault().getImageRegistry();
					String key = bpm.birep.admin.client.trees.TreeHelperType.getKeyForType(rep.getType(), rep);	
					return reg.get(key);
				}
				else if(element instanceof RepositoryDirectory){
					return bpm.es.recycled.ui.Activator.getDefault().getImageRegistry().get(Icons.FOLDER);
				}
				else{
					return null;
				}
			}
		});
	}	
	
	@Override
	public void setFocus() { }

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		IStructuredSelection ss = (IStructuredSelection)selection;
		Object o = ss.getFirstElement();
		
		ViewTree.selectedObject = o;
		
		if (o instanceof TreeDirectory){
			directory_id = ((TreeDirectory) o).getDirectory().getId();
			viewer.setFilters(new ViewerFilter[]{});
			viewer.addFilter(deletedFilter);
		}
	}
	
	public void loadData(){
		List<Object> itemsAndDirectories = new ArrayList<Object>();
		List<RepositoryDirectory> dirList = new ArrayList<RepositoryDirectory>();
		List<RepositoryItem> itemsList = new ArrayList<RepositoryItem>();
		
		try {	
			dirList = Activator.getDefault().getRepositoryApi().getAdminService().getDeletedDirectories();
			itemsList = Activator.getDefault().getRepositoryApi().getAdminService().getDeletedItems();
			itemsAndDirectories.addAll(dirList != null ? dirList : new ArrayList<Object>());
			itemsAndDirectories.addAll(itemsList != null ? itemsList : new ArrayList<Object>());
		} catch (Exception e) {
			e.printStackTrace();
		}
		viewer.setInput(itemsAndDirectories);
		viewer.removeFilter(deletedFilter);
	}
	
	private void createToolbar(Composite parent){
		ToolBar toolbar = new ToolBar(parent, SWT.NONE);
		toolbar.setLayout(new GridLayout());
		
		ToolItem seeAllDeletedObjects = new ToolItem(toolbar, SWT.PUSH);
		seeAllDeletedObjects.setToolTipText(Messages.RecycledView_1);
		seeAllDeletedObjects.setImage(bpm.es.recycled.ui.Activator.getDefault().getImageRegistry().get(Icons.VIEW));
		seeAllDeletedObjects.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				viewer.removeFilter(deletedFilter);
			}
		});
		
		ToolItem viewXml = new ToolItem(toolbar, SWT.PUSH);
		viewXml.setToolTipText(Messages.RecycledView_2);
		viewXml.setImage(bpm.es.recycled.ui.Activator.getDefault().getImageRegistry().get(Icons.XML));
		viewXml.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				
				if (ss.isEmpty() ||  !(ss.getFirstElement() instanceof RepositoryItem)){
					return;
				}
				
				RepositoryItem it = (RepositoryItem)ss.getFirstElement();
				
				DialogXmlViewer dial = new DialogXmlViewer(getSite().getShell(), it);
				if (dial.open() == DialogXmlViewer.OK && dial.isModified()){
					try {
						Activator.getDefault().getRepositoryApi().getRepositoryService().updateModel(it, dial.getXml());
						MessageDialog.openInformation(getSite().getShell(), Messages.RecycledView_3, Messages.RecycledView_4);
					} catch (Exception e1) {
						e1.printStackTrace();
						MessageDialog.openError(getSite().getShell(), Messages.RecycledView_5, e1.getMessage());
					}
				}
			}
			
		});
		
		ToolItem restoreItem = new ToolItem(toolbar, SWT.PUSH);
		restoreItem.setToolTipText(Messages.RecycledView_6);
		restoreItem.setImage(bpm.es.recycled.ui.Activator.getDefault().getImageRegistry().get(Icons.RESTORE));
		restoreItem.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				if (ss.isEmpty()){
					return; 
				}
				try {
					List<Object> l = new ArrayList<Object>();
					l = ss.toList();
					for(int i=0; i<l.size();i++){
						if(l.get(i) instanceof RepositoryItem){
							Activator.getDefault().getRepositoryApi().getAdminService().restoreDirectoryItem(((RepositoryItem)l.get(i)).getId());
							loadData();
						}
						else if(l.get(i) instanceof RepositoryDirectory){
							Activator.getDefault().getRepositoryApi().getAdminService().restoreDirectory(((RepositoryDirectory)l.get(i)).getId());
							loadData();
						}
					}
				}catch (Exception ex) {
					MessageDialog.openError(getSite().getShell(), Messages.RecycledView_7, ex.getMessage());
				}
			}
		});
		
		ToolItem purgeSelectedObject = new ToolItem(toolbar, SWT.PUSH);
		purgeSelectedObject.setToolTipText(Messages.RecycledView_8);
		purgeSelectedObject.setImage(bpm.es.recycled.ui.Activator.getDefault().getImageRegistry().get(Icons.PURGE_SELECTED_ITEM));
		purgeSelectedObject.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				if (ss.isEmpty()){
					return; 
				}
				
				boolean confirm = MessageDialog.openConfirm(getSite().getShell(), Messages.RecycledView_9, Messages.RecycledView_10);
				if(confirm){
					try {
					
						List<Object> list = new ArrayList<Object>();
						list = ss.toList();
						
						List<RepositoryDirectory> dirToPurgeIdsTemp = new ArrayList<RepositoryDirectory>();
						List<RepositoryItem> itemToPurgeIdsTemp = new ArrayList<RepositoryItem>();
						for(Object object : list){
							if(object instanceof RepositoryItem){
								RepositoryItem item = (RepositoryItem)object;
								
								itemToPurgeIdsTemp.add(item);
							}
							
							if(object instanceof RepositoryDirectory){
								dirToPurgeIdsTemp.add(((RepositoryDirectory)object));
							}
						}
						
						Activator.getDefault().getRepositoryApi().getAdminService().purgeDeletedObjects(dirToPurgeIdsTemp, itemToPurgeIdsTemp);
					}catch (Exception ex) {
						MessageDialog.openError(getSite().getShell(), Messages.RecycledView_11, ex.getMessage());
					}

					loadData();
				}
			}
		});
		
		ToolItem purgeObjects = new ToolItem(toolbar, SWT.PUSH);
		purgeObjects.setToolTipText(Messages.RecycledView_12);
		purgeObjects.setImage(bpm.es.recycled.ui.Activator.getDefault().getImageRegistry().get(Icons.PURGE));
		purgeObjects.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean confirm = MessageDialog.openConfirm(getSite().getShell(), Messages.RecycledView_13, Messages.RecycledView_14);
				if(confirm){
					try {
						Activator.getDefault().getRepositoryApi().getAdminService().purgeAllDeletedObjects();
						
						loadData();
					}catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});
	}
	
	public void createFilter(){
		deletedFilter = new ViewerFilter(){
			
			   @Override
			   public boolean select(Viewer viewer, Object parentElement, Object element) {
			   		if(element instanceof RepositoryDirectory) {
						return false;
					}
					else{
						RepositoryItem item = (RepositoryItem) element;
						if(item.getDirectoryId() == directory_id) {
							return true;
						}
					}
					return false;
			   }
		};
	}
	
	public void refresh(){
		viewer.refresh();
	}
}
