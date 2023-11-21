package bpm.mdm.ui;

import bpm.mdm.ui.handlers.CommitModelHandler;
import bpm.mdm.ui.model.composites.ModelMasterDetails;

public class ModelControler {
	private CommitModelHandler dirtyModelHandler;
	private ModelMasterDetails masterDetails;
	
	public void setModelMasterDetails(ModelMasterDetails masterDetails){
		this.masterDetails = masterDetails;
	}
	
	public void setModelHandler(CommitModelHandler dirtyModelHandler){
		this.dirtyModelHandler = dirtyModelHandler;
	}
	
	public boolean isDirty(){
		return dirtyModelHandler.isEnabled();
	}
	
	public void setModelDirty(){
		
		dirtyModelHandler.setEnabled(true);

	}

	public void setModelReloaded() {
		dirtyModelHandler.setEnabled(false);
		if (masterDetails != null){
			masterDetails.refresh();
		}
		
	}
}
