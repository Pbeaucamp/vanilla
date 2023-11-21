package bpm.gwt.commons.client.viewer.aklabox;

import bpm.document.management.core.model.Tree;
import bpm.gwt.commons.client.images.CommonImages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AklaboxFolderWidget extends Composite {

	private static AklaboxFolderWidgetUiBinder uiBinder = GWT.create(AklaboxFolderWidgetUiBinder.class);

	interface AklaboxFolderWidgetUiBinder extends UiBinder<Widget, AklaboxFolderWidget> {
	}
	
	interface MyStyle extends CssResource {
		String imgObject();
	}

	@UiField
	Image imgObject;

	@UiField
	Label lblObject;
	
	@UiField
	MyStyle style;

	private Tree aklaboxFolder;

	public AklaboxFolderWidget(Tree aklaboxFolder) {
		initWidget(uiBinder.createAndBindUi(this));
		this.aklaboxFolder = aklaboxFolder;

		imgObject.setResource(CommonImages.INSTANCE.folder());
		imgObject.addStyleName(style.imgObject());

		lblObject.setText(aklaboxFolder.getName());
	}

	public Tree getAklaboxFolder() {
		return aklaboxFolder;
	}

}
