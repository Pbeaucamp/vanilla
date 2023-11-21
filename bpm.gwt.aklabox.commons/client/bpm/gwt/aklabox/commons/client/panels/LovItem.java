package bpm.gwt.aklabox.commons.client.panels;

import java.util.HashMap;

import bpm.document.management.core.model.LOV;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.aklabox.commons.client.utils.ChildDialogComposite;
import bpm.gwt.aklabox.commons.client.utils.DefaultDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class LovItem extends Composite {

	private static LovItemUiBinder uiBinder = GWT.create(LovItemUiBinder.class);

	interface LovItemUiBinder extends UiBinder<Widget, LovItem> {
	}

	@UiField
	Label lblName, lblTable, lblSource, lblItemCode, lblItemName;
	@UiField
	Image imgRemove, imgEdit;

	private LOV lov;
	private LovPanel parent;

	public LovItem(LOV lov, LovPanel parent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.lov = lov;
		this.parent = parent;
		lblName.setText(lov.getValueName());
		lblSource.setText(lov.getSource() != null ? lov.getSource().getName() : LabelsConstants.lblCnst.Unknown());
		lblTable.setText(lov.getTable());
		lblItemCode.setText(lov.getItemCode());
		lblItemName.setText(lov.getItemLabel());
	}

	public LOV getLov() {
		return lov;
	}

	public void setLov(LOV lov) {
		this.lov = lov;
	}

	@UiHandler("imgRemove")
	void onRemove(ClickEvent e) {
		parent.deleteLov(this);
	}

	@UiHandler("imgPreview")
	void onPreview(ClickEvent e) {
		AklaCommonService.Connect.getService().getAllLovByTableCode(lov, new GwtCallbackWrapper<HashMap<String, String>>(null, false, false) {

			@Override
			public void onSuccess(HashMap<String, String> result) {
				LOVPreview lovPreview = new LOVPreview(result);
				DefaultDialog defaultDialog = new DefaultDialog(LabelsConstants.lblCnst.FillFieldsErrorTitle(), lovPreview, 420, 210, 10);
				defaultDialog.center();
			}
		}.getAsyncCallback());
	}

	@UiHandler("imgEdit")
	void onEdit(ClickEvent e) {
		parent.editLov(this);
	}

	private class LOVPreview extends ChildDialogComposite {

		public LOVPreview(HashMap<String, String> result) {

			ListBox lst = new ListBox();
			lst.setWidth("350px");
			if (result != null) {
				for (String key : result.keySet()) {
					lst.addItem(key, result.get(key));
				}
			}

			this.initWidget(lst);

		}

	}
}
