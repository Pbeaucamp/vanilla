package bpm.gwt.commons.client.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.v2.GridPanel;
import bpm.gwt.commons.client.custom.v2.ParameterizedCheckboxCell;
import bpm.vanilla.platform.core.beans.data.D4CTypes;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Widget;

public class DatacolumnD4CDialog extends AbstractDialogBox {

	private enum TypeOption {
		DISPLAYPOPUP, FACETTE, FACETTEMULTIPLE, TABLEAU, INFOBULLE, TRI, DATEPONCTUELLE, DATEDEBUT, DATEFIN, IMAGES, NUAGEDEMOT, NUAGEDEMOTNOMBRE, DATEHEURE, FRISELIBELLE, FRISEDESCRIPTION, FRISEDATE;
	}

	private static DatacolumnD4CDialogUiBinder uiBinder = GWT.create(DatacolumnD4CDialogUiBinder.class);

	interface DatacolumnD4CDialogUiBinder extends UiBinder<Widget, DatacolumnD4CDialog> {
	}

	interface MyStyle extends CssResource {
	}

	@UiField
	MyStyle style;

	@UiField
	GridPanel<DataColumn> grid;

	private boolean hasChange;

	public DatacolumnD4CDialog(List<DataColumn> columns) {
		super(LabelsConstants.lblCnst.ColumnsOptions(), false, true);

		setWidget(uiBinder.createAndBindUi(this));

		createButton(LabelsConstants.lblCnst.Close(), closeHandler);

		buildGrid();
		loadItems(columns);
	}

	private void loadItems(List<DataColumn> columns) {
		if (columns == null) {
			new ArrayList<DataColumn>();
		}
		grid.loadItems(columns);
	}

	private void buildGrid() {
		grid.setTopManually(0);

		TextCell txtCell = new TextCell();
		Column<DataColumn, String> columnName = new Column<DataColumn, String>(txtCell) {

			@Override
			public String getValue(DataColumn object) {
				return object.getColumnName();
			}
		};

		grid.addColumn(LabelsConstants.lblCnst.Name(), columnName, "120px", null);
		for (TypeOption type : TypeOption.values()) {
			addColumn(getLabel(type), type);
		}
	}

	private void addColumn(String label, final TypeOption type) {
		Column<DataColumn, Boolean> column = new Column<DataColumn, Boolean>(new ParameterizedCheckboxCell()) {

			@Override
			public Boolean getValue(DataColumn object) {
				return getOptionValue(object, type);
			}
		};
		column.setFieldUpdater(new FieldUpdater<DataColumn, Boolean>() {
			@Override
			public void update(int index, DataColumn object, Boolean value) {
				setValue(object, type, value);
			}
		});
		grid.addColumn(label, column, "100px", null);
	}

	private String getLabel(TypeOption type) {
		switch (type) {
		case DATEDEBUT:
			return LabelsConstants.lblCnst.DateDebut();
		case DATEFIN:
			return LabelsConstants.lblCnst.DateFin();
		case DATEHEURE:
			return LabelsConstants.lblCnst.DateHeure();
		case DATEPONCTUELLE:
			return LabelsConstants.lblCnst.DatePonctuelle();
		case DISPLAYPOPUP:
			return LabelsConstants.lblCnst.AffichagePopup();
		case FACETTE:
			return LabelsConstants.lblCnst.Facette();
		case FACETTEMULTIPLE:
			return LabelsConstants.lblCnst.FacetteMulti();
		case FRISEDATE:
			return LabelsConstants.lblCnst.FriseDate();
		case FRISEDESCRIPTION:
			return LabelsConstants.lblCnst.FriseDescription();
		case FRISELIBELLE:
			return LabelsConstants.lblCnst.FriseLibelle();
		case IMAGES:
			return LabelsConstants.lblCnst.Images();
		case INFOBULLE:
			return LabelsConstants.lblCnst.Infobulle();
		case NUAGEDEMOT:
			return LabelsConstants.lblCnst.NuageDeMots();
		case NUAGEDEMOTNOMBRE:
			return LabelsConstants.lblCnst.NuageDeMotsNombre();
		case TABLEAU:
			return LabelsConstants.lblCnst.Tableau();
		case TRI:
			return LabelsConstants.lblCnst.Tri();
		default:
			break;
		}
		return LabelsConstants.lblCnst.Unknown();
	}

	private Boolean getOptionValue(DataColumn object, TypeOption type) {
		if (object.getTypes() == null) {
			return false;
		}

		switch (type) {
		case DATEDEBUT:
			return object.getTypes().isDateDebut();
		case DATEFIN:
			return object.getTypes().isDateFin();
		case DATEHEURE:
			return object.getTypes().isDateHeure();
		case DATEPONCTUELLE:
			return object.getTypes().isDatePonctuelle();
		case DISPLAYPOPUP:
			return object.getTypes().isDisplayPopup();
		case FACETTE:
			return object.getTypes().isFacette();
		case FACETTEMULTIPLE:
			return object.getTypes().isFacetteMultiple();
		case FRISEDATE:
			return object.getTypes().isFriseDate();
		case FRISEDESCRIPTION:
			return object.getTypes().isFriseDescription();
		case FRISELIBELLE:
			return object.getTypes().isFriseLibelle();
		case IMAGES:
			return object.getTypes().isImages();
		case INFOBULLE:
			return object.getTypes().isInfobulle();
		case NUAGEDEMOT:
			return object.getTypes().isNuageDeMot();
		case NUAGEDEMOTNOMBRE:
			return object.getTypes().isNuageDeMotNombre();
		case TABLEAU:
			return object.getTypes().isTableau();
		case TRI:
			return object.getTypes().isTri();
		default:
			break;
		}
		return false;
	}

	private Boolean setValue(DataColumn object, TypeOption type, boolean value) {
		if (object.getTypes() == null) {
			object.setTypes(new D4CTypes());
		}

		this.hasChange = true;

		switch (type) {
		case DATEDEBUT:
			object.getTypes().setDateDebut(value);
			break;
		case DATEFIN:
			object.getTypes().setDateFin(value);
			break;
		case DATEHEURE:
			object.getTypes().setDateHeure(value);
			break;
		case DATEPONCTUELLE:
			object.getTypes().setDatePonctuelle(value);
			break;
		case DISPLAYPOPUP:
			object.getTypes().setDisplayPopup(value);
			break;
		case FACETTE:
			object.getTypes().setFacette(value);
			break;
		case FACETTEMULTIPLE:
			object.getTypes().setFacetteMultiple(value);
			break;
		case FRISEDATE:
			object.getTypes().setFriseDate(value);
			break;
		case FRISEDESCRIPTION:
			object.getTypes().setFriseDescription(value);
			break;
		case FRISELIBELLE:
			object.getTypes().setFriseLibelle(value);
			break;
		case IMAGES:
			object.getTypes().setImages(value);
			break;
		case INFOBULLE:
			object.getTypes().setInfobulle(value);
			break;
		case NUAGEDEMOT:
			object.getTypes().setNuageDeMot(value);
			break;
		case NUAGEDEMOTNOMBRE:
			object.getTypes().setNuageDeMotNombre(value);
			break;
		case TABLEAU:
			object.getTypes().setTableau(value);
			break;
		case TRI:
			object.getTypes().setTri(value);
			break;
		default:
			break;
		}
		return false;
	}

	public boolean hasChange() {
		return hasChange;
	}

	private ClickHandler closeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
