package bpm.faweb.client.dnd;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.gwt.dnd.client.PickupDragController;
import bpm.faweb.client.gwt.dnd.client.drop.DropController;

import com.google.gwt.user.client.ui.AbsolutePanel;

public class FaWebDragController extends PickupDragController {

	private MainPanel mainPanel;
	
	private List<DropController> dropCtrls = new ArrayList<DropController>();
	private List<DropController> multAddCtrls = new ArrayList<DropController>();
	private List<DropController> chartDropCtrls = new ArrayList<DropController>();
	
	private List<DropController> allDropControllers = new ArrayList<DropController>();
	
	public FaWebDragController(MainPanel mainPanel, AbsolutePanel boundaryPanel, boolean allowDroppingOnBoundaryPanel) {
		super(boundaryPanel, allowDroppingOnBoundaryPanel);
		this.mainPanel = mainPanel;
		
		setBehaviorDragStartSensitivity(1);
		setBehaviorMultipleSelection(true);
		setBehaviorDragProxy(true);
	}
	
	public void removeAllDropControllers() {
		for(DropController ctrl : dropCtrls) {
			this.unregisterDropController(ctrl);
		}
		dropCtrls.clear();
	}

	public void removeAllChartDropCtrls() {
		for(DropController ctrl : chartDropCtrls) {
			this.unregisterDropController(ctrl);
		}
		chartDropCtrls.clear();
	}
	
	@Override
	public void registerDropController(DropController dropController) {
		if(dropController instanceof GridItemDropController) {
			dropCtrls.add(dropController);
		}
		if(dropController instanceof MultipleAddDropController) {
			multAddCtrls.add(dropController);
		}
		if(dropController instanceof ChartListDropController) {
			chartDropCtrls.add(dropController);
		}
		
		if(allDropControllers != null) {
			allDropControllers.add(dropController);
		}
		
		super.registerDropController(dropController);
	}

	@Override
	public void dragStart() {
		if(!mainPanel.getDisplayPanel().isMultipleAddVisible()) {
			for(DropController ctrl : multAddCtrls) {
				this.unregisterDropController(ctrl);
			}
			multAddCtrls.clear();
		}
		
		
		try {
			super.dragStart();
		} catch (Exception e) {
			List<Integer> toRm = new ArrayList<Integer>();
			int i = 0;
			for(DropController d : allDropControllers) {
				if(!d.getDropTarget().isAttached()) {
					this.unregisterDropController(d);
					toRm.add(i);
				}
				i++;
			}
			
			for(Integer rm : toRm) {
				allDropControllers.remove(rm);
			}
			super.dragStart();
		}
	}
}
