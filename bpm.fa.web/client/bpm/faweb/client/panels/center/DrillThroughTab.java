package bpm.faweb.client.panels.center;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.dialog.AddFilterDialog;
import bpm.faweb.client.dialog.AirShareDialog;
import bpm.faweb.client.services.FaWebService;
import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.popup.IShare;
import bpm.gwt.commons.client.popup.SharePopup;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.client.viewer.dialog.MailShareDialog;
import bpm.gwt.commons.shared.InfoShare;
import bpm.gwt.commons.shared.InfoShareCube;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.analysis.DrillInformations;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.commons.shared.utils.ExportResult;
import bpm.gwt.commons.shared.utils.TypeExport;
import bpm.gwt.commons.shared.utils.TypeShare;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class DrillThroughTab extends Tab implements IShare {

	private static DrillThroughTabUiBinder uiBinder = GWT.create(DrillThroughTabUiBinder.class);

	interface DrillThroughTabUiBinder extends UiBinder<Widget, DrillThroughTab> {
	}

	@UiField
	HTMLPanel panelToolbar;

	@UiField
	SimplePanel panelContent;
	
	private MainPanel mainCompParent;
	private InfoUser infoUser;
	
	private DrillThroughContainer drillThroughContainer;

	public DrillThroughTab(TabManager tabManager, MainPanel mainCompParent) {
		super(tabManager, FreeAnalysisWeb.LBL.drillThrough(), true);
		this.mainCompParent = mainCompParent;
		this.infoUser = mainCompParent.getInfosUser();
		
		add(uiBinder.createAndBindUi(this));
		
		panelToolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);
	}

	public void loadThroughPanel(DrillInformations drillInfo) {
		drillThroughContainer = new DrillThroughContainer(this, drillInfo);
		panelContent.setWidget(drillThroughContainer);
	}

	@UiHandler("btnExport")
	public void onExportToXLS(ClickEvent e) {
		openShare(TypeShare.EXPORT);
	}
	
	@UiHandler("btnAddFilter")
	public void onAddFilter(ClickEvent e) {
		AddFilterDialog dial = new AddFilterDialog(drillThroughContainer, drillThroughContainer.getDrillInfo());
		dial.center();
	}

	@UiHandler("btnClearFilter")
	public void onClearFilter(ClickEvent e) {
		drillThroughContainer.resetFilter();
	}
	
	@UiHandler("imgClose")
	public void onCloseClick(ClickEvent event) {
		close();
	}

	@UiHandler("imgShare")
	public void onShareClick(ClickEvent event) {
		//TODO: Add aklabox support
//		boolean isConnectedToAklabox = infoUser != null ? infoUser.isConnectedToAklabox() : false;
//		boolean isConnectedToAklabox = false;
//		boolean isConnectedToAir = infoUser != null ? infoUser.canAccessAir() : false;


//		SharePopup sharePopup = new SharePopup(DrillThroughTab.this, isConnectedToAklabox, isConnectedToAir);
		SharePopup sharePopup = new SharePopup(DrillThroughTab.this, TypeShare.EMAIL, TypeShare.EXPORT, TypeShare.AIR);
		sharePopup.setPopupPosition(event.getClientX(), event.getClientY());
		sharePopup.show();
	}

	@Override
	public void openShare(TypeShare typeShare) {
		switch (typeShare) {
		case AKLABOX:
			// AklaboxShareDialog dial = new
			// AklaboxShareDialog(itemInfo.getItem(), reportKey,
			// itemInfo.getOutputs());
			// dial.center();
			break;
		case EMAIL:
		case EXPORT:
			MailShareDialog mailDialog = new MailShareDialog(DrillThroughTab.this, mainCompParent.getInfosReport().getCubeName(), mainCompParent.getInfosUser().getAvailableGroups(), typeShare, TypeExport.DRILLTHROUGH, drillThroughContainer.getDrillInfo());
			mailDialog.center();
			break;
		case AIR:
			AirShareDialog airDialog = new AirShareDialog(DrillThroughTab.this, mainCompParent.getKeySession(), mainCompParent.getInfosUser().getUser().getId(), drillThroughContainer.getDrillInfo());
			airDialog.center();
			break;

		default:
			break;
		}
	}

	@Override
	public void share(final InfoShare infoShare) {
		if (infoShare instanceof InfoShareCube) {
			showWaitPart(true);

			final InfoShareCube infoShareCube = (InfoShareCube) infoShare;
			
			if (infoShare.getTypeExport() == TypeExport.DRILLTHROUGH && (infoShare.getTypeShare() == TypeShare.EMAIL || infoShare.getTypeShare() == TypeShare.EXPORT)) {
	
				FaWebService.Connect.getInstance().drillThroughExportXls(infoShareCube, new AsyncCallback<ExportResult>() {
	
					@Override
					public void onFailure(Throwable caught) {
						showWaitPart(false);
	
						caught.printStackTrace();
	
						ExceptionManager.getInstance().handleException(caught, "Export service failed");
					}
	
					@Override
					public void onSuccess(ExportResult result) {
						showWaitPart(false);
	
						if (infoShare.getTypeShare() == TypeShare.EXPORT) {
							String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_REPORT_SERVLET_ANALYSIS + "?" + CommonConstants.REPORT_HASHMAP_NAME + "=" + result.getReportName() + "&" + CommonConstants.REPORT_OUTPUT + "=" + infoShareCube.getFormat();
							ToolsGWT.doRedirect(fullUrl);
						}
						else if (infoShare.getTypeShare() == TypeShare.EMAIL) {
							MessageHelper.openMessageMailResult(LabelsConstants.lblCnst.Information(), result);
						}
					}
				});
			}
		}
	}
}
