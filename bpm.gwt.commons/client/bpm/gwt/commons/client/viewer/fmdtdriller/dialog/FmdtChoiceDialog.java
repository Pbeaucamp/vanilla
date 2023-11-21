package bpm.gwt.commons.client.viewer.fmdtdriller.dialog;

import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.viewer.FmdtVanillaViewer;
import bpm.gwt.commons.shared.fmdt.FmdtModel;
import bpm.gwt.commons.shared.fmdt.FmdtPackage;
import bpm.gwt.commons.shared.fmdt.FmdtQueryDriller;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class FmdtChoiceDialog extends AbstractDialogBox {

	private static ExampleDialogUiBinder uiBinder = GWT.create(ExampleDialogUiBinder.class);

	interface ExampleDialogUiBinder extends UiBinder<Widget, FmdtChoiceDialog> {
	}
	
	@UiField
	HTMLPanel contentPanel, panelConnection;
	
	@UiField
	ListBox lstModel, lstPackage, lstConnection;

	private FmdtVanillaViewer fmdtVanillaViewer;
	//private FmdtDriller fmdtDriller;
	private FmdtQueryDriller fmdtDriller;
	private List<FmdtModel> models;
	
	public FmdtChoiceDialog(FmdtVanillaViewer fmdtVanillaViewer, FmdtQueryDriller fmdtDriller, List<FmdtModel> models) {
		super(LabelsConstants.lblCnst.Metadata(), false, true);
		this.fmdtVanillaViewer = fmdtVanillaViewer;
		this.fmdtDriller = fmdtDriller;
		this.models = models;
		
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		for(FmdtModel mod : models) {
			lstModel.addItem(mod.getName(), mod.getName());
		}
		
		if(models.get(0).getPackages() != null) {				
			boolean first = true;
			for(FmdtPackage pack : models.get(0).getPackages()) {
				lstPackage.addItem(pack.getName(), pack.getName());
				if(first) {
					loadConnectionList(pack.getConnections());
					first = false;
				}
			}
		}
	}
	
	
	
	@UiHandler("lstModel")
	public void onModelChange(ChangeEvent event) {
		FmdtModel mod = models.get(lstModel.getSelectedIndex());
		lstPackage.clear();
		if(mod.getPackages() != null) {		
			boolean first = true;
			for(FmdtPackage pack : mod.getPackages()) {
				lstPackage.addItem(pack.getName(), pack.getName());
				if(first) {
					loadConnectionList(pack.getConnections());
					first = false;
				}
			}
		}
	}
	
	@UiHandler("lstPackage")
	public void onPackageChange(ChangeEvent event) {
		FmdtModel mod = models.get(lstModel.getSelectedIndex());
		if(mod.getPackages() != null) {				
			for(FmdtPackage pack : mod.getPackages()) {
				if(lstPackage.getValue(lstPackage.getSelectedIndex()).equals(pack.getName())) {
					loadConnectionList(pack.getConnections());
				}
			}
		}
	}
	
	private void loadConnectionList(List<String> connections) {
		panelConnection.setVisible(connections != null && connections.size() > 1);
		
		lstConnection.clear();
		for(String conn : connections) {
			lstConnection.addItem(conn, conn);
		}
	}
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
	
	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			/*
			fmdtDriller.setModelName(models.get(lstModel.getSelectedIndex()).getName());
			fmdtDriller.setPackageName(models.get(lstModel.getSelectedIndex()).getPackages().get(lstPackage.getSelectedIndex()).getName());
			fmdtDriller.setConnection(lstConnection.getValue(lstConnection.getSelectedIndex()));
			
			fmdtVanillaViewer.loadFmdtDrillerPart(fmdtDriller);
			
			hide();
			*/
			fmdtDriller.setModelName(models.get(lstModel.getSelectedIndex()).getName());
			fmdtDriller.setPackageName(models.get(lstModel.getSelectedIndex()).getPackages().get(lstPackage.getSelectedIndex()).getName());
//			fmdtDriller.setConnection(lstConnection.getValue(lstConnection.getSelectedIndex()));
			
			fmdtVanillaViewer.loadFmdtDrillerPart(fmdtDriller, null);
			
			hide();
		}
	};

}
