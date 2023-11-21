package bpm.fwr.client.widgets;

import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.action.ActionChangeImageURL;
import bpm.fwr.client.action.ActionType;
import bpm.fwr.client.dialogs.TextDialogBox;
import bpm.fwr.client.dialogs.TextDialogBox.TypeDialog;
import bpm.fwr.client.images.WysiwygImage;
import bpm.fwr.client.panels.ReportPanel;
import bpm.fwr.client.panels.ReportSheet;
import bpm.fwr.client.utils.WidgetType;
import bpm.gwt.commons.client.listeners.FinishListener;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;

public class ImageWidget extends ReportWidget implements HasMouseDownHandlers{
	private static final String URL_IMAGE_DEFAULT = "images/cancel.png";
	
	private FinishListener gridFinishListener;
	
	private ReportSheet reportSheetParent;
	private ReportPanel reportPanelParent;
	private GridWidget gridWidget;
	private Image img;
	private String urlImg;
	
	private boolean changeUrl = false;
	
	public ImageWidget(ReportSheet reportSheetParent, int width, FinishListener gridFinishListener, GridWidget gridWidget) {
		super(reportSheetParent, WidgetType.IMAGE, width);
		this.urlImg = URL_IMAGE_DEFAULT;
		this.reportSheetParent = reportSheetParent;
		this.gridFinishListener = gridFinishListener;
		this.gridWidget = gridWidget;
		
		img = new Image(WysiwygImage.INSTANCE.FWR_Image_WaitFor());
		img.addStyleName("imgTop");
		img.addDoubleClickHandler(doubleClickHandler);
		this.add(img);
	}
	
	public ImageWidget(ReportPanel reportPanelParent, int width) {
		super(null, WidgetType.IMAGE, width);
		this.urlImg = URL_IMAGE_DEFAULT;
		this.reportPanelParent = reportPanelParent;
		
		img = new Image(WysiwygImage.INSTANCE.FWR_Image_WaitFor());
		img.addStyleName("imgTop");
		img.addDoubleClickHandler(doubleClickHandler);
		this.add(img);
	}
	
	public void refreshImg(){
		img.setUrl(urlImg);
	}
	
	public void refreshImgWithDefault(){
		img.setResource(WysiwygImage.INSTANCE.FWR_Image_WaitFor());
	}

	public void setUrlImg(String urlImg) {
		this.urlImg = urlImg;
		if(!urlImg.isEmpty()){
			refreshImg();
		}
		else {
			refreshImgWithDefault();
		}
	}

	public void setUrlImg(String urlImg, String newUrl, ActionType type) {
		this.urlImg = urlImg;
		
		if(!urlImg.isEmpty()){
			refreshImg();
		}
		else {
			refreshImgWithDefault();
		}
		
		if(type == ActionType.REDO){
			ActionChangeImageURL action = new ActionChangeImageURL(ActionType.CHANGE_IMG_URL, this, newUrl, urlImg);
			if(reportSheetParent != null){
				reportSheetParent.getPanelParent().replaceLastActionToUndo(action);
			}
			else {
				reportPanelParent.replaceLastActionToUndo(action);
			}
		}
	}

	public String getUrlImg() {
		if(urlImg.equals(URL_IMAGE_DEFAULT))
			return "";
		else
			return urlImg;
	}
	
	public void showDialogUrl(boolean changeUrl){
		this.changeUrl = changeUrl;
		
		String imgUrl = getUrlImg();
		
		TextDialogBox dial = new TextDialogBox(Bpm_fwr.LBLW.Image(), Bpm_fwr.LBLW.ImageUrl(), imgUrl, null, TypeDialog.NORMAL);
		dial.addFinishListener(finishListener);
		dial.addFinishListener(gridFinishListener);
		dial.center();
	}

	private DoubleClickHandler doubleClickHandler = new DoubleClickHandler() {
		
		@Override
		public void onDoubleClick(DoubleClickEvent event) {
			showDialogUrl(true);
		}
	};

	private FinishListener finishListener = new FinishListener() {
		
		@Override
		public void onFinish(Object result, Object source, String result1) {
			if(result instanceof String){
				if(((String)result).isEmpty()){
					result = URL_IMAGE_DEFAULT;
				}
				
				if(changeUrl){
					ActionChangeImageURL action = new ActionChangeImageURL(ActionType.CHANGE_IMG_URL, ImageWidget.this, 
							getUrlImg(), (String)result);
					if(reportSheetParent != null){
						reportSheetParent.getPanelParent().addActionToUndoAndClearRedo(action);
					}
					else {
						reportPanelParent.addActionToUndoAndClearRedo(action);
					}
				}
				
				if(result.equals(URL_IMAGE_DEFAULT)){
					img.setResource(WysiwygImage.INSTANCE.FWR_Image_WaitFor());
					setUrlImg("");
				}
				else {
					setUrlImg((String)result);
				}
			}
		}
	};

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}

	@Override
	public int getWidgetHeight() {
		return getOffsetHeight();
	}

	@Override
	public void widgetToTrash(Object widget) {
		if(gridWidget != null){
			gridWidget.widgetToTrash(widget);
		}
		else {
			reportSheetParent.widgetToTrash(widget);
		}
	}
}
