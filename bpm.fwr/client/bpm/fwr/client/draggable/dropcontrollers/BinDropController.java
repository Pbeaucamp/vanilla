package bpm.fwr.client.draggable.dropcontrollers;

import bpm.fwr.client.images.WysiwygImage;
import bpm.fwr.client.widgets.BinWidget;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.user.client.ui.Widget;

public class BinDropController extends SimpleDropController {

	private BinWidget bin;
	private boolean isSmall;
	
	public BinDropController(BinWidget bin, boolean isSmall) {
		super(bin);
		this.bin = bin;
		this.isSmall = isSmall;
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
		if(isSmall){
			bin.setResource(WysiwygImage.INSTANCE.full_bin64());
		}
		else {
			bin.setResource(WysiwygImage.INSTANCE.full_bin());
		}
	}
	
	@Override
	public void onLeave(DragContext context) {
		if(isSmall){
			bin.setResource(WysiwygImage.INSTANCE.empty_bin64());
		}
		else {
			bin.setResource(WysiwygImage.INSTANCE.empty_bin());
		}
	    super.onLeave(context);
	}
}
