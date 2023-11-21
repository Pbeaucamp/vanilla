package bpm.fd.design.ui.wizard.pages;

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

import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.wizard.OdaDataSourceWizard;

public class DataSourceSelectionPage extends WizardPage{

	private ComboViewer dataSourcesViewer;
	private ComboViewer dataSetType;
	private DataSetDesignSession designSession;
	private Text dataSetName;
	private DataSet dataSet;
	
	private IWizardPage dataSetPage;
	
	private boolean datasourceHasChange = false;
	
	public DataSourceSelectionPage(String pageName, DataSet dataSet) {
		super(pageName);
		this.dataSet = dataSet;
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DataSourceSelectionPage_0);
		
		
		
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
				List<DataSource> l = ((Dictionary)inputElement).getDatasources();
				return l.toArray(new DataSource[l.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
		});
		
		Button create = new Button(main, SWT.PUSH);
		create.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		create.setText("..."); //$NON-NLS-1$
		create.setToolTipText(Messages.DataSourceSelectionPage_2);
		create.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				
				try{
					OdaDataSourceWizard wiz = new OdaDataSourceWizard();
					
					WizardDialog dial = new WizardDialog(getShell(), wiz);
					if (dial.open() == Dialog.OK){
						fillDatas();
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
				
			}
			
			
		});
		
		Label l2 = new Label(main, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.DataSourceSelectionPage_3);
		
		dataSetName = new Text(main, SWT.BORDER);
		dataSetName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		
		
		
		l2 = new Label(main, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.DataSourceSelectionPage_4);
		
		dataSetType = new ComboViewer(main, SWT.READ_ONLY);
		dataSetType.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
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
					
					datasourceHasChange = true;
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
		dataSourcesViewer.setInput(Activator.getDefault().getProject().getDictionary());
		if (dataSet != null){
			DataSource ds = Activator.getDefault().getProject().getDictionary().getDatasource(dataSet.getDataSourceName());
			
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
		boolean b =  !dataSetType.getSelection().isEmpty() && !dataSourcesViewer.getSelection().isEmpty() && !dataSetName.getText().equals(Messages.DataSourceSelectionPage_5);
		
		
		if (b){
			String dataSetName = this.dataSetName.getText();
			
			char c = dataSetName.charAt(0);
			if (!Character.isJavaIdentifierStart(c)){
				
				setErrorMessage(Messages.DataSourceSelectionPage_6);
//				MessageDialog.openInformation(getShell(), "DataSet Creation","DataSetName can only be a string with numbers and letters and '_' and must not start with a number.");
				return false;
			}
			for(int i = 0; i < dataSetName.length(); i++){
				c = dataSetName.charAt(i);
				if ((c < '0' || c >'9') && (c<'a' || c>'z') && (c<'A' || c>'Z') && c !='_'){
					setErrorMessage(Messages.DataSourceSelectionPage_7);
					//MessageDialog.openInformation(getShell(), "DataSet Creation","DataSetName can only be a string with numbers and letters and '_' and must not start with a number.");
					return false;
				}
			}
			setErrorMessage(null);
		}
		return b;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
	 */
	@Override
	public IWizardPage getNextPage() {
		
		if (dataSetPage != null && !datasourceHasChange){
			return dataSetPage;
		}
		
		DataSource ds = null;
		if (dataSet != null && !datasourceHasChange){
			ds = Activator.getDefault().getProject().getDictionary().getDatasource(dataSet.getDataSourceName());
		}
		else{
			ds = (DataSource)((IStructuredSelection)dataSourcesViewer.getSelection()).getFirstElement();
		}
			
		datasourceHasChange = false;
		
		
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
			if (dataSetPage instanceof WizardPage){
				((WizardPage)dataSetPage).getWizard().setContainer(getContainer());
			}
			
			
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
