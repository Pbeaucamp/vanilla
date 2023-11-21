package bpm.vanilla.map.oda.ui.dataset;

import java.util.List;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.ui.designsession.DesignSessionUtil;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage;
import org.eclipse.datatools.connectivity.oda.design.util.DesignUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.vanilla.map.core.design.fusionmap.IFusionMapObject;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapRegistry;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapSpecificationEntity;
import bpm.vanilla.map.oda.runtime.impl.Connection;
import bpm.vanilla.map.oda.ui.Activator;
import bpm.vanilla.map.remote.core.design.impl.RemoteFusionMapRegistry;

public class FusionMapDataSetPage extends DataSetWizardPage {

	
	private ComboViewer fusionMaps;
	private Text description;
	private TableViewer datas;
	
	public FusionMapDataSetPage(String pageName) {
		super(pageName);
	}

	public FusionMapDataSetPage(String pageName, String title,ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	@Override
	public void createPageCustomControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		main.setLayout(new GridLayout(2, false));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Select A FusionMap");
		
	
		fusionMaps = new ComboViewer(main, SWT.READ_ONLY);
		fusionMaps.setContentProvider(new ArrayContentProvider());
		fusionMaps.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((IFusionMapObject)element).getName();
			}
		});
		fusionMaps.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		fusionMaps.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object o = ((IStructuredSelection)fusionMaps.getSelection()).getFirstElement();
				
				description.setText(((IFusionMapObject)o).getDescription());
				datas.setInput(((IFusionMapObject)o).getSpecificationsEntities());
				try {
					if (getContainer() != null){
						getContainer().updateButtons();
					}
				} catch(Exception e) {
				}
				
			}
		});
		
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		l.setText("Description");
		
		description = new Text(main, SWT.BORDER);
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		
		
		datas = new TableViewer(main, SWT.BORDER | SWT.V_SCROLL |SWT.H_SCROLL);
		datas.setContentProvider(new ArrayContentProvider());
		datas.getTable().setHeaderVisible(true);
		datas.getTable().setLinesVisible(true);
		datas.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		TableViewerColumn col = new TableViewerColumn(datas, SWT.LEFT);
		col.getColumn().setWidth(100);
		col.getColumn().setText("Internal Id");
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				
				return ((IFusionMapSpecificationEntity)element).getFusionMapInternalId();
			}
		});
		
		
		col = new TableViewerColumn(datas, SWT.LEFT);
		col.getColumn().setWidth(100);
		col.getColumn().setText("Short Name");
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				
				return ((IFusionMapSpecificationEntity)element).getFusionMapShortName();
			}
		});
		
		
		col = new TableViewerColumn(datas, SWT.LEFT);
		col.getColumn().setWidth(100);
		col.getColumn().setText("Short Name");
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				
				return ((IFusionMapSpecificationEntity)element).getFusionMapLongName();
			}
		});
		
		setControl(main);
		initialiseControl();
	}

	
	@Override
	public boolean isPageComplete() {
		return !fusionMaps.getSelection().isEmpty();
	}
	
	@Override
	protected DataSetDesign collectDataSetDesign(DataSetDesign dataSetDesign) {

        if (fusionMaps != null){
        	String queryText = ""+ ((IFusionMapObject)((IStructuredSelection)fusionMaps.getSelection()).getFirstElement()).getId();
        	dataSetDesign.setQueryText(queryText);
        }
        else{
        	
        }
        
//
        if (dataSetDesign.getPublicProperties() == null) {
            try {
                dataSetDesign.setPublicProperties(DesignSessionUtil
                        .createDataSetPublicProperties(dataSetDesign
                                .getOdaExtensionDataSourceId(), dataSetDesign
                                .getOdaExtensionDataSetId(),
                                getBlankPageProperties()));
            } catch (OdaException e) {
                // TODO need some logging here
                e.printStackTrace();
            }
        }

      


		return dataSetDesign;
	}
	
	private static java.util.Properties getBlankPageProperties() {
        java.util.Properties prop = new java.util.Properties();
        prop.setProperty(Connection.VANILLA_RUNTIME_URL, "");
        return prop;
	}
	
	private void initialiseControl(){
		IFusionMapRegistry fusionMapRegistry = null;
		Properties connProps = DesignUtil.convertDataSourceProperties(getInitializationDesign().getDataSourceDesign());
		String vanillaServerUrl = null;
		//fill the fusionMapsObject availables
		
		try{
			vanillaServerUrl= connProps.getProperty(Connection.VANILLA_RUNTIME_URL);
			fusionMapRegistry = new RemoteFusionMapRegistry();
			fusionMapRegistry.configure(vanillaServerUrl);
			
			fusionMaps.setInput(fusionMapRegistry.getFusionMapObjects());
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getShell(), "Error", "Unable to load available FusionMapsObject from Vanilla Platform at " + vanillaServerUrl + ".\n" + ex.getMessage());
		}
		
		
		
		// Restores the last saved data set design
        DataSetDesign dataSetDesign = getInitializationDesign();
        
        if (dataSetDesign == null || dataSetDesign.getQueryText() == null || dataSetDesign.getQueryText().trim().equals("")){ //$NON-NLS-1$
        	return;
        }
	    
        try{
        	Long l = Long.parseLong(dataSetDesign.getQueryText());
        	
        	for(Object o : (List)fusionMaps.getInput()){
        		if (((IFusionMapObject)o).getId() == l.longValue()){
        			fusionMaps.setSelection(new StructuredSelection(o));
        			break;
        		}
        	}
        	
        }catch(Exception ex){
        	ex.printStackTrace();
        	MessageDialog.openError(getShell(), "Error", "The query could not been rebuilt.\n" + ex.getMessage());
        }
	       
		
        
        
	}
}

