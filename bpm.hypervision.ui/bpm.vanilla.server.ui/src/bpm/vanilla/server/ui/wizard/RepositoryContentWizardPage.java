package bpm.vanilla.server.ui.wizard;

import java.util.List;
import java.util.Properties;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import bpm.repository.api.model.IDirectoryItem;
import bpm.repository.api.model.IRepository;
import bpm.repository.api.model.IRepositoryConnection;
import bpm.vanilla.repository.ui.composites.CompositeRepositoryItemSelecter;
import bpm.vanilla.repository.ui.viewers.RepositoryViewerFilter;
import bpm.vanilla.server.ui.Activator;

public class RepositoryContentWizardPage extends WizardPage{

	protected CompositeRepositoryItemSelecter compositeRepository;
	
	
	public RepositoryContentWizardPage(String pageName) {
		super(pageName);
		setDescription("Select A Repository Item");
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		compositeRepository = new CompositeRepositoryItemSelecter(main, SWT.MULTI);
		compositeRepository.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		compositeRepository.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				getContainer().updateButtons();
				
			}
		});
		
		switch(Activator.getDefault().getServerRemote().getServerType()){
		case REPORTING:
			compositeRepository.addViewerFilter(new RepositoryViewerFilter(new int[]{IRepositoryConnection.FWR_TYPE, IRepositoryConnection.CUST_TYPE}, new int[]{IRepositoryConnection.JASPER_REPORT_SUBTYPE, IRepositoryConnection.BIRT_REPORT_SUBTYPE}));
			break;
		case GATEWAY:
			compositeRepository.addViewerFilter(new RepositoryViewerFilter(new int[]{IRepositoryConnection.GTW_TYPE}, new int[]{}));
			break;
		}
		setControl(main);
		
	}

	
	public void fillContent(IRepository repository){
		compositeRepository.setInput(repository);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return  !compositeRepository.getSelectedItems().isEmpty() && compositeRepository.getSelectedItems().get(0) instanceof IDirectoryItem;
	}
	
	
	public Properties getProperties(){
		Properties p = new Properties();
		p.setProperty("directoryItemId", ((IDirectoryItem)compositeRepository.getSelectedItems().get(0)).getId() + "");
		return p;
	}

	
	public List getDirectoryItem(){
		return compositeRepository.getSelectedItems();
	}
}
