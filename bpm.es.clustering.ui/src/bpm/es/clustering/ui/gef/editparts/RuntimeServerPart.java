package bpm.es.clustering.ui.gef.editparts;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import bpm.es.clustering.ui.gef.figures.RuntimeServerFigure;
import bpm.vanilla.platform.core.IVanillaServerManager;
import bpm.vanilla.platform.core.beans.tasks.ServerType;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.ReportingComponent;

public class RuntimeServerPart extends AbstractGraphicalEditPart {

	@Override
	protected IFigure createFigure() {
		IVanillaServerManager model = (IVanillaServerManager)getModel();
		if(model instanceof ReportingComponent) {
			Figure f = new RuntimeServerFigure(ServerType.REPORTING.name());
			return f;
		}
		else if(model instanceof GatewayComponent) {
			Figure f = new RuntimeServerFigure(ServerType.GATEWAY.name());
			return f;
		}
		else {
			return null;
		}
	}

	@Override
	protected void createEditPolicies() { }

}
