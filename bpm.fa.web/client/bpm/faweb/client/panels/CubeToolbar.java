package bpm.faweb.client.panels;

import java.util.HashMap;
import java.util.LinkedHashMap;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.dialog.SortingDialog;
import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.client.panels.center.CubeViewerTab;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.shared.SortElement;
import bpm.faweb.shared.infoscube.InfosReport;
import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.comment.CommentPanel;
import bpm.gwt.commons.client.popup.SharePopup;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.client.utils.ToolsGWT.TypeCollaboration;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.utils.TypeShare;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class CubeToolbar extends Composite {

	private static CubeToolbarUiBinder uiBinder = GWT.create(CubeToolbarUiBinder.class);

	interface CubeToolbarUiBinder extends UiBinder<Widget, CubeToolbar> {
	}

	@UiField
	HTMLPanel panelToolbar;

	@UiField
	Image imgOpenFull, imgRefresh, imgSave, imgUpdate, imgExport, imgShare, imgUndo, imgRedo, imgProjection, imgDash, imgSnapshot, imgListSnapshot, imgSort;

	@UiField
	Image imgSwap, imgPercent, imgDel, imgMdx, imgOnOff, imgTotal, imgNull, imgProperties;

	@UiField
	Image imgAddCol, imgAddRow, imgSearch, imgFilters, imgPrompts, imgCalculator, imgSum, imgDiff, imgDiv, imgMulti, btnComment;

	@UiField
	HTMLPanel separatorOne, separatorTwo, separatorThree, separatorFour;

	private CubeViewerTab cubeViewerTab;
	private InfoUser infoUser;

	private Integer fasdId;

	private boolean isTotals = true;
	private boolean hideNull = false;
	private boolean showProperties = false;
	private boolean showCalculator = false;
	private boolean isOn = true;

	public CubeToolbar(CubeViewerTab cubeViewerTab, InfoUser infoUser, boolean complete) {
		initWidget(uiBinder.createAndBindUi(this));
		this.cubeViewerTab = cubeViewerTab;
		this.infoUser = infoUser;

		panelToolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);

		imgMulti.setVisible(false);
		imgSum.setVisible(false);
		imgDiv.setVisible(false);
		imgDiff.setVisible(false);
		
		btnComment.setVisible(false);

		setViewerToolbar(complete);
	}

	public void setClickHandlers(ClickHandler sumRowHandler, ClickHandler sumColHandler, ClickHandler diffRowHandler, ClickHandler diffColHandler) {
		imgSum.addClickHandler(sumRowHandler);
		imgSum.addClickHandler(sumColHandler);

		imgDiff.addClickHandler(diffRowHandler);
		imgDiff.addClickHandler(diffColHandler);
	}

	private void setViewerToolbar(boolean complete) {
		// imgRefresh.setVisible(false);
		// imgExport.setVisible(false);
		// imgSwap.setVisible(false);
		// imgOnOff.setVisible(false);
		// imgDel.setVisible(false);
		if (complete) {
			imgOpenFull.setVisible(false);
			separatorOne.setVisible(false);
		}
		else {
			imgSave.removeFromParent();
			imgUpdate.removeFromParent();
			imgUndo.removeFromParent();
			imgRedo.removeFromParent();
			imgProjection.removeFromParent();
			imgDash.removeFromParent();
			imgSnapshot.removeFromParent();
			imgListSnapshot.removeFromParent();
			imgPercent.removeFromParent();
			//imgSort.removeFromParent();
//			imgDrill.removeFromParent();
			imgMdx.removeFromParent();
			imgTotal.removeFromParent();
			imgNull.removeFromParent();
			imgProperties.removeFromParent();
			imgAddCol.removeFromParent();
			imgAddRow.removeFromParent();
			//imgSearch.removeFromParent();
			imgFilters.removeFromParent();
			imgPrompts.removeFromParent();
			imgCalculator.removeFromParent();
			imgSum.removeFromParent();
			imgDiff.removeFromParent();
			imgDiv.removeFromParent();
			imgMulti.removeFromParent();

			separatorThree.removeFromParent();
			separatorFour.removeFromParent();
		}
	}

	@UiHandler("imgOpenFull")
	public void onOpenFullClick(ClickEvent event) {
		String url = Window.Location.getHref();
		if (url.contains(FreeAnalysisWeb.VIEWER)) {
			url = url.replace(FreeAnalysisWeb.VIEWER + "=true", FreeAnalysisWeb.VIEWER + "=false");
		}
		System.out.println(url);
		ToolsGWT.doRedirect(url);
	}

	@UiHandler("imgRefresh")
	public void onRefreshClick(ClickEvent event) {
		cubeViewerTab.refresh();
	}

	@UiHandler("imgSave")
	public void onSaveClick(ClickEvent event) {
		cubeViewerTab.save();
	}
	
	@UiHandler("imgUpdate")
	public void onUpdateClick(ClickEvent event) {
		cubeViewerTab.update();
	}

	@UiHandler("imgExport")
	public void onExportClick(ClickEvent event) {
		cubeViewerTab.openShare(TypeShare.EXPORT);
	}

	@UiHandler("imgShare")
	public void onShareClick(ClickEvent event) {
		//TODO: Add aklabox support
//		boolean isConnectedToAklabox = infoUser != null ? infoUser.isConnectedToAklabox() : false;
		boolean isConnectedToAklabox = false;

		SharePopup sharePopup = new SharePopup(cubeViewerTab, isConnectedToAklabox);
		sharePopup.setPopupPosition(event.getClientX(), event.getClientY());
		sharePopup.show();
	}

	@UiHandler("imgProjection")
	public void onProjectionClick(ClickEvent event) {
		cubeViewerTab.projection();
	}

	@UiHandler("imgDash")
	public void onDashClick(ClickEvent event) {
		cubeViewerTab.dash();
	}

	@UiHandler("imgSnapshot")
	public void onSnapshotClick(ClickEvent event) {
		cubeViewerTab.snapshot();
	}

	@UiHandler("imgListSnapshot")
	public void onListSnapshotClick(ClickEvent event) {
		cubeViewerTab.listSnapshot();
	}

	@UiHandler("imgUndo")
	public void onUndoClick(ClickEvent event) {
		cubeViewerTab.undo();
	}

	@UiHandler("imgRedo")
	public void onRedoClick(ClickEvent event) {
		cubeViewerTab.redo();
	}

	@UiHandler("imgSwap")
	public void onSwapClick(ClickEvent event) {
		cubeViewerTab.swap();
	}

	@UiHandler("imgPercent")
	public void onPercentClick(ClickEvent event) {
		cubeViewerTab.percent();
	}

	@UiHandler("imgDel")
	public void onDeleteClick(ClickEvent event) {
		cubeViewerTab.delete();
	}

//	@UiHandler("imgDrill")
//	public void onDrillClick(ClickEvent event) {
//		cubeViewerTab.drill();
//	}

	@UiHandler("imgMdx")
	public void onMdxClick(ClickEvent event) {
		cubeViewerTab.mdx();
	}

	@UiHandler("imgOnOff")
	public void onOnOFfClick(ClickEvent event) {
		isOn = !isOn;

		if (isOn) {
			imgOnOff.setResource(FaWebImage.INSTANCE.on_24());
			imgOnOff.setTitle(FreeAnalysisWeb.LBL.Off());
		}
		else {
			imgOnOff.setResource(FaWebImage.INSTANCE.off_24());
			imgOnOff.setTitle(FreeAnalysisWeb.LBL.On());
		}

		cubeViewerTab.onOff();
	}

	@UiHandler("imgTotal")
	public void onTotalsClick(ClickEvent event) {
		isTotals = !isTotals;

		if (isTotals) {
			imgTotal.setResource(FaWebImage.INSTANCE.totaux_afficher_24());
			imgTotal.setTitle(FreeAnalysisWeb.LBL.HideTotals());
		}
		else {
			imgTotal.setResource(FaWebImage.INSTANCE.totaux_cacher_24());
			imgTotal.setTitle(FreeAnalysisWeb.LBL.ShowTotals());
		}

		cubeViewerTab.totals();
	}

	@UiHandler("imgNull")
	public void onNullClick(ClickEvent event) {
		hideNull = !hideNull;

		if (hideNull) {
			imgNull.setResource(FaWebImage.INSTANCE.null_cacher_24());
			imgNull.setTitle(FreeAnalysisWeb.LBL.ShowNull());
		}
		else {
			imgNull.setResource(FaWebImage.INSTANCE.null_afficher_24());
			imgNull.setTitle(FreeAnalysisWeb.LBL.HideNull());
		}

		cubeViewerTab.onNull();
	}

	@UiHandler("imgProperties")
	public void onPropertiesClick(ClickEvent event) {
		showProperties = !showProperties;

		if (showProperties) {
			imgProperties.setResource(FaWebImage.INSTANCE.proprietes_afficher_24());
			imgProperties.setTitle(FreeAnalysisWeb.LBL.HideProperties());
		}
		else {
			imgProperties.setResource(FaWebImage.INSTANCE.proprietes_cacher_24());
			imgProperties.setTitle(FreeAnalysisWeb.LBL.ShowProperties());
		}

		cubeViewerTab.properties();
	}

	@UiHandler("imgAddCol")
	public void onAddColClick(ClickEvent event) {
		cubeViewerTab.addCol();
	}

	@UiHandler("imgAddRow")
	public void onAddRowClick(ClickEvent event) {
		cubeViewerTab.addRow();
	}

	@UiHandler("imgSearch")
	public void onSearchClick(ClickEvent event) {
		cubeViewerTab.search();
	}

	@UiHandler("imgFilters")
	public void onFiltersClick(ClickEvent event) {
		cubeViewerTab.filters();
	}

	@UiHandler("imgPrompts")
	public void onPromptsClick(ClickEvent event) {
		cubeViewerTab.prompts();
	}

	@UiHandler("imgCalculator")
	public void onCalculatorClick(ClickEvent event) {
		showCalculator = !showCalculator;

		cubeViewerTab.calculator(showCalculator);

		if (showCalculator) {
			imgCalculator.setResource(FaWebImage.INSTANCE.calculatrice_activer_24());
			imgCalculator.setTitle(FreeAnalysisWeb.LBL.DisableCalculator());
		}
		else {
			imgCalculator.setResource(FaWebImage.INSTANCE.calculatrice_desactiver_24());
			imgCalculator.setTitle(FreeAnalysisWeb.LBL.EnableCalculator());
		}

		imgSum.setVisible(showCalculator);
		imgDiff.setVisible(showCalculator);
		imgDiv.setVisible(showCalculator);
		imgMulti.setVisible(showCalculator);
	}

	@UiHandler("imgDiv")
	public void onDivClick(ClickEvent event) {
		cubeViewerTab.div(event);
	}

	@UiHandler("imgMulti")
	public void onMultiClick(ClickEvent event) {
		cubeViewerTab.multi(event);
	}

	@UiHandler("btnComment")
	public void onCommentClick(ClickEvent event) {
		if (fasdId != null) {
			CommentPanel commentsPanel = new CommentPanel(cubeViewerTab, fasdId, TypeCollaboration.ITEM_NOTE, infoUser.getGroup(), infoUser.getAvailableGroups());
			commentsPanel.show();
			commentsPanel.setPopupPosition(event.getClientX() - 332, event.getClientY() + 20);
		}
	}
	
	@UiHandler("imgSort")
	public void onSort(ClickEvent event) {
		final SortingDialog dial = new SortingDialog();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if(dial.isConfirm()) {
					HashMap<String, String> sortElements = new LinkedHashMap<String, String>();
					for(SortElement elem : MainPanel.getInstance().getInfosReport().getSortElements()) {
						sortElements.put(elem.getUname(), elem.getType());
					}
					
					FaWebService.Connect.getInstance().setSortingElements(MainPanel.getInstance().getKeySession(), sortElements, new AsyncCallback<InfosReport>() {
						
						@Override
						public void onSuccess(InfosReport result) {
							MainPanel.getInstance().setGridFromRCP(result);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							ExceptionManager.getInstance().handleException(caught, caught.getMessage());
							
						}
					});
					
					MainPanel.getInstance().setGridFromRCP(MainPanel.getInstance().getInfosReport());
				}
			}
		});
		dial.center();
	}

	public void loadComment(Integer fasdId) {
		this.fasdId = fasdId;
		if (fasdId == null) {
			btnComment.setVisible(false);
		}
		else {
			cubeViewerTab.showWaitPart(true);

			FaWebService.Connect.getInstance().canComment(cubeViewerTab.getMainPanel().getKeySession(), fasdId, new AsyncCallback<Boolean>() {

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();

					cubeViewerTab.showWaitPart(false);

					btnComment.setVisible(false);
				}

				@Override
				public void onSuccess(Boolean result) {
					cubeViewerTab.showWaitPart(false);
					btnComment.setVisible(result);
				}
			});
		}
	}

	public boolean isOn() {
		return isOn;
	}
}
