package org.fasd.cubewizard.oda;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.DataSourceDesign;
import org.eclipse.datatools.connectivity.oda.design.DesignFactory;
import org.eclipse.datatools.connectivity.oda.design.DesignSessionRequest;
import org.eclipse.datatools.connectivity.oda.design.Properties;
import org.eclipse.datatools.connectivity.oda.design.ui.designsession.DataSetDesignSession;
import org.eclipse.datatools.connectivity.oda.util.manifest.DataSetType;
import org.eclipse.datatools.connectivity.oda.util.manifest.ExtensionManifest;
import org.eclipse.datatools.connectivity.oda.util.manifest.ManifestExplorer;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fasd.datasource.DataObjectOda;
import org.fasd.datasource.DataSource;
import org.fasd.datasource.DatasourceOda;
import org.fasd.i18N.LanguageText;
import org.freeolap.FreemetricsPlugin;

public class OdaDatasourceSelectionPage extends WizardPage {

	private ComboViewer dataSourcesViewer;
	private ComboViewer dataSetType;
	private DataSetDesignSession designSession;
	private Text dataSetName;
	private DataObjectOda dataSet;

	private IWizardPage dataSetPage;
	
	private boolean datasourceHasChange = false;

	public OdaDatasourceSelectionPage(String pageName, DataObjectOda dataSet) {
		super(pageName);
		this.dataSet = dataSet;
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(LanguageText.OdaDatasourceSelectionPage_0);

		dataSourcesViewer = new ComboViewer(main, SWT.READ_ONLY);
		dataSourcesViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dataSourcesViewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((DatasourceOda) element).getName();
			}

		});
		dataSourcesViewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				return ((List<DatasourceOda>) inputElement).toArray();
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});

		Button create = new Button(main, SWT.PUSH);
		create.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		create.setText("..."); //$NON-NLS-1$
		create.setToolTipText(LanguageText.OdaDatasourceSelectionPage_2);
		create.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				try {
					OdaDatasourceWizard wiz = new OdaDatasourceWizard(new DatasourceOda());

					WizardDialog dial = new WizardDialog(getShell(), wiz);
					if (dial.open() == Dialog.OK) {
						fillDatas();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}

		});

		Label l2 = new Label(main, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(LanguageText.OdaDatasourceSelectionPage_3);

		dataSetName = new Text(main, SWT.BORDER);
		dataSetName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		l2 = new Label(main, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(LanguageText.OdaDatasourceSelectionPage_4);

		dataSetType = new ComboViewer(main, SWT.READ_ONLY);
		dataSetType.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		dataSetType.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				return (Object[]) inputElement;
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});
		dataSetType.setLabelProvider(new LabelProvider() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				return ((DataSetType) element).getDisplayName();
			}

		});

		fillDatas();

		dataSetType.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				getContainer().updateButtons();

			}

		});

		dataSourcesViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				getContainer().updateButtons();
				String s = ((DatasourceOda) ((IStructuredSelection) dataSourcesViewer.getSelection()).getFirstElement()).getOdaDatasourceExtensionId();

				try {
					ExtensionManifest ext = ManifestExplorer.getInstance().getExtensionManifest(s);
					Object[] o = ext.getDataSetTypes();
					dataSetType.setInput(o);
					if (o.length >= 1) {
						dataSetType.setSelection(new StructuredSelection(o[0]));
					}

					datasourceHasChange = true;
				} catch (OdaException e) {
					e.printStackTrace();
				}

			}

		});
		dataSetName.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();

			}

		});

		setControl(main);

	}

	public void fillDatas() {
		List<DatasourceOda> dss = new ArrayList<DatasourceOda>();
		for (DataSource ds : FreemetricsPlugin.getDefault().getFAModel().getDataSources()) {
			dss.add((DatasourceOda) ds);
		}
		dataSourcesViewer.setInput(dss);
		if (dataSet != null && dataSet.getDataSource() != null) {
			DatasourceOda ds = (DatasourceOda) dataSet.getDataSource();

			dataSourcesViewer.setSelection(new StructuredSelection(ds));
			dataSourcesViewer.getControl().setEnabled(false);
			dataSetName.setText(dataSet.getName());
			dataSetName.setEnabled(false);

			try {
				ExtensionManifest ext = ManifestExplorer.getInstance().getExtensionManifest(ds.getOdaDatasourceExtensionId());
				Object[] o = ext.getDataSetTypes();

				dataSetType.setInput(o);
				if (o.length >= 1) {
					Object dst = ext.getDataSetType(dataSet.getOdaDatasetExtensionId());
					dataSetType.setSelection(new StructuredSelection(dst));
				}

			} catch (OdaException e) {
				e.printStackTrace();
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return !dataSetType.getSelection().isEmpty() && !dataSourcesViewer.getSelection().isEmpty() && !dataSetName.getText().equals(""); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
	 */
	@Override
	public IWizardPage getNextPage() {

		if (dataSetPage != null && !datasourceHasChange) {
			return dataSetPage;
		}

		DatasourceOda ds = null;
		if (dataSet != null && !datasourceHasChange) {
			ds = (DatasourceOda) ((IStructuredSelection) dataSourcesViewer.getSelection()).getFirstElement();
		} else {
			ds = (DatasourceOda) ((IStructuredSelection) dataSourcesViewer.getSelection()).getFirstElement();
		}
		
		datasourceHasChange = false;

		try {

			DataSourceDesign design = DesignFactory.eINSTANCE.createDataSourceDesign();
			design.setName(ds.getId());
			design.setOdaExtensionDataSourceId(ds.getOdaDatasourceExtensionId());
			design.setOdaExtensionId(ds.getOdaExtensionId());

			Properties pPr = DesignFactory.eINSTANCE.createProperties();
			java.util.Properties _pr = ds.getPrivateProperties();

			for (Object o : _pr.keySet()) {
				pPr.setProperty((String) o, _pr.getProperty((String) o));
			}

			Properties pPu = DesignFactory.eINSTANCE.createProperties();
			java.util.Properties _pu = ds.getPublicProperties();

			for (Object o : _pu.keySet()) {
				pPu.setProperty((String) o, _pu.getProperty((String) o));
			}

			if (_pu.size() > 0) {
				design.setPublicProperties(pPu);
			}

			if (_pr.size() > 0) {
				design.setPrivateProperties(pPr);
			}

			if (dataSet != null && dataSet.getDataSource() != null) {
				String id = null;

				if (dataSetType == null) {
					id = dataSet.getOdaDatasetExtensionId();
				} else {
					id = ((DataSetType) (((IStructuredSelection) this.dataSetType.getSelection()).getFirstElement())).getID();
				}

				DataSetDesign dataSetDesign = DesignFactory.eINSTANCE.createDataSetDesign();
				dataSetDesign.setDataSourceDesign(design);
				dataSetDesign.setOdaExtensionDataSetId(id);
				dataSetDesign.setName(dataSet.getName());
				dataSetDesign.setQueryText(dataSet.getQueryText());

				DesignSessionRequest dsd = DesignFactory.eINSTANCE.createDesignSessionRequest(dataSetDesign);
				designSession = DataSetDesignSession.startEditDesign(dsd);
			} else {
				String id = ((DataSetType) (((IStructuredSelection) this.dataSetType.getSelection()).getFirstElement())).getID();
				designSession = DataSetDesignSession.startNewDesign(dataSetName.getText(), id, design);
			}

			dataSetPage = designSession.getWizardStartingPage();
			if (dataSetPage instanceof WizardPage) {
				((WizardPage) dataSetPage).getWizard().setContainer(getContainer());
			}

			return dataSetPage;
		} catch (OdaException e) {
			e.printStackTrace();
		}
		return dataSetPage.getNextPage();

	}

	public IWizardPage getDataSetPage() {
		return dataSetPage;
	}

	public String getDataSetName() {
		return dataSetName.getText();
	}

	public DatasourceOda getDataSourceSelected() {
		return (DatasourceOda) ((IStructuredSelection) dataSourcesViewer.getSelection()).getFirstElement();
	}

	public void setDataSet(DataObjectOda dataSet) {
		this.dataSet = dataSet;

	}
}
