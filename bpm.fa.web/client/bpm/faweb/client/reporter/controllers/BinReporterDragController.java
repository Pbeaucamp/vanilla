package bpm.faweb.client.reporter.controllers;

import com.google.gwt.user.client.ui.AbsolutePanel;

import bpm.faweb.client.gwt.dnd.client.PickupDragController;

public class BinReporterDragController extends PickupDragController{

	public BinReporterDragController(AbsolutePanel boundaryPanel, boolean allowDroppingOnBoundaryPanel) {
		super(boundaryPanel, allowDroppingOnBoundaryPanel);
	    this.setBehaviorMultipleSelection(false);
	    this.setBehaviorDragProxy(false);
	    this.setBehaviorDragStartSensitivity(1);
	}

}
