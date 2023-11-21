package bpm.faweb.client.reporter.controllers;

import bpm.faweb.client.gwt.dnd.client.DragContext;
import bpm.faweb.client.gwt.dnd.client.drop.SimpleDropController;
import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.client.reporter.widgets.BinWidget;

import com.google.gwt.user.client.ui.Widget;

public class BinDropController extends SimpleDropController {

	private BinWidget bin;
	
	public BinDropController(BinWidget bin) {
		super(bin);
		this.bin = bin;
	}

	@Override
	public void onDrop(DragContext context) {
		for (Widget widget : context.selectedWidgets) {
			bin.manageWidget(widget);
		}
		super.onDrop(context);
	}
	
	@Override
	public void onEnter(DragContext context) {
		super.onEnter(context);
		bin.setResource(FaWebImage.INSTANCE.full_bin64());
	}
	
	@Override
	public void onLeave(DragContext context) {
		bin.setResource(FaWebImage.INSTANCE.empty_bin64());
	    super.onLeave(context);
	}
}
