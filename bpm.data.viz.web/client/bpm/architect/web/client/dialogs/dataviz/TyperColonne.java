package bpm.architect.web.client.dialogs.dataviz;

import java.util.Arrays;

import bpm.data.viz.core.preparation.DataPreparation;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.CommonService;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.DataColumn.FunctionalType;
import bpm.vanilla.platform.core.beans.data.Dataset;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class TyperColonne extends AbstractDialogBox {

	
	private DataColumn datacolumn;
	private static TyperColonneUiBinder uiBinder = GWT.create(TyperColonneUiBinder.class);

	interface TyperColonneUiBinder extends UiBinder<Widget, TyperColonne> {}

	@UiField
	ListBoxWithButton<FunctionalType> lstType;

	private Dataset dataset;
	private DataPreparation dp;
	
	public TyperColonne(DataColumn dt, DataPreparation dp) {
		super("Typer Cette Colonne", false, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.dp = dp;
		datacolumn = dt;
		this.dataset = dp.getDataset();
		FunctionalType[] ft = FunctionalType.values();

		lstType.setList(Arrays.asList(ft));
		if (dt.getFt() != null){
			
			lstType.setSelectedObject(dt.getFt());
		}

		lstType.setLabel("Colonnes");
		lstType.setMultiple(false);

		lstType.getListBox().setVisibleItemCount(15);

		
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		

		
	}
	
	private ClickHandler confirmHandler = new ClickHandler() {
		

		@Override
		public void onClick(ClickEvent event) {
       
			datacolumn.setFt(lstType.getSelectedObject());
			
			CommonService.Connect.getInstance().updateDataset(dataset, new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void result) {
					dp.setDataset(dataset);
					hide();
					
				}
				
				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					
				}
			});
	   
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
	
	

	


