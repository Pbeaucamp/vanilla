package bpm.birep.admin.client.inport.vanillaplace.wizard;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.birep.admin.client.dialog.DialogDirectory;
import bpm.birep.admin.client.trees.TreeContentProvider;
import bpm.birep.admin.client.trees.TreeDirectory;
import bpm.birep.admin.client.trees.TreeItem;
import bpm.birep.admin.client.trees.TreeLabelProvider;
import bpm.birep.admin.client.trees.TreeParent;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class RepositoryPlacementPage extends WizardPage {
	public static final Color COLOR_LOCKED = new Color(Display.getDefault(), 250, 25, 120);
	public static final Font FONT_ACTIVE_CONNECTION = new Font(Display.getCurrent(), "Arial", 8, SWT.BOLD | SWT.ITALIC); //$NON-NLS-1$
		
	private TreeViewer viewer;
	private Label lblSelectedFolder;
	
	private RepositoryDirectory selectedDirectory;
	
	public RepositoryPlacementPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		createToolbar(mainComposite);
		createPageContent(mainComposite);		
		
		setControl(mainComposite);
		setPageComplete(true);
		
		createInput();
	}

	
	private void createPageContent(Composite parent){
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		viewer = new TreeViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.VIRTUAL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new DecoratingLabelProvider(new TreeLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()){

			@Override
			public Color getForeground(Object element) {
				if (element instanceof TreeItem){
					if (((TreeItem)element).getItem().isLocked()){
						return COLOR_LOCKED ;
					}
				}
				return super.getBackground(element);
			}

			@Override
			public Font getFont(Object element) {
				if (element instanceof TreeItem){
					if (((TreeItem)element).getItem().isLocked()){
						return  FONT_ACTIVE_CONNECTION;
					}
				}
				return super.getFont(element);
			}

			
			@Override
			public String getText(Object element) {
				if (element instanceof TreeItem){
					if (((TreeItem)element).getItem().isLocked()){
						return super.getText(element) + Messages.ViewTree_45 ;
					}
				}
				return super.getText(element);
			}
		});
		viewer.addSelectionChangedListener(folderSelectionListener);
		
		lblSelectedFolder = new Label(container, SWT.NONE);
		lblSelectedFolder.setLayoutData(new GridData(GridData.FILL, GridData.END, true, false));
	}
	
	private void createInput() {
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		try{
			Repository repository = new Repository(Activator.getDefault().getRepositoryApi());
			
			StringBuffer warnings = new StringBuffer();
			for(RepositoryDirectory d : repository.getRootDirectories()){
				TreeDirectory tp = new TreeDirectory(d);
				root.addChild(tp);
				warnings.append(buildChilds(repository, tp));
				for(RepositoryItem it : repository.getItems(d)){
					TreeItem ti = new TreeItem(it);
					tp.addChild(ti);
				}
				
			}
			if (warnings.length() > 0){
				MessageDialog.openWarning(getShell(),Messages.ViewTree_51 , warnings.toString());
			}
		}catch(Exception e){
			e.printStackTrace();
			MessageDialog.openWarning(getShell(), Messages.ViewTree_52 + Activator.getDefault().getCurrentRepository().getUrl(), e.getMessage());
			
		}

		viewer.setInput(root);
	}
	
	protected String buildChilds(Repository rep, TreeDirectory parent){
		RepositoryDirectory dir = ((TreeDirectory)parent).getDirectory();
		StringBuffer errors = new StringBuffer();
		
		try{
			for(RepositoryDirectory d : rep.getChildDirectories(dir)){
				TreeDirectory td = new TreeDirectory(d);
				parent.addChild(td);
			
				try{
					for(RepositoryItem it : rep.getItems(d)){
						TreeItem ti = new TreeItem(it);
						td.addChild(ti);
					}
				}catch(Exception ex){
					ex.printStackTrace();
					errors.append(" - Error when getting " + d.getName() + " items : " + ex.getMessage() +"\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				
				errors.append(buildChilds(rep, td));	
			
			}
		}catch(Exception ex1){
			ex1.printStackTrace();
			errors.append(" - Error when getting " + dir.getName() + " childs : " + ex1.getMessage() +"\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		}
		
		return errors.toString();
	}
	
	private void createToolbar(Composite parent){
		ToolBar toolbar = new ToolBar(parent, SWT.NONE);
		toolbar.setLayout(new GridLayout());
		
		ToolItem addDir = new ToolItem(toolbar, SWT.PUSH);
		addDir.setToolTipText(Messages.ViewTree_59);
		addDir.setImage(Activator.getDefault().getImageRegistry().get("folder")); //$NON-NLS-1$
		addDir.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				//find the type
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				
				TreeParent tp = null;
				
				if (ss.getFirstElement() instanceof TreeDirectory){
					tp = (TreeParent)ss.getFirstElement();
				}
				
				int type = 0;
				DialogDirectory dial = null;
				
				try{
					dial = new DialogDirectory(getShell(), Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups(), type);
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getShell(), Messages.ViewTree_61, Messages.ViewTree_62 + ex.getMessage());
					
					return;
				}
				
				RepositoryDirectory parent = null;
				if (ss.getFirstElement() instanceof TreeDirectory){
					parent = ((TreeDirectory)ss.getFirstElement()).getDirectory();
				}
				
				if (dial.open() == DialogDirectory.OK){
					RepositoryDirectory d = dial.getDirectory();
					try {
						d= Activator.getDefault().getRepositoryApi().getRepositoryService().addDirectory(
								d.getName(), d.getComment(), parent);

						createInput();
					} catch (Exception e1) {
						MessageDialog.openError(getShell(), Messages.ViewTree_63, e1.getMessage());
						e1.printStackTrace();
					}
					
					try{
						Activator.getDefault().getRepositoryApi().getAdminService().addGroupForDirectory(Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroupByName(dial.getGroupName()).getId(), d.getId());
					}catch(Exception ex){
						MessageDialog.openError(getShell(), Messages.ViewTree_64, ex.getMessage());
						ex.printStackTrace();
					}
				}	
			}
		});
	}
	
	private ISelectionChangedListener folderSelectionListener = new ISelectionChangedListener(){

		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection ss = (IStructuredSelection)event.getSelection();
			if (ss.isEmpty() || !(ss.getFirstElement() instanceof TreeDirectory)){
				lblSelectedFolder.setText(""); //$NON-NLS-1$
				return;
			}
			
			TreeDirectory treeDir = (TreeDirectory)ss.getFirstElement();
			RepositoryDirectory directory = treeDir.getDirectory();
			
			selectedDirectory = directory;
			
			lblSelectedFolder.setText(Messages.RepositoryPlacementPage_1 + directory.getName());
			
			getContainer().updateButtons();
		}
	};
	
	public String getSelectedDirectoryPath(){
		IRepositoryApi sock = Activator.getDefault().getRepositoryApi();
		try {
			Repository rep = new Repository(sock);
			if(selectedDirectory == null){
				return ""; //$NON-NLS-1$
			}
			return lookInto(rep, selectedDirectory.getId()) + "/"; //$NON-NLS-1$
		} catch (Exception e) {
			e.printStackTrace();
			return ""; //$NON-NLS-1$
		}
	}
	
	public RepositoryDirectory getSelectedDirectory(){
		return selectedDirectory;
	}

	@Override
	public boolean isPageComplete() {
		return selectedDirectory != null;
	}
	
	private String lookInto(Repository rep, int dirId) throws Exception{
		RepositoryDirectory cur = rep.getDirectory(dirId);
		if (cur == null){
			return ""; //$NON-NLS-1$
		}
		if (cur.getParentId() > 0){
			return lookInto(rep, cur.getParentId()) + "/" + cur.getName(); //$NON-NLS-1$
		}
		else{
			return cur.getName();
		}
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return false;
	}
}
