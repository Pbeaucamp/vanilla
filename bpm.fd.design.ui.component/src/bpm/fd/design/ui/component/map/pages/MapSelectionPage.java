package bpm.fd.design.ui.component.map.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import bpm.fd.api.core.model.components.definition.maps.MapInfo;
import bpm.fd.api.core.model.components.definition.maps.MapRenderer;
import bpm.fd.api.core.model.components.definition.maps.openlayers.IOpenLayersOptions;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.component.map.CompositeMapSelection;

public class MapSelectionPage extends WizardPage {

	public static final String PAGE_NAME = "bpm.fd.design.ui.component.map.pages.MapSelectionPage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = Messages.MapSelectionPage_1;
	public static final String PAGE_DESCRIPTION = Messages.MapSelectionPage_2;
	
	private CompositeMapSelection vanillaComposite;
	
	public MapSelectionPage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
	
	protected MapSelectionPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		vanillaComposite = new CompositeMapSelection();
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
	
	public MapRenderer getMapRenderer() throws Exception {
		return vanillaComposite.getMapRenderer();
	}
	
	public IOpenLayersOptions getOpenLayersOptions() throws Exception {
		return vanillaComposite.getOpenLayersOptions();
	}
}
