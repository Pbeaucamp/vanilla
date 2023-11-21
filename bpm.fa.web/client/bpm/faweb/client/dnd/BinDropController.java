package bpm.faweb.client.dnd;

import bpm.faweb.client.gwt.dnd.client.DragContext;
import bpm.faweb.client.gwt.dnd.client.drop.SimpleDropController;
import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.client.panels.center.HasFilter;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class BinDropController extends SimpleDropController {
	
	private HasFilter viewerParent;
	private Image bin;
	private boolean isBig;
	
	public BinDropController(HasFilter viewerParent, Image bin, boolean isBig) {
		super(bin);
		this.viewerParent = viewerParent;
		this.bin = bin;
		this.isBig = isBig;
	}

	@Override
	public void onDrop(DragContext context) {
		for (Widget widget : context.selectedWidgets) {
			viewerParent.trashFilter(widget);
		}
		super.onDrop(context);
	}
	
	@Override
	public void onEnter(DragContext context) {
		super.onEnter(context);
		if(isBig){
			bin.setResource(FaWebImage.INSTANCE.full_bin());
		}
		else {
			bin.setResource(FaWebImage.INSTANCE.full_bin64());
		}
	}
	
	@Override
	public void onLeave(DragContext context) {
		if(isBig){
			bin.setResource(FaWebImage.INSTANCE.empty_bin());
		}
		else {
			bin.setResource(FaWebImage.INSTANCE.empty_bin64());
		}
	    super.onLeave(context);
	}
}
