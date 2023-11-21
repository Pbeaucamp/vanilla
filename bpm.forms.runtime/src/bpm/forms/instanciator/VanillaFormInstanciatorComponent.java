package bpm.forms.instanciator;

import bpm.forms.core.design.IServiceProvider;
import bpm.forms.core.tools.IFactoryModelElement;
import bpm.forms.instanciator.launcher.InstanceLauncher;

public class VanillaFormInstanciatorComponent extends InstanceLauncher{
	public static final String PLUGIN_ID = "bpm.forms.instanciator";
	
	private IFactoryModelElement factoryModel;
	private IServiceProvider serviceProvider;
	
	
	public 	VanillaFormInstanciatorComponent(IFactoryModelElement factoryModel, IServiceProvider serviceProvider){
		this.factoryModel = factoryModel;
		this.serviceProvider = serviceProvider;
	}
	
	
	@Override
	public IFactoryModelElement getFactoryModel() throws Exception {
		if (factoryModel == null){
			throw new Exception("The IFactoryModelElement is not available");
		}
		return factoryModel;
	}
	@Override
	public IServiceProvider getServiceProvider() throws Exception {
		if (serviceProvider == null){
			throw new Exception("The IServiceProvider is not available");
		}
		return serviceProvider;
	}
	@Override
	public void configure(Object object) {
		
		
	}
	
	
	
	
}
