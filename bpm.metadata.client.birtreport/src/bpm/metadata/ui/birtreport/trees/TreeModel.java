package bpm.metadata.ui.birtreport.trees;

import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;

public class TreeModel extends TreeParent {

	private IBusinessModel model;
	
	public TreeModel(IBusinessModel model, String groupName) {
		super(model.getName());
		this.model = model;
		buildChilds(groupName);
	}
	
	public IBusinessModel getModel(){
		return model;
	}
	
	@Override
	public String toString(){
		return model.getName();
	}
	
	private void buildChilds(String groupName){
		
		TreeParent rp = new TreeParent("Business Packages"); //$NON-NLS-1$
		addChild(rp);
		for(IBusinessPackage p : model.getBusinessPackages(groupName)){
			rp.addChild(new TreePackage(p, groupName));
		}
	}
	
	public void refresh(String groupName){
		removeAll();
		buildChilds(groupName);
	}

}
