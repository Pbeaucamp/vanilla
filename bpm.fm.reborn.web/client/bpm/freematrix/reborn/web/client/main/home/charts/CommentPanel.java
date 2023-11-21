package bpm.freematrix.reborn.web.client.main.home.charts;

import java.util.Date;

import bpm.fm.api.model.Comment;
import bpm.fm.api.model.CommentAlert;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class CommentPanel extends Composite {

	private static CommentPanelUiBinder uiBinder = GWT.create(CommentPanelUiBinder.class);

	interface CommentPanelUiBinder extends UiBinder<Widget, CommentPanel> {
	}
	
	@UiField
	HTML htmlCom, htmlDateGroup;
	
	public CommentPanel(Comment comment) {
		this(comment.getUser().getName(), comment.getComment(), comment.getValueDate());
	}

	public CommentPanel(CommentAlert com) {
		this(com.getUser().getName(), com.getComment(), com.getDate());
	}
	
	public CommentPanel(String user, String comment, Date date) {
		initWidget(uiBinder.createAndBindUi(this));

		DateTimeFormat sdf = DateTimeFormat.getFormat("yyyy-MM-dd");
		
		htmlCom.setHTML(comment);
		htmlDateGroup.setHTML(user + " - " + sdf.format(date));
		
		this.getElement().getStyle().setMarginLeft(20, Unit.PX);
	}

}
