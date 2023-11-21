package bpm.fwr.client.wizard.map;

import java.util.List;

import bpm.fwr.api.beans.components.OptionsFusionMap;
import bpm.fwr.api.beans.components.VanillaMapComponent;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.panels.ReportSheet;
import bpm.fwr.shared.models.metadata.FwrMetadata;
import bpm.gwt.commons.client.wizard.GwtWizard;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;

public class VanillaMapsWizard extends GwtWizard {

	private IGwtPage currentPage;
	private VanillaMapsGeneralPage vanillaMapsGeneralPage;
	private VanillaMapsDefinitionPage vanillaMapsDefinitionPage;
	
	public VanillaMapsWizard(ReportSheet reportSheetParent, VanillaMapComponent mapComponent, List<DataSet> dsAvailable) {
		super(Bpm_fwr.LBLW.MapTitle());

		List<FwrMetadata> metadatas = reportSheetParent.getPanelParent().getPanelparent().getMetadatas();
		vanillaMapsGeneralPage = new VanillaMapsGeneralPage(this, metadatas, dsAvailable, mapComponent);
		vanillaMapsDefinitionPage = new VanillaMapsDefinitionPage(mapComponent);
		setCurrentPage(vanillaMapsGeneralPage);
	}

	@Override
	public boolean canFinish() {
		return vanillaMapsGeneralPage.isComplete();
	}

	@Override
	public void setCurrentPage(IGwtPage page) {
		if(page instanceof VanillaMapsGeneralPage)
			setContentPanel((VanillaMapsGeneralPage)page);
		if(page instanceof VanillaMapsDefinitionPage)
			setContentPanel((VanillaMapsDefinitionPage)page);
		
		currentPage = page;
		updateBtn();
	}

	@Override
	public void updateBtn() {
		setBtnBackState(currentPage.canGoBack() ? true : false);
		setBtnNextState(currentPage.canGoFurther() ? true : false);
		setBtnFinishState(canFinish() ? true : false);
	}

	@Override
	protected void onClickFinish() {
		if(vanillaMapsGeneralPage != null && vanillaMapsGeneralPage.isComplete()){
			VanillaMapComponent mapComp = vanillaMapsGeneralPage.generateMap(false);
			List<ColorRange> colors = vanillaMapsDefinitionPage.getColors();
			OptionsFusionMap options = vanillaMapsDefinitionPage.getMapOptions();
			mapComp.setColors(colors);
			mapComp.setOptions(options);
			
			VanillaMapsWizard.this.hide();
			super.finish(mapComp, VanillaMapsWizard.this, null);
		}
	}

	@Override
	protected void onBackClick() {
		if(currentPage instanceof VanillaMapsDefinitionPage){
			setCurrentPage(vanillaMapsGeneralPage);
		}
	}

	@Override
	protected void onNextClick() {
		if(currentPage instanceof VanillaMapsGeneralPage){
			setCurrentPage(vanillaMapsDefinitionPage);
		}
	}
}
