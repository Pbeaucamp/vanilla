package bpm.faweb.client.dnd;

import com.google.gwt.user.client.ui.AbsolutePanel;

import bpm.faweb.client.gwt.dnd.client.PickupDragController;

public class BinDragController extends PickupDragController{

	public BinDragController(AbsolutePanel boundaryPanel, boolean allowDroppingOnBoundaryPanel) {
		super(boundaryPanel, allowDroppingOnBoundaryPanel);
	    this.setBehaviorMultipleSelection(false);
	    this.setBehaviorDragProxy(true);
	    this.setBehaviorDragStartSensitivity(1);
	}

}
