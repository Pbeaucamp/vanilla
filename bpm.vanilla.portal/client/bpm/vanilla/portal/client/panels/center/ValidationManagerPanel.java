package bpm.vanilla.portal.client.panels.center;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.client.viewer.comments.DefineValidationDialog;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.platform.core.beans.validation.Validation;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.images.PortalImage;
import bpm.vanilla.portal.client.utils.ToolsGWT;
import bpm.vanilla.portal.client.widget.custom.CustomResources;
import bpm.vanilla.portal.client.widget.custom.SchedulerImageCell;

public class ValidationManagerPanel extends Tab {

	private static ValidationManagerPanelUiBinder uiBinder = GWT.create(ValidationManagerPanelUiBinder.class);

	interface ValidationManagerPanelUiBinder extends UiBinder<Widget, ValidationManagerPanel> {
	}

	interface MyStyle extends CssResource {
		String mainPanel();

		String pager();

		String imgPlanned();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelToolbar;

	@UiField
	SimplePanel panelContent, panelPager;

	@UiField
	Image /*btnAddValidation,*/ btnShowValidation, btnStopValidation;
	
	private InfoUser infoUser;

	private ListDataProvider<Validation> dataProvider;
	private SingleSelectionModel<Validation> selectionModel;
	private ListHandler<Validation> sortHandler;

	private DateTimeFormat sdf = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM);

	public ValidationManagerPanel(TabManager tabManager) {
		super(tabManager, ToolsGWT.lblCnst.ValidationManager(), true);
		this.add(uiBinder.createAndBindUi(this));
		this.infoUser = biPortal.get().getInfoUser();

		panelContent.add(createGridData());

		panelToolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);
		this.addStyleName(style.mainPanel());

		loadValidations();
	}

	private void loadValidations() {
		ReportingService.Connect.getInstance().getValidations(true, new GwtCallbackWrapper<List<Validation>>(this, true, true) {

			@Override
			public void onSuccess(List<Validation> result) {
				dataProvider.setList(result);
				sortHandler.setList(dataProvider.getList());
				selectionModel.clear();
			}
		}.getAsyncCallback());
	}

	private DataGrid<Validation> createGridData() {
		CustomCell cell = new CustomCell();

		Column<Validation, ImageResource> actifColumn = new Column<Validation, ImageResource>(new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Validation object) {
				if (!object.isActif()) {
					return PortalImage.INSTANCE.ic_flag_grey600_18dp();
				}
				else if (object.isValid()) {
					return PortalImage.INSTANCE.ic_flag_green_18dp();
				}
				else {
					return PortalImage.INSTANCE.ic_flag_red_18dp();
				}
			}
		};
		
		Column<Validation, String> itemName = new Column<Validation, String>(cell) {

			@Override
			public String getValue(Validation object) {
				if (object.getItem() != null) {
					return object.getItem().getItemName();
				}
				return "";
			}
		};
		itemName.setSortable(true);

		Column<Validation, String> beginDateColumn = new Column<Validation, String>(cell) {

			@Override
			public String getValue(Validation object) {
				return object.getDateBegin() != null ? sdf.format(object.getDateBegin()) : LabelsConstants.lblCnst.Unknown();
			}
		};
		beginDateColumn.setSortable(true);
		
		Column<Validation, String> itemOfflineColumn = new Column<Validation, String>(cell) {

			@Override
			public String getValue(Validation object) {
				return object.isOffline() ? LabelsConstants.lblCnst.Yes() : LabelsConstants.lblCnst.No();
			}
		};
		
		Column<Validation, String> nbCommentatorsColumn = new Column<Validation, String>(cell) {

			@Override
			public String getValue(Validation object) {
				return object.getCommentators() != null ? String.valueOf(object.getCommentators().size()) : "0";
			}
		};
		
		Column<Validation, String> nbValidatorsColumn = new Column<Validation, String>(cell) {

			@Override
			public String getValue(Validation object) {
				return object.getValidators() != null ? String.valueOf(object.getValidators().size()) : "0";
			}
		};

		DataGrid.Resources resources = new CustomResources();
		DataGrid<Validation> dataGrid = new DataGrid<Validation>(12, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.addColumn(actifColumn, ToolsGWT.lblCnst.ValidationState());
		dataGrid.setColumnWidth(actifColumn, "80px");
		dataGrid.addColumn(itemName, ToolsGWT.lblCnst.ItemName());
		dataGrid.addColumn(beginDateColumn, ToolsGWT.lblCnst.BeginDate());
		dataGrid.addColumn(itemOfflineColumn, ToolsGWT.lblCnst.IsItemOffline());
		dataGrid.addColumn(nbCommentatorsColumn, LabelsConstants.lblCnst.NumberOfCommentators());
		dataGrid.addColumn(nbValidatorsColumn, LabelsConstants.lblCnst.NumberOfValidators());
		dataGrid.setEmptyTableWidget(new Label(ToolsGWT.lblCnst.NoResult()));

		dataProvider = new ListDataProvider<Validation>();
		dataProvider.addDataDisplay(dataGrid);

		sortHandler = new ListHandler<Validation>(new ArrayList<Validation>());
		sortHandler.setComparator(itemName, new Comparator<Validation>() {

			@Override
			public int compare(Validation o1, Validation o2) {
				if (o1.getItem() != null && o2.getItem() != null) {
					return o1.getItem().getItemName().compareTo(o2.getItem().getItemName());
				}
				if (o1.getItem() == null) {
					return -1;
				}
				return 0;
			}
		});
		sortHandler.setComparator(beginDateColumn, new Comparator<Validation>() {

			@Override
			public int compare(Validation o1, Validation o2) {
				if (o1.getDateBegin() == null) {
					return -1;
				}
				else if (o2.getDateBegin() == null) {
					return 1;
				}

				return o2.getDateBegin().before(o1.getDateBegin()) ? -1 : o2.getDateBegin().after(o1.getDateBegin()) ? 1 : 0;
			}
		});
		dataGrid.addColumnSortHandler(sortHandler);

		// Add a selection model so we can select cells.
		selectionModel = new SingleSelectionModel<Validation>();
		selectionModel.addSelectionChangeHandler(selectionChangeHandler);
		dataGrid.setSelectionModel(selectionModel);

		// Create a Pager to control the table.
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName(style.pager());
		pager.setDisplay(dataGrid);

		panelPager.setWidget(pager);

		return dataGrid;
	}

	@UiHandler("btnRefresh")
	public void onRefreshClick(ClickEvent event) {
		loadValidations();
	}

	@UiHandler("btnShowValidation")
	public void onPropertiesClick(ClickEvent event) {
		Validation selectedValidation = selectionModel.getSelectedObject();
		showProcess(selectedValidation);
	}

	@UiHandler("btnStopValidation")
	public void onValidationStop(ClickEvent event) {
		Validation selectedValidation = selectionModel.getSelectedObject();
		selectedValidation.setActif(false);

		manageValidation(selectedValidation);
	}

	public void manageValidation(Validation validation) {
		validation.setActif(false);
		
		ReportingService.Connect.getInstance().setReportValidation(validation.getItem(), validation, false, new GwtCallbackWrapper<Void>(this, true, true) {

			@Override
			public void onSuccess(Void result) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.CommentaryValidation(), ToolsGWT.lblCnst.TheProcessusHasBeenStopped());
				loadValidations();
			}
		}.getAsyncCallback());
	}

	private void showProcess(Validation selectedValidation) {
		if (selectedValidation == null) {
			return;
		}

		DefineValidationDialog dial = new DefineValidationDialog(infoUser, selectedValidation.getItem());
		dial.center();
	}

	private Handler selectionChangeHandler = new Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			Validation selectedValidation = selectionModel.getSelectedObject();
			btnShowValidation.setVisible(selectedValidation != null);
			btnStopValidation.setVisible(selectedValidation != null && selectedValidation.isActif() && !selectedValidation.isValid());
		}
	};

	private class CustomCell extends TextCell {

		public CustomCell() {
			super();
		}

		@Override
		public Set<String> getConsumedEvents() {
			Set<String> consumedEvents = new HashSet<String>();
			consumedEvents.add("dblclick");
			consumedEvents.add("contextmenu");
			return consumedEvents;
		}

		@Override
		public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
			Validation obj = (Validation) context.getKey();
			if (event.getType().equals("dblclick")) {
				showProcess(obj);
			}
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
	}

}
