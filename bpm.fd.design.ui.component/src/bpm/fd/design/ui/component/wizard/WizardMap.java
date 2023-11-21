package bpm.fd.design.ui.component.wizard;

import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.maps.ComponentMap;
import bpm.fd.api.core.model.components.definition.maps.MapInfo;
import bpm.fd.api.core.model.components.definition.maps.openlayers.IOpenLayersOptions;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.exception.DictionaryException;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.component.map.pages.MapColorRangePage;
import bpm.fd.design.ui.component.map.pages.MapDatasPage;
import bpm.fd.design.ui.component.map.pages.MapFreeMetricsConnectionPage;
import bpm.fd.design.ui.component.map.pages.MapSelectionPage;
import bpm.fd.design.ui.component.pages.GeneralPage;
import bpm.fd.design.ui.wizard.IWizardComponent;
import bpm.vanilla.map.core.design.IMapDefinition;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;

public class WizardMap extends Wizard implements IWizardComponent{

	private GeneralPage generic;
//	private MapRendererPage rendererPage;
	private MapDatasPage datasPage;
	private MapColorRangePage colorRangesPage;
//	private MapFusionMapPage mapPage;
//	private MapVanillaMapPage vanillaMapPage;
	private MapFreeMetricsConnectionPage fmPage;
	private MapSelectionPage mapPage;
	
//	@Override
//	public void setContainer(IWizardContainer wizardContainer) {
//		super.setContainer(wizardContainer);
//		if (wizardContainer instanceof WizardDialog){
//			
//			((WizardDialog)wizardContainer).addPageChangingListener(new IPageChangingListener() {
//				
//				public void handlePageChanging(PageChangingEvent event) {
//					if (event.getTargetPage() == datasPage){
//						try{
//							if (rendererPage.getRenderer() != null){
//								datasPage.rebuildContent(rendererPage.getRenderer().getRendererStyle());
//							}
//						}catch(Exception ex){
//							
//						}
//							
//						
//					}
//					
//				}
//			});
//		}
//	}
	private IComponentDefinition def;
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		generic = new GeneralPage();
		addPage(generic);
		
		mapPage = new MapSelectionPage();
		addPage(mapPage);
		
//		rendererPage = new MapRendererPage();
//		addPage(rendererPage);
//		
//		mapPage = new MapFusionMapPage();
//		addPage(mapPage);
//		
//		vanillaMapPage = new MapVanillaMapPage();
//		addPage(vanillaMapPage);
		
		colorRangesPage = new MapColorRangePage();
		addPage(colorRangesPage);
		
		datasPage = new MapDatasPage();
		addPage(datasPage);
		
		fmPage = new MapFreeMetricsConnectionPage();
		addPage(fmPage);
		
		super.addPages();
	}

	@Override
	public boolean performFinish() {
		Dictionary dictionaty = Activator.getDefault().getProject().getDictionary();
		Properties prop = generic.getValues();
		
		ComponentMap component = new ComponentMap(prop.getProperty(GeneralPage.PROPERTY_NAME), dictionaty);
		component.setComment(prop.getProperty(GeneralPage.PROPERTY_DESCRIPTION));
		try{
//			component.setRenderer(rendererPage.getRenderer());
			component.setRenderer(mapPage.getMapRenderer());
			
			try {
				IOpenLayersOptions opt = mapPage.getOpenLayersOptions();
				if(opt != null) {
				
					component.setComponentOption(opt);
				}
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			
		}catch(Exception e){
			e.printStackTrace();
			ErrorDialog.openError(getShell(), Messages.WizardMap_0, Messages.WizardMap_1, 
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			return false;
		}
		MapInfo inf = mapPage.getMapInfo();
		
		
		
		
//		if (component.getRenderer().getRendererStyle() == MapRenderer.VANILLA_FUSION_MAP ){
//			MapInfo inf = mapPage.getMapInfo();
////			((MapInfo)component.getMapInfo()).setFusionMapObjectId(inf.getFusionMapObjectId());
////			((MapInfo)component.getMapInfo()).setVanillaRuntimeUrl(inf.getVanillaRuntimeUrl());
//			component.getMapInfo().updateValues(inf);
//		}
//		
//		else if(component.getRenderer().getRendererStyle() == MapRenderer.VANILLA_FLASH_MAP) {
//			MapInfo inf = vanillaMapPage.getMapInfo();
////			((MapInfo)component.getMapInfo()).setFusionMapObjectId(inf.getFusionMapObjectId());
////			((MapInfo)component.getMapInfo()).setVanillaRuntimeUrl(inf.getVanillaRuntimeUrl());
//			component.getMapInfo().updateValues(inf);
//		}
		
		if(inf.getMapType() != null && inf.getMapType().equals(IMapDefinition.MAP_TYPE_FM)) {
			String user = fmPage.getUser();
			String password = fmPage.getPassword();
			inf.setFmPassword(password);
			inf.setFmUser(user);
			//TODO Create fmContext
		}
		else {
		
			try {
				component.setComponentDatas(datasPage.getMapDatas());
			} catch (Exception e) {
				e.printStackTrace();
				ErrorDialog.openError(getShell(), Messages.WizardMap_2, Messages.WizardMap_3, 
						new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
				return false;
			}
		}
		
		for(ColorRange cr : colorRangesPage.getColorRanges()){
			component.addColorRange(cr);
		}
		
		component.getMapInfo().updateValues(inf);
		
		try {
			dictionaty.addComponent(component);
			
		} catch (DictionaryException e) {
			ErrorDialog.openError(getShell(), Messages.WizardMap_4, Messages.WizardMap_5, 
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			return false;
		}
		
		def = component;
		Activator.getDefault().firePropertyChange(Activator.PROJECT_CONTENT, null, component);
		
		
		
		return true;
	}

	
	@Override
	public boolean canFinish() {
		
		try{
			
			if(datasPage.isPageComplete() || fmPage.isPageComplete()) {
				return true;
			}
			
//			if (rendererPage.getRenderer().getRendererStyle() == MapRenderer.VANILLA_GOOGLE_MAP){
//				
//				for(IWizardPage p : getPages()){
//					if (p != mapPage){
//						if (!p.isPageComplete()){
//							return false;
//						}
//					}
//				}
//				return true;
//			}
//			
//			if (rendererPage.getRenderer().getRendererStyle() == MapRenderer.VANILLA_FLASH_MAP){
//				
//				for(IWizardPage p : getPages()){
//					if (p != mapPage){
//						if (!p.isPageComplete()){
//							return false;
//						}
//					}
//				}
//				return true;
//			}
//			
//			if (rendererPage.getRenderer().getRendererStyle() == MapRenderer.VANILLA_FUSION_MAP){
//				
//				for(IWizardPage p : getPages()){
//					if (p != vanillaMapPage){
//						if (!p.isPageComplete()){
//							return false;
//						}
//					}
//				}
//				return true;
//			}
		}catch(Exception ex){
			
		}
		
		
		return super.canFinish();
	}
	
	public IComponentDefinition getComponent() {
		return def;
	}
	
	public Class<? extends IComponentDefinition> getComponentClass() {
		return ComponentMap.class;
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
	}
	public boolean needRepositoryConnections() {
		return false;
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
//		if (getContainer().getCurrentPage() == rendererPage){
//			
//			try{
//				if (rendererPage.getRenderer().getRendererStyle() == MapRenderer.VANILLA_GOOGLE_MAP){
//					return colorRangesPage;
//				}
//				else if(rendererPage.getRenderer().getRendererStyle() == MapRenderer.VANILLA_FLASH_MAP) {
//					return vanillaMapPage;
//				}
//			}catch(Exception ex){
//				
//			}
//			
//		}
		if (getContainer().getCurrentPage() == colorRangesPage) {
			if(mapPage.getMapInfo().getMapType() == null || !mapPage.getMapInfo().getMapType().equals(IMapDefinition.MAP_TYPE_FM)) {
				return datasPage;
			}
			else {
				return fmPage;
			}
//			return colorRangesPage;
		}
		return super.getNextPage(page);
	}
	
	
}
