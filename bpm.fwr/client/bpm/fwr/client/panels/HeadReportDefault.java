package bpm.fwr.client.panels;

import bpm.fwr.api.beans.components.ImageComponent;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.widgets.ImageWidget;
import bpm.fwr.client.widgets.LabelWidget;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class HeadReportDefault extends AbsolutePanel{
	
	private static final String CSS_LBL_TOP = "textArea";
	private static final String CSS_LBL_TITLE = "title";
	private static final String CSS_LBL_SUBTITLE = "subtitle";
//	private static final String DEFAULT_HEIGHT = "defaultHeight";
	
	private ImageWidget imgLeft;
	private ImageWidget imgRight;
	
	private LabelWidget lblTopLeft;
	private LabelWidget lblTopRight;
	private LabelWidget lblTitle;
	private LabelWidget lblSubtitle;
	
	public HeadReportDefault(ReportPanel reportPanelParent, int width) {
		HorizontalPanel topPanelImg = new HorizontalPanel();
		topPanelImg.setWidth("100%");
		this.add(topPanelImg);
		
		imgLeft = new ImageWidget(reportPanelParent, width);
		imgRight = new ImageWidget(reportPanelParent, width);
		
		topPanelImg.add(imgLeft);
		topPanelImg.add(imgRight);
		topPanelImg.setCellHorizontalAlignment(imgRight, HasHorizontalAlignment.ALIGN_RIGHT);
		
		HorizontalPanel topPanelLabel = new HorizontalPanel();
		topPanelLabel.setWidth("100%");
		this.add(topPanelLabel);
		
		lblTopLeft = new LabelWidget(reportPanelParent, 0, Bpm_fwr.LBLW.LabelTopLeft());
		lblTopLeft.setWidth("95%");
		lblTopLeft.addStyleName(CSS_LBL_TOP);
//		lblTopLeft.addDoubleClickHandler(labelDoubleClickHandler);
		
		lblTopRight = new LabelWidget(reportPanelParent, 0, Bpm_fwr.LBLW.LabelTopRight());
		lblTopRight.setWidth("95%");
		lblTopRight.addStyleName(CSS_LBL_TOP);
//		lblTopRight.addDoubleClickHandler(labelDoubleClickHandler);

		topPanelLabel.add(lblTopLeft);
		topPanelLabel.setCellWidth(lblTopLeft, "50%");
		topPanelLabel.add(lblTopRight);
		topPanelLabel.setCellWidth(lblTopRight, "50%");
		topPanelLabel.setCellHorizontalAlignment(lblTopRight, HasHorizontalAlignment.ALIGN_RIGHT);
		
		
		lblTitle = new LabelWidget(reportPanelParent, 0, "<div style=\"text-align: center;\"><font size=\"4\">" + Bpm_fwr.LBLW.LabelTitle() 
				+ "</font></div>");
		lblTitle.setWidth("100%");
		lblTitle.addStyleName(CSS_LBL_TITLE);
//		lblTitle.addDoubleClickHandler(labelDoubleClickHandler);
		this.add(lblTitle);

		lblSubtitle = new LabelWidget(reportPanelParent, 0, Bpm_fwr.LBLW.LabelSubtitle());
		lblSubtitle.setWidth("100%");
		lblSubtitle.addStyleName(CSS_LBL_SUBTITLE);
//		lblSubtitle.addDoubleClickHandler(labelDoubleClickHandler);
		this.add(lblSubtitle);
	}
	
//	private DoubleClickHandler labelDoubleClickHandler = new DoubleClickHandler() {
//		
//		@Override
//		public void onDoubleClick(DoubleClickEvent event) {
//			TextDialogBox dial = null;
//			if(event.getSource().equals(lblTopLeft)){
//				dial = new TextDialogBox(Bpm_fwr.LBLW.LabelTopLeft(), Bpm_fwr.LBLW.TextFill(), 
//						lblTopLeft.getHTML(), lblTopLeft, true);
//				dial.addFinishListener(finishListener);
//			}
//			else if(event.getSource().equals(lblTopRight)){
//				dial = new TextDialogBox(Bpm_fwr.LBLW.LabelTopRight(), Bpm_fwr.LBLW.TextFill(), 
//						lblTopRight.getHTML(), lblTopRight, true);
//				dial.addFinishListener(finishListener);
//			}
//			else if(event.getSource().equals(lblTitle)){
//				dial = new TextDialogBox(Bpm_fwr.LBLW.LabelTitle(), Bpm_fwr.LBLW.TextFillTitle(), 
//						lblTitle.getHTML(), lblTitle, true);
//				dial.addFinishListener(finishListener);
//			}
//			else if(event.getSource().equals(lblSubtitle)){
//				dial = new TextDialogBox(Bpm_fwr.LBLW.LabelSubtitle(), Bpm_fwr.LBLW.TextFillSubtitle(), 
//						lblSubtitle.getHTML(), lblSubtitle, true);
//				dial.addFinishListener(finishListener);
//			}
//			
//			if(dial != null){
//				dial.center();
//			}
//		}
//	};
	
//	private FinishListener finishListener = new FinishListener() {
//
//		@Override
//		public void onFinish(Object result, Object source) {
//			if(source.equals(lblTopLeft)){
//				String txt = (String)result;
//				if(txt.isEmpty()){
//					lblTopLeft.addStyleName(DEFAULT_HEIGHT);
//				}
//				else {
//					lblTopLeft.removeStyleName(DEFAULT_HEIGHT);
//				}
//				lblTopLeft.setHTML(txt);
//			}
//			else if(source.equals(lblTopRight)){
//				String txt = (String)result;
//				if(txt.isEmpty()){
//					lblTopRight.addStyleName(DEFAULT_HEIGHT);
//				}
//				else {
//					lblTopRight.removeStyleName(DEFAULT_HEIGHT);
//				}
//				lblTopRight.setHTML(txt);
//			}
//			else if(source.equals(lblTitle)){
//				String txt = (String)result;
//				if(txt.isEmpty()){
//					lblTitle.addStyleName(DEFAULT_HEIGHT);
//				}
//				else {
//					lblTitle.removeStyleName(DEFAULT_HEIGHT);
//				}
//				lblTitle.setHTML(txt);
//			}
//			else if(source.equals(lblSubtitle)){
//				String txt = (String)result;
//				if(txt.isEmpty()){
//					lblSubtitle.addStyleName(DEFAULT_HEIGHT);
//				}
//				else {
//					lblSubtitle.removeStyleName(DEFAULT_HEIGHT);
//				}
//				lblSubtitle.setHTML(txt);
//			}
//		}
//	};
	
	public String getLblTopLeft(){
		return lblTopLeft.getText();
	}
	
	public void setLblTopLeft(String html){
		this.lblTopLeft.setText(html);
	}
	
	public String getLblTopRight(){
		return lblTopRight.getText();
	}
	
	public void setLblTopRight(String html){
		this.lblTopRight.setText(html);
	}
	
	public String getImgTopLeft(){
		return imgLeft.getUrlImg();
	}
	
	public void setImgTopLeft(ImageComponent img){
		this.imgLeft.setUrlImg(img.getUrl());
	}
	
	public String getImgTopRight(){
		return imgRight.getUrlImg();
	}
	
	public void setImgTopRight(ImageComponent img){
		this.imgRight.setUrlImg(img.getUrl());
	}
	
	public String getTitle(){
		return lblTitle.getText();
	}
	
	public void setLblTitle(String title){
		this.lblTitle.setText(title);
	}
	
	public String getSubtitle(){
		return lblSubtitle.getText();
	}
	
	public void setLblSubtitle(String subtitle){
		this.lblSubtitle.setText(subtitle);
	}
}
