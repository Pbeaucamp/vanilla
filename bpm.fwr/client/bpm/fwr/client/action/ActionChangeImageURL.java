package bpm.fwr.client.action;

import bpm.fwr.client.widgets.ImageWidget;

public class ActionChangeImageURL extends Action{

	private ImageWidget image;
	private String oldUrl;
	private String newUrl;
	
	public ActionChangeImageURL(ActionType type, ImageWidget image, String oldUrl, String newUrl) {
		super(type);
		this.image = image;
		this.oldUrl = oldUrl;
		this.newUrl = newUrl;
	}

	@Override
	public void executeRedo() {
		image.setUrlImg(newUrl, oldUrl, ActionType.REDO);
	}

	@Override
	public void executeUndo() {
		image.setUrlImg(oldUrl, "",ActionType.UNDO);
	}
}
