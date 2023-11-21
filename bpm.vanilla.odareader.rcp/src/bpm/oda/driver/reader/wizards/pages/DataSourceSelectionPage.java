package bpm.oda.driver.reader.wizards.pages;

import java.util.List;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.DataSourceDesign;
import org.eclipse.datatools.connectivity.oda.design.DesignFactory;
import org.eclipse.datatools.connectivity.oda.design.DesignSessionRequest;
import org.eclipse.datatools.connectivity.oda.design.Properties;
import org.eclipse.datatools.connectivity.oda.design.ui.designsession.DataSetDesignSession;
import org.eclipse.datatools.connectivity.oda.design.ui.manifest.UIExtensionManifest;
import org.eclipse.datatools.connectivity.oda.design.ui.manifest.UIManifestExplorer;
import org.eclipse.datatools.connectivity.oda.util.manifest.DataSetType;
import org.eclipse.datatools.connectivity.oda.util.manifest.ExtensionManifest;
import org.eclipse.datatools.connectivity.oda.util.manifest.ManifestExplorer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.oda.driver.reader.Activator;
import bpm.oda.driver.reader.model.dataset.DataSet;
import bpm.oda.driver.reader.model.datasource.DataSource;

public class DataSourceSelectionPage extends WizardPage{

	private ComboViewer dataSourcesViewer;
	private ComboViewer dataSetType;
	private DataSetDesignSession designSession;
	private Text dataSetName;
	private DataSet dataSet;
	
	private IWizardPage dataSetPage;
	
	public DataSourceSelectionPage(String pageName, DataSet dataSet) {
		super(pageName);
		this.dataSet = dataSet;
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Select DataSource");
		
		dataSourcesViewer = new ComboViewer(main, SWT.READ_ONLY);
		dataSourcesViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dataSourcesViewer.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((DataSource)element).getName();
			}
			
		});
		dataSourcesViewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<DataSource> l = Activator.getInstance().getListDataSource();
				return l.toArray(new DataSource[l.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
		});
		
		
		Label l2 = new Label(main, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText("Enter DataSet name");
		
		dataSetName = new Text(main, SWT.BORDER);
		dataSetName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		
		
		l2 = new Label(main, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText("Select DataSet Type");
		
		dataSetType = new ComboViewer(main, SWT.READ_ONLY);
		dataSetType.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dataSetType.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				return (Object[])inputElement;
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
		});
		dataSetType.setLabelProvider(new LabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				return ((DataSetType)element).getDisplayName();
			}
			
		});
		
		fillDatas();
		
		dataSetType.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				getContainer().updateButtons();
				
			}
			
		});
		
		

		dataSourcesViewer.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				getContainer().updateButtons();
				String s = ((DataSource)((IStructuredSelection)dataSourcesViewer.getSelection()).getFirstElement()).getOdaExtensionDataSourceId();
				
				try {
					ExtensionManifest ext = ManifestExplorer.getInstance().getExtensionManifest(s);
					Object[] o = ext.getDataSetTypes();					
					dataSetType.setInput(o);
					if (o.length >= 1){
						dataSetType.setSelection(new StructuredSelection(o[0]));
					}
					
				} catch (OdaException e) {
					
					e.printStackTrace();
				}
				
				
			}
			
		});
		dataSetName.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
				
			}
			
		});

		setControl(main);

	}
	
	public void fillDatas(){
		dataSourcesViewer.setInput(Activator.getInstance().getListDataSource());
	
		if (dataSet != null){
			
			DataSource ds = Activator.getInstance().getDataSource(dataSet);
			
			dataSourcesViewer.setSelection(new StructuredSelection(ds));
			dataSourcesViewer.getControl().setEnabled(false);
			dataSetName.setText(dataSet.getName());
			dataSetName.setEnabled(false);

			try {
				ExtensionManifest ext = ManifestExplorer.getInstance().getExtensionManifest(ds.getOdaExtensionDataSourceId());
				Object[] o = ext.getDataSetTypes();
				
				dataSetType.setInput(o);
				if (o.length >= 1){
					Object dst = ext.getDataSetType(dataSet.getOdaExtensionDataSetId());
					dataSetType.setSelection(new StructuredSelection(dst));
				}
				
			} catch (OdaException e) {
				
				e.printStackTrace();
			}
			
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return !dataSetType.getSelection().isEmpty() && !dataSourcesViewer.getSelection().isEmpty() && !dataSetName.getText().equals("");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
	 */
	@Override
	public IWizardPage getNextPage() {
		
		if (dataSetPage != null){
			return dataSetPage;
		}
		
		DataSource ds = null;
		if (dataSet != null){
			ds = Activator.getInstance().getDataSource(dataSet);
		}
		else{
			ds = (DataSource)((IStructuredSelection)dataSourcesViewer.getSelection()).getFirstElement();
		}
			
		
		
		try {
			
			DataSourceDesign design = DesignFactory.eINSTANCE.createDataSourceDesign();
			design.setName(ds.getName());
			design.setOdaExtensionDataSourceId(ds.getOdaExtensionDataSourceId());
			design.setOdaExtensionId(ds.getOdaExtensionId());

			
			UIExtensionManifest mnf = UIManifestExplorer.getInstance().getExtensionManifest(ds.getOdaExtensionDataSourceId());
			String[] ids = mnf.getDataSetUIElementIDs();

			Properties pPr = DesignFactory.eINSTANCE.createProperties();
			java.util.Properties _pr = ds.getPrivateProperties(); 
			
			for(Object o : _pr.keySet()){
				pPr.setProperty((String)o, _pr.getProperty((String)o));
			}
			
			Properties pPu = DesignFactory.eINSTANCE.createProperties();
			java.util.Properties _pu = ds.getPublicProperties(); 
			
			for(Object o : _pu.keySet()){
				pPu.setProperty((String)o, _pu.getProperty((String)o));
			}
			
			if (_pu.size() > 0){
				design.setPublicProperties(pPu);
			}
			
			if (_pr.size() > 0){
				design.setPrivateProperties(pPr);
			}
			
			if (dataSet != null){
				String id = null;
				
				if (dataSetType == null){
					id = dataSet.getOdaExtensionDataSetId();
				}
				else{
					id = ((DataSetType)(((IStructuredSelection)this.dataSetType.getSelection()).getFirstElement())).getID();
				}
				 
					
				
				
				DataSetDesign dataSetDesign = DesignFactory.eINSTANCE.createDataSetDesign();
				dataSetDesign.setDataSourceDesign(design);
				dataSetDesign.setOdaExtensionDataSetId(id);
				dataSetDesign.setName(dataSet.getName());
//				dataSetDesign.setPrivateProperties(dataSet.getPrivateProperties());
//				dataSetDesign.setPublicProperties(dataSet.getPublicProperties());
				dataSetDesign.setQueryText(dataSet.getQueryText());
				
				DesignSessionRequest dsd = DesignFactory.eINSTANCE.createDesignSessionRequest(dataSetDesign);
					//rebuildDataSetDesign(dataSet.getName(), dataSet.getQueryText(), design);
				designSession = DataSetDesignSession.startEditDesign(dsd);	
			}
			else{
				String id = ((DataSetType)(((IStructuredSelection)this.dataSetType.getSelection()).getFirstElement())).getID();
				designSession = DataSetDesignSession.startNewDesign(dataSetName.getText(), id, design);
			}
			
			
			dataSetPage = designSession.getWizardStartingPage();

			
			return dataSetPage;
		} catch (OdaException e) {
			
			e.printStackTrace();
		}
		return dataSetPage.getNextPage();
		
	}
	
	public IWizardPage getDataSetPage(){
		return dataSetPage;
	}

	public String getDataSetName(){
		return dataSetName.getText();
	}
	
	public DataSource getDataSourceSelected(){
		return (DataSource)((IStructuredSelection)dataSourcesViewer.getSelection()).getFirstElement();
	}
	
	private DesignSessionRequest rebuildDataSetDesign(String dataSetName, String query, DataSourceDesign dataSourceDesign){
		DataSetDesign design = DesignFactory.eINSTANCE.createDataSetDesign();
		design.setDataSourceDesign(dataSourceDesign);
		design.setName(dataSetName);
		design.setQueryText(query);
		
		return DesignFactory.eINSTANCE.createDesignSessionRequest(design);
		
		
	}

	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;  
		
	}
}
