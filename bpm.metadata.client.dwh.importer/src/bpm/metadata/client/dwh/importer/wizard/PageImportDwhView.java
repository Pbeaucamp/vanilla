package bpm.metadata.client.dwh.importer.wizard;

import metadataclient.Activator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.composites.CompositeRepositoryItemSelecter;
import bpm.vanilla.repository.ui.viewers.RepositoryViewerFilter;

public class PageImportDwhView extends WizardPage{

	private CompositeRepositoryItemSelecter itemSelecter;
	
	protected PageImportDwhView(String pageName) {
		super(pageName);
		
	}


	
	
	@Override
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		
		createContent(main);
		
		setControl(main);
		
	}
	
	private void createContent(Composite parent){
		itemSelecter = new CompositeRepositoryItemSelecter(parent, SWT.NONE);
		itemSelecter.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		//itemSelecter.addViewerFilter(new RepositoryViewerFilter(new int[]{IRepositoryApi.DWH_VIEW_TYPE}, new int[]{}));
		
		itemSelecter.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				getContainer().updateButtons();
				
			}
		});
	
		
		try{
			itemSelecter.setInput(new Repository(Activator.getDefault().getRepositoryConnection(), IRepositoryApi.DWH_VIEW_TYPE));
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getShell(), "Problem", "Error when browsing repository : \n" + ex.getMessage());
		}
	}

	public RepositoryItem getSelectedItem(){
		try{
			return ((RepositoryItem)itemSelecter.getSelectedItems().get(0));
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
		
	}
	
	@Override
	public boolean isPageComplete() {
		if (itemSelecter.getSelectedItems().isEmpty()){
			return false;
		}
		else if (itemSelecter.getSelectedItems().get(0) instanceof RepositoryItem){
			return true;
		}
		else{
			return false;
		}
	}
}
