package bpm.fd.design.ui.component.map.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import bpm.fd.api.core.model.components.definition.maps.MapInfo;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.component.map.CompositeFusionMap;

public class MapFusionMapPage extends WizardPage{
	public static final String PAGE_NAME = "bpm.fd.design.ui.component.map.pages.MapFusionMapPage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = Messages.MapFusionMapPage_1;
	public static final String PAGE_DESCRIPTION = Messages.MapFusionMapPage_2;

	private CompositeFusionMap fusionMap;
	
	public MapFusionMapPage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
	
	protected MapFusionMapPage(String pageName) {
		super(pageName);
	}
	
	public void createControl(Composite parent) {
		fusionMap = new CompositeFusionMap();
		fusionMap.createContent(parent);
		
		fusionMap.getClient().addListener(SWT.Modify, new Listener() {
			
			public void handleEvent(Event event) {
				getContainer().updateButtons();
				
			}
		});
		
		setControl(fusionMap.getClient());
	}
	
	@Override
	public boolean isPageComplete() {
		return fusionMap.getMapInfo().getMapObjectId() != null;
	}
	
	public MapInfo getMapInfo(){
		return fusionMap.getMapInfo();
	}
}
