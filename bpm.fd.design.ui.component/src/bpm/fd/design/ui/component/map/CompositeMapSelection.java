package bpm.fd.design.ui.component.map;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.fd.api.core.model.components.definition.maps.IMapInfo;
import bpm.fd.api.core.model.components.definition.maps.MapInfo;
import bpm.fd.api.core.model.components.definition.maps.MapRenderer;
import bpm.fd.api.core.model.components.definition.maps.openlayers.IOpenLayersOptions;
import bpm.fd.api.core.model.components.definition.maps.openlayers.MapWMSOptions;
import bpm.fd.design.ui.component.Messages;
import bpm.vanilla.map.core.design.IMapDefinition;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapObject;
import bpm.vanilla.map.core.design.opengis.IOpenGisMapObject;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapObject;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapObjectProperty;
import bpm.vanilla.map.remote.core.design.fusionmap.impl.RemoteMapDefinitionService;
import bpm.vanilla.map.remote.core.design.impl.RemoteOpenGisMapService;

public class CompositeMapSelection implements ICompositeMapDefinition {

	private Composite client;
	
	private Text vanillaRuntimeServerUrl;
	private Text description;
	
	private Button browse;
	private ComboViewer mapDefinitions;
	
	private RemoteMapDefinitionService reg;
	
	@Override
	public Composite createContent(Composite parent) {
		client = new Composite(parent, SWT.NONE);
		client.setLayout(new GridLayout(2, false));
		
		Label l = new Label(client, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeMapSelection_0);
		
		
		vanillaRuntimeServerUrl = new Text(client, SWT.BORDER);
		vanillaRuntimeServerUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		vanillaRuntimeServerUrl.setText("http://localhost:7171/VanillaRuntime"); //$NON-NLS-1$
		vanillaRuntimeServerUrl.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				getClient().notifyListeners(SWT.Modify, new Event());
				
			}
		});
		
		browse = new Button(client, SWT.PUSH);
		browse.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		browse.setText(Messages.CompositeMapSelection_2);
		browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				reg = new RemoteMapDefinitionService();
				reg.setVanillaRuntimeUrl(vanillaRuntimeServerUrl.getText());
				
				try{
					mapDefinitions.setInput(reg.getAllMapDefinition());
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(client.getShell(), Messages.CompositeMapSelection_3, Messages.CompositeMapSelection_4 + ex.getMessage());
				}
			}
		});
		
		l = new Label(client, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeMapSelection_5);
		
		mapDefinitions = new ComboViewer(client, SWT.READ_ONLY);
		mapDefinitions.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		mapDefinitions.setContentProvider(new ArrayContentProvider());
		mapDefinitions.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((IMapDefinition)element).getLabel();
			}
		});
		
		mapDefinitions.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				IMapDefinition fso = (IMapDefinition)((IStructuredSelection)mapDefinitions.getSelection()).getFirstElement();
				if(fso != null) {
					description.setText(fso.getLabel());
					getClient().notifyListeners(SWT.Modify, new Event());
				}
			}
		});
		
		l = new Label(client, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		l.setText(Messages.CompositeMapSelection_6);
		
		description = new Text(client, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		description.setEnabled(false);


		return client;
	}

	@Override
	public Composite getClient() {
		return client;
	}

	@Override
	public MapInfo getMapInfo() {
		MapInfo info = new MapInfo();
		if (!mapDefinitions.getSelection().isEmpty()){
			IMapDefinition mapDef = ((IMapDefinition)((IStructuredSelection)mapDefinitions.getSelection()).getFirstElement());
			info.setMapId(mapDef.getId());
			info.setMapType(mapDef.getMapType());
			try {
				info.setFusionMap(!mapDef.getFusionMapObject().getType().equals("Vanilla Map"));
			} catch(Exception e1) {
				e1.printStackTrace();
			}
			
			if(mapDef.getFusionMapObjectId() != null) {
				info.setMapObjectId(mapDef.getFusionMapObjectId());
				info.setSwfFileName(mapDef.getFusionMapObject().getSwfFileName());
			}
			
			else if(mapDef.getOpenLayersObjectId() != null) {
				info.setMapObjectId(mapDef.getOpenLayersObjectId());
			}
			else if(mapDef.getMapType().equals(IMapDefinition.MAP_TYPE_OPEN_GIS)) {
				RemoteOpenGisMapService rem = new RemoteOpenGisMapService();
				rem.configure(vanillaRuntimeServerUrl.getText());
				try {
					IOpenGisMapObject map = rem.getOpenGisMapByDefinitionId(mapDef.getId());
					info.setMapObjectId(map.getId());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			else {
				info.setMapObjectId(mapDef.getKmlObjectId());
			}
		}
		info.setVanillaRuntimeUrl(vanillaRuntimeServerUrl.getText());
		
		return info; 
	}
	
	public MapRenderer getMapRenderer() throws Exception {
		if (!mapDefinitions.getSelection().isEmpty()){
			IMapDefinition mapDef = ((IMapDefinition)((IStructuredSelection)mapDefinitions.getSelection()).getFirstElement());
			
			if(mapDef.getFusionMapObject() != null) {
				if(mapDef.getFusionMapObject().getType() == null || mapDef.getFusionMapObject().getType().equals(IFusionMapObject.FUSIONMAP_TYPE)) {
					return MapRenderer.getRenderer(MapRenderer.VANILLA_FUSION_MAP);
				}
				else {
					return MapRenderer.getRenderer(MapRenderer.VANILLA_FLASH_MAP);
				}
			}
			
			else  {
				return MapRenderer.getRenderer(MapRenderer.VANILLA_OPENLAYERS_MAP);
			}
			
		}
		return null;
	}

	@Override
	public void init(IMapInfo inf) {
		MapInfo info = (MapInfo)inf;
		if (inf == null){
			mapDefinitions.setInput(new ArrayList());
			vanillaRuntimeServerUrl.setText("http://localhost:7171/VanillaRuntime"); //$NON-NLS-1$
		}
		else{
			vanillaRuntimeServerUrl.setText(info.getVanillaRuntimeUrl());
			
			try{
				loadMaps(info.getVanillaRuntimeUrl(), info.getMapId());
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(client.getShell(), Messages.CompositeMapSelection_8, Messages.CompositeMapSelection_9 + ex.getMessage());
			}	
		}
	}
	
	private void loadMaps(String vanillaServerUrl, Integer mapId) throws Exception{
		RemoteMapDefinitionService reg = new RemoteMapDefinitionService();
		reg.setVanillaRuntimeUrl(vanillaRuntimeServerUrl.getText());
		
		mapDefinitions.setInput(reg.getAllMapDefinition());
		
		if (mapId != null){
			for(Object o : (List)mapDefinitions.getInput()){
				if (((IMapDefinition)o).getId() == mapId){
					mapDefinitions.setSelection(new StructuredSelection(o));
					return;
				}
			}
		}
		mapDefinitions.setSelection(StructuredSelection.EMPTY);
		
		
	}

	public IOpenLayersOptions getOpenLayersOptions() throws Exception {
		
		if(getMapRenderer().getRendererStyle() == MapRenderer.VANILLA_OPENLAYERS_MAP) {
			
			IMapDefinition mapDef = ((IMapDefinition)((IStructuredSelection)mapDefinitions.getSelection()).getFirstElement());
			List<IOpenLayersMapObjectProperty> props = mapDef.getOpenLayersMapObject().getProperties();
			
			if(mapDef.getOpenLayersMapObject().getType().equals(IOpenLayersMapObject.TYPE_WMS)) {
				MapWMSOptions option = new MapWMSOptions();
				
				for(IOpenLayersMapObjectProperty prop : props) {
					setProperty(option, prop);
				}
				
				return option;
			}
			
		}
		return null;
	}

	private void setProperty(MapWMSOptions option, IOpenLayersMapObjectProperty prop) {
//		if(prop.getName().equals(IOpenLayersMapObjectProperty.PROP_URL)) {
//			option.setWmsUrl(prop.getValue());
//		}
//		else if(prop.getName().equals(IOpenLayersMapObjectProperty.PROP_LAYERS)) {
//			option.setLayerName(prop.getValue());
//		}
//		else if(prop.getName().equals(IOpenLayersMapObjectProperty.PROP_TYPE)) {
//			option.setType(prop.getValue());
//		}
		
	}

}
