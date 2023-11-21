package bpm.gwt.aklabox.commons.client.viewers;

import bpm.document.management.core.model.DocPages;
import bpm.gwt.aklabox.commons.client.utils.PathHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class VideoThumbItem extends Composite {

	private static VideoThumbItemUiBinder uiBinder = GWT.create(VideoThumbItemUiBinder.class);

	interface VideoThumbItemUiBinder extends UiBinder<Widget, VideoThumbItem> {
	}

	@UiField
	Image imgThumb;
	private VideoThumbCreator videoThumbCreator;
	private DocPages p;

	public VideoThumbItem(VideoThumbCreator videoThumbCreator, DocPages p) {
		initWidget(uiBinder.createAndBindUi(this));
		this.videoThumbCreator = videoThumbCreator;
		this.p = p;
		imgThumb.setUrl(PathHelper.getRightPath(p.getImagePath()));
	}

	public VideoThumbItem(DocPages p) {
		initWidget(uiBinder.createAndBindUi(this));
		this.p = p;
		imgThumb.setWidth("80px;");
		imgThumb.setUrl(PathHelper.getRightPath(p.getImagePath()));
	}

	@UiHandler("imgThumb")
	void onSelectItem(ClickEvent e) {
		videoThumbCreator.imgPreview.setUrl(PathHelper.getRightPath(p.getImagePath()));
	}
}
