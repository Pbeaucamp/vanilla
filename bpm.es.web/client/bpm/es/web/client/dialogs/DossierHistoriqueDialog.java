package bpm.es.web.client.dialogs;

import java.util.ArrayList;
import java.util.List;

import bpm.es.web.client.I18N.Labels;
import bpm.es.web.client.images.Images;
import bpm.es.web.shared.beans.Dossier;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.CustomResources;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class DossierHistoriqueDialog extends AbstractDialogBox {

	private DateTimeFormat dtf = DateTimeFormat.getFormat("HH:mm dd/MM/yyyy");

	private static NameDateGridUiBinder uiBinder = GWT.create(NameDateGridUiBinder.class);

	interface NameDateGridUiBinder extends UiBinder<Widget, DossierHistoriqueDialog> {
	}

	interface MyStyle extends CssResource {
		String imgGrid();

		String imgPlanned();

		String pager();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel panelGrid;

	private DataGrid<Dossier> datagrid;
	private ListDataProvider<Dossier> dataProvider;
	private ListHandler<Dossier> sortHandler;

	private List<Dossier> items;

	public DossierHistoriqueDialog(Dossier dossier) {
		super(dossier.getId() + " - " + Labels.lblCnst.Historique(), true, true);

		setWidget(uiBinder.createAndBindUi(this));

		createButton(LabelsConstants.lblCnst.Close(), closeHandler);

		datagrid = buildGrid();
		panelGrid.setWidget(datagrid);
		
		createContent();
	}
	
	private void createContent() {
		Dossier dos1 = new Dossier();
		dos1.setId("DOS03");
		dos1.setCreationDate(dtf.parse("11:52 20/06/2016"));
		
		Dossier dos2 = new Dossier();
		dos2.setId("DOS03");
		dos2.setCreationDate(dtf.parse("11:59 20/06/2016"));
		
		Dossier dos3 = new Dossier();
		dos3.setId("DOS03");
		dos3.setCreationDate(dtf.parse("10:20 22/06/2016"));
		
		Dossier dos4 = new Dossier();
		dos4.setId("DOS03");
		dos4.setCreationDate(dtf.parse("13:28 22/06/2016"));
		
		List<Dossier> doss = new ArrayList<>();
		doss.add(dos1);
		doss.add(dos2);
		doss.add(dos3);
		doss.add(dos4);

		loadItems(doss);
	}

	public void loadItems(List<Dossier> result) {
		this.items = result != null ? result : new ArrayList<Dossier>();
		dataProvider.setList(items);
		sortHandler.setList(dataProvider.getList());
	}

	private DataGrid<Dossier> buildGrid() {
		TextCell txtCell = new TextCell();

		Column<Dossier, String> colDate = new Column<Dossier, String>(txtCell) {

			@Override
			public String getValue(Dossier object) {
				return object.getCreationDate() != null ? dtf.format(object.getCreationDate()) : Labels.lblCnst.Unknown();
			}
		};
		colDate.setSortable(true);

		Column<Dossier, String> colModificator = new Column<Dossier, String>(txtCell) {

			@Override
			public String getValue(Dossier object) {
				return object.getManager().getLastName() != null ? object.getManager().getLastName() + " " + object.getManager().getFirstName() : Labels.lblCnst.Unknown();
			}
		};
		colModificator.setSortable(true);

		ButtonImageCell showCell = new ButtonImageCell(Images.INSTANCE.ic_visibility_black_27dp(), style.imgGrid());
		Column<Dossier, String> colShow = new Column<Dossier, String>(showCell) {

			@Override
			public String getValue(Dossier object) {
				return "";
			}
		};
		colShow.setFieldUpdater(new FieldUpdater<Dossier, String>() {

			@Override
			public void update(int index, final Dossier object, String value) {

			}
		});

		DataGrid.Resources resources = new CustomResources();
		DataGrid<Dossier> dataGrid = new DataGrid<Dossier>(10000, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.addColumn(colDate, Labels.lblCnst.ModificationDate());
		dataGrid.addColumn(colDate, Labels.lblCnst.Manager());
		dataGrid.addColumn(colShow);
		dataGrid.setColumnWidth(colShow, "70px");
		dataGrid.setEmptyTableWidget(new Label("No Data"));

		// columns.put(surnameColumn, Labels.lblCnst.Surname());
		// columns.put(nameColumn, Labels.lblCnst.Name());
		// columns.put(mailColumn, Labels.lblCnst.Mail());
		// columns.put(creationDate, Labels.lblCnst.CreationDate());

		dataProvider = new ListDataProvider<Dossier>();
		dataProvider.addDataDisplay(dataGrid);

		sortHandler = new ListHandler<Dossier>(new ArrayList<Dossier>());
		dataGrid.addColumnSortHandler(sortHandler);
		sortHandler.setList(dataProvider.getList());

		return dataGrid;
	}

	private ClickHandler closeHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
