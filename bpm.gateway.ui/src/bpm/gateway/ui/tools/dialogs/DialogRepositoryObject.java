package bpm.gateway.ui.tools.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.viewer.TreeContentProvider;
import bpm.gateway.ui.viewer.TreeDirectory;
import bpm.gateway.ui.viewer.TreeItem;
import bpm.gateway.ui.viewer.TreeLabelProvider;
import bpm.gateway.ui.viewer.TreeParent;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class DialogRepositoryObject extends Dialog {


	private TreeViewer treeViewer;
//	private Combo type;
	private RepositoryItem item;
	private RepositoryDirectory directory;
	private int itemType;
	private Integer subType;
	
	
	private ViewerFilter filter;
	
	public DialogRepositoryObject(Shell parentShell){
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		itemType = -1;
	}
	
	
	public DialogRepositoryObject(Shell parentShell, int itemType) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.itemType = itemType;
		
	}
	
	public DialogRepositoryObject(Shell parentShell, int itemType, int subType) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.itemType = itemType;
		this.subType = subType;
		
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(700, 500);
		getShell().setText(Messages.DialogRepositoryObject_0);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite filter = new Composite(parent, SWT.NONE);
		filter.setLayout(new GridLayout(2, false));
		filter.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
	
		treeViewer = new TreeViewer(container);
		treeViewer.setContentProvider(new TreeContentProvider());
		treeViewer.setLabelProvider(new TreeLabelProvider());
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)treeViewer.getSelection();
				
				if (ss.isEmpty()){
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					return;
				}
				
				if (ss.getFirstElement() instanceof TreeItem){
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				}
				else if (ss.getFirstElement() instanceof TreeDirectory){
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				}
				else{
					getButton(IDialogConstants.OK_ID).setEnabled(false);
				}
				
			}
			
		});
		
		buildTree();
		
		
		if (this.filter != null){
			treeViewer.addFilter(this.filter);
		}
		return container;
	}
	
	
	private void buildTree(){
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		
		IRepository rep;
		
		try {
			IRepositoryApi sock = bpm.gateway.ui.Activator.getDefault().getRepositoryConnection();
			if (subType != null){
				rep = new Repository(sock, itemType, subType);
			}
			else if (itemType == -1 || itemType < -1){
				rep = new Repository(sock);
			}
			else{
				rep = new Repository(sock, itemType);
			}
			
			
			for(RepositoryDirectory dir : rep.getRootDirectories()){
				TreeDirectory td = new TreeDirectory(dir);
				buildChilds(td, rep);
				
				if (itemType != -1){
					for(RepositoryItem it : rep.getItems(dir)){
						TreeItem ti = new TreeItem(it);
						td.addChild(ti);
					}
				}
				
				
				root.addChild(td);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.close();
			MessageDialog.openError(getShell(), Messages.DialogRepositoryObject_2, e.getMessage());
		}
		
		this.treeViewer.setInput(root);
		
		
	}
	
	private void buildChilds(TreeDirectory parent, IRepository rep){

		RepositoryDirectory dir = ((TreeDirectory)parent).getDirectory();
		
		try{
			for(RepositoryDirectory d : rep.getChildDirectories(dir)){
				TreeDirectory td = new TreeDirectory(d);
				parent.addChild(td);
				
				if (itemType != -1){
					try{
						for(RepositoryItem di : rep.getItems(d)){
							TreeItem ti = new TreeItem(di);
							td.addChild(ti);
						}
					}catch(Exception ex){
						ex.printStackTrace();
					}
					
				}
				
				buildChilds(td, rep);
			}
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogRepositoryObject_1, Messages.DialogRepositoryObject_3 + ex.getMessage());
		}
		
		
		
	}

	@Override
	protected void okPressed() {

		if(((IStructuredSelection)treeViewer.getSelection()).getFirstElement() instanceof TreeItem) {
			item = ((TreeItem)((IStructuredSelection)treeViewer.getSelection()).getFirstElement()).getItem();
		}
		else{
			directory = ((TreeDirectory)((IStructuredSelection)treeViewer.getSelection()).getFirstElement()).getDirectory();
		}
		
		
		super.okPressed();
	}
	
	public RepositoryDirectory getRepositoryDirectory(){
		return directory;
	}

	public RepositoryItem getRepositoryItem(){
		return item;
	}
	
	/**
	 * add a custom Filter to the Viewer
	 * @param filter
	 */
	public void addViewerFilter(ViewerFilter filter){
		this. filter = filter;;
	}
	
	/**
	 * remove a custom Filter to the Viewer
	 * @param filter
	 */
	public void removeViewerFilter(ViewerFilter filter){
		treeViewer.removeFilter(filter);
	}
}
