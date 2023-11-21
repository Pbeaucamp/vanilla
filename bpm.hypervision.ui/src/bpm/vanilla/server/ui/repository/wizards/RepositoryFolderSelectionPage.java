package bpm.vanilla.server.ui.repository.wizards;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.repository.ui.composites.CompositeRepositoryItemSelecter;
import bpm.vanilla.server.ui.Messages;
import bpm.vanilla.server.ui.wizard.RepositoryContentWizardPage;

public class RepositoryFolderSelectionPage extends RepositoryContentWizardPage {

	public RepositoryFolderSelectionPage(String pageName) {
		super(pageName);
		setDescription(Messages.RepositoryFolderSelectionPage_0);
	}
	
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		compositeRepository = new CompositeRepositoryItemSelecter(main, SWT.NONE);
		compositeRepository.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		compositeRepository.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				getContainer().updateButtons();
				
			}
		});
		
		
		compositeRepository.addViewerFilter(new ViewerFilter() {
			
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return element instanceof RepositoryDirectory;
			}
		});
			
		setControl(main);
		
	}
	
	@Override
	public boolean isPageComplete() {
		return  !compositeRepository.getSelectedItems().isEmpty();
	}
	
	public RepositoryDirectory getTargetFolder(){
		if (compositeRepository.getSelectedItems().isEmpty()){
			return null;
		}
		return (RepositoryDirectory)compositeRepository.getSelectedItems().get(0);
	}
}
