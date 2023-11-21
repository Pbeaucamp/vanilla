package bpm.es.clustering.ui.gef.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import bpm.es.clustering.ui.Activator;
import bpm.es.clustering.ui.icons.Icons;
import bpm.vanilla.platform.core.beans.Server;
import bpm.vanilla.platform.core.components.VanillaComponentType;

public class ClientFigure extends Figure{
	private static final Font FONT = null;
	private Label clientName;
//	private ImageFigure picture;

	public ClientFigure(String type, String label){
		this.setLayoutManager(new GridLayout());
		Image im = null;
		if (type != null){
			if (type.equals(VanillaComponentType.COMPONENT_FREEANALYSISWEB)){
				im = Activator.getDefault().getImageRegistry().get(Icons.FAWEB);
			}
			else if (type.equals(VanillaComponentType.COMPONENT_FREEWEBREPORT)){
				im = Activator.getDefault().getImageRegistry().get(Icons.FWR);
			}
			else if (type.equals(VanillaComponentType.COMPONENT_FREEMETRICS)){
				im = Activator.getDefault().getImageRegistry().get(Icons.FM);
			}
			else if (type.equals(VanillaComponentType.COMPONENT_REPORTING)){
				im = Activator.getDefault().getImageRegistry().get(Icons.REPORT);
			}
			else if (type.equals(VanillaComponentType.COMPONENT_UNITEDOLAP)){
				
			}
			else if (type.equals(VanillaComponentType.COMPONENT_FREEDASHBOARD)){
				
			}
			else if (type.equals(VanillaComponentType.COMPONENT_WORKFLOW)){
				
			}
			else if (type.equals(VanillaComponentType.COMPONENT_GATEWAY)){
				im = Activator.getDefault().getImageRegistry().get(Icons.BIG);
			}
			else if (type.equals(VanillaComponentType.COMPONENT_GED)){
				im = Activator.getDefault().getImageRegistry().get(Icons.GED);
			}
//			else if (type.equals(VanillaComponentType.COMPONENT_)){
//				im = Activator.getDefault().getImageRegistry().get(Icons.SCHED);
//			}
		}
		
		
//		switch(type){
//		case Server.TYPE_FAWEB:
//			im = Activator.getDefault().getImageRegistry().get(Icons.FAWEB);
//			break;
//		case Server.TYPE_FWR:
//			im = Activator.getDefault().getImageRegistry().get(Icons.REPORT);
//			break;
//		case Server.TYPE_FMWEB:
//			im = Activator.getDefault().getImageRegistry().get(Icons.FM);
//			break;
//		case Server.TYPE_ORBEON:
//			im = Activator.getDefault().getImageRegistry().get(Icons.ORBEON);
//			break;
//		case Server.TYPE_BIRT:
//			im = Activator.getDefault().getImageRegistry().get(Icons.BIRT);
//			break;
//		}

		clientName = new Label(label, im);
		clientName.setFont(FONT);
		add(clientName, new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		setMinimumSize(new Dimension(-1, 100));
	}


	
}
