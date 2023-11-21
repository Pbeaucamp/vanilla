package bpm.fwr.client.draggable.dragcontrollers;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class PaletteDragController extends PickupDragController{

	public PaletteDragController(AbsolutePanel boundaryPanel, boolean allowDroppingOnBoundaryPanel) {
		super(boundaryPanel, allowDroppingOnBoundaryPanel);
	    this.setBehaviorMultipleSelection(false);
	    this.setBehaviorDragProxy(true);
	    this.setBehaviorDragStartSensitivity(1);
	}

}
