package bpm.fd.repository.ui.wizard.pages;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.FdProjectRepositoryDescriptor;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.editors.FdEditor;
import bpm.fd.design.ui.editors.FdProjectEditorInput;
import bpm.fd.repository.ui.Messages;
import bpm.fd.repository.ui.RepositoryLabelProvider;
import bpm.fd.repository.ui.RepositoryTreeContentProvider;
import bpm.fd.repository.ui.icons.Icons;
import bpm.fd.repository.ui.wizard.actions.ActionCreateDirectory;
import bpm.fd.repository.ui.wizard.actions.ActionCreateFdProject;
import bpm.fd.repository.ui.wizard.actions.ActionLoadFdProject;
import bpm.fd.repository.ui.wizard.actions.ActionUpdateProject;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class PageRepository extends WizardPage{

	protected TreeViewer viewer;
	protected IRepositoryApi sock;
	protected IVanillaAPI vanillaApi;
	
	protected ToolItem newFd, newDirectory, updateFd, deleteFd, loadFd;
	private boolean isExport;
	
	public PageRepository(String pageName, boolean isExport, IVanillaAPI vanillaApi) {
		super(pageName);
		this.isExport = isExport;
		this.vanillaApi = vanillaApi;
	}

	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new FillLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl( mainComposite );
//		setPageComplete(false);
		
	}
	
	
	private void createPageContent(Composite parent){
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		
		createToolBar(main);
		viewer = new TreeViewer(main, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.VIRTUAL);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setLabelProvider(new RepositoryLabelProvider());
		viewer.setContentProvider(new RepositoryTreeContentProvider(isExport));
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				Object o = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
				if (o instanceof RepositoryDirectory){
					newDirectory.setEnabled(isExport);
					newFd.setEnabled(isExport);
					updateFd.setEnabled(false);
					loadFd.setEnabled(false);
				}
				else if (o instanceof RepositoryItem){
					newDirectory.setEnabled(false);
					newFd.setEnabled(false);
					loadFd.setEnabled(true);
					
					if (Activator.getDefault().getProject() == null  || !(Activator.getDefault().getProject().getProjectDescriptor() instanceof FdProjectRepositoryDescriptor)){
						updateFd.setEnabled(false);
					}
					else{
						FdProjectRepositoryDescriptor desc = (FdProjectRepositoryDescriptor )Activator.getDefault().getProject().getProjectDescriptor(); 
						RepositoryItem it = (RepositoryItem)o;
						if (it.getType() == IRepositoryApi.FD_TYPE){
							if (it.getId() == desc.getModelDirectoryItemId().intValue()){
								updateFd.setEnabled(isExport);
								return;
							}
						}
					}
					updateFd.setEnabled(false);
					
					
					
					
				}
				
			}
			
		});
		viewer.addFilter(new ViewerFilter(){

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof RepositoryItem){
					return ((RepositoryItem)element).getType() == IRepositoryApi.FD_TYPE;
				}
				
				return true;
			}
			
		});
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
	 */
	@Override
	public boolean canFlipToNextPage() {
		return false;
	}
	
	public void loadRepository(IRepositoryApi sock) throws Exception{
		this.sock = sock;
		Repository r = new Repository(sock);
		viewer.setInput(r);
	}

	

	protected void createToolBar(Composite parent){
		ToolBar toolbar = new ToolBar(parent, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		loadFd = new ToolItem(toolbar, SWT.PUSH);
		loadFd.setImage(bpm.fd.repository.ui.Activator.getDefault().getImageRegistry().get(Icons.load));
		loadFd.setText(Messages.PageRepository_0);
		loadFd.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					RepositoryItem item = (RepositoryItem)((IStructuredSelection)viewer.getSelection()).getFirstElement() ;
					
					FdProject project = new ActionLoadFdProject(sock, item).perform();
					
					
					Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new FdProjectEditorInput(project.getFdModel()), FdEditor.ID);
					if (project instanceof MultiPageFdProject){
						for(FdModel m : ((MultiPageFdProject)project).getPagesModels()){
							Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new FdProjectEditorInput(m), FdEditor.ID);
						}
					}
					getWizard().performFinish();
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getShell(), Messages.PageRepository_1, e1.getMessage());
				}
			}
			
		});
		
		newFd = new ToolItem(toolbar, SWT.PUSH);
		newFd.setImage(bpm.fd.design.ui.Activator.getDefault().getImageRegistry().get(bpm.fd.design.ui.icons.Icons.fd_16));
		newFd.setText(Messages.PageRepository_2);
		newFd.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					RepositoryDirectory target = (RepositoryDirectory)((IStructuredSelection)viewer.getSelection()).getFirstElement() ;
					RepositoryItem it = new ActionCreateFdProject(sock, target, vanillaApi.getVanillaSecurityManager()).perform(false);
					((Repository)viewer.getInput()).add(it);
					viewer.refresh();
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getShell(), Messages.PageRepository_3, e1.getMessage());
				}
			}
			
		});
		
		newDirectory = new ToolItem(toolbar, SWT.PUSH);
		newDirectory.setText(Messages.PageRepository_4);
		newDirectory.setImage(bpm.fd.repository.ui.Activator.getDefault().getImageRegistry().get(Icons.folder));
		newDirectory.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				new ActionCreateDirectory(viewer, sock, vanillaApi.getVanillaSecurityManager()).run();
				viewer.refresh();
			}
			
		});
		
		updateFd = new ToolItem(toolbar, SWT.PUSH);
		updateFd.setText(Messages.PageRepository_5);
		updateFd.setImage(bpm.fd.repository.ui.Activator.getDefault().getImageRegistry().get(Icons.update));
		updateFd.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				new ActionUpdateProject(sock, Activator.getDefault().getProject(), (Repository)viewer.getInput(), vanillaApi).perform();

			}
		});
		
		deleteFd = new ToolItem(toolbar, SWT.PUSH);
		deleteFd.setText(Messages.PageRepository_6);
		deleteFd.setImage(bpm.fd.repository.ui.Activator.getDefault().getImageRegistry().get(Icons.delete));
		deleteFd.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object o = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
				try{
					if (o instanceof RepositoryDirectory){
						sock.getRepositoryService().delete((RepositoryDirectory)o);
						((Repository)viewer.getInput()).remove((RepositoryDirectory)o);
						
					}
					else if (o instanceof RepositoryItem){
						sock.getRepositoryService().delete((RepositoryItem)o);
						((Repository)viewer.getInput()).remove((RepositoryItem)o);
					}
					
					viewer.refresh();
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getShell(), Messages.PageRepository_7, Messages.PageRepository_8 + ex.getMessage());
				}
				
			
			}
			
		});
	}
}
