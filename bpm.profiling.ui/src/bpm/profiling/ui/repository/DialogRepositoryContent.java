package bpm.profiling.ui.repository;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.profiling.ui.trees.TreeContentProvider;
import bpm.profiling.ui.trees.TreeLabelProvider;
import bpm.profiling.ui.trees.TreeParent;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class DialogRepositoryContent extends Dialog {

	private TreeViewer viewer;
	private IRepositoryApi sock;
	private Button ok ;
	private RepositoryItem fmdtItem;
	
	public DialogRepositoryContent(Shell parentShell, IRepositoryApi sock) {
		super(parentShell);
		this.sock = sock;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		viewer = new TreeViewer(comp, SWT.BORDER | SWT.V_SCROLL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setContentProvider(new TreeContentProvider());
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				
				if (ss.isEmpty() || !(ss.getFirstElement() instanceof TreeItem)){
					ok.setEnabled(false);
				}
				else{
					ok.setEnabled(true);
				}
				
			}
			
		});
		try{
			buildModel();
		}catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), "Error", e.getMessage());
		}
		
		
		return comp;
	}
	
	private void buildModel() throws Exception{
		IRepository rep = new Repository(sock,IRepositoryApi.FMDT_TYPE);
		
		TreeParent root = new TreeParent("");
		
		for(RepositoryDirectory d : rep.getRootDirectories()){
			TreeDirectory tp = new TreeDirectory(d);
			root.addChild(tp);
			buildChilds(tp,rep);
			//XXX
			for(RepositoryItem di : rep.getItems(d)){
				TreeItem ti = new TreeItem(di);
					tp.addChild(ti);
			}
		}
		
		viewer.setInput(root);
		
	}
	private void buildChilds(TreeDirectory parent, IRepository rep){

		RepositoryDirectory dir = parent.getDirectory();
		
		try{
			for(RepositoryDirectory d : rep.getChildDirectories(dir)){
				TreeDirectory td = new TreeDirectory(d);
				parent.addChild(td);
				for(RepositoryItem di : rep.getItems(d)){
					TreeItem ti = new TreeItem(di);
					td.addChild(ti);
				}
				buildChilds(td, rep);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		ok = createButton(parent, IDialogConstants.OK_ID, "Select", true);
		ok.setEnabled(false);
		
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
		getShell().setText("Select a FreeMetaData Model");
		setShellStyle(SWT.CLOSE | SWT.RESIZE);

	}

	@Override
	protected void okPressed() {
		fmdtItem = ((TreeItem)((IStructuredSelection)viewer.getSelection()).getFirstElement()).getItem();
		super.okPressed();
	}

	public RepositoryItem getItem(){
		return fmdtItem;
	}
}
