package bpm.fwr.client.dialogs;

import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.utils.RichTextToolbar;
import bpm.fwr.client.widgets.BirtCommentWidget;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TextDialogBox extends AbstractDialogBox implements ICustomDialogBox {
	
	public enum TypeDialog {
		NORMAL,
		EDITABLE,
		BIRT
	}

	private static TextDialogBoxUiBinder uiBinder = GWT.create(TextDialogBoxUiBinder.class);

	interface TextDialogBoxUiBinder extends UiBinder<Widget, TextDialogBox> {
	}

	@UiField
	HTMLPanel contentPanel;

	private Button confirmBtn;
	
	private TextBox txtBox, txtBox2;
	private RichTextArea txtRichArea;

	private Widget source;

	private TypeDialog typeDial;
	
	public TextDialogBox(String title, String description, String oldText, Widget source, TypeDialog typeDial) {
		super(title, false, true);
		
		this.source = source;
		this.typeDial = typeDial;
		
		VerticalPanel mainPanel = buildContent();

		if(typeDial == TypeDialog.BIRT) {
			HTML html = new HTML("NAME :");
			mainPanel.add(html);
			txtBox = new TextBox();
			txtBox.setWidth("250px");
			txtBox.setText(oldText);
			mainPanel.add(txtBox);
	
			HTML html2 = new HTML("number of Comment (displayed) :");
			mainPanel.add(html2);
			txtBox2 = new TextBox();
			txtBox2.setWidth("250px");
			String commentDisplayed = ((BirtCommentWidget) source).getCommentDisplayed();
			txtBox2.setText(commentDisplayed);
			mainPanel.add(txtBox2);
			
			createButton(Bpm_fwr.LBLW.Cancel(), cancelHandler);
		}
		else if(typeDial == TypeDialog.EDITABLE) {
			HTML html = new HTML(description);
			mainPanel.add(html);
			txtRichArea = new RichTextArea();
			txtRichArea.setWidth("100%");
			txtRichArea.setHTML(oldText);

			RichTextToolbar toolbar = new RichTextToolbar(txtRichArea);
			toolbar.setWidth("100%");

			mainPanel.add(toolbar);
			mainPanel.add(txtRichArea);

			createButton(Bpm_fwr.LBLW.Cancel(), cancelHandler);
		}
		else {
			HTML html = new HTML(description);
			mainPanel.add(html);
			txtBox = new TextBox();
			txtBox.setWidth("250px");
			txtBox.setText(oldText);
			mainPanel.add(txtBox);
		}
		confirmBtn = createButton(Bpm_fwr.LBLW.Confirmation(), confirmHandler);

	}

	private VerticalPanel buildContent() {
		setWidget(uiBinder.createAndBindUi(this));

		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.setWidth("100%");
		
		contentPanel.add(mainPanel);
		
		return mainPanel;
	}

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {

			TextDialogBox.this.hide();

			if (typeDial == TypeDialog.EDITABLE) {
				finish(txtRichArea.getHTML(), source, null);
			}
			else if (typeDial == TypeDialog.BIRT) {
				if (!txtBox.getText().equals("")) {
					finish(txtBox.getText(), source, txtBox2.getText());
				}
			}
			else {
				finish(txtBox.getText(), source, null);
			}
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			TextDialogBox.this.hide();
		}
	};

	@Override
	public void updateBtn() {
		confirmBtn.setEnabled(true);
	}
}
