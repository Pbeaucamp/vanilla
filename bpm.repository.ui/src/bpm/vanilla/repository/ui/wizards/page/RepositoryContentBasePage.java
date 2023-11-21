package bpm.vanilla.repository.ui.wizards.page;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.Activator;
import bpm.vanilla.repository.ui.Messages;
import bpm.vanilla.repository.ui.composites.CompositeRepositoryItemSelecter;

public class RepositoryContentBasePage extends WizardPage{
	
//	protected int objectType;
//	protected int objectSubtype;
//	
//	private boolean showDirectoryItems;
	
	protected CompositeRepositoryItemSelecter compositeRepository;
	private Button closeOnImport;
	
	public RepositoryContentBasePage(String pageName/*, int objectType, int objectSubtype, boolean showItems*/) {
		super(pageName);
//		this.objectSubtype = objectSubtype;
//		this.objectType = objectType;
//		this.showDirectoryItems = showItems;
//		if (showDirectoryItems){
//			setDescription("Select A Repository Item");
//		}
//		else{
//			setDescription("Select A Folder");
//		}
		
	}

	protected boolean shouldCloseWizardOnImport(){
		if (closeOnImport != null && ! closeOnImport.isDisposed()){
			return closeOnImport.getSelection();
		}
		return false;
	}
	
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		ToolBar bar = new ToolBar(main, SWT.NONE);
		bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		
		compositeRepository = new CompositeRepositoryItemSelecter(main, SWT.BORDER);
		compositeRepository.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		compositeRepository.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				getContainer().updateButtons();
				
			}
		});
		
		
		closeOnImport = new Button(main, SWT.CHECK);
		closeOnImport.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		closeOnImport.setText(Messages.RepositoryContentBasePage_2);
		closeOnImport.setSelection(true);
		
		
//		if (showDirectoryItems){
//			compositeRepository.addViewerFilter(new RepositoryViewerFilter(new int[]{objectType}, new int[]{objectSubtype}));
//		}
//		else{
//			compositeRepository.addViewerFilter(new ViewerFilter() {
//				
//				@Override
//				public boolean select(Viewer viewer, Object parentElement, Object element) {
//					return element instanceof IDirectory;
//				}
//			});
//		}
		setControl(main);
		fillToolbar(bar);
	}
	
	protected void fillToolbar(ToolBar bar){
		
	}

	
	public void fillContent(IRepositoryContext ctx, IRepositoryApi sock){
		try{
			if (Activator.getDefault().getDesignerActivator() != null){
				Activator.getDefault().getDesignerActivator().setRepositoryContext(ctx);
			}
			
			compositeRepository.setInput(new Repository(sock, Activator.getDefault().getDesignerActivator().getRepositoryItemType()));
		}catch(Exception ex){
			ex.printStackTrace();
			compositeRepository.setInput(new Repository(sock));
			MessageDialog.openError(getShell(), Messages.RepositoryContentBasePage_0, Messages.RepositoryContentBasePage_1 + ex.getMessage());
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return  !compositeRepository.getSelectedItems().isEmpty() && compositeRepository.getSelectedItems().get(0) instanceof RepositoryItem;
	}
	
	public List<Object> getSelectedObjects(){
		return compositeRepository.getSelectedItems();
	}
}
