package bpm.entrepriseservices.fmdturl.views;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import adminbirep.Activator;
import bpm.birep.admin.client.content.views.ViewTree;
import bpm.birep.admin.client.trees.TreeDirectory;
import bpm.birep.admin.client.trees.TreeItem;
import bpm.birep.admin.client.trees.TreeParent;
import bpm.entrepriseservices.fmdturl.Messages;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;



public class TreeFmdt extends ViewTree {
	public static final String ID = "bpm.entrepriseservices.fmdturl.views.TreeFmdt"; //$NON-NLS-1$
	
	public static Object selectedObject;
	
	public TreeFmdt() { }

	@Override
	public void createPartControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
	
		createTree(container);
		getSite().setSelectionProvider(viewer);

		createInput();
	}
	
	public void createInput() {
		
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		
		try{
			Repository repository = new Repository(Activator.getDefault().getRepositoryApi(), IRepositoryApi.FMDT_TYPE);
			for(RepositoryDirectory d : repository.getRootDirectories()){
				TreeDirectory tp = new TreeDirectory(d);
				root.addChild(tp);
				buildChilds(repository,tp);
				//XXX
				for(RepositoryItem it : repository.getItems(d)){
					TreeItem ti = new TreeItem(it);
					tp.addChild(ti);
				}
			
			}
		}catch(Exception e){
			e.printStackTrace();
			MessageDialog.openWarning(getSite().getShell(), Messages.TreeFmdt_2 + Activator.getDefault().getCurrentRepository().getName(), e.getMessage());
			
		}

		
		viewer.setInput(root);
		tableViewer.setInput(root);
	}

	
}
