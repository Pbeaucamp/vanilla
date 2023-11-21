package bpm.birep.admin.client.views.datalists.wizards.oda;

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
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import adminbirep.Messages;
import bpm.vanilla.platform.core.beans.data.OdaInput;




public class DataSetTypePage extends WizardPage{

	private ComboViewer dataSetType;
	private OdaInput odaInput;	
	private IWizardPage designPage;
	private boolean multipleDataSetType;
	private DataSetType type;
	/**
	 * @param pageName
	 */
	public DataSetTypePage(String pageName, OdaInput odaInput) {
		super(pageName);
		this.odaInput = odaInput;
	}

	public boolean isMultipleDataSetType(){
		return multipleDataSetType;
	}
	
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.Client_Views_DataLists_DataSetTypePage_0);
		
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
		
		try {
			ExtensionManifest ext = ManifestExplorer.getInstance().getExtensionManifest(odaInput.getOdaExtensionDataSourceId());
			Object[] o = ext.getDataSetTypes();
			multipleDataSetType = o.length > 1;
			
			dataSetType.setInput(o);
			type  = (DataSetType)o[0];
			dataSetType.setSelection(new StructuredSelection(type ));
		} catch (OdaException e) {
			
			e.printStackTrace();
		}
		
		dataSetType.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				getContainer().updateButtons();
				
			}
			
		});

		
		
		
		setControl(main);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
	 */
	@Override
	public IWizardPage getNextPage() {

		if (designPage != null){
			return designPage;
		}
		DataSourceDesign design = DesignFactory.eINSTANCE.createDataSourceDesign();
		design.setName(odaInput.getName());
		design.setOdaExtensionDataSourceId(odaInput.getOdaExtensionDataSourceId());
		design.setOdaExtensionId(odaInput.getOdaExtensionId());

		
	
		Properties pPr = DesignFactory.eINSTANCE.createProperties();
		java.util.Properties _pr = odaInput.getDatasourcePrivateProperties(); 
		
		for(Object o : _pr.keySet()){
			pPr.setProperty((String)o, _pr.getProperty((String)o));
		}
		
		Properties pPu = DesignFactory.eINSTANCE.createProperties();
		java.util.Properties _pu = odaInput.getDatasourcePublicProperties(); 
		
		for(Object o : _pu.keySet()){
			pPu.setProperty((String)o, _pu.getProperty((String)o));
		}
		
		if (_pu.size() > 0){
			design.setPublicProperties(pPu);
		}
		
		if (_pr.size() > 0){
			design.setPrivateProperties(pPr);
		}

		//rebuildDataSetDesign(dataSet.getName(), dataSet.getQueryText(), design);
		ExtensionManifest mf;
		try {
			mf = ManifestExplorer.getInstance().getExtensionManifest(odaInput.getOdaExtensionDataSourceId());
			
			if (type == null  && !multipleDataSetType){
				ExtensionManifest ext = ManifestExplorer.getInstance().getExtensionManifest(odaInput.getOdaExtensionDataSourceId());
				type = ext.getDataSetTypes()[0];
			}
			String id = type.getID();
			
			if (odaInput.getQueryText() == null){
				DataSetDesignSession designSession = DataSetDesignSession.startNewDesign(odaInput.getName() + "_dataset",id, design); //$NON-NLS-1$
				designPage =  designSession.getWizardStartingPage();

			}
			else{
				DataSetDesign dataSetDesign = DesignFactory.eINSTANCE.createDataSetDesign();
				dataSetDesign.setDataSourceDesign(design);
				dataSetDesign.setOdaExtensionDataSetId(id);
				dataSetDesign.setName(odaInput.getName() + "_dataset"); //$NON-NLS-1$
//				dataSetDesign.setPrivateProperties(dataSet.getPrivateProperties());
//				dataSetDesign.setPublicProperties(dataSet.getPublicProperties());
				dataSetDesign.setQueryText(odaInput.getQueryText());
				
				DesignSessionRequest dsd = DesignFactory.eINSTANCE.createDesignSessionRequest(dataSetDesign);
					//rebuildDataSetDesign(dataSet.getName(), dataSet.getQueryText(), design);
				DataSetDesignSession designSession = DataSetDesignSession.startEditDesign(dsd);	
				designPage = designSession.getWizardStartingPage();
			}
			
						
			return designPage;
		} catch (OdaException e) {
			
			e.printStackTrace();
		}
		return super.getNextPage();
	}

	public IWizardPage getDesignPage() {
		return designPage;
	}
	
}
