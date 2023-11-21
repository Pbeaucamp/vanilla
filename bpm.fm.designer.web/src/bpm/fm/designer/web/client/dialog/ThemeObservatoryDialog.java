package bpm.fm.designer.web.client.dialog;

import java.util.List;

import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.Theme;
import bpm.fm.designer.web.client.Messages;
import bpm.fm.designer.web.client.services.MetricService;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.dialog.RepositoryDialog;
import bpm.gwt.commons.client.dialog.RepositoryDirectoryDialog;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.LoginService;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.KpiTheme;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

public class ThemeObservatoryDialog extends AbstractDialogBox {

	private static DateTimeFormat dateFormatter = DateTimeFormat.getFormat("HH:mm - dd/MM/yyyy");

	private static ThemeObservatoryDialogUiBinder uiBinder = GWT.create(ThemeObservatoryDialogUiBinder.class);

	interface ThemeObservatoryDialogUiBinder extends UiBinder<Widget, ThemeObservatoryDialog> {
	}

	interface MyStyle extends CssResource {
		String grid();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel contentPanel, gridPanel;

	@UiField
	Image imgAdd, imgDelete, imgRefresh, imgEdit, imgPublish;

	private DataGrid<Theme> dataGrid;
	private ListDataProvider<Theme> dataProvider;

	private SingleSelectionModel<Theme> selectionModel;

	private Observatory observatory;

	public ThemeObservatoryDialog(Observatory observatory) {
		super(Messages.lbl.observatoryAddTheme(), false, true);
		setWidget(uiBinder.createAndBindUi(this));

		this.observatory = observatory;

		createButtonBar(Messages.lbl.Ok(), okHandler, Messages.lbl.Cancel(), cancelHandler);

		createGrid();

		gridPanel.add(dataGrid);

		loadThemes();
	}

	private void loadThemes() {
		dataProvider.setList(observatory.getThemes());
	}

	private void createGrid() {
		dataGrid = new DataGrid<Theme>(15);
		dataGrid.addStyleName(style.grid());

		TextCell cell = new TextCell();

		Column<Theme, String> colName = new Column<Theme, String>(cell) {
			@Override
			public String getValue(Theme object) {
				return object.getName();
			}
		};

		Column<Theme, String> colDate = new Column<Theme, String>(cell) {
			@Override
			public String getValue(Theme object) {
				if (object.getCreationDate() != null) {
					return dateFormatter.format(object.getCreationDate());
				}
				return "";
			}
		};

		dataGrid.addColumn(colName, Messages.lbl.name());
		dataGrid.addColumn(colDate, Messages.lbl.CreationDate());

		dataGrid.setColumnWidth(colName, 100, Unit.PX);
		dataGrid.setColumnWidth(colDate, 150, Unit.PX);

		dataGrid.setEmptyTableWidget(new Label("No existing themes"));

		dataProvider = new ListDataProvider<Theme>();
		dataProvider.addDataDisplay(dataGrid);

		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName("pageGrid");
		pager.setDisplay(dataGrid);

		selectionModel = new SingleSelectionModel<Theme>();
		dataGrid.setSelectionModel(selectionModel);
	}

	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			
			MetricService.Connection.getInstance().addObservatory(observatory, new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable caught) {
					InformationsDialog dial = new InformationsDialog(Messages.lbl.Error(), Messages.lbl.Ok(), Messages.lbl.problemSaveObs(), caught.getMessage(), caught);
					dial.center();
				}
				@Override
				public void onSuccess(Void result) {
					InformationsDialog dial = new InformationsDialog(Messages.lbl.Success(), Messages.lbl.Ok(), Messages.lbl.Cancel(), Messages.lbl.successSaveObs(), false);
					dial.center();
					ThemeObservatoryDialog.this.hide();
				}
			});
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			ThemeObservatoryDialog.this.hide();
		}
	};

	@UiHandler("imgAdd")
	public void onAdd(ClickEvent e) {
		final ThemeDialog dial = new ThemeDialog(null);

		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					Theme theme = dial.getTheme();
					observatory.getThemes().add(theme);
					loadThemes();
				}
			}
		});

		dial.center();
	}

	@UiHandler("imgEdit")
	public void onEdit(ClickEvent e) {
		final ThemeDialog dial = new ThemeDialog(selectionModel.getSelectedObject());

		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					// Theme theme = dial.getTheme();
					// observatory.getThemes().add(theme);
					loadThemes();
				}
			}
		});

		dial.center();
	}

	@UiHandler("imgDelete")
	public void onDelete(ClickEvent e) {
		final Theme ds = selectionModel.getSelectedObject();
		if (ds != null) {
			final InformationsDialog dial = new InformationsDialog(Messages.lbl.deleteTheme(), Messages.lbl.Ok(), Messages.lbl.Cancel(), Messages.lbl.deleteThemePartOne() + " " + ds.getName() + " " + Messages.lbl.deleteThemePartTwo(), true);
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if (dial.isConfirm()) {
						observatory.getThemes().remove(ds);
						loadThemes();
					}
				}
			});
			dial.center();
		}
	}

	@UiHandler("imgPublish")
	public void onPublish(ClickEvent event) {
		final Theme ds = selectionModel.getSelectedObject();
		if (ds != null) {
			showWaitPart(true);
			
			final KpiTheme itemToSave = new KpiTheme(ds.getId());
			
			LoginService.Connect.getInstance().getAvailableGroups(new GwtCallbackWrapper<List<Group>>(this, true) {

				@Override
				public void onSuccess(List<Group> result) {
					RepositoryDirectoryDialog dial = new RepositoryDirectoryDialog(IRepositoryApi.KPI_THEME, result, itemToSave);
					dial.center();
				}
			}.getAsyncCallback());
		}
	}

	@UiHandler("imgMetadata")
	public void onMetadata(ClickEvent event) {
		final Theme ds = selectionModel.getSelectedObject();
		if (ds != null) {
			MetadataCreationDialog dial = new MetadataCreationDialog(ds);
			dial.center();
		}
	}
}
