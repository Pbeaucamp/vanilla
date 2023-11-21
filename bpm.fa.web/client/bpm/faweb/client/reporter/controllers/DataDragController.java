package bpm.faweb.client.reporter.controllers;

import com.google.gwt.user.client.ui.AbsolutePanel;

import bpm.faweb.client.gwt.dnd.client.PickupDragController;

public class DataDragController extends PickupDragController{

	public DataDragController(AbsolutePanel boundaryPanel, boolean allowDroppingOnBoundaryPanel) {
		super(boundaryPanel, allowDroppingOnBoundaryPanel);
	    this.setBehaviorMultipleSelection(true);
	    this.setBehaviorDragProxy(true);
	    this.setBehaviorDragStartSensitivity(1);
	}

}
