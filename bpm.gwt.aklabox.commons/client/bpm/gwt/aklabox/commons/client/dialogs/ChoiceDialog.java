package bpm.gwt.aklabox.commons.client.dialogs;

import java.util.Map;

import bpm.gwt.aklabox.commons.client.utils.ChildDialogComposite;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class ChoiceDialog extends ChildDialogComposite {

	private static ChoiceDialogUiBinder uiBinder = GWT.create(ChoiceDialogUiBinder.class);

	interface ChoiceDialogUiBinder extends UiBinder<Widget, ChoiceDialog> {
	}

	interface MyStyle extends CssResource{
		String col4();
		String col3();
		String col6();
	}
	
	@UiField
	Image imgChoice1, imgChoice2, imgChoice3, imgChoice4;
	@UiField
	Label lblChoice1, lblChoice2, lblChoice3, lblChoice4, lblTitle;
	@UiField
	HTMLPanel main, panelChoice1, panelChoice2, panelChoice3, panelChoice4, panelOption;
	@UiField
	ListBox lstOption;
	@UiField
	MyStyle style;
	
	public ChoiceDialog(){
		initWidget(uiBinder.createAndBindUi(this));
		panelChoice1.addStyleName(style.col6());
		panelChoice2.addStyleName(style.col6());
		panelChoice3.setVisible(false);
		panelChoice4.setVisible(false);
		panelOption.setVisible(false);
	}

	public ChoiceDialog(ImageResource image1, String lbl1, ClickHandler handler1, 
			ImageResource image2, String lbl2, ClickHandler handler2, String question) {
		
		initWidget(uiBinder.createAndBindUi(this));
		imgChoice1.setResource(image1);
		imgChoice1.setTitle(lbl1);
		imgChoice1.addClickHandler(handler1);
		
		lblChoice1.setText(lbl1);
		
		imgChoice2.setResource(image2);
		imgChoice2.setTitle(lbl2);
		imgChoice2.addClickHandler(handler2);
		
		lblChoice2.setText(lbl2);
		
		lblTitle.setText(question);
		
		panelChoice1.addStyleName(style.col6());
		panelChoice2.addStyleName(style.col6());
		panelChoice3.setVisible(false);
		panelOption.setVisible(false);
	}
	
	public ChoiceDialog(ImageResource image1, String lbl1, ClickHandler handler1, 
			ImageResource image2, String lbl2, ClickHandler handler2, 
			ImageResource image3, String lbl3, ClickHandler handler3, 
			String question) {
		
		initWidget(uiBinder.createAndBindUi(this));
		imgChoice1.setResource(image1);
		imgChoice1.setTitle(lbl1);
		imgChoice1.addClickHandler(handler1);
		
		lblChoice1.setText(lbl1);
		
		imgChoice2.setResource(image2);
		imgChoice2.setTitle(lbl2);
		imgChoice2.addClickHandler(handler2);
		
		lblChoice2.setText(lbl2);
		
		imgChoice3.setResource(image3);
		imgChoice3.setTitle(lbl3);
		imgChoice3.addClickHandler(handler3);
		
		lblChoice3.setText(lbl3);
		
		lblTitle.setText(question);
		
		panelChoice1.addStyleName(style.col4());
		panelChoice2.addStyleName(style.col4());
		panelChoice3.addStyleName(style.col4());
		
		panelOption.setVisible(false);
	}
	
	public ChoiceDialog(String title, Map<String, String> options){
		initWidget(uiBinder.createAndBindUi(this));
		panelChoice1.addStyleName(style.col6());
		panelChoice2.addStyleName(style.col6());
		panelChoice1.setVisible(false);
		panelChoice2.setVisible(false);
		panelChoice3.setVisible(false);
		panelOption.setVisible(true);
		lblTitle.setText(title);
		main.setHeight("auto");
		lstOption.clear();
		for(String opt : options.keySet()){
			lstOption.addItem(opt, options.get(opt));
		}
	}
	
	public HTMLPanel getContent(){
		return main;
	}

	public void setImgChoice1(ImageResource img1) {
		this.imgChoice1.setResource(img1);
	}

	public void setImgChoice2(ImageResource img2) {
		this.imgChoice2.setResource(img2);
	}
	
	public void setImgChoice3(ImageResource img3) {
		this.imgChoice3.setResource(img3);
		panelChoice3.setVisible(true);
		panelChoice1.addStyleName(style.col4());
		panelChoice2.addStyleName(style.col4());
		panelChoice3.addStyleName(style.col4());
	}

	public void setLblChoice1(String lbl1) {
		imgChoice1.setTitle(lbl1);
		lblChoice1.setText(lbl1);
	}

	public void setLblChoice2(String lbl2) {
		imgChoice2.setTitle(lbl2);
		lblChoice2.setText(lbl2);
	}
	
	public void setLblChoice3(String lbl3) {
		imgChoice3.setTitle(lbl3);
		lblChoice3.setText(lbl3);
	}

	public void setLblTitle(String lblTitle) {
		this.lblTitle.setText(lblTitle);
	}
	
	public void setHandler1(ClickHandler handler1) {
		imgChoice1.addClickHandler(handler1);
	}

	public void setHandler2(ClickHandler handler2) {
		imgChoice2.addClickHandler(handler2);
	}
	
	public void setHandler3(ClickHandler handler3) {
		imgChoice3.addClickHandler(handler3);
	}
	
	public void setOption(String lblOption, Map<String, String> options){
		main.setHeight("200px");
		panelOption.setVisible(true);
		lstOption.clear();
		lstOption.addItem(lblOption, "");
		for(String opt : options.keySet()){
			lstOption.addItem(opt, options.get(opt));
		}
	}
	
	public String getOptionValue(boolean hasEmptyRow){
		if(hasEmptyRow && lstOption.getSelectedIndex() == 0) {
			return null;
		} else {
			return lstOption.getValue(lstOption.getSelectedIndex());
		}
	}
	public void setImgChoice4(ImageResource img) {
		this.imgChoice4.setResource(img);
		panelChoice4.setVisible(true);
		panelChoice1.addStyleName(style.col3());
		panelChoice2.addStyleName(style.col3());
		panelChoice3.addStyleName(style.col3());
		panelChoice4.addStyleName(style.col3());
	}

	public void setLblChoice4(String label) {
		imgChoice4.setTitle(label);
		lblChoice4.setText(label);
	}

	public void setHandler4(ClickHandler clickHandler) {
		imgChoice4.addClickHandler(clickHandler);
	}
}
