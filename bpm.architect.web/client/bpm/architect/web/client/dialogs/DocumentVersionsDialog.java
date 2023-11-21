package bpm.architect.web.client.dialogs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.images.Images;
import bpm.architect.web.client.panels.ConsultPanel;
import bpm.architect.web.client.services.ArchitectService;
import bpm.architect.web.client.utils.DocumentHelper;
import bpm.architect.web.shared.HistoricLog.HistoricType;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.commons.client.utils.DatagridHandler;
import bpm.mdm.model.supplier.Contract;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

public class DocumentVersionsDialog extends AbstractDialogBox implements DatagridHandler<DocumentVersion> {

	private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";

	private static DialogStepsUiBinder uiBinder = GWT.create(DialogStepsUiBinder.class);

	interface DialogStepsUiBinder extends UiBinder<Widget, DocumentVersionsDialog> {
	}

	interface MyStyle extends CssResource {
		String imgGrid();

		String selectedBackground();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel panelGrid;

	private ConsultPanel parent;
	private Contract contract;

	private ListDataProvider<DocumentVersion> dataProvider;
	private SingleSelectionModel<DocumentVersion> selectionModel;

	public DocumentVersionsDialog(ConsultPanel parent, Contract contract) {
		super(Labels.lblCnst.DocumentVersions() + " : " + contract.getFileVersions().getName(), false, true);
		this.parent = parent;
		this.contract = contract;

		setWidget(uiBinder.createAndBindUi(this));

		createButton(LabelsConstants.lblCnst.Close(), closeHandler);

		DataGrid<DocumentVersion> grid = buildGrid();
		panelGrid.setWidget(grid);

		loadHistoric(contract.getFileVersions().getDocumentVersions());
	}

	private void loadHistoric(List<DocumentVersion> result) {
		if (result == null) {
			result = new ArrayList<>();
		}

		dataProvider.setList(result);
	}

	private DataGrid<DocumentVersion> buildGrid() {
		final DateTimeFormat dateFormatter = DateTimeFormat.getFormat(DATE_FORMAT);

		TextCell txtCell = new TextCell();
		Column<DocumentVersion, String> nameColumn = new Column<DocumentVersion, String>(txtCell) {

			@Override
			public String getValue(DocumentVersion object) {
				return object.getParent().getName() + " - V" + object.getVersion();
			}
		};

		Column<DocumentVersion, String> creationDateColumn = new Column<DocumentVersion, String>(txtCell) {

			@Override
			public String getValue(DocumentVersion object) {
				Date date = object.getModificationDate();
				if (date == null || date.equals(new Date(0))) {
					return LabelsConstants.lblCnst.Unknown();
				}
				return dateFormatter.format(date);
			}
		};

		Column<DocumentVersion, String> formatColumn = new Column<DocumentVersion, String>(txtCell) {

			@Override
			public String getValue(DocumentVersion object) {
				return object.getFormat();
			}
		};

		Column<DocumentVersion, String> summaryColumn = new Column<DocumentVersion, String>(txtCell) {

			@Override
			public String getValue(DocumentVersion object) {
				return object.getSummary();
			}
		};

		ButtonImageCell viewCell = new ButtonImageCell(Images.INSTANCE.ic_visibility_black_18dp(), Labels.lblCnst.ViewDocumentVersion(), style.imgGrid());
		Column<DocumentVersion, String> colView = new Column<DocumentVersion, String>(viewCell) {

			@Override
			public String getValue(DocumentVersion object) {
				return "";
			}
		};
		colView.setFieldUpdater(new FieldUpdater<DocumentVersion, String>() {

			@Override
			public void update(int index, final DocumentVersion object, String value) {
				DocumentHelper.viewCurrentDocument(DocumentVersionsDialog.this, contract, object.getId());
			}
		});

		ButtonImageCell backToVersion = new ButtonImageCell(Images.INSTANCE.ic_check_black_18dp(), Labels.lblCnst.SetCurrentVersion(), style.imgGrid());
		Column<DocumentVersion, String> colBackToVersion = new Column<DocumentVersion, String>(backToVersion) {

			@Override
			public String getValue(DocumentVersion object) {
				return "";
			}
		};
		colBackToVersion.setFieldUpdater(new FieldUpdater<DocumentVersion, String>() {

			@Override
			public void update(int index, final DocumentVersion object, String value) {
				setAsCurrent(object);
			}
		});

		dataProvider = new ListDataProvider<DocumentVersion>(new ArrayList<DocumentVersion>());

		DataGrid.Resources resources = new CustomResources();
		DataGrid<DocumentVersion> dataGrid = new DataGrid<DocumentVersion>(10000, resources);
		dataGrid.setSize("100%", "100%");
		dataGrid.addColumn(nameColumn, Labels.lblCnst.Name());
		dataGrid.addColumn(creationDateColumn, Labels.lblCnst.CreationDate());
		dataGrid.addColumn(formatColumn, Labels.lblCnst.Format());
		dataGrid.addColumn(summaryColumn, Labels.lblCnst.Summary());
		dataGrid.addColumn(colView);
		dataGrid.setColumnWidth(colView, "70px");
		dataGrid.addColumn(colBackToVersion);
		dataGrid.setColumnWidth(colBackToVersion, "70px");
		dataGrid.setEmptyTableWidget(new Label(Labels.lblCnst.NoLinkedItem()));
		dataGrid.setRowStyles(new RowStyles<DocumentVersion>() {

			@Override
			public String getStyleNames(DocumentVersion version, int rowIndex) {
				if (contract.getVersionId() != null && contract.getVersionId() == version.getId()) {
					return style.selectedBackground();
				}
				else if (contract.getVersionId() == null && contract.getFileVersions().getLastVersion() != null && contract.getFileVersions().getLastVersion().getId() == version.getId()) {
					return style.selectedBackground();
				}
				return null;
			}
		});

		dataProvider.addDataDisplay(dataGrid);

		this.selectionModel = new SingleSelectionModel<DocumentVersion>();
		dataGrid.setSelectionModel(selectionModel);

		return dataGrid;
	}

	private void setAsCurrent(DocumentVersion version) {
		if (version == null) {
			return;
		}
		
		contract.setVersionId(version.getId());
		
		showWaitPart(true);
		
		ArchitectService.Connect.getInstance().saveOrUpdateContract(contract, HistoricType.CHANGE_CURRENT_VERSION, new GwtCallbackWrapper<Void>(this, true) {

			@Override
			public void onSuccess(Void result) {
				dataProvider.refresh();
				parent.refresh(null);
			}
		}.getAsyncCallback());
	}

	@Override
	public void onRightClick(DocumentVersion item, NativeEvent event) {
	}

	@Override
	public void onDoubleClick(final DocumentVersion item) {
	}

	private ClickHandler closeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
