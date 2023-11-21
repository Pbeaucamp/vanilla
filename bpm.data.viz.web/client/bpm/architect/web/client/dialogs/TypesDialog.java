package bpm.architect.web.client.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.architect.web.client.services.ArchitectService;
import bpm.data.viz.core.preparation.DataPreparation;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomDatagrid;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.DataColumn.FunctionalType;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class TypesDialog extends AbstractDialogBox  {

	private static TypesDialogUiBinder uiBinder = GWT.create(TypesDialogUiBinder.class);

	interface TypesDialogUiBinder extends UiBinder<Widget, TypesDialog> {
	}

	interface MyStyle extends CssResource {
		String label();
		String btnBottom();
		String verticalPanel();
	}

	@UiField
	MyStyle style;

	@UiField
	Label txtCol;

	@UiField
	SimplePanel panelContent;

	@UiField 
	HTMLPanel mainPanel;

	@UiField
	VerticalPanel verticalPanel;


	private VerticalPanel btns; 
	private DataPreparation dataPrep;
	private MultiSelectionModel<DataColumn> multiselectionModel;
	private CustomDatagrid<DataColumn> gridColumns;
	private Map<DataColumn, String> resetColumns;
	private CustomTreeModel model;
	private CellBrowser browser;

	public TypesDialog(DataPreparation dataPrep) {
		super(LabelsConstants.lblCnst.TypeOfColumn(), false, true);
		setWidget(uiBinder.createAndBindUi(this));

		this.dataPrep = dataPrep;

		resetColumns = new HashMap<>();
		for(DataColumn d: dataPrep.getDataset().getMetacolumns()) {
			if(d.getFt()!=null){
				resetColumns.put(d, d.getFt().name());
			}
		}

		buildGrid();

		model = new CustomTreeModel();
		browser = new CellBrowser(model, null);
		browser.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		browser.setHeight("150px");
		browser.setWidth("570px");
		browser.getElement().getStyle().setLeft(10, Unit.PX);
		browser.getElement().getStyle().setTop(60, Unit.PX);
		verticalPanel.add(browser);

		Label lblType = new Label("Select Type");
		lblType.addStyleName(style.label());
		verticalPanel.add(lblType);

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);


		Button btnUpdate = new Button(LabelsConstants.lblCnst.Update());
		btnUpdate.addStyleName(style.btnBottom());
		btnUpdate.addClickHandler(updateHandler);

		Button btnReset = new Button(LabelsConstants.lblCnst.Reset());
		btnReset.addStyleName(style.btnBottom());
		btnReset.addClickHandler(resetHandler);

		Button btnAddParent = new Button(LabelsConstants.lblCnst.AddParent());
		btnAddParent.addStyleName(style.btnBottom());
		btnAddParent.addClickHandler(addParentHandler);

		btns = new VerticalPanel();
		btns.addStyleName(style.verticalPanel());

		btns.add(btnUpdate);
		btns.add(btnReset);
		btns.add(btnAddParent);

		verticalPanel.add(btns);
		
		multiselectionModel.clear();
	}

	public void buildGrid() {
		multiselectionModel = new MultiSelectionModel<>();
		gridColumns = new CustomDatagrid<>(dataPrep.getDataset().getMetacolumns(), multiselectionModel, 250, "Aucune colonne", "Colonnes");
		gridColumns.loadItems(dataPrep.getDataset().getMetacolumns());

		TextCell cell = new TextCell();
		Column<DataColumn, String> colType = new Column<DataColumn, String>(cell) {
			@Override
			public String getValue(DataColumn object) {
				if(object.getFt()!=null) {
					return object.getFt().name();
				}
				return "PAS DEFINI";
			}
		};
		gridColumns.addColumn(colType, "Type", "200px");
		panelContent.setWidget(gridColumns);
	}

	private ClickHandler addParentHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			String dim = "";
			for(String s: model.getSelected().keySet()) {
				if(model.getSelected().get(s)) {
					dim = s;
				}
			}

			if(dim.equals("Dimension")) {
				createParentDialog();
			}
			else {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.UnableToAddParent());
			}
		}
	};

	public void createParentDialog() {
		final AbstractDialogBox dial = new AbstractDialogBox(LabelsConstants.lblCnst.AddParent(), false, true);

		final ListBoxWithButton<DataColumn> lstCols = new ListBoxWithButton<>();
		lstCols.setLabel("Parent");
		lstCols.addItem("Aucune selection");
		for(DataColumn d: dataPrep.getDataset().getMetacolumns()) {
			lstCols.addItem(d);
		}


		HTMLPanel html = new HTMLPanel("");
		html.setHeight("80px");
		html.setWidth("500px");
		html.add(lstCols);

		dial.setWidget(html);
		dial.createButtonBar(LabelsConstants.lblCnst.Confirmation(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				for(DataColumn d: dataPrep.getDataset().getMetacolumns()) {
					if(multiselectionModel.isSelected(d)) {
						//map.put(d, "DIMENSION:"+lstCols.getSelectedItem());
						d.setFt(FunctionalType.DIMENSION);
					}
				}
				dial.hide();
				//stock parent
			}
		}, LabelsConstants.lblCnst.Cancel(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				dial.hide();
			}
		});
		dial.center();
	}
	private ClickHandler confirmHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			ArchitectService.Connect.getInstance().updateDataset(dataPrep.getDataset(), new AsyncCallback<Void>() {

				@Override
				public void onSuccess(Void result) {
					hide();

				}

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub

				}
			});
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			for(DataColumn d: dataPrep.getDataset().getMetacolumns()) {
				if(resetColumns.containsKey(d)){
					d.setFt(FunctionalType.valueOf(resetColumns.get(d)));
				}
				else {
					d.setFt(null);
				}
			}
			
			hide();
		}
	};

	private ClickHandler updateHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			
			String dim = "";
			for(String s: model.getSelected().keySet()) {
				if(model.getSelected().get(s)) {
					dim = s;
				}
			}
			
			for(DataColumn d: dataPrep.getDataset().getMetacolumns()) {
				if(multiselectionModel.isSelected(d)){
					if(model.getSelectedItem() != null) {
						d.setFt(FunctionalType.valueOf(model.getSelectedItem()));
					}
					else if(dim.equals("Dimension")) {
						d.setFt(FunctionalType.DIMENSION);
					}
				}
			}
			buildGrid();
			
			multiselectionModel.clear();
		}
	};

	private ClickHandler resetHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
//			for(DataColumn d: dataPrep.getDataset().getMetacolumns()) {
////				if(resetColumns.containsKey(d)){
////					d.setFt(FunctionalType.valueOf(resetColumns.get(d)));
////				}
//				//else {
//					d.setFt(null);
//				//}
//			}
			
			for(DataColumn d: dataPrep.getDataset().getMetacolumns()) {
				if(multiselectionModel.isSelected(d)){
					d.setFt(null);
				}
			}

			buildGrid();
		}
	};
	private static class Type {
		private final String name;
		private final List<String> sousTypes = new ArrayList<String>();

		public Type(String name) {
			this.name = name;
		}

		public String addSousType(String sousType) {
			sousTypes.add(sousType);
			return sousType;
		}

		public String getName() {
			return name;
		}

		public List<String> getSousTypes() {
			return sousTypes;
		}
	}

	private static class CustomTreeModel implements TreeViewModel {

		private final List<Type> types;

		private SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();

		private Map<String, Boolean> selected;

		public CustomTreeModel() {
			types = new ArrayList<Type>();

			Type geo = new Type("Geographique");
			types.add(geo);

			geo.addSousType(FunctionalType.CONTINENT.name());
			geo.addSousType(FunctionalType.PAYS.name());
			geo.addSousType(FunctionalType.REGION.name());
			geo.addSousType(FunctionalType.COMMUNE.name());
			geo.addSousType(FunctionalType.ADRESSE.name());
			geo.addSousType(FunctionalType.CODE_POSTAL.name());
			geo.addSousType(FunctionalType.ZONEID.name());
			geo.addSousType(FunctionalType.GEOLOCAL.name());
			geo.addSousType(FunctionalType.LATITUDE.name());
			geo.addSousType(FunctionalType.LONGITUDE.name());

			Type date = new Type("Date");
			types.add(date);

			date.addSousType(FunctionalType.ANNEE.name());
			date.addSousType(FunctionalType.TRIMESTRE.name());
			date.addSousType(FunctionalType.MOIS.name());
			date.addSousType(FunctionalType.SEMAINE.name());

			Type mesure = new Type("Mesure");
			types.add(mesure);

			mesure.addSousType(FunctionalType.SUM.name());
			mesure.addSousType(FunctionalType.AVG.name());
			mesure.addSousType(FunctionalType.COUNT.name());
			mesure.addSousType(FunctionalType.MIN.name());
			mesure.addSousType(FunctionalType.MAX.name());

			Type dimension = new Type("Dimension");
			types.add(dimension);
			
			dimension.addSousType(FunctionalType.EXCLUSIF.name());
			dimension.addSousType(FunctionalType.NON_EXCLUSIF.name());

			selected = new HashMap<>();
			for(Type t: types) {
				selected.put(t.getName(), false);
			}
		}

		public <T> NodeInfo<?> getNodeInfo(T value) {
			if (value == null) {
				// LEVEL 0.

				ListDataProvider<Type> dataProvider = new ListDataProvider<Type>(types);
				Cell<Type> cell = new AbstractCell<Type>() {
					@Override
					public void render(Context context, Type value, SafeHtmlBuilder sb) {
						// TODO Auto-generated method stub
						sb.appendEscaped(value.getName());
					}
				};

				return new DefaultNodeInfo<Type>(dataProvider, cell);
			} else if (value instanceof Type) {
				// LEVEL 2 - LEAF.
				ListDataProvider<String> dataProvider = new ListDataProvider<String>(((Type) value).getSousTypes());
				selected = new HashMap<>();
				for(Type t: types) {
					if(((Type) value).getName().equals(t.getName())){
						selected.put(t.getName(), true);
					} else {
						selected.put(t.getName(), false);
					}
				}
				selectionModel = new SingleSelectionModel<String>();
				return new DefaultNodeInfo<String>(dataProvider, new TextCell(),
						selectionModel, null);
			}
			return null;
		}

		public boolean isLeaf(Object value) {
			if (value instanceof String) {
				return true;
			}
			return false;
		}

		public String getSelectedItem() {
			return selectionModel.getSelectedObject();
		}

		public Map<String, Boolean> getSelected() {
			return selected;
		}
	}
}
