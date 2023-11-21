package bpm.es.clustering.ui.gef;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import bpm.es.clustering.ui.gef.editparts.ClientPart;
import bpm.es.clustering.ui.gef.editparts.ModulePart;
import bpm.es.clustering.ui.gef.editparts.PlatformPart;
import bpm.es.clustering.ui.gef.editparts.RepositoryPart;
import bpm.es.clustering.ui.gef.editparts.RuntimeServerPart;
import bpm.es.clustering.ui.model.VanillaPlatformModule;
import bpm.vanilla.platform.core.IVanillaServerManager;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.Server;

public class PlatformEditPartFactory implements EditPartFactory{

	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = null;
		if (model instanceof VanillaPlatformModule){
			part = new ModulePart();
		}
		else if (model instanceof Server){
			part = new ClientPart();
		}
		else if (model instanceof Repository){
			part = new RepositoryPart();
		}
		else if (model instanceof IVanillaServerManager){
			part = new RuntimeServerPart();
		}
		else if (model instanceof GefModel){
			part = new PlatformPart();
		}
				
		if (part != null){
			part.setModel(model);
		}
		return part;
	}

}
