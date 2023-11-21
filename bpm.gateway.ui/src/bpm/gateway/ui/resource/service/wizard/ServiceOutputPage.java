package bpm.gateway.ui.resource.service.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import bpm.gateway.ui.composites.service.ServiceViewerComposite;
import bpm.gateway.ui.composites.service.ServiceViewerComposite.TypeViewer;
import bpm.vanilla.platform.core.beans.service.IService;
import bpm.vanilla.platform.core.beans.service.ServiceOutputData;
import bpm.vanilla.platform.core.beans.service.ServiceTransformationDefinition;

public class ServiceOutputPage extends WizardPage {
	
	private ServiceTransformationDefinition service;
	private ServiceViewerComposite serviceViewer;
	private ServiceInputPage inputPage;

	protected ServiceOutputPage(String pageName, ServiceTransformationDefinition service, ServiceInputPage inputPage) {
		super(pageName);
		this.service = service;
		this.inputPage = inputPage;
	}

	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(1, false));
		mainComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		serviceViewer = new ServiceViewerComposite(mainComposite, SWT.NONE, TypeViewer.TYPE_VANILLA_OUTPUT, inputPage);
		serviceViewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		setControl(mainComposite);
		setPageComplete(true);

		if(service != null && service.getOutputs() != null){
			setOutputs(service.getOutputs());
		}
	}

	@Override
	public boolean isPageComplete() {
		return true;
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	public void updateWizardButtons() {
		getContainer().updateButtons();
	}

	public List<ServiceOutputData> getOutputs() {
		List<IService> outputs = serviceViewer.getInput();
		List<ServiceOutputData> data = new ArrayList<ServiceOutputData>();
		if(outputs != null){
			for(IService serv : outputs){
				if(serv instanceof ServiceOutputData){
					data.add((ServiceOutputData) serv);
				}
			}
		}
		return data;
	}

	public void setOutputs(List<ServiceOutputData> inputs) {
		List<IService> data = new ArrayList<IService>();
		if(inputs != null){
			for(IService serv : inputs){
				data.add(serv);
			}
		}
		serviceViewer.setInput(data);
	}
}
