package org.fasd.cubewizard.oda;

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
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.fasd.datasource.DatasourceOda;

public class OdaDatasourceTypePage extends WizardPage {

	private ListViewer viewer;
	private DatasourceOda datasource;

	public OdaDatasourceTypePage(String pageName) {
		super(pageName);
	}

	public OdaDatasourceTypePage(String pageName, DatasourceOda datasource) {
		super(pageName);
		this.datasource = datasource;
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		viewer = new ListViewer(main, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		viewer.setLabelProvider(new LabelProvider() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				return ((ExtensionManifest) element).getDataSourceDisplayName();
			}

		});

		viewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				return (Object[]) inputElement;
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});

		viewer.setInput(ManifestExplorer.getInstance().getExtensionManifests());

		if (datasource != null && datasource.getId() != null && !datasource.getId().equalsIgnoreCase("")) { //$NON-NLS-1$
			for (ExtensionManifest ext : (ExtensionManifest[]) viewer.getInput()) {
				if (ext.getDataSourceElementID().equals(datasource.getOdaDatasourceExtensionId())) {
					viewer.setSelection(new StructuredSelection(ext));
					break;
				}
			}
		}

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				getContainer().updateButtons();

			}

		});
		setControl(main);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {

		return !viewer.getSelection().isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
	 */
	@Override
	public boolean canFlipToNextPage() {

		return isPageComplete();
	}

	public ExtensionManifest getExtensionManifest() {
		return (ExtensionManifest) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
	}
}
