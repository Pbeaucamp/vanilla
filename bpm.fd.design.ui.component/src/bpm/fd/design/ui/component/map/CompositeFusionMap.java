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
import bpm.fd.design.ui.component.Messages;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapObject;
import bpm.vanilla.map.remote.core.design.impl.RemoteFusionMapRegistry;

public class CompositeFusionMap implements ICompositeMapDefinition{
	private Composite client;
	
	private Text vanillaRuntimeServerUrl;
	private Text description;
	
	private Button browse;
	private ComboViewer fusionMapObjects;
	
	public Composite createContent(Composite parent){
		client = new Composite(parent, SWT.NONE);
		client.setLayout(new GridLayout(2, false));
		
		Label l = new Label(client, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeFusionMap_0);
		
		
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
		browse.setText(Messages.CompositeFusionMap_2);
		browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RemoteFusionMapRegistry reg = new RemoteFusionMapRegistry();
				reg.setVanillaRuntimeUrl(vanillaRuntimeServerUrl.getText());
				
				try{
					fusionMapObjects.setInput(reg.getFusionMapObjects(IFusionMapObject.FUSIONMAP_TYPE));
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(client.getShell(), Messages.CompositeFusionMap_3, Messages.CompositeFusionMap_4 + ex.getMessage());
				}
			}
		});
		
		l = new Label(client, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeFusionMap_5);
		
		fusionMapObjects = new ComboViewer(client, SWT.READ_ONLY);
		fusionMapObjects.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		fusionMapObjects.setContentProvider(new ArrayContentProvider());
		fusionMapObjects.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((IFusionMapObject)element).getName();
			}
		});
		
		fusionMapObjects.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				IFusionMapObject fso = (IFusionMapObject)((IStructuredSelection)fusionMapObjects.getSelection()).getFirstElement();
				description.setText(fso.getName());
				getClient().notifyListeners(SWT.Modify, new Event());
			}
		});
		
		l = new Label(client, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		l.setText(Messages.CompositeFusionMap_6);
		
		description = new Text(client, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		description.setEnabled(false);


		return client;
	}

	public Composite getClient(){
		return client;
	}
	
	public void init(IMapInfo inf){
		MapInfo info = (MapInfo)inf;
		if (inf == null){
			fusionMapObjects.setInput(new ArrayList());
			vanillaRuntimeServerUrl.setText("http://localhost:7171/VanillaRuntime"); //$NON-NLS-1$
		}
		else{
			vanillaRuntimeServerUrl.setText(info.getVanillaRuntimeUrl());
			
			try{
				loadMaps(info.getVanillaRuntimeUrl(), info.getMapObjectId());
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(client.getShell(), Messages.CompositeFusionMap_8, Messages.CompositeFusionMap_9 + ex.getMessage());
			}	
		}
		
		
	}
	
	
	private void loadMaps(String vanillaServerUrl, Long fusionMapId) throws Exception{
		RemoteFusionMapRegistry reg = new RemoteFusionMapRegistry();
		reg.setVanillaRuntimeUrl(vanillaRuntimeServerUrl.getText());
		
		
		
		fusionMapObjects.setInput(reg.getFusionMapObjects());
		
		if (fusionMapId != null){
			for(Object o : (List)fusionMapObjects.getInput()){
				if (((IFusionMapObject)o).getId() == fusionMapId){
					fusionMapObjects.setSelection(new StructuredSelection(o));
					return;
				}
			}
		}
		fusionMapObjects.setSelection(StructuredSelection.EMPTY);
		
		
	}
	
	public MapInfo getMapInfo(){
		MapInfo info = new MapInfo();
		if (!fusionMapObjects.getSelection().isEmpty()){
			info.setMapObjectId(((IFusionMapObject)((IStructuredSelection)fusionMapObjects.getSelection()).getFirstElement()).getId());
			info.setSwfFileName(((IFusionMapObject)((IStructuredSelection)fusionMapObjects.getSelection()).getFirstElement()).getSwfFileName());
			try {
				info.setFusionMap(!((IFusionMapObject)((IStructuredSelection)fusionMapObjects.getSelection()).getFirstElement()).getType().equals("Vanilla Map"));
			} catch(Exception e1) {
				e1.printStackTrace();
			}
		}
		info.setVanillaRuntimeUrl(vanillaRuntimeServerUrl.getText());
		
		return info; 
	}
}
