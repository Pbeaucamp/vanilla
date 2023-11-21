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

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.composites.CompositeRepositoryItemSelecter;
import bpm.vanilla.repository.ui.viewers.RepositoryViewerFilter;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.Messages;

public class RepositoryContentWizardPage extends WizardPage {

	protected CompositeRepositoryItemSelecter compositeRepository;

	public RepositoryContentWizardPage(String pageName) {
		super(pageName);
		this.setTitle(Messages.RepositoryContentWizardPage_1);
		setDescription(Messages.RepositoryContentWizardPage_0);
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

		switch (Activator.getDefault().getServerType()) {
			case REPORTING:
				compositeRepository.addViewerFilter(new RepositoryViewerFilter(new int[] { IRepositoryApi.FWR_TYPE, IRepositoryApi.CUST_TYPE }, new int[] { IRepositoryApi.JASPER_REPORT_SUBTYPE, IRepositoryApi.BIRT_REPORT_SUBTYPE }));
				break;
			case GATEWAY:
				compositeRepository.addViewerFilter(new RepositoryViewerFilter(new int[] { IRepositoryApi.GTW_TYPE }, new int[] {}));
				break;
		}
		setControl(main);

	}

	public void fillContent(IRepository repository) {
		compositeRepository.setInput(repository);
	}

	@Override
	public boolean isPageComplete() {
		return !compositeRepository.getSelectedItems().isEmpty() && compositeRepository.getSelectedItems().get(0) instanceof RepositoryItem;
	}

	public Properties getProperties() {
		Properties p = new Properties();
		p.setProperty("directoryItemId", ((RepositoryItem) compositeRepository.getSelectedItems().get(0)).getId() + ""); //$NON-NLS-1$ //$NON-NLS-2$
		return p;
	}

	public List getDirectoryItem() {
		return compositeRepository.getSelectedItems();
	}
}
