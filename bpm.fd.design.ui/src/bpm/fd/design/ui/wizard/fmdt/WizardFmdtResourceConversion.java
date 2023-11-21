package bpm.fd.design.ui.wizard.fmdt;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.statushandlers.StatusManager;

import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.design.ui.wizard.fmdt.FmdDataSetHelper.Options;
import bpm.metadata.resource.IResource;

public class WizardFmdtResourceConversion extends Wizard{
	private FmdtResourcePage page;
	private FmdDataSetHelper helper;
	
	
	
	public WizardFmdtResourceConversion(DataSource dataSource) {
		super();
		helper = new FmdDataSetHelper(dataSource);
	}

	@Override
	public void addPages() {
		page = new FmdtResourcePage("bpm.fd.design.ui.wizard.fmdt.resourcesPages", helper);
		page.setTitle("Fmdt Resource Conversion");
		page.setDescription("Allow to create dataSet's based on FMDT resources and an associated Filter Component");
		addPage(page);

	}
	
	@Override
	public boolean performFinish() {
		List<IResource> resources = page.getResources();
		Options opt = page.getOptions();
		
		
		IStatus res = helper.convertResources(resources, opt);
		StatusManager.getManager().handle(res, StatusManager.BLOCK | StatusManager.LOG);
		return res.getSeverity() == IStatus.OK;
	}

}
