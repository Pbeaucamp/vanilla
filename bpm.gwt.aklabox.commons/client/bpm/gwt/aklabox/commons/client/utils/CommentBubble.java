package bpm.gwt.aklabox.commons.client.utils;

import bpm.document.management.core.model.Comments;
import bpm.gwt.aklabox.commons.client.images.CommonImages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CommentBubble extends Composite {

	private static CommentBubbleUiBinder uiBinder = GWT.create(CommentBubbleUiBinder.class);

	interface CommentBubbleUiBinder extends UiBinder<Widget, CommentBubble> {
	}
	
	interface MyStyle extends CssResource {
		String panelBig();
		String panelSmall();
		String hidden();
	}

	@UiField MyStyle style;
	@UiField
	Image imgComment;
	@UiField
	Label lblComment;

	//private Comments comment;

	public CommentBubble(Comments comment) {
		initWidget(uiBinder.createAndBindUi(this));

		//this.comment = comment;
		
		
		lblComment.addStyleName(style.hidden());
		this.getElement().getStyle().setProperty("top", "calc("+ comment.getY() +"% + 1000px)");//Top(comment.getY()+1000, Unit.PCT);
		this.getElement().getStyle().setLeft(comment.getX(), Unit.PCT);
		
		imgComment.setResource(CommonImages.INSTANCE.ic_comment48());
		lblComment.setText(comment.getMessage());
		this.addStyleName(style.panelSmall());
		imgComment.addMouseOverHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				lblComment.removeStyleName(style.hidden());
				imgComment.addStyleName(style.hidden());
				CommentBubble.this.removeStyleName(style.panelSmall());
				CommentBubble.this.addStyleName(style.panelBig());
			}
		});
		lblComment.addMouseOutHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				lblComment.addStyleName(style.hidden());
				imgComment.removeStyleName(style.hidden());
				CommentBubble.this.removeStyleName(style.panelBig());
				CommentBubble.this.addStyleName(style.panelSmall());
			}
		});
	}

	@Override
	public void onLoad(){
		this.getElement().getParentElement().getStyle().setMarginTop(-1000, Unit.PX);
	}
}