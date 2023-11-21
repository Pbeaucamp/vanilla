package metadata.client.views;

import metadataclient.Activator;

import org.eclipse.jface.action.Action;

public class MtdAction extends Action {
	
	public MtdAction(String name){
		super(name);
	}
	
	public void update(){
		Activator.getDefault().setChanged();
	}
}
