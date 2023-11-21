package bpm.birt.gmaps.ui.wizardeditor;

import org.eclipse.birt.report.model.api.DataSetHandle;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bpm.birt.gmaps.core.reportitem.GooglemapsItem;

public class WizardEditor extends Wizard implements INewWizard {
	private static final String PAGE_DEFINITION = "Vanilla GMap Definition";	
	private static final String PAGE_DEFINITION_DESCRIPTION = "Set informations about the Vanilla GMap";
	private static final String PAGE_DATA = "Vanilla GMap Data";	
	private static final String PAGE_DATA_DESCRIPTION = "Set data to the GMap";

	private ExtendedItemHandle handle;
	private GooglemapsItem vanillaGMap;
	private DataSetHandle dataSet;
	
	private WizardPageDefinition pageDef;
	private WizardPageData pageData;
	
	public WizardEditor(GooglemapsItem item, DataSetHandle ds, ExtendedItemHandle handle){
		super();
		this.vanillaGMap = item;
		this.dataSet = ds;
		this.handle = handle;
	}
	
	@Override
	public boolean performFinish() {
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
		
	}
	
	@Override
	public void addPages() {
		pageDef = new WizardPageDefinition("Event parameters", vanillaGMap);
		pageDef.setDescription(PAGE_DEFINITION_DESCRIPTION);
		pageDef.setTitle(PAGE_DEFINITION);
		addPage(pageDef);
		
		pageData = new WizardPageData("Event parameters", vanillaGMap, dataSet, handle);
		pageData.setDescription(PAGE_DATA_DESCRIPTION);
		pageData.setTitle(PAGE_DATA);
		addPage(pageData);
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		return super.getNextPage(page);
	}
}
