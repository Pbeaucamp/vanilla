package bpm.workflow.ui.gef.part;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import bpm.workflow.ui.gef.model.ContainerPanelModel;
import bpm.workflow.ui.gef.model.ContainerPanelPool;
import bpm.workflow.ui.gef.model.Link;
import bpm.workflow.ui.gef.model.LoopModel;
import bpm.workflow.ui.gef.model.MacroProcessModel;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.model.Pool;

public class NodeEditPartFactory implements EditPartFactory {

	public final static Color BLUE = new Color(Display.getCurrent(), 0, 0, 255);

	public EditPart createEditPart(EditPart context, Object model) {
		AbstractGraphicalEditPart part = null;

		if(model instanceof ContainerPanelPool) {
			part = new ContentPoolEditPart();
		}
		else if(model instanceof ContainerPanelModel) {
			part = new ContentPanelEditPart();
		}
		else if(model instanceof LoopModel) {
			part = new LoopEditPart();

		}
		else if(model instanceof MacroProcessModel) {
			part = new MacroProcessEditPart();

		}
		else if(model instanceof Node) {
			part = new NodePart();
		}
		else if(model instanceof Link) {
			Link link = (Link) model;
			if(link.getColor() != null) {
				part = new LinkEditPart(link.getColor());
			}
			else {
				part = new LinkEditPart();
			}
		}
		else if(model instanceof Pool) {
			part = new PoolEditPart();
		}
		part.setModel(model);

		return part;
	}

}
