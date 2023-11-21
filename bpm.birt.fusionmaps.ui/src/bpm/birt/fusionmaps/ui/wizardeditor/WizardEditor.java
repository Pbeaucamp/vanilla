package bpm.birt.fusionmaps.ui.wizardeditor;

import org.eclipse.birt.report.model.api.DataSetHandle;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bpm.birt.fusionmaps.core.reportitem.FusionmapsItem;

public class WizardEditor extends Wizard implements INewWizard {
	private static final String PAGE_DEFINITION = "Vanilla Map Definition";	
	private static final String PAGE_DEFINITION_DESCRIPTION = "Set informations about the vanilla map";
	
	private static final String PAGE_DATA = "Vanilla Map Data";	
	private static final String PAGE_DATA_DESCRIPTION = "Set data to the map";

	private static final String PAGE_PROPERTIES = "Vanilla Map Properties";	
	private static final String PAGE_PROPERTIES_DESCRIPTION = "A set of properties to improve the Vanilla Map";
	
	private ExtendedItemHandle handle;
	private FusionmapsItem vanillaMap;
	private DataSetHandle dataSet;
	
	private WizardPageDefinition pageDef;
	private WizardPageData pageData;
	private WizardPageOptions pageProperties;
	
	public WizardEditor(FusionmapsItem item, DataSetHandle ds, ExtendedItemHandle handle){
		super();
		this.vanillaMap = item;
		this.dataSet = ds;
		this.handle = handle;
	}
	
	@Override
	public boolean performFinish() {
		pageData.prepareMapDataXml();
		pageProperties.buildProperties();
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		//Do nothing here
	}
	
	@Override
	public void addPages() {
		pageDef = new WizardPageDefinition("Event parameters", vanillaMap);
		pageDef.setDescription(PAGE_DEFINITION_DESCRIPTION);
		pageDef.setTitle(PAGE_DEFINITION);
		addPage(pageDef);
		
		pageData = new WizardPageData("Event parameters", vanillaMap, dataSet, handle);
		pageData.setDescription(PAGE_DATA_DESCRIPTION);
		pageData.setTitle(PAGE_DATA);
		addPage(pageData);
		
		pageProperties = new WizardPageOptions("Page Properties", vanillaMap);
		pageProperties.setDescription(PAGE_PROPERTIES_DESCRIPTION);
		pageProperties.setTitle(PAGE_PROPERTIES);
		addPage(pageProperties);
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		return super.getNextPage(page);
	}
}
