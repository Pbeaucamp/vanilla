package bpm.smart.web.client.panels;

import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.smart.web.client.images.Images;
import bpm.smart.web.client.panels.resources.WorkspacePanel;
import bpm.smart.web.client.services.SmartAirService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class Vignette extends CompositeWaitPanel {

	private static VignetteUiBinder uiBinder = GWT.create(VignetteUiBinder.class);

	interface VignetteUiBinder extends UiBinder<Widget, Vignette> {
	}
	
	public enum TypeVignette {
		R, MARKDOWN, LOG, HTML, PDF, WORD, GENCODE;
	}
	
	interface MyStyle extends CssResource {
		String clrR();
		String clrMarkdown();
		String clrLog();
		String clrHTML();
		String clrPDF();
		String clrWord();
		String clrGenCode();
		String selected();
		String contentFix();
	}

	@UiField
	Label lblTitle;
	
	@UiField
	HTMLPanel vignette;
	
	@UiField
	SimplePanel gomete;
	
	@UiField
	Image btnClose, imgType, thumb;

	@UiField
	MyStyle style;
	
	private WorkspacePanel parent;
	private TypeVignette type;
	private String title;
	private boolean locked;
	private byte[] thumbImg;
	private Widget widget;
	private boolean isNew = false;
	
	public Vignette(WorkspacePanel parent, TypeVignette type, String title, String html, Widget widget, boolean locked) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.type = type;
		this.title = title;
		this.widget = widget;
		this.locked = locked;
		
		setNew(false);
		
		initUI();
		
		vignette.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				onClickVignette();
			}
		}, ClickEvent.getType());
		
		setScreenCapture(html);
	}

	private void initUI() {
		lblTitle.setText(title);
		if(thumbImg != null)
		thumb.setUrl("data:image/png;base64," + new String(thumbImg));
		
		switch (type) {
		case R:
			setStyleUI(style.clrR());
			setLocked(locked);
			imgType.setResource(Images.INSTANCE.ic_social_R());
			break;
		case MARKDOWN:
			setStyleUI(style.clrMarkdown());
			setLocked(locked);
			imgType.setResource(Images.INSTANCE.ic_social_markdown());
			break;
		case LOG:
			setStyleUI(style.clrLog());
			setLocked(locked);
			imgType.setVisible(false);
			vignette.addStyleName(style.contentFix());
			break;
		case HTML:
			setStyleUI(style.clrHTML());
			setLocked(locked);
			imgType.setResource(Images.INSTANCE.ic_social_html2());
			break;
		case PDF:
			setStyleUI(style.clrPDF());
			setLocked(locked);
			imgType.setResource(Images.INSTANCE.ic_social_pdf());
			break;
		case WORD:
			setStyleUI(style.clrWord());
			setLocked(locked);
			imgType.setResource(Images.INSTANCE.ic_social_word());
			break;
		case GENCODE:
			setStyleUI(style.clrGenCode());
			setLocked(locked);
			imgType.setVisible(false);
			vignette.addStyleName(style.contentFix());
			break;
		}
		
	}

	private void setLocked(boolean locked) {
		this.locked = locked;
		btnClose.setVisible(!locked);
		
	}

	private void setStyleUI(String style) {
		btnClose.addStyleName(style);
		lblTitle.addStyleName(style);
		
	}

	@UiHandler("btnClose")
	public void onCancelClick(ClickEvent event) {
		parent.onCloseVignette(Vignette.this);
		Vignette.this.removeFromParent();
	}

	public TypeVignette getType() {
		return type;
	}

	public void setType(TypeVignette type) {
		this.type = type;
	}

	public Widget getWidget() {
		return widget;
	}

	public void setWidget(Widget widget) {
		this.widget = widget;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		this.lblTitle.setText(title);
	}

	public boolean isLocked() {
		return locked;
	}

	public void onClickVignette(){
		parent.showVignette(this);
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
		if(isNew){
			gomete.setVisible(true);
		} else {
			gomete.setVisible(false);
		}
		
	}

	public byte[] getThumbImg() {
		return thumbImg;
	}

	public void setThumbImg(byte[] thumbImg) {
		this.thumbImg = thumbImg;
		if(thumbImg != null)
			thumb.setUrl("data:image/png;base64," + new String(thumbImg));
	}
	
	public void setSelected(boolean selection) {
		if(selection){
			vignette.addStyleName(style.selected());
		} else {
			vignette.removeStyleName(style.selected());
		}
	}
	
	public void setScreenCapture(String html) {
		showWaitPart(true);
		String typeString;
		switch(type){
		case MARKDOWN:
		case R:
		case LOG:
		case HTML:
			typeString = CommonConstants.FORMAT_HTML;
			break;
		case PDF:
			typeString = CommonConstants.FORMAT_PDF;
			break;
		case WORD:
			typeString = CommonConstants.FORMAT_DOCX;
			break;
		default:
			typeString = CommonConstants.FORMAT_HTML;
			break;
		}
		SmartAirService.Connect.getInstance().getScreenCapture(html, typeString, new GwtCallbackWrapper<byte[]>(this, true){
			@Override
			public void onSuccess(byte[] result) {
				//showWaitPart(false);
				setThumbImg(result);
			}
		}.getAsyncCallback());
	}
}
