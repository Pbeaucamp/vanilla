package bpm.gateway.ui.gef.commands;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;

import bpm.gateway.core.Comment;
import bpm.gateway.ui.gef.model.ContainerPanelModel;
import bpm.gateway.ui.gef.model.GIDModel;
import bpm.gateway.ui.gef.model.Link;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.model.NodeLinkerHelper;

public class DeleteCommand extends Command {

	private Object model;
	private ContainerPanelModel parentModel;
	private GIDModel gidModel;
	
	public void execute(){
		if (model instanceof Node){
			if (parentModel != null){
				this.parentModel.removeChild((Node)model);
			}
			if (gidModel != null){
				this.gidModel.removeChild((Node)model);
			}
			
			
			List<Link> list = new ArrayList<Link>();
			for(Link l : ((Node)model).getSourceLink()){
				list.add(l);
			}
			
			for(Link l : ((Node)model).getTargetLink()){
				list.add(l);
			}
			
			for(Link l : list){
				l.disconnect();
				NodeLinkerHelper.remove(l);
			}
		}
		else{
			this.parentModel.removeAnnote((Comment)model);
		}
		
	}
	
	public void setModel(Object model){
		this.model = model;
	}
	
	public void setParentModel(Object model){
		if (model instanceof ContainerPanelModel){
			parentModel = (ContainerPanelModel)model;
		}
		else if (model instanceof GIDModel){
			gidModel = (GIDModel)model;
		}
		
	}
	
	public void undo(){
		
		if (model instanceof Node){
			if (parentModel != null){
				this.parentModel.addChild((Node)model);
			}
			if (gidModel != null){
				this.gidModel.addChild((Node)model);
			}
			
			
			List<Link> list = new ArrayList<Link>();
			for(Link l : ((Node)model).getSourceLink()){
				list.add(l);
			}
			
			for(Link l : ((Node)model).getTargetLink()){
				list.add(l);
			}
			
			for(Link l : list){
				try {
					l.reconnect();
					NodeLinkerHelper.add(l);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
		}
		else{
			this.parentModel.addAnnote((Comment)model);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	@Override
	public void redo() {
		execute();
	}
	
}
