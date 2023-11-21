package bpm.vanillahub.web.client.properties.parameters;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.aklabox.commons.shared.AklaboxConnection;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.workflow.commons.client.images.Images;
import bpm.vanillahub.core.beans.activities.AklaboxActivity;
import bpm.vanillahub.core.beans.activities.attributes.AklaboxDispatch;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.dialogs.AddDispatchRuleDialog;
import bpm.vanillahub.web.client.properties.creation.AklaboxActivityProperties;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

public class AklaboxDispatchPanel extends Composite {

	private static WebServiceParametersPanelUiBinder uiBinder = GWT.create(WebServiceParametersPanelUiBinder.class);

	interface WebServiceParametersPanelUiBinder extends UiBinder<Widget, AklaboxDispatchPanel> {
	}

	interface MyStyle extends CssResource {
		String imgGrid();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel panelGrid;
	
	private AklaboxActivityProperties parent;
	private AklaboxActivity activity;
	
	private ListDataProvider<AklaboxDispatch> dataProvider;

	public AklaboxDispatchPanel(AklaboxActivityProperties parent, AklaboxActivity activity) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.activity = activity;
	
		panelGrid.setWidget(createGrid());
		
		loadData(activity.getRules());
	}

	private void loadData(List<AklaboxDispatch> result) {
		if (result != null) {
			dataProvider.setList(result);
		}
		else {
			dataProvider.setList(new ArrayList<AklaboxDispatch>());
		}
	}

	private DataGrid<AklaboxDispatch> createGrid() {
		TextCell cell = new TextCell();
		Column<AklaboxDispatch, String> colPattern = new Column<AklaboxDispatch, String>(cell) {

			@Override
			public String getValue(AklaboxDispatch object) {
				return object.getPattern();
			}
		};
		colPattern.setFieldUpdater(new FieldUpdater<AklaboxDispatch, String>() {

			@Override
			public void update(int index, AklaboxDispatch object, String value) {
				object.setPattern(value);
			}
		});
		
		Column<AklaboxDispatch, String> colDestination = new Column<AklaboxDispatch, String>(cell) {

			@Override
			public String getValue(AklaboxDispatch object) {
				return object.getAklaboxFolderName();
			}
		};

		ButtonImageCell deleteCell = new ButtonImageCell(Images.INSTANCE.ic_delete_black_18dp(), style.imgGrid());
		Column<AklaboxDispatch, String> colDelete = new Column<AklaboxDispatch, String>(deleteCell) {

			@Override
			public String getValue(AklaboxDispatch object) {
				return "";
			}
		};
		colDelete.setFieldUpdater(new FieldUpdater<AklaboxDispatch, String>() {

			@Override
			public void update(int index, final AklaboxDispatch object, String value) {
				deleteAklaboxDispatch(object);
			}
		});

		DataGrid.Resources resources = new CustomResources();
		DataGrid<AklaboxDispatch> dataGrid = new DataGrid<AklaboxDispatch>(10000, resources);
		dataGrid.setSize("100%", "100%");
		dataGrid.addColumn(colPattern, Labels.lblCnst.Pattern());
		dataGrid.addColumn(colDestination, Labels.lblCnst.DestinationFolder());
		dataGrid.addColumn(colDelete, "");
		dataGrid.setColumnWidth(colDelete, "70px");

		dataProvider = new ListDataProvider<AklaboxDispatch>();
		dataProvider.addDataDisplay(dataGrid);

		SingleSelectionModel<AklaboxDispatch> selectionModel = new SingleSelectionModel<AklaboxDispatch>();
		dataGrid.setSelectionModel(selectionModel);

		return dataGrid;
	}

	@UiHandler("btnAdd")
	public void onAddAklaboxDispatch(ClickEvent event) {
		AklaboxConnection server = parent.getAklaboxConnection();
		if (server == null) {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), Labels.lblCnst.PleaseSelectAnAklaboxServer());
			return;
		}
		
		AddDispatchRuleDialog dial = new AddDispatchRuleDialog(this, server);
		dial.center();
	}

	public void addAklaboxDispatch(AklaboxDispatch aklaboxDispatch) {
		activity.addRule(aklaboxDispatch);
		loadData(activity.getRules());
	}

	private void deleteAklaboxDispatch(AklaboxDispatch value) {
		activity.removeRule(value);
		loadData(activity.getRules());
	}
}
