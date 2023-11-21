package bpm.fwr.client.dialogs;

import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.widgets.GridOptions;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class TableOptionDialogBox extends AbstractDialogBox implements ICustomDialogBox {

	private static TableOptionDialogBoxUiBinder uiBinder = GWT.create(TableOptionDialogBoxUiBinder.class);

	interface TableOptionDialogBoxUiBinder extends UiBinder<Widget, TableOptionDialogBox> {
	}

	@UiField
	HTMLPanel contentPanel;

	private Button confirmBtn;
	
	private TableOptionDialogBoxPanel optionPanel;
	private GridOptions options = new GridOptions();

	public TableOptionDialogBox() {
		super(Bpm_fwr.LBLW.TableOptions(), false, true);
		
		setWidget(uiBinder.createAndBindUi(this));

		createButton(Bpm_fwr.LBLW.Cancel(), cancelHandler);
		confirmBtn = createButton(Bpm_fwr.LBLW.Ok(), confirmHandler);

		optionPanel = new TableOptionDialogBoxPanel(this, options);

		contentPanel.add(optionPanel);
	}

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			TableOptionDialogBox.this.hide();
			finish(options, TableOptionDialogBox.this, null);
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			TableOptionDialogBox.this.hide();
		}
	};

	@Override
	public void updateBtn() {
		confirmBtn.setEnabled(optionPanel.isComplete() ? true : false);
	}
}
