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
import bpm.vanilla.platform.core.beans.service.ServiceInputData;
import bpm.vanilla.platform.core.beans.service.ServiceTransformationDefinition;

public class ServiceInputPage extends WizardPage {
	
	private ServiceTransformationDefinition service;
	private ServiceViewerComposite serviceViewer;

	protected ServiceInputPage(String pageName, ServiceTransformationDefinition service) {
		super(pageName);
		this.service = service;
	}

	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(1, false));
		mainComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		serviceViewer = new ServiceViewerComposite(mainComposite, SWT.NONE, TypeViewer.TYPE_VANILLA_INPUT, null);
		serviceViewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		setControl(mainComposite);
		setPageComplete(true);

		if(service != null && service.getInputs() != null){
			setInputs(service.getInputs());
		}
	}

	@Override
	public boolean isPageComplete() {
		return true;
	}

	@Override
	public boolean canFlipToNextPage() {
		return true;
	}

	public void updateWizardButtons() {
		getContainer().updateButtons();
	}
	
	public List<IService> getIServices(){
		return serviceViewer.getInput();
	}

	public List<ServiceInputData> getInputs() {
		List<IService> inputs = serviceViewer.getInput();
		List<ServiceInputData> data = new ArrayList<ServiceInputData>();
		if(inputs != null){
			for(IService serv : inputs){
				if(serv instanceof ServiceInputData){
					data.add((ServiceInputData) serv);
				}
			}
		}
		return data;
	}

	public void setInputs(List<ServiceInputData> inputs) {
		List<IService> data = new ArrayList<IService>();
		if(inputs != null){
			for(IService serv : inputs){
				data.add(serv);
			}
		}
		serviceViewer.setInput(data);
	}
}
