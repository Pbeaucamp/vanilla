package bpm.gwt.aklabox.commons.client.viewers;

import java.util.List;

import bpm.document.management.core.model.DocPages;
import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.User;
import bpm.document.management.core.utils.AklaboxConstant;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.aklabox.commons.client.utils.DefaultResultDialog;
import bpm.gwt.aklabox.commons.client.utils.PathHelper;
import bpm.gwt.aklabox.commons.client.utils.WaitDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.kiouri.sliderbar.client.event.BarValueChangedEvent;
import com.kiouri.sliderbar.client.event.BarValueChangedHandler;

public class VideoThumbCreator extends CompositeWaitPanel {

	private static VideoThumbCreatorUiBinder uiBinder = GWT.create(VideoThumbCreatorUiBinder.class);

	interface VideoThumbCreatorUiBinder extends UiBinder<Widget, VideoThumbCreator> {
	}

	@UiField
	HTMLPanel thumbCreatorPanel, sliderPanel, thumbsPanel, directInput, sliderInput;
	@UiField
	Image imgPreview;
	@UiField
	Button btnGenerate;
	@UiField
	TextBox txtDirectInput;

	private Documents doc;
	private GWTSlider gSlider;
	private int sec = 10;
	private int versionNumber;
	private PopupPanel p;
	private Composite parent;
	private User user;

	public VideoThumbCreator(Composite parent, Documents doc, int versionNumber, User user) {
		initWidget(uiBinder.createAndBindUi(this));
		this.doc = doc;
		this.parent = parent;
		this.versionNumber = versionNumber;
		this.user = user;
		onInitializeWidget();
	}

	private void onInitializeWidget() {
		txtDirectInput.getElement().setAttribute(AklaboxConstant.PLACE_HOLDER, LabelsConstants.lblCnst.InSeconds());
		directInput.setVisible(false);
		sliderInput.setVisible(true);
		gSlider = new GWTSlider(9, "500px");
		gSlider.drawMarks("#40b9eb", 9);
		gSlider.addBarValueChangedHandler(new BarValueChangedHandler() {

			@Override
			public void onBarValueChanged(BarValueChangedEvent event) {
				VideoThumbCreator.this.sec = (event.getValue() + 1) * 10;
			}
		});
		sliderPanel.add(gSlider);
		thumbCreatorPanel.removeFromParent();
		imgPreview.setUrl(PathHelper.getRightPath(doc.getThumbImage()));
	}

	@UiHandler("btnOpenThumbCreator")
	void onOpenThumbCreator(ClickEvent e) {
		p = new PopupPanel();
		p.add(thumbCreatorPanel);
		p.setAutoHideEnabled(true);
		p.setGlassEnabled(true);
		p.center();
		p.setPopupPosition(650, 100);

		p.show();
	}

	@UiHandler("btnGenerate")
	void onGenerateThumbnail(ClickEvent e) {
		if (directInput.isVisible()) {
			if (!txtDirectInput.getText().isEmpty()) {
				try {
					sec = Integer.parseInt(txtDirectInput.getText());
					onGenerateVideoThumb();
				} catch (NumberFormatException ex) {
					new DefaultResultDialog(LabelsConstants.lblCnst.PleaseEnterAValidInterval(), "failure").show();
				}
			}
			else {
				new DefaultResultDialog(LabelsConstants.lblCnst.PleaseEnterAValidInterval(), "failure").show();
			}

		}
		else {
			onGenerateVideoThumb();
		}

	}

	private void onGenerateVideoThumb() {
		WaitDialog.showWaitPart(true);
		AklaCommonService.Connect.getService().generateVideoThumb(versionNumber, sec, doc, new GwtCallbackWrapper<Void>(this, true, true, true) {

			@Override
			public void onSuccess(Void result) {
				thumbsPanel.clear();
				onDisplayThumbnails();
			}

		}.getAsyncCallback());
	}

	public void onDisplayThumbnails() {
		AklaCommonService.Connect.getService().getPages(doc.getId(), versionNumber, new GwtCallbackWrapper<List<DocPages>>(this, true, true, true) {


			@Override
			public void onSuccess(List<DocPages> result) {
				if (result.isEmpty() || result == null) {
				}
				else {
					for (DocPages p : result) {
						thumbsPanel.add(new VideoThumbItem(VideoThumbCreator.this, p));
					}
				}

			}
		}.getAsyncCallback());
	}

	@UiHandler("btnPick")
	void onPickThumb(ClickEvent e) {
		doc.setThumbImage(PathHelper.getRightPath(imgPreview.getUrl()));
		AklaCommonService.Connect.getService().updateDocStatus(doc, new GwtCallbackWrapper<Void>(this, true, true, true) {

			@Override
			public void onSuccess(Void result) {
				//parent.closeParent(); //TODO ???
			}

		}.getAsyncCallback());
		p.hide();
	}

	@UiHandler("btnSliderInput")
	void onSliderInput(ClickEvent e) {
		sliderInput.setVisible(true);
		directInput.setVisible(false);
	}

	@UiHandler("btnDirectInput")
	void onDirectInput(ClickEvent e) {
		directInput.setVisible(true);
		sliderInput.setVisible(false);
	}
}
