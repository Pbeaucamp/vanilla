package bpm.freematrix.reborn.web.client.main.home.information;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.freematrix.reborn.web.client.ClientSession;
import bpm.freematrix.reborn.web.client.dialog.WaitDialog;
import bpm.freematrix.reborn.web.client.i18n.LabelConstants;
import bpm.freematrix.reborn.web.client.main.header.FreeMatrixHeader;
import bpm.freematrix.reborn.web.client.main.home.HomeView;
import bpm.freematrix.reborn.web.client.services.MetricService;
import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.CkanPackageDialog;
import bpm.gwt.commons.client.listeners.CkanManager;
import bpm.gwt.commons.client.popup.IShare;
import bpm.gwt.commons.client.popup.SharePopup;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.InfoShare;
import bpm.gwt.commons.shared.utils.TypeShare;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.CkanResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class InformationPanel extends Composite implements IShare, CkanManager {

	private static InformationPanelUiBinder uiBinder = GWT.create(InformationPanelUiBinder.class);

	interface InformationPanelUiBinder extends UiBinder<Widget, InformationPanel> {
	}

	@UiField
	HTMLPanel informationTabPanel, informationViewerPanel;

	@UiField
	Label lblTitle;

	@UiField
	Image btnShare;

	private Axis axis;
	private Metric metric;
	private Level level;

	private InformationViewer viewer = new InformationViewer();
	private HomeView homeView;

	public InformationPanel(boolean isAxis, Object object, Metric metric, HomeView homeView) {
		initWidget(uiBinder.createAndBindUi(this));
		this.homeView = homeView;
		informationViewerPanel.add(viewer);
		informationTabPanel.add(new InformationTabs(isAxis, object, viewer, metric, homeView));
		setInformationDetails(object);
	}

	private void setInformationDetails(Object object) {
		if (object instanceof Axis) {
			axis = (Axis) object;
			lblTitle.setText(LabelConstants.lblCnst.axis() + ": " + axis.getName());
			informationTabPanel.setVisible(false);
			informationViewerPanel.getElement().getStyle().setLeft(0, Unit.PX);
			viewer.generateApplicationDetails(homeView.getFilterDate(), axis, homeView.getMetric());
		}
		else if (object instanceof Metric) {
			metric = (Metric) object;
			lblTitle.setText(LabelConstants.lblCnst.Metric() + ": " + metric.getName());
			viewer.generateMetricEvolution(homeView.getFilterDate(), metric);
			
			btnShare.setVisible(true);
		}
		else if (object instanceof Level) {
			level = (Level) object;
			lblTitle.setText(LabelConstants.lblCnst.level() + ": " + level.getName());
			viewer.generateAxisPie(homeView.getFilterDate(), level, homeView.getMetric());
		}
	}


	@UiHandler("btnShare")
	public void onShareClick(ClickEvent event) {
		boolean isConnectedToCkan = ClientSession.getInstance().isConnectedToCkan();

		if (isConnectedToCkan) {
			SharePopup sharePopup = new SharePopup(this, TypeShare.CKAN);
			sharePopup.setPopupPosition(event.getClientX(), event.getClientY());
			sharePopup.show();
		}
	}

	@Override
	public void openShare(TypeShare typeShare) {
		switch (typeShare) {
		case CKAN:
			CkanPackageDialog dial = new CkanPackageDialog(this, null, null, true, null);
			dial.center();
			break;

		default:
			break;
		}
	}

	@Override
	public void managePackage(CkanPackage pack, CkanResource resource) {
		pack.setSelectedResource(resource);
		
		WaitDialog.showWaitPart(true);
		
		MetricService.Connection.getInstance().exportToCkan(resource.getName(), pack, homeView.getFilterDate(), metric.getId(), FreeMatrixHeader.getInstance().getSelectedGroup(), FreeMatrixHeader.getInstance().getSelectedTheme(), new com.google.gwt.user.client.rpc.AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				WaitDialog.showWaitPart(false);
				
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				WaitDialog.showWaitPart(false);
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.DatasetUploadedToCkan());
			}
		});
	}
	
	@Override
	public void share(InfoShare infoShare) { }
}
