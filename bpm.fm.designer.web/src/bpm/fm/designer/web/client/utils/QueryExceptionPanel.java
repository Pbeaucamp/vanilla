package bpm.fm.designer.web.client.utils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class QueryExceptionPanel extends Composite {

	private static QueryExceptionPanelUiBinder uiBinder = GWT.create(QueryExceptionPanelUiBinder.class);

	interface QueryExceptionPanelUiBinder extends UiBinder<Widget, QueryExceptionPanel> {
	}
	
	@UiField
	HTML lblQuery;
	
	@UiField
	TextArea lblException;

	public QueryExceptionPanel(String query, Exception exception) {
		initWidget(uiBinder.createAndBindUi(this));
		String result = query.replaceAll("(\r\n|\n)", "<br />");
		result = result.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
		lblQuery.setHTML(result);
		lblException.setText(exception.getMessage());
		
	}

}
