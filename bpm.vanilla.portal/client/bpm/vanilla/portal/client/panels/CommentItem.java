package bpm.vanilla.portal.client.panels;

import bpm.vanilla.platform.core.repository.Comment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CommentItem extends Composite {

	private static CommentItemUiBinder uiBinder = GWT.create(CommentItemUiBinder.class);

	interface CommentItemUiBinder extends UiBinder<Widget, CommentItem> {
	}
	
	interface MyStyle extends CssResource {
		
	}

	
	@UiField
	Label lblName, lblDate, lblText;
	
	@UiField
	MyStyle style;
	
//	private ForumItem parent;
//	private Comment comment;
	
	public CommentItem(ForumItem parent, Comment comment, int index) {
		initWidget(uiBinder.createAndBindUi(this));
		
//		this.parent = parent;
//		this.comment = comment;
		DateTimeFormat fmt = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM);
		lblDate.setText(fmt.format(comment.getCreationDate()));
		lblName.setText(comment.getUser().getName());
		lblText.setText(comment.getComment());
		
		this.getElement().getStyle().setMarginLeft(index*50, Unit.PX);
		
		if(comment.getChilds() != null){
			for(Comment child: comment.getChilds()){
				parent.addCommentItem(new CommentItem(parent, child, index+1));
			}
		}
		
	}
	
}
