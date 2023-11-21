package bpm.fm.designer.web.client.dialog;

import java.util.HashMap;

import bpm.fm.designer.web.client.Messages;
import bpm.fm.designer.web.client.utils.QueryExceptionPanel;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class QueryExceptionDialog extends AbstractDialogBox {

	private static QueryExceptionDialogUiBinder uiBinder = GWT.create(QueryExceptionDialogUiBinder.class);

	interface QueryExceptionDialogUiBinder extends UiBinder<Widget, QueryExceptionDialog> {
	}

	@UiField
	HTMLPanel contentPanel;
	
	public QueryExceptionDialog(HashMap<String, Exception> exceptions) {
		super(Messages.lbl.queryErrorExec(), true, true);
		setWidget(uiBinder.createAndBindUi(this));
		
		createButton(Messages.lbl.Ok(), okHandler);
		
		for(String query : exceptions.keySet()) {
			contentPanel.add(new QueryExceptionPanel(query, exceptions.get(query)));
		}
		
	}
	
	private ClickHandler okHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			QueryExceptionDialog.this.hide();
		}
	};

}
