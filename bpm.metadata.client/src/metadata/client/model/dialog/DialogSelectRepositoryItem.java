package metadata.client.model.dialog;

import java.util.List;

import metadata.client.i18n.Messages;
import metadata.client.trees.TreeDirectory;
import metadata.client.trees.TreeItem;
import metadata.client.trees.TreeParent;
import metadata.client.viewer.TreeContentProvider;
import metadata.client.viewer.TreeLabelProvider;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class DialogSelectRepositoryItem extends Dialog {

	private TreeViewer viewer;
	private RepositoryItem item;
	private int type, subtype;
	public DialogSelectRepositoryItem(Shell parentShell, int type, int subtype) {
		super(parentShell);
		this.type = type;
		this.subtype = subtype;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer = new TreeViewer(container, SWT.BORDER);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setContentProvider(new TreeContentProvider());
		try{
			createModel();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return container;
	}
	
	
	public void createModel() throws Exception{
		
		
		IRepository r = null;
		
		if (subtype > -1){
			r = new Repository(Activator.getDefault().getRepositoryConnection(), type, subtype);
		}
		else{
			r = new Repository(Activator.getDefault().getRepositoryConnection(), type);
		}
		
		List<RepositoryDirectory> list = r.getRootDirectories();
		TreeParent root = new TreeParent("root"); //$NON-NLS-1$
		
		for(RepositoryDirectory d : list){
			TreeDirectory tp = new TreeDirectory(d);
			root.addChild(tp);
			buildChilds(r, tp);
			
			for(RepositoryItem di : r.getItems(d)){
				TreeItem ti = new TreeItem(di);
					tp.addChild(ti);
			}
		}
		
		viewer.setInput(root);
	}
	
	private void buildChilds(IRepository r, TreeDirectory parent){

		RepositoryDirectory dir = ((TreeDirectory)parent).getDirectory();
		
		try{
			for(RepositoryDirectory d : r.getChildDirectories(dir)){
				TreeDirectory td = new TreeDirectory(d);
				parent.addChild(td);
				for(RepositoryItem di : r.getItems(d)){
					TreeItem ti = new TreeItem(di);
					td.addChild(ti);
				}
				buildChilds(r, td);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
	}

	@Override
	protected void okPressed() {
		IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
		
		if (!ss.isEmpty()){
			if (ss.getFirstElement() instanceof TreeItem){
				RepositoryItem it = ((TreeItem)ss.getFirstElement()).getItem();
				
				item = it;
				
			}
		}
		super.okPressed();
	}

	public RepositoryItem getItem(){
		return item;
	}

	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogSelectRepositoryItem_1);
		getShell().setSize(600, 400);
		super.initializeBounds();
	}
	
	
}
