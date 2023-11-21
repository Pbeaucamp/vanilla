package bpm.fd.design.ui.component.report.pages;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.handlers.IHandlerService;

import bpm.fd.design.ui.component.Messages;
import bpm.fd.repository.ui.Activator;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.viewers.RepositoryTreeViewer;

public class ReportPage extends WizardPage{
	public static final String PAGE_NAME = "bpm.fd.design.ui.component.report.pages.ReportPage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = Messages.ReportPage_1;
	public static final String PAGE_DESCRIPTION = Messages.ReportPage_2;
	
	protected TreeViewer viewer;
	
	public ReportPage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
	
	protected ReportPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		
		createViewer(main);
		
		IRepository rep;
		try {
			if (bpm.fd.repository.ui.Activator.getDefault().getRepositoryConnection() == null){
//				SampleAction connectAction = new SampleAction();
//				connectAction.init(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow());
//				connectAction.run(null);
				IHandlerService handlerService = (IHandlerService) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getService(IHandlerService.class);
				try {
					handlerService.executeCommand("bpm.repository.ui.commands.connection", null);
				} catch (Exception e) {
					
					e.printStackTrace();
				} 
				
				if (bpm.fd.repository.ui.Activator.getDefault().getRepositoryConnection() == null){
					MessageDialog.openInformation(getShell(), Messages.ReportPage_3, Messages.ReportPage_4);
					return;
				}
			}
			
			rep = new Repository(Activator.getDefault().getRepositoryConnection());
			viewer.setInput(rep);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		
		setControl(main);
	}
	
	protected void createViewer(Composite main){
		
		viewer = new RepositoryTreeViewer(main, SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				getContainer().updateButtons();				
			}
			
		});
		viewer.addFilter(new ViewerFilter() {
			
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof RepositoryItem){
					return ((RepositoryItem)element).isReport();
				}
				return true;
			}
		});
	}
	
	public RepositoryItem getDirectoryItemId(){
		return ((RepositoryItem)((IStructuredSelection)viewer.getSelection()).getFirstElement());
	}

	@Override
	public boolean isPageComplete() {
		return !viewer.getSelection().isEmpty() && ((IStructuredSelection)viewer.getSelection()).getFirstElement() instanceof RepositoryItem;
	}
	
	
}
