package bpm.gateway.ui.resource.service.wizard;

import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import bpm.gateway.core.transformations.webservice.WebServiceVanillaInput;
import bpm.gateway.ui.i18n.Messages;
import bpm.vanilla.platform.core.beans.service.ServiceInputData;
import bpm.vanilla.platform.core.beans.service.ServiceOutputData;
import bpm.vanilla.platform.core.beans.service.ServiceTransformationDefinition;

public class ServiceDefinitionWizard extends Wizard {

	protected static final String GENERAL_PAGE_NAME = "Service Definition"; //$NON-NLS-1$
	protected static final String INPUT_PAGE_NAME = "Service Input Definition"; //$NON-NLS-1$
	protected static final String OUTPUT_PAGE_NAME = "Service Output Definition"; //$NON-NLS-1$

	private WebServiceVanillaInput transfo;
	private ServiceTransformationDefinition service;
	
	private ServiceDefinitionPage definitionPage;
	private ServiceInputPage inputPage;
	private ServiceOutputPage outputPage;

	public ServiceDefinitionWizard(WebServiceVanillaInput transfo, ServiceTransformationDefinition service) {
		this.transfo = transfo;
		this.service = service;
	}

	@Override
	public boolean performFinish() {
		if(definitionPage.isPageComplete() && inputPage.isPageComplete() && outputPage.isPageComplete()){
			String name = definitionPage.getServiceName();
			String xmlRoot = definitionPage.getXmlRoot();
			String xmlRow = definitionPage.getXmlRow();
			
			List<ServiceInputData> inputs = inputPage.getInputs();
			
			List<ServiceOutputData> outputs = outputPage.getOutputs();
			
			boolean isNew = false;
			if(service == null){
				service = new ServiceTransformationDefinition();
				isNew = true;
			}
			service.setName(name);
			service.setInputs(inputs);
			service.setOutputs(outputs);
			service.setXmlRoot(xmlRoot);
			service.setXmlRow(xmlRow);
			
			try {
				if(isNew){
					transfo.getDocument().getWebServiceVanillaHelper().saveServiceDefinition(service);
				}
				else {
					transfo.getDocument().getWebServiceVanillaHelper().updateService(service);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public void addPages() {
		definitionPage = new ServiceDefinitionPage(GENERAL_PAGE_NAME, service);
		definitionPage.setDescription(Messages.ServiceDefinitionWizard_0);
		definitionPage.setTitle(Messages.ServiceDefinitionWizard_1);
		addPage(definitionPage);
		
		inputPage = new ServiceInputPage(INPUT_PAGE_NAME, service);
		inputPage.setDescription(Messages.ServiceDefinitionWizard_2);
		inputPage.setTitle(Messages.ServiceDefinitionWizard_3);
		addPage(inputPage);
		
		outputPage = new ServiceOutputPage(OUTPUT_PAGE_NAME, service, inputPage);
		outputPage.setDescription(Messages.ServiceDefinitionWizard_4);
		outputPage.setTitle(Messages.ServiceDefinitionWizard_5);
		addPage(outputPage);
	}
}
