package bpm.architect.web.client.panels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.services.ArchitectService;
import bpm.architect.web.client.tree.DimensionTreeItem;
import bpm.architect.web.client.utils.CustomMultiWordSuggest;
import bpm.data.viz.core.preparation.DataPreparation;
import bpm.data.viz.core.preparation.DataPreparationResult;
import bpm.data.viz.core.preparation.LinkItem;
import bpm.data.viz.core.preparation.LinkItemParam;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.RepositoryDirectoryDialog;
import bpm.gwt.commons.client.dialog.SelectLayersDialog;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.vanilla.map.core.design.MapInformation;
import bpm.vanilla.map.core.design.MapLayer;
import bpm.vanilla.map.core.design.MapLayerOption;
import bpm.vanilla.map.core.design.MapServer.TypeServer;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.DataColumn.FunctionalType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DataVizMapPanel extends Composite {
	
	private static final String DIV_CONTAINER = "map";

	private static DataVizMapPanelUiBinder uiBinder = GWT.create(DataVizMapPanelUiBinder.class);

	interface DataVizMapPanelUiBinder extends UiBinder<Widget, DataVizMapPanel> {
	}

	public interface MyStyle extends CssResource {
		String filterLbl();

		String filterLblCount();

		String clear();

		String suggestbox();

		String filterTree();

		String subItems();

		String simplePanel();

		String expendItems();

		String disjPos();

		String disjNeg();
	}

	public interface MyResources extends ClientBundle {
		public static final MyResources INSTANCE = GWT.create(MyResources.class);

		@Source("transparent_16.png")
		public ImageResource leaf();
	}

	public class TreeImages implements Tree.Resources {

		@Override
		public ImageResource treeClosed() {
			return MyResources.INSTANCE.leaf();
		}

		@Override
		public ImageResource treeLeaf() {
			return MyResources.INSTANCE.leaf();
		}

		@Override
		public ImageResource treeOpen() {
			return MyResources.INSTANCE.leaf();
		}

	}

	@UiField
	HTMLPanel panelPopup, mapContent, filter;

	@UiField
	MyStyle style;

	private DataVizDesignPanel parent;
	private DataPreparation dataPrep;
	private String geojson;
	private DataPreparationResult dataPrepResult;

	private Tree filterTree;

	private SuggestBox suggestBox;
	private boolean open = false;
	private List<String> measures = new ArrayList<String>();
	private Map<String, String> filters = new HashMap<String, String>();
	private List<CheckBox> checkBoxGroup = new ArrayList<CheckBox>();
	private Map<String, String> cities;
	
	private MapInformation mapInformation;
	private List<LinkItem> linkedItems;

	public DataVizMapPanel(DataVizDesignPanel prt) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = prt;
		this.dataPrep = parent.getDataPreparation();
		this.linkedItems = dataPrep.getLinkedItems();
		this.mapInformation = dataPrep.getMapInformation();

		ArchitectService.Connect.getInstance().executeDataPreparation(dataPrep, new GwtCallbackWrapper<DataPreparationResult>(parent, true, true) {

			@Override
			public void onSuccess(DataPreparationResult result) {
				dataPrepResult = result;

				createFilters();
			}
		}.getAsyncCallback());
		
		refreshMap(false);
	}
	
	private void refreshPopup() {
		panelPopup.clear();
		panelPopup.add(new MapPopup(linkedItems));
		panelPopup.add(new MapBullePopup());
	}

	@UiHandler("btnSaveMap")
	public void onBtnSaveMap(ClickEvent event) {
		ArchitectService.Connect.getInstance().saveDataPreparation(dataPrep, new GwtCallbackWrapper<DataPreparation>(parent, false, false) {
			@Override
			public void onSuccess(DataPreparation result) {
				dataPrep = result;
				List<Group> groups = parent.getInfoUser().getAvailableGroups();
				String itemToSave = parent.getDataPreparation().getId() + "";
				RepositoryDirectoryDialog dial = new RepositoryDirectoryDialog(IRepositoryApi.MAP_TYPE, groups, itemToSave);
				dial.center();
			}
		}.getAsyncCallback());

	}
	
	@UiHandler("btnLayers")
	public void onLayers(ClickEvent event) {
		final SelectLayersDialog dial = new SelectLayersDialog(mapInformation);
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					mapInformation = dial.getMapInformation();
					dataPrep.setMapInformation(mapInformation);

					refreshMap(true);
				}
			}
		});
	}
	
	@UiHandler("btnRefresh")
	public void onRefresh(ClickEvent event) {
		refreshMap(true);
	}

	public void createFilters() {

		VerticalPanel vp = new VerticalPanel();
		vp.getElement().getStyle().setPadding(5, Unit.PX);

		// Label test = new Label(reportUrl);
		// vp.add(test);
		//
		// for(DataColumn d: popupInfo) {
		// vp.add(new Label(d.getColumnLabel()));
		// }
		HTMLPanel p = new HTMLPanel("");
		Label lbl = new Label(LabelsConstants.lblCnst.Filters());
		lbl.getElement().getStyle().setFontSize(25, Unit.PX);
		lbl.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		lbl.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
		p.add(lbl);
		Button btnClearFilters = new Button(Labels.lblCnst.ClearFilters(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String newData = setFilters(geojson, null);
				filters = new HashMap<String, String>();
				suggestBox.setValue("");
				loadFilters(convertDataForFilters(newData));
			}
		});
		btnClearFilters.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		btnClearFilters.getElement().getStyle().setMarginLeft(10, Unit.PX);
		btnClearFilters.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
		p.add(btnClearFilters);
		vp.add(p);

		SimplePanel s = new SimplePanel(new Label(Labels.lblCnst.Location()));
		s.addStyleName(style.simplePanel());
		vp.add(s);
		// barre de recherche
		final CustomMultiWordSuggest oracle = new CustomMultiWordSuggest();
		suggestBox = new SuggestBox(oracle);
		loadSuggestBox();

		suggestBox.addStyleName(style.suggestbox());
		suggestBox.getElement().setPropertyString("placeholder", LabelsConstants.lblCnst.Search() + "...");
		suggestBox.setLimit(3);
		suggestBox.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {

			@Override
			public void onSelection(SelectionEvent<SuggestOracle.Suggestion> event) {
				/*
				 * for(String city : result.keySet()){
				 * if(result.get(city).equals
				 * (event.getSelectedItem().getDisplayString())){
				 * //setFilters(obj, key, value); } }
				 */
				for (String city : cities.keySet()) {
					if (cities.get(city).equals(event.getSelectedItem().getDisplayString())) {
						filters.put("__location__", city);
						onFilter();
						break;
					}
				}

			}
		});
		suggestBox.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				oracle.clear();
				if (suggestBox.getText().length() > 1) {
					for (String city : cities.keySet()) {
						if (city.toLowerCase().startsWith(suggestBox.getText().toLowerCase())) {
							oracle.add(cities.get(city));
						}
					}
					// suggestBox.showSuggestionList();
				}
			}
		});
		vp.add(suggestBox);

		// vp.add(new SimplePanel(new Label("Dimensions")));
		s = new SimplePanel(new Label(LabelsConstants.lblCnst.Dimensions()));
		s.addStyleName(style.simplePanel());
		vp.add(s);
		// liste des dimensions
		// filterTree = new Tree();
		TreeImages tx = new TreeImages();
		filterTree = new Tree(tx);
		filterTree.addStyleName(style.filterTree());

		loadFilters(dataPrepResult.getValues());

		filterTree.addSelectionHandler(new SelectionHandler<TreeItem>() {
			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				TreeItem selected = event.getSelectedItem();
				if (selected instanceof DimensionTreeItem) {
					/*
					 * if (open) { event.getSelectedItem().setState(false); open
					 * = false; } else { event.getSelectedItem().setState(true);
					 * open = true; }
					 */
					selected.setState(!selected.getState());
				}
				else {
					if (selected.getUserObject() != null) {
						// click sur filtre
						String selectedVal = selected.getUserObject().toString().equals(LabelsConstants.lblCnst.None()) ? "null" : selected.getUserObject().toString();
						boolean exists = false;
						for (String param : filters.keySet()) {
							if (param.equals(selected.getParentItem().getUserObject().toString()) && ((filters.get(param).contains("_;_") && Arrays.asList(filters.get(param).split("_;_")).contains(selectedVal)) || (!filters.get(param).contains("_;_") && filters.get(param).equals(selectedVal)))) {
								exists = true;
								if (filters.get(param).contains("_;_")) {
									List<String> values = new ArrayList<>(Arrays.asList(filters.get(param).split("_;_")));
									int d = -1;
									for (String value : values) {
										if (value.equals(selectedVal)) {
											d = values.indexOf(value);
											break;
										}
									}
									if (d != -1)
										values.remove(d);
									String val = "";
									for (String value : values) {
										val += value + "_;_";
									}
									val = val.substring(0, val.length() - 3);
									filters.put(param, val);
								}
								else {
									filters.remove(param);
								}

								break;
							}

						}
						if (!exists) {
							String key = selected.getParentItem().getUserObject().toString();
							if (filters.get(key) != null) {

								filters.put(key, filters.get(key) + "_;_" + selectedVal);
							}
							else {
								filters.put(key, selectedVal);
							}

						}

						onFilter();
					}
				}

			}
		});

		vp.add(filterTree);

		p = new HTMLPanel("");
		Label l = new Label(LabelsConstants.lblCnst.Measures());
		l.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		p.add(l);
		p.addStyleName(style.simplePanel());

		Button btnClearMeasures = new Button(Labels.lblCnst.ClearMeasures(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				JsArrayMixed bigArray = JavaScriptObject.createArray().cast();
				changeMeasures(bigArray);
				measures.clear();
				for (CheckBox c : checkBoxGroup) {
					// measures.remove(c.getText());
					c.setValue(false);
				}
			}
		});
		btnClearMeasures.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		btnClearMeasures.getElement().getStyle().setMarginLeft(5, Unit.PX);
		p.add(btnClearMeasures);
		vp.add(p);

		// final List<String> cbox = new ArrayList<String>();
		for (DataColumn d : dataPrep.getDataset().getMetacolumns()) {
			if (d.getFt() == FunctionalType.COUNT || d.getFt() == FunctionalType.MIN || d.getFt() == FunctionalType.MAX || d.getFt() == FunctionalType.SUM || d.getFt() == FunctionalType.AVG) {

				CheckBox checkBox = new CheckBox(d.getColumnLabel());
				checkBox.setValue(true);
				measures.add(checkBox.getText());
				vp.add(checkBox);
				checkBoxGroup.add(checkBox);
				checkBox.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						CheckBox checkBox = (CheckBox) event.getSource();
						if (checkBox.getValue()) {
							measures.add(checkBox.getText());
						}
						else {
							measures.remove(checkBox.getText());
						}
						onMeasureChange();
					}
				});
			}
		}

		// Button btnFilter = new Button("Filtrer", filterClick);
		// p.add(btnFilter);
		// btnFilter.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		// btnFilter.getElement().getStyle().setMargin(5, Unit.PX);

		filter.add(vp);
	}

	public void loadFilters(List<Map<DataColumn, Serializable>> datas) {
		LinkedHashMap<DataColumn, List<Map.Entry<String, Integer>>> filterValues = new LinkedHashMap<DataColumn, List<Map.Entry<String, Integer>>>();
		filterTree.clear();
		// List<String> lstSubItems;
		List<Map<DataColumn, Serializable>> datasTemp;
		for (DataColumn d : dataPrep.getDataset().getMetacolumns()) {
			if (d.getFt() == FunctionalType.DIMENSION || d.getFt() == FunctionalType.EXCLUSIF || d.getFt() == FunctionalType.NON_EXCLUSIF) {
				TreeMap<String, Integer> values = new TreeMap<String, Integer>();
				// filterValues.put(d.getColumnLabel(), values);
				// lstSubItems = new ArrayList<String>();
				// TreeItem item = new TreeItem();
				// item.setText(d.getColumnLabel());
				if (d.getFt() == FunctionalType.NON_EXCLUSIF) {
					datasTemp = dataPrepResult.getValues();
				}
				else {
					datasTemp = datas;
				}
				// subItems
				for (Map<DataColumn, Serializable> line : datasTemp) {
					if (line.get(d) == null) {
						if (values.get(LabelsConstants.lblCnst.None()) == null) {
							values.put(LabelsConstants.lblCnst.None(), 0);
						}
						values.put(LabelsConstants.lblCnst.None(), values.get(LabelsConstants.lblCnst.None()) + 1);
					}
					else {
						if (values.get(line.get(d).toString()) == null) {
							values.put(line.get(d).toString(), 0);
							// TreeItem subItem = new TreeItem();
							// subItem.setText(line.get(d).toString());
							// item.addItem(subItem);
							// lstSubItems.add(line.get(d).toString());
						}
						values.put(line.get(d).toString(), values.get(line.get(d).toString()) + 1);
					}
				}
				List<Map.Entry<String, Integer>> sortedset = new ArrayList<Map.Entry<String, Integer>>(values.entrySet());
				Collections.sort(sortedset, new Comparator<Map.Entry<String, Integer>>() {
					@Override
					public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
						return e2.getValue().compareTo(e1.getValue());
					}
				});
				// sortedset.addAll(values.entrySet());

				filterValues.put(d, sortedset);
				// filterTree.addItem(item);
			}
		}

		for (DataColumn dim : filterValues.keySet()) {
			HTMLPanel pan = new HTMLPanel("");
			Label lblDim = new Label(dim.getColumnLabel());
			pan.add(lblDim);
			DimensionTreeItem it = new DimensionTreeItem(pan, dim, filters.get(dim.getColumnName()));
			it.setStyleName(style.clear());

			filterTree.addItem(it);
			it.setUserObject(dim.getColumnLabel());
			List<Map.Entry<String, Integer>> values = filterValues.get(dim);
			/*
			 * for (String val : values.keySet()) { HTMLPanel panSub = new
			 * HTMLPanel(""); panSub.addStyleName(style.subItems()); Label
			 * lblVal = new Label(val); Label lblValCount = new
			 * Label(values.get(val) + ""); panSub.add(lblVal);
			 * panSub.add(lblValCount);
			 * 
			 * lblVal.addStyleName(style.filterLbl());
			 * lblValCount.addStyleName(style.filterLblCount());
			 * 
			 * TreeItem itSub = new TreeItem(panSub); it.addItem(itSub);
			 * itSub.setUserObject(val); }
			 */
			it.setStyle(style);
			it.setValues(values);
			it.showValues();
			it.setState(true);
		}
	}

	private List<Map<DataColumn, Serializable>> convertDataForFilters(String dataJson) {
		List<Map<DataColumn, Serializable>> res = new ArrayList<>();

		JSONObject o = new JSONObject(JsonUtils.safeEval(dataJson));
		JSONArray a = (JSONArray) o.get("features");
		for (int i = 0; i < a.size(); i++) {
			JSONObject line = (JSONObject) a.get(i);
			JSONObject properties = (JSONObject) line.get("properties");
			Map<DataColumn, Serializable> row = new HashMap<>();
			for (DataColumn c : dataPrep.getDataset().getMetacolumns()) {
				if (properties.containsKey(c.getColumnName())) {
					row.put(c, ((JSONString) properties.get(c.getColumnName())).stringValue());
				}
			}

			res.add(row);
		}

		return res;
	}

	public void loadSuggestBox() {
		ArchitectService.Connect.getInstance().getCities(new GwtCallbackWrapper<Map<String, String>>(parent, true, true) {

			@Override
			public void onSuccess(final Map<String, String> result) {
				cities = result;

				/*
				 * for (Map<DataColumn, Serializable> line :
				 * dataPrepResult.getValues()) { for (DataColumn dc :
				 * line.keySet()) { if (dc.getFt() == FunctionalType.PAYS ||
				 * dc.getFt() == FunctionalType.CONTINENT || dc.getFt() ==
				 * FunctionalType.COMMUNE || dc.getFt() ==
				 * FunctionalType.REGION) { if (line.get(dc) != null) {
				 * oracle.add(line.get(dc).toString()); } } } }
				 */

			}
		}.getAsyncCallback());

	}

	private void onFilter() {
		JSONObject obj = new JSONObject();
		for (String param : filters.keySet()) {
			if (filters.get(param).contains("_;_")) {
				JSONArray arr = new JSONArray();
				String[] vals = filters.get(param).split("_;_");
				for (int i = 0; i < vals.length; i++) {
					arr.set(i, new JSONString(vals[i]));
				}
				obj.put(param, arr);
			}
			else {
				obj.put(param, new JSONString(filters.get(param)));
			}
		}

		String newData = setFilters(geojson, obj.toString());
		loadFilters(convertDataForFilters(newData));
		/*
		 * try { TreeItem selected = filterTree.getSelectedItem(); if
		 * (suggestBox.getText() != null && !suggestBox.getText().isEmpty()) {
		 * setFilters(geojson, "nom_commune", suggestBox.getText()); } else {
		 * setFilters(geojson,
		 * selected.getParentItem().getUserObject().toString(),
		 * selected.getUserObject().toString()); } } catch (Exception e) {
		 * e.printStackTrace(); }
		 */
	}

	private void onMeasureChange() {
		JsArrayMixed bigArray = JavaScriptObject.createArray().cast();
		for (DataColumn d : dataPrep.getDataset().getMetacolumns()) {

			if (measures.contains(d.getColumnLabel())) {
				JsArrayString row = JavaScriptObject.createArray().cast();
				row.push(d.getColumnLabel());
				row.push(d.getFt().name());
				bigArray.push(row);
			}
		}
		changeMeasures(bigArray);
	}

	public String getGeojson() {
		return geojson;
	}

	public void setGeojson(String geojson) {
		this.geojson = geojson;
	}

	public void refreshMap(final boolean refresh) {
		mapContent.clear();
		
		refreshPopup();
		
		HTMLPanel mapContainer = new HTMLPanel("");
		mapContent.add(mapContainer);
		mapContainer.getElement().setId(DIV_CONTAINER);
		if (mapContainer.isAttached()) {
			loadMap(DIV_CONTAINER, refresh);
		}
		else {
			mapContainer.addAttachHandler(new AttachEvent.Handler() {
		
				@Override
				public void onAttachOrDetach(AttachEvent event) {
					if (event.isAttached())
						loadMap(DIV_CONTAINER, refresh);
				}
			});
		}
	}

	public void loadMap(final String divContainer, final boolean refresh) {
		ArchitectService.Connect.getInstance().map(dataPrep, new GwtCallbackWrapper<String>(null, false, false) {

			@Override
			public void onSuccess(String result) {
				try {
					// Map<String,String> mesures = new HashMap<String,
					// String>();
					JsArrayMixed bigArray = JavaScriptObject.createArray().cast();

					for (DataColumn d : dataPrep.getDataset().getMetacolumns()) {

						if (measures.contains(d.getColumnLabel())) {
							JsArrayString row = JavaScriptObject.createArray().cast();
							row.push(d.getColumnLabel());
							row.push(d.getFt().name());
							bigArray.push(row);
						}
					}

					JsArrayMixed links = JavaScriptObject.createArray().cast();
					if (linkedItems != null) {
						for (LinkItem item : linkedItems) {
							
							String itemName = item.getCustomName();
							String publicUrl = item.getPublicUrl();

							JsArrayString paramsNames = JavaScriptObject.createArray().cast();
							JsArrayString paramsColumns = JavaScriptObject.createArray().cast();
							if (item.getParameters() != null) {
								for (LinkItemParam param : item.getParameters()) {
									if (param.getParamName() != null && !param.getParamName().isEmpty() && param.getDataPrepColumnName() != null && !param.getDataPrepColumnName().isEmpty()) {
										paramsNames.push(param.getParamName());
										paramsColumns.push(param.getDataPrepColumnName());
									}
								}
							}
							
							JsArrayMixed link = JavaScriptObject.createArray().cast();
							link.push(itemName);
							link.push(publicUrl);
							link.push(paramsNames);
							link.push(paramsColumns);

							links.push(link);
						}
					}

					String columnName = null;
					for (DataColumn d : dataPrep.getDataset().getMetacolumns()) {
						if (d.getFt() == FunctionalType.COMMUNE || d.getFt() == FunctionalType.PAYS || d.getFt() == FunctionalType.ZONEID) {
							columnName = d.getColumnLabel();
							break;
						}
					}

					geojson = result;

					String wmtsSource = null;
					JsArrayMixed wmsSource = JavaScriptObject.createArray().cast();
					if (mapInformation != null) {
						if (mapInformation.getWmts() != null) {
							wmtsSource = mapInformation.getWmts().getUrl();
						}
						
						if (mapInformation.getLayers() != null) {
							for (MapLayer layer : mapInformation.getLayers()) {
								if (layer.isSelected()) {
									TypeServer type = layer.getParent().getType();
									
									String wmsUrl = type == TypeServer.WFS ? layer.getUrl() : layer.getParent().getUrl();
									String layerName = layer.getName();
									Integer opacity = layer.getOptions() != null ? layer.getOptions().getOpacity() : MapLayerOption.DEFAULT_OPACITY;
									String layerType = type.toString();
									
									JsArrayString arr = JavaScriptObject.createArray().cast();

									arr.push(wmsUrl);
									arr.push(layerName);
									arr.push(String.valueOf(opacity));
									arr.push(layerType);

									wmsSource.push(arr);
								}
							}
						}
					}

					renderDataPrepMap(divContainer, geojson, columnName, links, bigArray, wmtsSource, wmsSource, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.getAsyncCallback());
	}

	private final native void renderDataPrepMap(String div, String obj, String columnName, JsArrayMixed links, JsArrayMixed bigArray, String wmtsSource, JsArrayMixed wmsSource, boolean refresh) /*-{
		$wnd.renderGeoJsonMapWithWms(div, obj, columnName, links, bigArray, wmtsSource, wmsSource, refresh);
	}-*/;

	private final native String setFilters(String obj, String paramsJson) /*-{
		return $wnd.filterFunction(obj, paramsJson);
	}-*/;

	private final native void changeMeasures(JsArrayMixed bigArray) /*-{
		$wnd.changeMeasures(bigArray);
	}-*/;
}
