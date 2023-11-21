package bpm.birt.fusioncharts.ui.wizardeditor;

import org.eclipse.birt.report.model.api.DataSetHandle;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bpm.birt.fusioncharts.core.reportitem.FusionchartsItem;

public class WizardEditor extends Wizard implements INewWizard {
	private static final String PAGE_DEFINITION = "Vanilla Chart Definition";	
	private static final String PAGE_DEFINITION_DESCRIPTION = "Set informations about the vanilla chart";
	
	private static final String PAGE_DATA = "Vanilla Chart Data";	
	private static final String PAGE_DATA_DESCRIPTION = "Set data to the chart";
	
	private static final String PAGE_PROPERTIES = "Vanilla Chart Properties";	
	private static final String PAGE_PROPERTIES_DESCRIPTION = "A set of properties to improve the Vanilla Chart";
	
	private ExtendedItemHandle handle;
	private FusionchartsItem vanillaChart;
	private DataSetHandle dataSet;
	
	private WizardPageDefinition pageDef;
	private WizardPageData pageData;
	private WizardPageOptions pageOptions;
	
	public WizardEditor(FusionchartsItem item, DataSetHandle ds, ExtendedItemHandle handle){
		super();
		this.vanillaChart = item;
		this.dataSet = ds;
		this.handle = handle;
	}
	
	@Override
	public boolean performFinish() {
		pageData.prepareChartDataXml();
		pageOptions.buildProperties();
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) { }
	
	@Override
	public void addPages() {
		pageDef = new WizardPageDefinition("Event parameters", vanillaChart);
		pageDef.setDescription(PAGE_DEFINITION_DESCRIPTION);
		pageDef.setTitle(PAGE_DEFINITION);
		addPage(pageDef);
		
		pageData = new WizardPageData("Event parameters", vanillaChart, dataSet, handle);
		pageData.setDescription(PAGE_DATA_DESCRIPTION);
		pageData.setTitle(PAGE_DATA);
		addPage(pageData);
		
		pageOptions = new WizardPageOptions("Event parameters", vanillaChart);
		pageOptions.setDescription(PAGE_PROPERTIES_DESCRIPTION);
		pageOptions.setTitle(PAGE_PROPERTIES);
		addPage(pageOptions);
	}
}
