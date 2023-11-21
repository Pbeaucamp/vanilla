package bpm.fd.web.client.panels.properties ;

import java.util.List;

import bpm.fd.core.IComponentOption;
import bpm.fd.core.component.ImageComponent;
import bpm.fd.web.client.ClientSession;
import bpm.fd.web.client.I18N.Labels;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.dialog.UploadDialog;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.vanilla.platform.core.beans.VanillaImage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class ImageOptionsProperties extends CompositeProperties<IComponentOption> {

	private static ImageOptionsPropertiesUiBinder uiBinder = GWT.create(ImageOptionsPropertiesUiBinder.class);

	interface ImageOptionsPropertiesUiBinder extends UiBinder<Widget, ImageOptionsProperties> {
	}
	
	@UiField
	HTMLPanel mainPanel;
	
	@UiField
	ListBoxWithButton lstImages;
	
	private IWait waitPanel;
	private List<VanillaImage> images;
	
	public ImageOptionsProperties(IWait waitPanel, ImageComponent imageComp) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		this.images = ClientSession.getInstance().getImages();
		
		loadImages(images);
		
		if (imageComp != null) {
			buildContent(imageComp);
		}
	}
	
	private void loadImages(List<VanillaImage> images) {
		this.images = images;
		
		lstImages.clear();
		if (images != null) {
			for (VanillaImage image : images) {
				lstImages.addItem(image.getName(), String.valueOf(image.getId()));
			}
		}
	}

	private void buildContent(ImageComponent imageComp) {
		if (imageComp.getUrl() != null && !imageComp.getUrl().isEmpty() && images != null) {
			int index = 0;
			for (VanillaImage image : images) {
				if (image.getUrl().equals(imageComp.getUrl())) {
					break;
				}
				index++;
			}
			
			lstImages.setSelectedIndex(index);
		}
	}
	
	@UiHandler("lstImages")
	public void onAddClick(ClickEvent event) {
		UploadDialog dial = new UploadDialog(Labels.lblCnst.UploadImage());
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				refreshImages();
			}
		});
	}

	private void refreshImages() {
		waitPanel.showWaitPart(true);
		
		CommonService.Connect.getInstance().getImages(new GwtCallbackWrapper<List<VanillaImage>>(waitPanel, true) {
			@Override
			public void onSuccess(List<VanillaImage> result) {
				ClientSession.getInstance().setImages(result);
				loadImages(result);
			}
		}.getAsyncCallback());
	}

	@Override
	public void buildProperties(IComponentOption component) {
		VanillaImage image = getSelectedImage();
		((ImageComponent) component).setUrl(image != null ? image.getUrl() : "");
	}
	
	private VanillaImage getSelectedImage() {
		int imageId = Integer.parseInt(lstImages.getSelectedItem());
		for (VanillaImage image : images) {
			if (image.getId() == imageId) {
				return image;
			}
		}
		return null;
	}
}
