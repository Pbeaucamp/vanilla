package bpm.fwr.api.beans;

import java.io.Serializable;

import bpm.fwr.api.beans.components.ImageComponent;
import bpm.fwr.api.beans.components.TextHTMLComponent;

public class WysiwygReportHeader implements Serializable {
	private TextHTMLComponent lblTopLeft;
	private TextHTMLComponent lblTopRight;
	
	private ImageComponent imagesTopLeft;
	private ImageComponent imagesTopRight;
	
	public WysiwygReportHeader() { }


	public TextHTMLComponent getLblTopLeft() {
		return lblTopLeft;
	}

	public void setLblTopLeft(TextHTMLComponent lblTopLeft) {
		this.lblTopLeft = lblTopLeft;
	}

	public TextHTMLComponent getLblTopRight() {
		return lblTopRight;
	}

	public void setLblTopRight(TextHTMLComponent lblTopRight) {
		this.lblTopRight = lblTopRight;
	}


	public ImageComponent getImagesTopLeft() {
		return imagesTopLeft;
	}


	public void setImagesTopLeft(ImageComponent imagesTopLeft) {
		this.imagesTopLeft = imagesTopLeft;
	}


	public ImageComponent getImagesTopRight() {
		return imagesTopRight;
	}


	public void setImagesTopRight(ImageComponent imagesTopRight) {
		this.imagesTopRight = imagesTopRight;
	}
}
