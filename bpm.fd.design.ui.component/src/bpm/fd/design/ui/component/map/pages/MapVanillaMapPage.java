package bpm.fd.design.ui.component.map.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import bpm.fd.api.core.model.components.definition.maps.MapInfo;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.component.map.CompositeVanillaMap;

public class MapVanillaMapPage extends WizardPage {

	public static final String PAGE_NAME = "bpm.fd.design.ui.component.map.pages.MapVanillaMapPage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = Messages.MapVanillaMapPage_1;
	public static final String PAGE_DESCRIPTION = Messages.MapVanillaMapPage_2;
	
	private CompositeVanillaMap vanillaComposite;
	
	public MapVanillaMapPage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
	
	protected MapVanillaMapPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		vanillaComposite = new CompositeVanillaMap();
		vanillaComposite.createContent(parent);
		
		vanillaComposite.getClient().addListener(SWT.Modify, new Listener() {
			
			public void handleEvent(Event event) {
				getContainer().updateButtons();
				
			}
		});
		
		setControl(vanillaComposite.getClient());
	}
	
	@Override
	public boolean isPageComplete() {
		return vanillaComposite.getMapInfo().getMapObjectId() != null;
	}
	
	public MapInfo getMapInfo(){
		return vanillaComposite.getMapInfo();
	}

}
