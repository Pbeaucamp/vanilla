package bpm.vanilla.portal.client.wizard.map;

import java.util.ArrayList;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapDataSource;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.services.BiPortalService;
import bpm.vanilla.portal.client.utils.ToolsGWT;
import bpm.vanilla.portal.shared.MapColumns;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/*
 * Kevin Monnery
 * 
 */

public class MapDataSetDialog extends AbstractDialogBox {

	private static MapDataSetDialogUiBinder uiBinder = GWT.create(MapDataSetDialogUiBinder.class);

	interface MapDataSetDialogUiBinder extends UiBinder<Widget, MapDataSetDialog> {
	}

	interface MyStyle extends CssResource {
		String disabled();
	}

	private AddMapDataSetPage addMapDataSetPage;
	private MapDataSource dtSource;
	private MapDataSet selectedDataSet;
	private Button btnConfirm;
	private boolean dtSedit;

	private String markerUrl;
	private int minSize = -1;
	private int maxSize = -1;

	@UiField
	Image displayMarker, editMarker;

	@UiField
	Label lblQuery, lblLongitude, lblLatitude, lblIdZone, lblParent, lblType, lblName, lblZoneLabel, lblParentDs;

	@UiField
	ListBox lstLongitude, lstLatitude, lstIdZone, lstParent, lstType, lstZoneLabel, lstParentDs;

	@UiField
	Button btnQuery;

	@UiField
	TextArea txaQuery;

	@UiField
	TextBox txbName;

	@UiField
	MyStyle style;

	public MapDataSetDialog(AddMapDataSetPage addMapDataSetPage, MapDataSource dtSource) {
		super(ToolsGWT.lblCnst.AddDataSet(), false, true);

		setWidget(uiBinder.createAndBindUi(this));
		this.dtSource = dtSource;
		this.dtSedit = false;
		this.addMapDataSetPage = addMapDataSetPage;

		createButton(ToolsGWT.lblCnst.Cancel(), cancelHandler);
		this.btnConfirm = createButton(ToolsGWT.lblCnst.Ok(), okHandler);

		lblName.setText(ToolsGWT.lblCnst.MapdtSName());
		lblQuery.setText(ToolsGWT.lblCnst.MapQuery());
		lblLongitude.setText(ToolsGWT.lblCnst.MapLongitude());
		lblLatitude.setText(ToolsGWT.lblCnst.MapLatitude());
		lblIdZone.setText(ToolsGWT.lblCnst.MapIdZone());
		lblZoneLabel.setText(ToolsGWT.lblCnst.MapLabelZone());
		lblParent.setText(ToolsGWT.lblCnst.MapParent());
		lblParentDs.setText(ToolsGWT.lblCnst.MapParentDs());
		lblType.setText(ToolsGWT.lblCnst.MapType());
		btnQuery.setText(ToolsGWT.lblCnst.MapExecuteQuery());

		String markerUrl = "";
		if (biPortal.get().getIcons() != null && !biPortal.get().getIcons().isEmpty() && biPortal.get().getIcons().get(0).contains("webapps")) {
			markerUrl = biPortal.get().getIcons().get(0).substring(biPortal.get().getIcons().get(0).indexOf("webapps") + "webapps".length(), biPortal.get().getIcons().get(0).length());
		}
		String url = GWT.getHostPageBaseURL() + ".." + markerUrl.replace("\\", "/");

		displayMarker.setUrl(url);

		setBtnConfirmState(false);

		lstParent.addItem(ToolsGWT.lblCnst.NoParentDataSet());
		lstType.addItem(ToolsGWT.lblCnst.Polygon());
		lstType.addItem("Line");
		lstType.addItem(ToolsGWT.lblCnst.Point());

		fillDatasetList();
	}

	private void fillDatasetList() {
		lstParentDs.clear();
		lstParentDs.addItem(ToolsGWT.lblCnst.NoParentDataSet(), "-1");

		int i = 1;
		if (addMapDataSetPage.getDatasets() != null) {
			for (MapDataSet ds : addMapDataSetPage.getDatasets()) {
				lstParentDs.addItem(ds.getName(), ds.getId() <= 0 ? ds.getName() : ds.getId() + "");
				if (selectedDataSet != null && selectedDataSet.getParentId() != null && selectedDataSet.getParentId().intValue() == ds.getId()) {
					lstParentDs.setSelectedIndex(i);
				}
				i++;
			}
		}

	}

	public MapDataSetDialog(AddMapDataSetPage addMapDataSetPage, MapDataSource dtSource, MapDataSet selectedDataSet) {
		this(addMapDataSetPage, dtSource);
		this.setTitle(ToolsGWT.lblCnst.EditDataSet());

		this.selectedDataSet = selectedDataSet;
		this.dtSedit = true;
		setName(selectedDataSet.getName());
		txaQuery.setValue(selectedDataSet.getQuery());
		onExecuteQueryChange(null);

		String markerUrl = "";
		if (selectedDataSet.getMarkerUrl() != null && !selectedDataSet.getMarkerUrl().isEmpty()) {
			if (selectedDataSet.getMarkerUrl().contains("webapps")) {
				markerUrl = selectedDataSet.getMarkerUrl().substring(selectedDataSet.getMarkerUrl().indexOf("webapps") + "webapps".length(), selectedDataSet.getMarkerUrl().length());
			}
			else {
				markerUrl = selectedDataSet.getMarkerUrl();
			}
			String url = GWT.getHostPageBaseURL() + ".." + markerUrl.replace("\\", "/");

			displayMarker.setUrl(url);
		}
		else {
			if (biPortal.get().getIcons() != null && !biPortal.get().getIcons().isEmpty() && biPortal.get().getIcons().get(0).contains("webapps")) {
				markerUrl = biPortal.get().getIcons().get(0).substring(biPortal.get().getIcons().get(0).indexOf("webapps") + "webapps".length(), biPortal.get().getIcons().get(0).length());
			}
			String url = GWT.getHostPageBaseURL() + ".." + markerUrl.replace("\\", "/");

			displayMarker.setUrl(url);
		}

		fillDatasetList();

		// setSelectedColumns(selectedDataSet.getLatitude(),
		// selectedDataSet.getLongitude(), selectedDataSet.getIdZone(),
		// selectedDataSet.getParent(), selectedDataSet.getType(),
		// selectedDataSet.getZoneLabel());
	}

	public boolean isComplete() {
		if (txaQuery.getText().equals("")) {
			return false;
		}
		else if (txbName.getText().equals("")) {
			return false;
		}
		else {
			return checkAllBox();
		}
	}

	public void setBtnConfirmState(boolean enabled) {
		// if (enabled) {
		// btnConfirm.setEnabled(enabled);
		// btnConfirm.removeStyleName(style.disabled());
		// }
		// else {
		// btnConfirm.setEnabled(enabled);
		// btnConfirm.addStyleName(style.disabled());
		// }
	}

	@UiHandler("txbName")
	public void onNameChange(ValueChangeEvent<String> event) {
		setBtnConfirmState(isComplete() ? true : false);
	}

	public String getName() {
		return txbName.getText().trim();
	}

	public void setName(String name) {
		txbName.setText(name);
	}

	@UiHandler("txaQuery")
	public void onQueryChange(ValueChangeEvent<String> event) {
		setBtnConfirmState(isComplete() ? true : false);
		// setBtnConfirmState(false);

	}

	public String getQuery() {
		return txaQuery.getText().trim();
	}

	public void setQuery(String query) {
		txaQuery.setText(query);
	}

	@UiHandler("lstLongitude")
	public void onLongitudeChange(ChangeEvent event) {
		setBtnConfirmState(isComplete() ? true : false);
	}

	public String getLongitude() {
		return lstLongitude.getValue(lstLongitude.getSelectedIndex());
	}

	public void setLongitude(String longitude) {
		lstLongitude.addItem(longitude);
	}

	@UiHandler("lstLatitude")
	public void onLatitudeChange(ChangeEvent event) {
		setBtnConfirmState(isComplete() ? true : false);
	}

	@UiHandler("lstZoneLabel")
	public void onZoneLabelChange(ChangeEvent event) {
		setBtnConfirmState(isComplete() ? true : false);
	}

	public String getLatitude() {
		return lstLatitude.getValue(lstLatitude.getSelectedIndex());
	}

	public void setLatitude(String latitude) {
		lstLatitude.addItem(latitude);
	}

	public String getZoneLabel() {
		return lstZoneLabel.getValue(lstZoneLabel.getSelectedIndex());
	}

	public void setZoneLabel(String zoneLabel) {
		lstZoneLabel.addItem(zoneLabel);
	}

	@UiHandler("lstIdZone")
	public void onIdZoneChange(ChangeEvent event) {
		setBtnConfirmState(isComplete() ? true : false);
	}

	public String getIdZone() {
		return lstIdZone.getValue(lstIdZone.getSelectedIndex());
	}

	public void setIdZone(String idZone) {
		lstIdZone.addItem(idZone);
	}

	@UiHandler("lstParent")
	public void onParentChange(ChangeEvent event) {
		setBtnConfirmState(isComplete() ? true : false);
	}

	public String getParent() {
		if (lstParent.getValue(lstParent.getSelectedIndex()).equals(ToolsGWT.lblCnst.NoParentDataSet())) {
			return "";
		}
		return lstParent.getValue(lstParent.getSelectedIndex());
	}

	public void setParent(String parent) {
		lstParent.addItem(parent);
	}

	@UiHandler("lstType")
	public void onTypeChange(ChangeEvent event) {
		setBtnConfirmState(isComplete() ? true : false);
	}

	public String getType() {
		String type;
		if (lstType.getSelectedIndex() == 0) {
			type = "polygon";
		}
		else if (lstType.getSelectedIndex() == 1) {
			type = "line";
		}
		else {
			type = "point";
		}
		return type;
	}

	public void setType(String type) {
		lstType.addItem(type);
	}

	@UiHandler("btnQuery")
	public void onExecuteQueryChange(ClickEvent event) {
		if (lstLatitude.getItemCount() != 0 && lstLongitude.getItemCount() != 0 && lstIdZone.getItemCount() != 0) {
			lstLatitude.clear();
			lstLongitude.clear();
			lstIdZone.clear();
			lstZoneLabel.clear();
			lstParent.clear();
			lstParent.addItem(ToolsGWT.lblCnst.NoParentDataSet());
			lstType.setItemSelected(0, true);
		}

		MapDataSource currentDataSource = AddMapWizard.getCurrentMapDataSource();
		String query = getQuery();
		BiPortalService.Connect.getInstance().getDataSetMetaData(currentDataSource.getLogin(), currentDataSource.getMdp(), currentDataSource.getUrl(), currentDataSource.getDriver(), query, new AsyncCallback<ArrayList<MapColumns>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.UnableToLoadDataSetMetaData());

			}

			@Override
			public void onSuccess(ArrayList<MapColumns> result) {
				for (MapColumns mapColumn : result) {
					lstLatitude.addItem(mapColumn.getColumnName());
					lstLongitude.addItem(mapColumn.getColumnName());
					lstIdZone.addItem(mapColumn.getColumnName());
					lstParent.addItem(mapColumn.getColumnName());
					lstZoneLabel.addItem(mapColumn.getColumnName());
				}
				if (selectedDataSet != null) {
					setSelectedColumns(selectedDataSet.getLatitude(), selectedDataSet.getLongitude(), selectedDataSet.getIdZone(), selectedDataSet.getParent(), selectedDataSet.getType(), selectedDataSet.getZoneLabel());
				}
			}

		});
	}

	public boolean checkAllBox() {
		if (lstLatitude.getItemText(lstLatitude.getSelectedIndex()).equals(lstLongitude.getItemText(lstLongitude.getSelectedIndex())) || lstLatitude.getItemText(lstLatitude.getSelectedIndex()).equals(lstIdZone.getItemText(lstIdZone.getSelectedIndex())) || lstLatitude.getItemText(lstLatitude.getSelectedIndex()).equals(lstParent.getItemText(lstParent.getSelectedIndex()))) {
			return false;

		}
		else if (lstLongitude.getItemText(lstLongitude.getSelectedIndex()).equals(lstLatitude.getItemText(lstLatitude.getSelectedIndex())) || lstLongitude.getItemText(lstLongitude.getSelectedIndex()).equals(lstIdZone.getItemText(lstIdZone.getSelectedIndex())) || lstLongitude.getItemText(lstLongitude.getSelectedIndex()).equals(lstParent.getItemText(lstParent.getSelectedIndex()))) {
			return false;

		}
		else if (lstIdZone.getItemText(lstIdZone.getSelectedIndex()).equals(lstLatitude.getItemText(lstLatitude.getSelectedIndex())) || lstIdZone.getItemText(lstIdZone.getSelectedIndex()).equals(lstLongitude.getItemText(lstLongitude.getSelectedIndex())) || lstIdZone.getItemText(lstIdZone.getSelectedIndex()).equals(lstParent.getItemText(lstParent.getSelectedIndex()))) {
			return false;
		}
		else if (lstParent.getItemText(lstParent.getSelectedIndex()).equals(lstLatitude.getItemText(lstLatitude.getSelectedIndex())) || lstParent.getItemText(lstParent.getSelectedIndex()).equals(lstLongitude.getItemText(lstLongitude.getSelectedIndex())) || lstParent.getItemText(lstParent.getSelectedIndex()).equals(lstIdZone.getItemText(lstIdZone.getSelectedIndex()))) {
			return false;
		}

		else {
			return true;
		}
	}

	public void setSelectedColumns(final String latitude, final String longitude, final String idZone, final String parent, final String type, final String zoneLabel) {
		if (lstLatitude.getItemCount() != 0 && lstLongitude.getItemCount() != 0 && lstIdZone.getItemCount() != 0) {
			lstLatitude.clear();
			lstLongitude.clear();
			lstIdZone.clear();
			lstParent.clear();
			lstParent.addItem(ToolsGWT.lblCnst.NoParentDataSet());
			lstZoneLabel.clear();
			lstType.setItemSelected(0, true);
		}

		MapDataSource currentDataSource = AddMapWizard.getCurrentMapDataSource();
		String query = getQuery();
		BiPortalService.Connect.getInstance().getDataSetMetaData(currentDataSource.getLogin(), currentDataSource.getMdp(), currentDataSource.getUrl(), currentDataSource.getDriver(), query, new AsyncCallback<ArrayList<MapColumns>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.UnableToLoadDataSetMetaData());

			}

			@Override
			public void onSuccess(ArrayList<MapColumns> result) {
				for (MapColumns mapColumn : result) {
					lstLatitude.addItem(mapColumn.getColumnName());
					lstLongitude.addItem(mapColumn.getColumnName());
					lstIdZone.addItem(mapColumn.getColumnName());
					lstParent.addItem(mapColumn.getColumnName());
					lstZoneLabel.addItem(mapColumn.getColumnName());
				}

				for (int i = 0; i < lstLatitude.getItemCount(); i++) {
					if (lstLatitude.getItemText(i).equals(latitude)) {
						lstLatitude.setSelectedIndex(i);
					}
					else if (lstLongitude.getItemText(i).equals(longitude)) {
						lstLongitude.setSelectedIndex(i);
					}
					else if (lstIdZone.getItemText(i).equals(idZone)) {
						lstIdZone.setSelectedIndex(i);
					}
					else if (lstZoneLabel.getItemText(i).equals(zoneLabel)) {
						lstZoneLabel.setSelectedIndex(i);
					}
					else if (lstParent.getItemText(i + 1).equals(parent)) { // i+1
																			// car
																			// premiere
																			// ligne
																			// differente
						lstParent.setSelectedIndex(i + 1);
					}
				}
				if (type.equals("polygon")) {
					lstType.setSelectedIndex(0);
				}
				else if (type.equals("line")) {
					lstType.setSelectedIndex(1);
				}
				else {
					lstType.setSelectedIndex(2);
				}
			}

		});
	}

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			MapDataSetDialog.this.hide();
		}
	};

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			MapDataSet newMapDataSet;
			int i = 0;
			if (dtSedit) {
				newMapDataSet = new MapDataSet(selectedDataSet.getId(), getName(), getQuery(), getIdZone(), getLatitude(), getLongitude(), getZoneLabel(), dtSource, getParent(), getType(), selectedDataSet.getIdMapVanilla());

				for (MapDataSet dtS : addMapDataSetPage.dataProvider.getList()) {
					if (dtS.getId() == selectedDataSet.getId()) {
						addMapDataSetPage.dataProvider.getList().remove(i);
						break;
					}
					i++;
				}

			}
			else {
				newMapDataSet = new MapDataSet(0, getName(), getQuery(), getIdZone(), getLatitude(), getLongitude(), getZoneLabel(), dtSource, getParent(), getType(), 0); // 0
																																											// car
																																											// on
																																											// ne
																																											// connait
																																											// pas
																																											// l'id
																																											// mapvanilla
			}

			newMapDataSet.setMarkerUrl(markerUrl);
			newMapDataSet.setMarkerSizeMin(minSize);
			newMapDataSet.setMarkerSizeMax(maxSize);

			try {
				int parentId = Integer.parseInt(lstParentDs.getValue(lstParentDs.getSelectedIndex()));
				if (parentId > -1) {
					newMapDataSet.setParentId(parentId);
				}
			} catch (NumberFormatException e) {
				newMapDataSet.setParentName(lstParentDs.getValue(lstParentDs.getSelectedIndex()));
			}

			addMapDataSetPage.dataProvider.getList().add(newMapDataSet);
			addMapDataSetPage.parent.updateBtn();
			MapDataSetDialog.this.hide();
		}
	};

	@UiHandler("editMarker")
	public void onEditMarker(ClickEvent e) {
		MapDataSet ds = selectedDataSet != null ? selectedDataSet : new MapDataSet();
		String marker = ds.getMarkerUrl();

		final MarkerDialog dial = new MarkerDialog(marker != null ? marker : "", ds.getMarkerSizeMin(), ds.getMarkerSizeMax());
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					markerUrl = dial.getMarkerUrl();
					minSize = dial.getMinSize();
					maxSize = dial.getMaxSize();

					String current = "";
					if (markerUrl.contains("webapps")) {
						current = markerUrl.substring(markerUrl.indexOf("webapps") + "webapps".length(), markerUrl.length());
					}
					else {
						current = markerUrl;
					}
					String url = GWT.getHostPageBaseURL() + ".." + current.replace("\\", "/");

					displayMarker.setUrl(url);

				}
			}
		});
		dial.center();
	}

}
