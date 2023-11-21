package bpm.fa.api.olap.unitedolap;

import bpm.united.olap.api.communication.IModelService;
import bpm.united.olap.api.communication.IRuntimeService;

public class UnitedOlapServiceProvider {

	private static UnitedOlapServiceProvider instance;
	private IRuntimeService runtimeService;
	private IModelService modelService;
	
	private UnitedOlapServiceProvider(){};
	
	public static UnitedOlapServiceProvider getInstance() {
		if(instance == null) {
			instance = new UnitedOlapServiceProvider();
		}
		return instance;
	}
	
	public void init(IRuntimeService runtimeService, IModelService modelService) {
		this.runtimeService = runtimeService;
		this.modelService = modelService;
	}
	
	public IRuntimeService getRuntimeService() throws Exception {
		if(runtimeService == null) {
			throw new Exception("United olap runtime service has not been inited");
		}
		return runtimeService;
	}
	
	public IModelService getModelService() throws Exception {
		if(modelService == null) {
			throw new Exception("United olap model service has not been inited");
		}
		return modelService;
	}
	
	public boolean isInited() {
		if(modelService != null && runtimeService != null) {
			return true;
		}
		return false;
	}
}
