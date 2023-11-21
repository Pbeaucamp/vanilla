package bpm.birt.osm.ui.wizard;

import org.eclipse.birt.report.model.api.DataSetHandle;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bpm.birt.osm.core.reportitem.OsmReportItem;
import bpm.vanilla.map.core.design.MapVanilla;

public class WizardOsmMap extends Wizard implements INewWizard {

	private OsmReportItem item;
	private DataSetHandle dataSet;
	private ExtendedItemHandle handle;
	
	private OsmDefinitionPage definitionPage;
	private OsmSeriePage seriePage;

	public WizardOsmMap(OsmReportItem item, DataSetHandle dataSet, ExtendedItemHandle handle) {
		super();
		this.item = item;
		this.dataSet = dataSet;
		this.handle = handle;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

	@Override
	public boolean performFinish() {
		try {
			definitionPage.fillItem();
		} catch (SemanticException e) {
			e.printStackTrace();
		}
		try {
			item.setSerieList(seriePage.fillItem());
		} catch (SemanticException e) {
			e.printStackTrace();
		}
		
		
		return true;
	}
	
	@Override
	public void addPages() {
		definitionPage = new OsmDefinitionPage("Definition", item, dataSet, handle);
		addPage(definitionPage);
		
		seriePage = new OsmSeriePage("Definition", item, handle);
		addPage(seriePage);
	}

	public MapVanilla getSelectedMap() {
		return definitionPage.getSelectedMap();
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if(page == definitionPage) {
			seriePage.fillPage();
		}
		return super.getNextPage(page);
	}
}
