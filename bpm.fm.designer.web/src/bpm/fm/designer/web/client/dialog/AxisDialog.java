package bpm.fm.designer.web.client.dialog;

import java.util.List;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Level;
import bpm.fm.designer.web.client.ClientSession;
import bpm.fm.designer.web.client.Messages;
import bpm.fm.designer.web.client.services.MetricService;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.dialog.ItemLinkedDialog;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

public class AxisDialog extends AbstractDialogBox {

	private static AxeDialogUiBinder uiBinder = GWT.create(AxeDialogUiBinder.class);

	interface MyStyle extends CssResource {
		String grid();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel contentPanel, gridPanel;

	@UiField
	Label lblName;

	@UiField
	TextBox txtName;

	@UiField
	Image imgAdd, imgDelete, imgRefresh, imgEdit, imgLink, imgUp, imgDown;

	private DataGrid<Level> dataGrid;
	private ListDataProvider<Level> dataProvider;

	private SingleSelectionModel<Level> selectionModel;

	private Axis axe;

	interface AxeDialogUiBinder extends UiBinder<Widget, AxisDialog> {
	}

	private CloseHandler<PopupPanel> refreshCloseHandler;

	public AxisDialog(CloseHandler<PopupPanel> refreshCloseHandler) {
		super(Messages.lbl.axis(), false, true);
		this.refreshCloseHandler = refreshCloseHandler;
		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(Messages.lbl.Ok(), okHandler, Messages.lbl.Cancel(), cancelHandler);

		createGrid();

		gridPanel.add(dataGrid);

		if (axe == null) {
			axe = new Axis();
		}
	}

	public AxisDialog(Axis axe, CloseHandler<PopupPanel> refreshCloseHandler) {
		this(refreshCloseHandler);

		this.axe = axe;
		txtName.setText(axe.getName());

		refresh();
	}

	private void refresh() {
		dataProvider.setList(this.axe.getChildren());
	}

	private void createGrid() {
		dataGrid = new DataGrid<Level>(15);
		dataGrid.addStyleName(style.grid());

		TextCell cell = new TextCell();

		Column<Level, String> colName = new Column<Level, String>(cell) {
			@Override
			public String getValue(Level object) {
				return object.getName();
			}
		};

		Column<Level, String> colTable = new Column<Level, String>(cell) {
			@Override
			public String getValue(Level object) {
				return object.getTableName();
			}
		};

		Column<Level, String> colId = new Column<Level, String>(cell) {
			@Override
			public String getValue(Level object) {
				return object.getColumnId();
			}
		};

		Column<Level, String> colcolName = new Column<Level, String>(cell) {
			@Override
			public String getValue(Level object) {
				return object.getColumnName();
			}
		};

		Column<Level, String> colDs = new Column<Level, String>(cell) {
			@Override
			public String getValue(Level object) {
				return object.getDatasourceName();
			}
		};

		Column<Level, String> colParent = new Column<Level, String>(cell) {
			@Override
			public String getValue(Level object) {
				return object.getParentColumnId();
			}
		};

		Column<Level, String> colGeo = new Column<Level, String>(cell) {
			@Override
			public String getValue(Level object) {
				return object.getGeoColumnId();
			}
		};

		dataGrid.addColumn(colName, Messages.lbl.name());
		dataGrid.addColumn(colTable, Messages.lbl.table());
		dataGrid.addColumn(colId, Messages.lbl.columnId());
		dataGrid.addColumn(colcolName, Messages.lbl.columnName());
		dataGrid.addColumn(colDs, Messages.lbl.datasource());
		dataGrid.addColumn(colParent, Messages.lbl.parentColumnName());
		dataGrid.addColumn(colGeo, Messages.lbl.geoCol());

		dataGrid.setColumnWidth(colName, 100, Unit.PX);
		dataGrid.setColumnWidth(colTable, 120, Unit.PX);
		dataGrid.setColumnWidth(colId, 260, Unit.PX);
		dataGrid.setColumnWidth(colcolName, 120, Unit.PX);
		dataGrid.setColumnWidth(colDs, 120, Unit.PX);
		dataGrid.setColumnWidth(colParent, 100, Unit.PX);
		dataGrid.setColumnWidth(colParent, 100, Unit.PX);

		dataGrid.setEmptyTableWidget(new Label(Messages.lbl.NoLevels()));

		dataProvider = new ListDataProvider<Level>();
		dataProvider.addDataDisplay(dataGrid);

		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName("pageGrid");
		pager.setDisplay(dataGrid);

		selectionModel = new SingleSelectionModel<Level>();
		dataGrid.setSelectionModel(selectionModel);
	}

	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {

			axe.setName(txtName.getText());

			if (axe.getChildren() == null || axe.getChildren().isEmpty()) {
				InformationsDialog dial = new InformationsDialog(Messages.lbl.Error(), Messages.lbl.Ok(), Messages.lbl.Cancel(), Messages.lbl.levelNeeded(), false);
				dial.center();
			}
			else {

				MetricService.Connection.getInstance().addAxe(axe, new AsyncCallback<Axis>() {

					@Override
					public void onSuccess(Axis result) {
						InformationsDialog dial = new InformationsDialog(Messages.lbl.Success(), Messages.lbl.Ok(), Messages.lbl.Cancel(), Messages.lbl.AxisCreated(), false);
						dial.center();

						if (axe.getId() <= 0) {
							LinkThemeDialog d = new LinkThemeDialog(result);
							d.addCloseHandler(refreshCloseHandler);
							d.center();
						}

						AxisDialog.this.hide();
					}

					@Override
					public void onFailure(Throwable caught) {
						InformationsDialog dial = new InformationsDialog(Messages.lbl.Error(), Messages.lbl.Ok(), Messages.lbl.AxisError(), caught.getMessage(), caught);
						dial.center();
					}
				});
			}

		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			AxisDialog.this.hide();
		}
	};

	@UiHandler("imgAdd")
	public void onAdd(ClickEvent e) {
		LevelDialog dial = new LevelDialog(axe, null);

		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				refresh();
			}
		});

		dial.center();
	}

	@UiHandler("imgEdit")
	public void onEdit(ClickEvent e) {
		LevelDialog dial = new LevelDialog(axe, selectionModel.getSelectedObject());

		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				refresh();
			}
		});

		dial.center();
	}

	@UiHandler("imgRefresh")
	public void onRefresh(ClickEvent e) {
		refresh();
	}

	@UiHandler("imgLink")
	public void onLink(ClickEvent e) {
		final Level level = selectionModel.getSelectedObject();
		if (level != null) {
			String login = ClientSession.getInstance().getLogin();
			String password = ClientSession.getInstance().getPassword();
			String vanillaUrl = ClientSession.getInstance().getVanillaUrl();
			
			//TODO: To change with the real group
			Group group = ClientSession.getInstance().getGroup();
			
			ItemLinkedDialog dial = new ItemLinkedDialog(login, password, vanillaUrl, group, level);
			dial.center();
		}
	}

	@UiHandler("imgDelete")
	public void onDelete(ClickEvent e) {
		final Level ds = selectionModel.getSelectedObject();
		if (ds != null) {
			final InformationsDialog dial = new InformationsDialog(Messages.lbl.DeleteLevel(), Messages.lbl.Ok(), Messages.lbl.Cancel(), Messages.lbl.DeleteLevelPartOne() + " " + ds.getName() + " " + Messages.lbl.DeleteLevelPartTwo(), true);
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if (dial.isConfirm()) {
						axe.removeChild(ds);
						refresh();
					}
				}
			});
			dial.center();
		}
	}

	@UiHandler("imgUp")
	public void onUp(ClickEvent e) {
		Level ds = selectionModel.getSelectedObject();

		List<Level> levels = dataProvider.getList();
		int index = levels.indexOf(ds);
		if (index > 0) {
			levels.remove(index);
			levels.add(index - 1, ds);
		}

		dataProvider.setList(levels);
	}

	@UiHandler("imgDown")
	public void onDown(ClickEvent e) {
		Level ds = selectionModel.getSelectedObject();

		List<Level> levels = dataProvider.getList();
		int index = levels.indexOf(ds);
		if (index < levels.size() - 1) {
			levels.remove(index);
			if (index < levels.size() - 1) {
				levels.add(index + 1, ds);
			}
			else {
				levels.add(ds);
			}
		}

		dataProvider.setList(levels);
	}
}
