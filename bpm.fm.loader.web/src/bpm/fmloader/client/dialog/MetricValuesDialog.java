package bpm.fmloader.client.dialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.fm.api.model.Metric;
import bpm.fmloader.client.constante.Constantes;
import bpm.fmloader.client.infos.InfosUser;
import bpm.fmloader.client.table.MetricDataPanel;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.v2.Button;
import bpm.gwt.commons.client.custom.v2.LabelDateBox;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.dialog.ArchitectExportDialog;
import bpm.gwt.commons.client.dialog.CkanPackageDialog;
import bpm.gwt.commons.client.listeners.CkanManager;
import bpm.gwt.commons.client.popup.IShare;
import bpm.gwt.commons.client.popup.SharePopup;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.InfoShare;
import bpm.gwt.commons.shared.InfoShareArchitect;
import bpm.gwt.commons.shared.InfoShareD4C;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.utils.TypeExport;
import bpm.gwt.commons.shared.utils.TypeShare;
import bpm.mdm.model.supplier.Contract;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.CkanResource;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MetricValuesDialog extends AbstractDialogBox implements IShare, CkanManager {

	private static MetricValuesDialogUiBinder uiBinder = GWT.create(MetricValuesDialogUiBinder.class);

	interface MetricValuesDialogUiBinder extends UiBinder<Widget, MetricValuesDialog> {
	}

	@UiField
	LabelDateBox dbDate;

	@UiField
	Button btnLoad;

	@UiField
	SimplePanel panelData;

	private InfoUser infoUser;
	private Metric metric;
	
	public MetricValuesDialog(InfoUser infoUser, Metric object) {
		super(Constantes.LBL.metric(), true, true);
		this.infoUser = infoUser;
		this.metric = object;
		
		setWidget(uiBinder.createAndBindUi(this));
		
		createButton(LabelsConstants.lblCnst.Close(), closeHandler);
		
		loadData();
	}

	private void loadData() {
		dbDate.setValue(new Date(), new Date());

		onLoad(null);
	}

	@UiHandler("btnLoad")
	public void onLoad(ClickEvent event) {
		Date firstDate = dbDate.getFirstDate();
		Date endDate = dbDate.getLastDate();

		List<Integer> ids = new ArrayList<Integer>();
		ids.add(metric.getId());
		
		Constantes.DATAS_SERVICES.getValues(ids, firstDate, endDate, true, new GwtCallbackWrapper<InfosUser>(this, true, true) {		
			@Override
			public void onSuccess(InfosUser result) {
				MetricDataPanel metricDataPanel = new MetricDataPanel(result.getValues(), false);
				String width = metricDataPanel.getWidth();
				
				metricDataPanel.getElement().getStyle().setProperty("minWidth", width);
				metricDataPanel.setWidth("100%");
				metricDataPanel.setHeight("100%");
//				dataPanel.getElement().getStyle().setOverflow(Overflow.AUTO);
				metricDataPanel.getElement().getStyle().setOverflow(Overflow.AUTO);
				panelData.setWidget(metricDataPanel);
			}
		}.getAsyncCallback());
	}


	@UiHandler("btnShare")
	public void onShareClick(ClickEvent event) {
		boolean isConnectedToCkan = infoUser.isConnected(VanillaConfiguration.P_CKAN_URL);

		SharePopup sharePopup = null;
		if (isConnectedToCkan) {
			sharePopup = new SharePopup(this, TypeShare.CKAN, TypeShare.ARCHITECT);
		}
		else {
			sharePopup = new SharePopup(this, TypeShare.ARCHITECT);
		}
		sharePopup.setPopupPosition(event.getClientX(), event.getClientY());
		sharePopup.show();
	}

	@Override
	public void openShare(TypeShare typeShare) {
		switch (typeShare) {
		case CKAN:
			CkanPackageDialog dial = new CkanPackageDialog(this, null, null, true, null);
			dial.center();
			break;
		case ARCHITECT:
			final ArchitectExportDialog dialArch = new ArchitectExportDialog(true, false);
			dialArch.center();
			dialArch.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if (dialArch.isConfirm()) {
						String name = dialArch.getName();
						Contract contract = dialArch.getSelectedContract();
						
						manageContract(name, contract);
					}
				}
			});
			break;

		default:
			break;
		}
	}

	private void manageContract(String name, Contract contract) {
		Date firstDate = dbDate.getFirstDate();
		Date endDate = dbDate.getLastDate();
		
		Constantes.DATAS_SERVICES.exportMetricValues(new InfoShareArchitect(TypeExport.KPI, name, "csv", contract), metric.getName(), metric.getId(), firstDate, endDate, new GwtCallbackWrapper<Void>(this, true, true) {

			@Override
			public void onSuccess(Void result) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.DatasetUploadedToArchitect());
			}
		}.getAsyncCallback());
	}

	@Override
	public void managePackage(CkanPackage pack, CkanResource resource) {
		pack.setSelectedResource(resource);

		Date firstDate = dbDate.getFirstDate();
		Date endDate = dbDate.getLastDate();
		
		Constantes.DATAS_SERVICES.exportMetricValues(new InfoShareD4C(TypeExport.KPI, pack), metric.getName(), metric.getId(), firstDate, endDate, new GwtCallbackWrapper<Void>(this, true, true) {

			@Override
			public void onSuccess(Void result) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.DatasetUploadedToCkan());
			}
		}.getAsyncCallback());
	}

	@Override
	public void share(InfoShare infoShare) {
		//Not used
	}
	
	private ClickHandler closeHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
