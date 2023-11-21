package bpm.faweb.client.popup;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.dialog.MonthSelectionDialog;
import bpm.faweb.client.dialog.TopxDialog;
import bpm.faweb.client.dnd.DraggableGridItem;
import bpm.faweb.client.services.CubeServices;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.client.utils.CumulateValues;
import bpm.faweb.client.utils.DateFunctionCalculator;
import bpm.faweb.shared.infoscube.GridCube;
import bpm.faweb.shared.infoscube.InfosReport;
import bpm.faweb.shared.infoscube.ItemDim;
import bpm.faweb.shared.infoscube.Topx;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class GridPopup extends PopupPanel {

	private static GridPopupUiBinder uiBinder = GWT.create(GridPopupUiBinder.class);

	interface GridPopupUiBinder extends UiBinder<Widget, GridPopup> {
	}

	@UiField
	HTMLPanel panelMenu;
	
	@UiField
	Label btnRenameElement, btnRemovePersonalName, btnTopX, btnModifyTopX, btnRemoveTopX, btnFlash, btnDrillDownAll, btnDrillUpAll, btnCumul, btnClearDate, 
	btnYearToDate, btnYearToYear, btnYearDifferences;

	private MainPanel mainPanel;

	private DraggableGridItem item;

	private String uname;
	private TextBox txt;
	private int i;
	private int j;

	public GridPopup(final MainPanel mainPanel, String uname, DraggableGridItem item) {
		setWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		this.uname = uname;
		this.item = item;
		
		i = GridPopup.this.item.getI();
		j = GridPopup.this.item.getJ();

		panelMenu.addStyleName(VanillaCSS.POPUP_PANEL);

		this.setAnimationEnabled(true);
		this.setAutoHideEnabled(true);

		boolean modifyTopx = false;
		for (Topx t : mainPanel.getInfosReport().getGrid().getTopx()) {
			if (t.getElementName().equals(uname)) {
				modifyTopx = true;
				break;
			}
		}

		boolean modifyName = false;
		for (String unam : mainPanel.getInfosReport().getGrid().getPersonalNames().keySet()) {
			if (unam.equals(uname)) {
				modifyName = true;
				break;
			}
		}
		
		if(!(mainPanel.dateFunction > 0)) {
			boolean isDateAllMember = false;
			String itemUname = item.getUname();
			for(ItemDim dim : MainPanel.getInstance().getInfosReport().getDims()) {
				if(itemUname.startsWith("[" + dim.getName())) {
					if(dim.isDate()) {
						isDateAllMember = true;
					}
					break;
				}
			}
			
			if(!isDateAllMember) {
				btnYearToDate.removeFromParent();
				btnYearToYear.removeFromParent();
				btnYearDifferences.removeFromParent();
			}
			else {
				if(!isYearToValid()) {
					btnYearToDate.removeFromParent();
					btnYearToYear.removeFromParent();
				}
				if(!isDiffYearValid()) {
					btnYearDifferences.removeFromParent();
				}
			}
			btnClearDate.removeFromParent();
		}
		else {
			btnYearToDate.removeFromParent();
			btnYearToYear.removeFromParent();
			btnYearDifferences.removeFromParent();
		}

		if (!modifyName) {
			btnRemovePersonalName.removeFromParent();
		}

		if (modifyTopx) {
			btnTopX.removeFromParent();
		}

		else {
			btnModifyTopX.removeFromParent();
			btnRemoveTopX.removeFromParent();
		}

		boolean isGeolocalisable = false;

		int pointIndex = GridPopup.this.item.getUname().indexOf(".");
		String selectedDimensionUname = GridPopup.this.item.getUname().substring(0, pointIndex);
		int beginIndex = selectedDimensionUname.indexOf("[") + "[".length();
		String selectedName = selectedDimensionUname.substring(beginIndex);

		selectedName = selectedName.replace("]", "").replace("[", "");

		for (ItemDim itemDim : mainPanel.getInfosReport().getDims()) {
			String dimensionUname = itemDim.getUname();
			int begin = dimensionUname.indexOf("[") + "[".length();
			int end = dimensionUname.lastIndexOf("]");
			String name = dimensionUname.substring(begin, end);
			if (selectedName.equals(name)) {
				isGeolocalisable = itemDim.isGeolocalisable();
				break;
			}
		}

		if (!isGeolocalisable) {
			btnFlash.removeFromParent();
		}
	}
	
	private boolean isDiffYearValid() {
//		if(item.getI() == 0 || item.getJ() == 0) {
//			if(item.getI() == 0) {
//				if(mainPanel.getInfosReport().getIFirst() == 2) {
//					return true;
//				}
//			}
//			else {
//				if(mainPanel.getInfosReport().getJFirst() == 2) {
//					return true;
//				}
//			}
//		}
		boolean onRow = false;
		if(item.getI() >= mainPanel.getInfosReport().getIFirst()) {
			onRow = true;
		}
		
		if(onRow) {
			if(mainPanel.getInfosReport().getJFirst() == 2) {
				return true;
			}
		}
		else {
			if(mainPanel.getInfosReport().getIFirst() == 2) {
				return true;
			}
		}
		
		return false;
	}

	private boolean isYearToValid() {
//		if(item.getI() == 0 || item.getJ() == 0) {
//			if(item.getI() == 0) {
//				if(mainPanel.getInfosReport().getIFirst() == 3) {
//					return true;
//				}
//			}
//			else {
//				if(mainPanel.getInfosReport().getJFirst() == 3) {
//					return true;
//				}
//			}
//		}
		
		
		boolean onRow = false;
		if(item.getI() >= mainPanel.getInfosReport().getIFirst()) {
			onRow = true;
		}
		
		if(onRow) {
			if(mainPanel.getInfosReport().getJFirst() == 3) {
				return true;
			}
		}
		else {
			if(mainPanel.getInfosReport().getIFirst() == 3) {
				return true;
			}
		}
		
		return false;
	}

	@UiHandler("btnClearDate")
	public void clearDateFunction(ClickEvent event) {
		mainPanel.dateFunction = 0;
		mainPanel.dateFunctionMonth = 0;
		
		FaWebService.Connect.getInstance().getGridCubeForActualQuery(mainPanel.getKeySession(), mainPanel.getInfosReport().isProjection(), false, new GwtCallbackWrapper<GridCube>(mainPanel, true) {
			@Override
			public void onSuccess(GridCube result) {
				MainPanel.getInstance().setGridFromRCP(result);
				GridPopup.this.hide();
			}
		}.getAsyncCallback());
	}
	
	@UiHandler("btnYearDifferences")
	public void diffBetweenDate(ClickEvent event) {
		
		mainPanel.dateFunction = DateFunctionCalculator.DIFFERENCE_BETWEEN_DATE;
		MainPanel.getInstance().setGridFromRCP(MainPanel.getInstance().getInfosReport().getGrid());
		
//		DateFunctionCalculator.differenceBetweenDate(item, MainPanel.getInstance().getInfosReport());
		GridPopup.this.hide();
	}
	
	@UiHandler("btnYearToYear")
	public void yearToYear(ClickEvent event) {
		final MonthSelectionDialog dial = new MonthSelectionDialog();
		dial.setPopupPosition(event.getClientX(), event.getClientY());
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if(dial.isConfirm()) {
					mainPanel.dateFunction = DateFunctionCalculator.YEAR_TO_YEAR;
					mainPanel.dateFunctionMonth = dial.getMonth();
					MainPanel.getInstance().setGridFromRCP(MainPanel.getInstance().getInfosReport().getGrid());
				}
			}
		});
		dial.show();
		GridPopup.this.hide();
	}
	
	@UiHandler("btnYearToDate")
	public void yearToDate(ClickEvent event) {
		final MonthSelectionDialog dial = new MonthSelectionDialog();
		dial.setPopupPosition(event.getClientX(), event.getClientY());
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if(dial.isConfirm()) {
					mainPanel.dateFunction = DateFunctionCalculator.YEAR_TO_DATE;
					mainPanel.dateFunctionMonth = dial.getMonth();
					MainPanel.getInstance().setGridFromRCP(MainPanel.getInstance().getInfosReport().getGrid());
				}
			}
		});
		dial.show();
		GridPopup.this.hide();
	}

	@UiHandler("btnRenameElement")
	public void onRenameElementClick(ClickEvent event) {
		GridPopup.this.hide();
		
		txt = new TextBox();
		txt.setWidth(GridPopup.this.item.getOffsetWidth() + "px");
		mainPanel.getGrid().setWidget(i, j, txt);
		txt.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					FaWebService.Connect.getInstance().addPersonalName(mainPanel.getKeySession(), GridPopup.this.uname, txt.getText(), new AsyncCallback<InfosReport>() {
						public void onSuccess(InfosReport result) {
							mainPanel.getInfosReport().getGrid().getPersonalNames().put(GridPopup.this.uname, txt.getText());
							GridPopup.this.item.setText(txt.getText());
							GridPopup.this.item.setTitle(txt.getText());
							mainPanel.getGrid().setWidget(i, j, GridPopup.this.item);
						}
	
						public void onFailure(Throwable caught) {
	
						}
					});
				}
			}
		});
		txt.setFocus(true);
	}

	@UiHandler("btnRemovePersonalName")
	public void onRemovePersonalNameClick(ClickEvent event) {
		GridPopup.this.hide();
		
		FaWebService.Connect.getInstance().removePersonalName(mainPanel.getKeySession(), GridPopup.this.uname, new AsyncCallback<InfosReport>() {
			public void onSuccess(InfosReport result) {
				mainPanel.getInfosReport().getGrid().getPersonalNames().remove(GridPopup.this.uname);
				GridPopup.this.item.setText(GridPopup.this.item.getName());
				GridPopup.this.item.setTitle(GridPopup.this.item.getName());
			}

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
	}

	@UiHandler("btnTopX")
	public void onTopXClick(ClickEvent event) {
		GridPopup.this.hide();

		TopxDialog dial = new TopxDialog(mainPanel, GridPopup.this.uname);
		dial.center();
	}

	@UiHandler("btnModifyTopX")
	public void onModifyTopXClick(ClickEvent event) {
		GridPopup.this.hide();

		TopxDialog dial = new TopxDialog(mainPanel, GridPopup.this.uname);
		dial.center();
	}

	@UiHandler("btnRemoveTopX")
	public void onRemoveTopXClick(ClickEvent event) {
		GridPopup.this.hide();

		FaWebService.Connect.getInstance().removeTopx(mainPanel.getKeySession(), GridPopup.this.uname, new AsyncCallback<InfosReport>() {
			public void onSuccess(InfosReport result) {
				mainPanel.setGridFromRCP(result);
			}

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
	}

	@UiHandler("btnFlash")
	public void onFlashClick(ClickEvent event) {
		GridPopup.this.hide();

		i = GridPopup.this.item.getI();
		j = GridPopup.this.item.getJ();
		
		String uname = GridPopup.this.item.getUname();
		String element = GridPopup.this.item.getName();
		
		mainPanel.getDisplayPanel().chooseMap(uname, element, false);		
	}
	
	@UiHandler("btnDrillDownAll")
	public void onDrillDownAllClick(ClickEvent event) {
		GridPopup.this.hide();
			mainPanel.showTabWaitPart(true);
			if (GridPopup.this.item.isDrillable()) {
				mainPanel.drillAll(GridPopup.this.item.getI(), GridPopup.this.item.getJ(), GridPopup.this.item.isProjection() ? mainPanel.getActualProjection() : null, true);
			}
			else {
				MessageHelper.openMessageDialog(FreeAnalysisWeb.LBL.Error(), "You're not allowed to drill here");

				mainPanel.showTabWaitPart(false);
			}
	}
	
	@UiHandler("btnDrillUpAll")
	public void onDrillUpAllClick(ClickEvent event) {
		GridPopup.this.hide();
			mainPanel.showTabWaitPart(true);
			if (GridPopup.this.item.isDrillable()) {
				mainPanel.drillAll(GridPopup.this.item.getI(), GridPopup.this.item.getJ(), GridPopup.this.item.isProjection() ? mainPanel.getActualProjection() : null, false);
			}
			else {
				MessageHelper.openMessageDialog(FreeAnalysisWeb.LBL.Error(), "You're not allowed to drill here");

				mainPanel.showTabWaitPart(false);
			}
	}
	

	@UiHandler("btnRemove")
	public void onRemoveClick(ClickEvent event) {
		GridPopup.this.hide();

		List<String> uname = new ArrayList<String>();
		uname.add(GridPopup.this.uname);
		CubeServices.remove(uname, mainPanel);
	}
	
	@UiHandler("btnCumul")
	public void onCumulClick(ClickEvent event) {
		GridPopup.this.hide();
		
		GridCube gc = mainPanel.getInfosReport().getGrid();
		
		mainPanel.setGridFromRCP(CumulateValues.cumulateValues(item.getI(), item.getJ(), gc));
	}
}
