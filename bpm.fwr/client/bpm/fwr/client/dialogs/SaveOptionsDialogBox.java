package bpm.fwr.client.dialogs;

import java.util.List;

import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.shared.models.metadata.FwrMetadata;
import bpm.fwr.shared.models.report.wysiwyg.ReportParameters;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class SaveOptionsDialogBox extends AbstractDialogBox implements ICustomDialogBox {

	private static SaveOptionsDialogBoxUiBinder uiBinder = GWT.create(SaveOptionsDialogBoxUiBinder.class);

	interface SaveOptionsDialogBoxUiBinder extends UiBinder<Widget, SaveOptionsDialogBox> {
	}
	
	@UiField
	HTMLPanel contentPanel;

	private Button confirmBtn;
	private SaveOptionsDialogBoxPanel optionPanel;
	
	public SaveOptionsDialogBox(ReportParameters reportParameters, List<FwrMetadata> metadatas) {
		super(Bpm_fwr.LBLW.PageParameters(), false, true);
		
		setWidget(uiBinder.createAndBindUi(this));

		createButton(Bpm_fwr.LBLW.Cancel(), cancelHandler);
		confirmBtn = createButton(Bpm_fwr.LBLW.Ok(), confirmHandler);
		
		optionPanel = new SaveOptionsDialogBoxPanel(reportParameters, metadatas);
		
		contentPanel.add(optionPanel);
	}
	
	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			SaveOptionsDialogBox.this.hide();
			ReportParameters reportParameters = optionPanel.getReportParameters();
			finish(reportParameters, null, null);
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			SaveOptionsDialogBox.this.hide();
		}
	};

	@Override
	public void updateBtn() {
		confirmBtn.setEnabled(optionPanel.isComplete() ? true : false);
	}
}
