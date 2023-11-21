package bpm.workflow.ui.gef.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;

import bpm.workflow.runtime.model.IAcceptInput;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IOutputProvider;
import bpm.workflow.runtime.model.WorkflowException;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.activities.StartActivity;
import bpm.workflow.runtime.model.activities.StopActivity;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.gef.model.ContainerPanelModel;
import bpm.workflow.ui.gef.model.Link;
import bpm.workflow.ui.gef.model.LoopModel;
import bpm.workflow.ui.gef.model.MacroProcessModel;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.model.Pool;

public class DeleteCommand extends Command {

	private Object model;
	private Object parentModel;

	public void execute() {
		if(model instanceof Node && !(((Node) model).getWorkflowObject() instanceof StartActivity) && !(((Node) model).getWorkflowObject() instanceof StopActivity)) {

			if(parentModel instanceof ContainerPanelModel) {
				((ContainerPanelModel) this.parentModel).removeChild((Node) model);
			}
			if(parentModel instanceof LoopModel) {
				((LoopModel) this.parentModel).removeChild((Node) model);
			}
			if(parentModel instanceof MacroProcessModel) {

				((MacroProcessModel) this.parentModel).removeChild((Node) model);
			}
			else if(parentModel instanceof Pool) {
				((Pool) this.parentModel).removeChild((Node) model);
			}

			List<Link> list = new ArrayList<Link>();
			for(Link l : ((Node) model).getSourceLink()) {
				list.add(l);
			}

			for(Link l : ((Node) model).getTargetLink()) {
				list.add(l);
			}

			for(Link l : list) {
				l.disconnect();
			}

			try {
				if(((Node) model).getWorkflowObject() instanceof IActivity) {
					((WorkflowModel) Activator.getDefault().getCurrentModel()).removeActivity(((Node) model).getWorkflowObject().getId());

					if(((Node) model).getWorkflowObject() instanceof IOutputProvider) {
						IOutputProvider current = (IOutputProvider) ((Node) model).getWorkflowObject();
						String oldoutPutVar = current.getOutputVariable();

						for(IActivity a : Activator.getDefault().getCurrentInput().getWorkflowModel().getActivities().values()) {
							if(a instanceof IAcceptInput) {
								((IAcceptInput) a).removeInput(oldoutPutVar);
							}
						}
					}

				}
				if(model instanceof LoopModel) {
					for(Node node : ((LoopModel) model).getContents()) {

						List<Link> _list = new ArrayList<Link>();
						for(Link l : (node).getSourceLink()) {
							_list.add(l);
						}

						for(Link l : (node).getTargetLink()) {
							_list.add(l);
						}

						for(Link l : _list) {
							l.disconnect();
						}

						((WorkflowModel) Activator.getDefault().getCurrentModel()).removeActivity(((Node) node).getWorkflowObject().getId());
					}
				}
				if(model instanceof MacroProcessModel) {
					for(Node node : ((MacroProcessModel) model).getContents()) {
						((WorkflowModel) Activator.getDefault().getCurrentModel()).removeActivity(((Node) node).getWorkflowObject().getId());

					}
				}

			} catch(WorkflowException e) {
				e.printStackTrace();
			}
		}

	}

	public void setModel(Object model) {
		this.model = model;
	}

	public void setParentModel(Object model) {
		if(model instanceof LoopModel) {
			parentModel = (LoopModel) model;
		}
		if(model instanceof MacroProcessModel) {
			parentModel = (MacroProcessModel) model;
		}
		else if(model instanceof ContainerPanelModel) {
			parentModel = (ContainerPanelModel) model;
		}

		else if(model instanceof Pool) {
			parentModel = (Pool) model;
		}
	}

	public void undo() {}

}
