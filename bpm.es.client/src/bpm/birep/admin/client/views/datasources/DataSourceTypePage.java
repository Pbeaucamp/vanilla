package bpm.birep.admin.client.views.datasources;

import org.eclipse.datatools.connectivity.oda.util.manifest.ExtensionManifest;
import org.eclipse.datatools.connectivity.oda.util.manifest.ManifestExplorer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import bpm.vanilla.platform.core.repository.DataSource;

public class DataSourceTypePage extends WizardPage {
	
	private ListViewer viewer;
	private DataSource odaInput;

	public DataSourceTypePage(String pageName, DataSource odaInput) {
		super(pageName);
		this.odaInput = odaInput;
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		viewer = new ListViewer(main, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		viewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((ExtensionManifest) element).getDataSourceDisplayName();
			}

		});

		viewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				return (Object[]) inputElement;
			}

			public void dispose() { }

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

		});

		viewer.setInput(ManifestExplorer.getInstance().getExtensionManifests());

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {

				try {
					getContainer().updateButtons();
				} catch (Exception ex) {

				}

			}

		});

		if (odaInput.getOdaExtensionDataSourceId() != null) {
			for (ExtensionManifest m : (ExtensionManifest[]) viewer.getInput()) {
				if (m.getExtensionID().equals(odaInput.getOdaExtensionDataSourceId())) {
					viewer.setSelection(new StructuredSelection(m));
					break;
				}
			}
		}
		setControl(main);
	}

	@Override
	public boolean isPageComplete() {
		return !viewer.getSelection().isEmpty();
	}

	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}

	public ExtensionManifest getExtensionManifest() {
		return (ExtensionManifest) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
	}

	@Override
	public IWizardPage getNextPage() {
		if (super.getNextPage() == null || super.getNextPage() == this) {
			((OdaDataSourceWizard) getWizard()).updatePages();
		}
		return super.getNextPage();
	}

}
