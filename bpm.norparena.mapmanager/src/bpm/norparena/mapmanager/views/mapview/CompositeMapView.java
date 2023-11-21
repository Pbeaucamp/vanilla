package bpm.norparena.mapmanager.views.mapview;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import bpm.norparena.mapmanager.Messages;
import bpm.norparena.mapmanager.fusionmap.FusionMapEntitiesDetails;
import bpm.vanilla.map.core.design.IMapDefinition;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapObject;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapSpecificationEntity;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapObject;

public class CompositeMapView {

	private Text name;
	private Text description;
	
	private FusionMapEntitiesDetails loadedDatas;
	
	private Composite client;
	
	
	public Composite getClient(){
		return client;
	}
	
	public Composite createContent(Composite parent){
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());
		
		client = toolkit.createComposite(parent);
		client.setLayout(new GridLayout(2, false));
		
		Label l = toolkit.createLabel(client, Messages.CompositeMapView_0);
		l.setLayoutData(new GridData());
		
		name = toolkit.createText(client, "", SWT.BORDER); //$NON-NLS-1$
		name.setEditable(false);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		l = toolkit.createLabel(client, Messages.CompositeMapView_2);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		
		description = toolkit.createText(client, ""); //$NON-NLS-1$
		description.setEditable(false);
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		
		
		l = toolkit.createLabel(client, Messages.CompositeMapView_4);
		l.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		
		loadedDatas = new FusionMapEntitiesDetails(toolkit);
		
		loadedDatas.createContents(client);
		loadedDatas.getViewer().getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		return client;
	}
	
	public IFusionMapSpecificationEntity getSelection(){
		IStructuredSelection selection = (IStructuredSelection)loadedDatas.getViewer().getSelection();
		if(selection.isEmpty()){
			return null;
		}
		return (IFusionMapSpecificationEntity)selection.getFirstElement();
	}


	public void setInput(IMapDefinition mapDef){
		if (mapDef == null){
			name.setText(""); //$NON-NLS-1$
			description.setText(""); //$NON-NLS-1$
			loadedDatas.getViewer().setInput(new ArrayList());
		}
		else if(mapDef.getFusionMapObjectId() != null){
			IFusionMapObject fusionMap = mapDef.getFusionMapObject();
			name.setText(fusionMap.getName());
			description.setText(fusionMap.getDescription());
			loadedDatas.getViewer().setInput(fusionMap.getSpecificationsEntities());
		}
		else if(mapDef.getOpenLayersObjectId() != null) {
			IOpenLayersMapObject fusionMap = mapDef.getOpenLayersMapObject();
			name.setText(fusionMap.getName());
			description.setText(mapDef.getDescription());
			loadedDatas.getViewer().setInput(fusionMap.getEntities());
		}
	}
}
