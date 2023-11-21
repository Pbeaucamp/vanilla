package bpm.workflow.ui.views.property;

import org.eclipse.ui.views.properties.tabbed.AbstractTypeMapper;

import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.Transition;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.WorkflowObject;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.gef.model.Link;
import bpm.workflow.ui.gef.model.LoopModel;
import bpm.workflow.ui.gef.model.MacroProcessModel;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.LinkEditPart;
import bpm.workflow.ui.gef.part.LoopEditPart;
import bpm.workflow.ui.gef.part.MacroProcessEditPart;
import bpm.workflow.ui.gef.part.NodePart;


/**
 * Mapper for the type of graphical parts
 * @author CHARBONNIER, MARTIN
 *
 */
public class NodeTypeMapper extends AbstractTypeMapper {
	public Class mapType(Object object) {
		if (object instanceof NodePart) {
			WorkflowObject o = ((Node)((NodePart) object).getModel()).getWorkflowObject();
			return o.getClass();
        }
		else if (object instanceof LoopEditPart) {
			WorkflowObject o = ((LoopModel)((LoopEditPart) object).getModel()).getWorkflowObject();
			return o.getClass();
		}
		else if (object instanceof MacroProcessEditPart) {
			WorkflowObject o = ((MacroProcessModel)((MacroProcessEditPart) object).getModel()).getWorkflowObject();
			return o.getClass();
		}
		else if (object instanceof LinkEditPart) {
			Link l = ((Link)((LinkEditPart) object).getModel());
			
			try {
				Transition t = ((WorkflowModel) Activator.getDefault().getCurrentModel()).getTransition((IActivity) l.getSource().getWorkflowObject(), (IActivity) l.getTarget().getWorkflowObject());
				return t.getClass();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
        return super.mapType(object);
	}
}
