package bpm.architect.web.client.dialogs;

import java.util.List;

import bpm.architect.web.client.dialogs.dataviz.IRulePanel;
import bpm.architect.web.client.services.ArchitectService;
import bpm.data.viz.core.preparation.DataPreparation;
import bpm.data.viz.core.preparation.PreparationRule;
import bpm.data.viz.core.preparation.PreparationRule.RuleType;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.InfoUser;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PreparationExportInLibreOfficeDailog extends AbstractDialogBox implements IRulePanel {

	private static PreparationExportInLibreOfficeDailogUiBinder uiBinder = GWT.create(PreparationExportInLibreOfficeDailogUiBinder.class);

	interface PreparationExportInLibreOfficeDailogUiBinder extends UiBinder<Widget, PreparationExportInLibreOfficeDailog> {
	}

	//Liste de selection du supplier
	@UiField
	ListBoxWithButton<Supplier> supplierListBox;

	//Liste de selection du contrat
	@UiField
	ListBoxWithButton<Contract> contractListBox;

	@UiField
	HTMLPanel mainPanel;

	private InfoUser infoUser;
	private DataPreparation dataprep;

	public PreparationExportInLibreOfficeDailog(DataPreparation dp, InfoUser info) {
		super("Ouvrir dans Libre Office online", false, true);
		this.dataprep = dp;
		this.infoUser = info;

		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		loadSuppliers();
	}

	private void loadSuppliers() {
		// Recuperation de la liste des suppliers
		ArchitectService.Connect.getInstance().getSuppliers(new GwtCallbackWrapper<List<Supplier>>(this, true) {

			@Override
			public void onSuccess(List<Supplier> result) {
				supplierListBox.setList(result, true);
			}

		}.getAsyncCallback());
	}

	@UiHandler("supplierListBox")
	public void onChangeSupplier(ChangeEvent event) {
		contractListBox.clear();
		if (supplierListBox.getSelectedObject() != null) {
			contractListBox.setList(supplierListBox.getSelectedObject().getContracts(), true);
		}
	}
	public Contract getSelectedContract() {
		return contractListBox.getSelectedObject();

	}

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event){
			try{
				if( getSelectedContract() == null ){
					MessageHelper.openMessageDialog("Alert", "Veuillez d'abord selectionner un contract ");
					return;
				}
				openLool();
				dataprep.addRule(new PreparationRule(RuleType.LIBREOFFICE));
				hide();
			}catch( Exception e){
				e.printStackTrace();
			}
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	private void openLool() {
		String fileId = "" + dataprep.getId() + "," + this.getSelectedContract().getId();

		String wopiCallUrl = this.infoUser.getWopiServiceUrl() + fileId;
		FormPanel form = new FormPanel("_blank");
		form.setAction(wopiCallUrl);
		form.setMethod(FormPanel.METHOD_POST);

		HTMLPanel pan = new HTMLPanel("");

		TextBox txtToken = new TextBox();
		txtToken.setName("access_token");
		txtToken.setText(fileId + this.infoUser.getUser().getLogin());

		TextBox txtTokenTtl = new TextBox();
		txtTokenTtl.setName("access_token_ttl");
		txtTokenTtl.setText("10000");

		pan.add(txtToken);
		pan.add(txtTokenTtl);
		pan.setVisible(false);
		form.add(pan);
		mainPanel.add(form);
		form.submit();
	}

	@Override
	public PreparationRule getRule() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void changeColumnSelection(DataColumn column) {
		// TODO Auto-generated method stub
		
	}
}
