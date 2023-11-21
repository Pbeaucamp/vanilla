package bpm.fm.designer.web.client.utils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class QueryPanel extends Composite {

	private static QueryPanelUiBinder uiBinder = GWT.create(QueryPanelUiBinder.class);

	interface QueryPanelUiBinder extends UiBinder<Widget, QueryPanel> {
	}
	
	@UiField
	TextArea txtQuery;
	
	@UiField
	CheckBox chkQuery;
	
	@UiField
	CaptionPanel captionQuery;

	private Object reference;

	public QueryPanel(String title, String checkBox, String query, Object reference) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.reference = reference;
		
		captionQuery.setCaptionHTML(title);
		
		chkQuery.setText(checkBox);
		
		txtQuery.setText(query);
		
	}

	public String getQuery() {
		return txtQuery.getText();
	}
	
	public boolean execute() {
		return chkQuery.getValue();
	}
	
	public Object getReference() {
		return reference;
	}
}
