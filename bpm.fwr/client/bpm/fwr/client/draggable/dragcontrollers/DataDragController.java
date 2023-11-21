package bpm.fwr.client.draggable.dragcontrollers;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class DataDragController extends PickupDragController{

	public DataDragController(AbsolutePanel boundaryPanel, boolean allowDroppingOnBoundaryPanel) {
		super(boundaryPanel, allowDroppingOnBoundaryPanel);
	    this.setBehaviorMultipleSelection(true);
	    this.setBehaviorDragProxy(true);
	    this.setBehaviorDragStartSensitivity(1);
	}

}
