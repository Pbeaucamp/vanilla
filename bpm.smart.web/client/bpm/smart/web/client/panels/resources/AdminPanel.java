package bpm.smart.web.client.panels.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.panels.ICatchCollapsePanel;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.workflow.commons.client.tabs.HorizontalTab;
import bpm.smart.core.model.MirrorCran;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.model.SmartAdmin;
import bpm.smart.web.client.MainPanel;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.images.Images;
import bpm.smart.web.client.services.SmartAirService;
import bpm.vanilla.platform.core.beans.User;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

public class AdminPanel extends HorizontalTab implements ICatchCollapsePanel{

	private static AdminPanelUiBinder uiBinder = GWT.create(AdminPanelUiBinder.class);

	interface AdminPanelUiBinder extends UiBinder<Widget, AdminPanel> {
	}

	interface MyStyle extends CssResource {
		String btnGrid();

		String frameMarkdown();
		
		String hyperlinkPackage();
		String centerPanel();
		
		String updatablePackage();
	}

	@UiField
	MyStyle style;

	@UiField
	ListBox lstMirrors;

	@UiField
	HTMLPanel mapMirrors, mapContainer;

	@UiField
	SimplePanel installedPackages, panelPager, suggestPanel, helpPanel;

	@UiField
	Label errorMirror, resultInstall;

	@UiField
	TextBox gridFilter;

	@UiField
	Button btnInstall, btnSave, btnLoadMaps;
	
	@UiField
	Image btnDelete, btnUpdate, btnReload;
	
	private MainPanel mainPanel;

	private MirrorCran selectedMirror;
	private List<MirrorCran> mirrors;

	private double maxLat = -200;
	private double minLat = Double.MAX_VALUE;
	private double maxLong = -200;
	private double minLong = Double.MAX_VALUE;

	private User user;
	private SmartAdmin adminconfig;

	private ListDataProvider<String> dataProvider;
	private ListHandler<String> sortHandler;
	private DataGrid<String> datagrid;
	private SingleSelectionModel<String> selectionModel;
	private List<String> installedPackagesList = new ArrayList<String>();
	private SafeHtmlBuilder sb = new SafeHtmlBuilder();
	private String selectedPackage;

	private MultiWordSuggestOracle soracle = new MultiWordSuggestOracle();
	private SuggestBox sbox = new SuggestBox(soracle);
	private Timer timerLoadInstalledPackages = null;

	public AdminPanel(MainPanel mainPanel) {
		super(LabelsConstants.lblCnst.TabAdmin(), Images.INSTANCE.ic_settings_black_18dp());
		add(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		this.user = mainPanel.getUserSession().getInfoUser().getUser();
		addStyleName(style.centerPanel());
		mapContainer.getElement().setId("mapContainer");
		//loadSmartAdmin();
		loadMirrors();
		errorMirror.setVisible(false);
		lstMirrors.addItem(LabelsConstants.lblCnst.NoMirrorSelected(), "");

		this.datagrid = createGridData();
		installedPackages.add(datagrid);
		this.gridFilter.getElement().setAttribute("placeholder", LabelsConstants.lblCnst.Filter());
		this.gridFilter.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				handleFilterGrid(gridFilter.getValue());
			}
		});

		this.suggestPanel.add(sbox);

	//	loadInstalledPackages();
		initJS(this);
		
		btnDelete.setVisible(false);
		btnUpdate.setVisible(false);
		
		mapContainer.addAttachHandler(new AttachEvent.Handler() {
			
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				btnLoadMaps.setVisible(false);
				//loadSmartAdmin();
				if(mapContainer.isAttached())
					loadMap();
			}
		});
	}
	
	@UiHandler("btnLoadMaps")
	public void onLoadMaps(ClickEvent event) {
		btnLoadMaps.setVisible(false);
		//loadSmartAdmin();
		if(mapContainer.isAttached())
			loadMap();
	}
	
	private void loadSmartAdmin() {
		//showWaitPart(true);
		SmartAirService.Connect.getInstance().getAdmin(user.getId(), new AsyncCallback<SmartAdmin>() {

			@Override
			public void onSuccess(SmartAdmin result) {
				if (result == null) {
					result = new SmartAdmin();
					result.setIdUser(user.getId());
					result.setIdMirror(32);
					if(mirrors != null){
						for(MirrorCran  mirror: mirrors){
							if(mirror.getId() == 32){ //lyon 1
								result.setMirror(mirror);
							}
						}
					}
					
					
				}
				adminconfig = result;

				selectedMirror = result.getMirror();

				if (selectedMirror == null) {
					btnInstall.setEnabled(false);
					errorMirror.setVisible(true);
				}

				//loadMirrors();
				if (selectedMirror != null) {
					handleMirrorClick(selectedMirror.getUrl());
				}
				else {
					handleMirrorClick("");
				}
				timerLoadInstalledPackages = new Timer() {
					public void run() {
						loadInstalledPackages();
						timerLoadInstalledPackages = null;
				    }
				};	
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				mainPanel.showWaitPart(false);
				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToLoadConfig());
			}
		});
	}

	private void loadMirrors() {
		mainPanel.showWaitPart(true);
		SmartAirService.Connect.getInstance().loadMirrors(new AsyncCallback<List<MirrorCran>>() {

			@Override
			public void onSuccess(List<MirrorCran> result) {
				for (MirrorCran mirror : result) {
					lstMirrors.addItem(mirror.getName(), mirror.getUrl());
				}
				mirrors = result;
				
				loadSmartAdmin();
//				if (selectedMirror != null) {
//					handleMirrorClick(selectedMirror.getUrl());
//				}
//				else {
//					handleMirrorClick("");
//				}
//				timerLoadInstalledPackages = new Timer() {
//					public void run() {
//						loadInstalledPackages();
//						timerLoadInstalledPackages = null;
//				    }
//				};	
				
				if(mapContainer.isAttached())
					loadMap();
			}

			@Override
			public void onFailure(Throwable caught) {
				mainPanel.showWaitPart(false);
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToLoadMirrors());
			}
		});

	}

	private void loadInstalledPackages() {
		String path = mainPanel.getUserSession().getRlibs();
		RScriptModel box = new RScriptModel();
		String script = "manual_result <- installed.packages(lib=\"" + path + "\")[,\"Package\"]\n";
		script+= "manual_result2 <- old.packages(lib=\"" + path + "\", repos=\"" + selectedMirror.getUrl() + "\")[,\"Package\"]";
		box.setScript(script);
		box.setUserREnv(user.getLogin() + user.getId());
		box.setOutputs(new String[]{"manual_result", "manual_result2"});

		SmartAirService.Connect.getInstance().executeScript(box, new AsyncCallback<RScriptModel>() {

			@Override
			public void onSuccess(RScriptModel result) {
				mainPanel.showWaitPart(false);
				mainPanel.getLogPanel().addLog(result.getOutputLog());
				// System.out.println(result.getOutputLog());
				List<String> listInstalled = new ArrayList<String>(Arrays.asList(result.getOutputVarstoString().get(0).split("\t")));
				List<String> listOld = new ArrayList<String>(Arrays.asList(result.getOutputVarstoString().get(1).split("\t")));
				List<String> listGrid = new ArrayList<String>();
				
				
				for(String pack : listInstalled){
					if(listOld.contains(pack.toString())){
						listGrid.add(pack+";"+"1");
					} else {
						listGrid.add(pack+";"+"0");
					}
				}
				installedPackagesList.clear();
				installedPackagesList.addAll(listGrid);
				dataProvider.setList(listGrid);
				sortHandler.setList(dataProvider.getList());
				
			}

			@Override
			public void onFailure(Throwable caught) {
				mainPanel.showWaitPart(false);
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToLoadPackages());
			}
		});
	}

	private void loadAvailablePackages() {

		RScriptModel box = new RScriptModel();
		String script = "manual_result <- available.packages(contriburl = contrib.url('" + selectedMirror.getUrl() + "'))[,\"Package\"]";
		box.setScript(script);
		box.setUserREnv(user.getLogin() + user.getId());
		box.setOutputs("manual_result".split(" "));

		SmartAirService.Connect.getInstance().executeScript(box, new AsyncCallback<RScriptModel>() {

			@Override
			public void onSuccess(RScriptModel result) {
				if(timerLoadInstalledPackages != null) timerLoadInstalledPackages.run(); //lors de l'update du datasource
				if(result.getOutputVarstoString().size()> 0){
					final List<String> list = new ArrayList<String>(Arrays.asList(result.getOutputVarstoString().get(0).split("\t")));
					soracle.clear();
					Scheduler.get().scheduleDeferred(new Command() {
				        public void execute () {
				         soracle.addAll(list);
				        }
				      });
				}
				mainPanel.getLogPanel().addLog(result.getOutputLog());
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				mainPanel.showWaitPart(false);
				ExceptionManager.getInstance().handleException(caught, caught.getMessage());
			}
		});
	}

	@UiHandler("lstMirrors")
	public void onMirrorChange(ChangeEvent event) {
		String url = lstMirrors.getValue(lstMirrors.getSelectedIndex());
		for (MirrorCran mirror : mirrors) {
			if (mirror.getUrl().equals(url)) {
				selectedMirror = mirror;
				break;
			}
		}
		if (url.equals("")) {
			selectedMirror = new MirrorCran();
			selectedMirror.setId(0);
			btnInstall.setEnabled(false);
			errorMirror.setVisible(true);
		}
		else {
			btnInstall.setEnabled(true);
			errorMirror.setVisible(false);
		}
		selectMirror(url);
	}

	protected void loadMap() {
		JSONObject obj = new JSONObject();
		obj.put("mirrors", new JSONArray());

		for (MirrorCran mirror : mirrors) {
			JSONArray array = obj.get("mirrors").isArray();
			JSONObject jsonMirror = new JSONObject();

			jsonMirror.put("id", new JSONNumber(mirror.getId()));
			jsonMirror.put("name", new JSONString(mirror.getName()));
			jsonMirror.put("state", new JSONNumber(mirror.getAvailable()));
			jsonMirror.put("url", new JSONString(mirror.getUrl()));
			jsonMirror.put("lat", new JSONString(mirror.getLatitude()));
			jsonMirror.put("long", new JSONString(mirror.getLongitude()));
			if (selectedMirror != null) {
				if (mirror.getUrl().equals(selectedMirror.getUrl())) {
					jsonMirror.put("state", new JSONNumber(2));
				}
			}

			array.set(array.size(), jsonMirror);
		}

		obj.put("minLat", new JSONNumber(minLat));
		obj.put("maxLat", new JSONNumber(maxLat));
		obj.put("minLong", new JSONNumber(minLong));
		obj.put("maxLong", new JSONNumber(maxLong));
		obj.put("lat", new JSONNumber(((maxLat + minLat) * 0.5)));
		obj.put("long", new JSONNumber(((maxLong + minLong) * 0.5)));
		obj.put("zoom", new JSONNumber(6));

		renderMap(this, "mapContainer", obj);
	}

	public void handleMirrorClick(String url) {
		int i = 1;
		for (MirrorCran mirror : mirrors) {
			if (mirror.getUrl().equals(url)) {
				selectedMirror = mirror;
				break;
			}
			i++;
		}
		if (selectedMirror != null) {
			lstMirrors.setSelectedIndex(i);
			loadAvailablePackages();
		}
		else {
			lstMirrors.setSelectedIndex(0);
		}

	}

	public void updateSize() {
		mapResize();
	}

	@UiHandler("btnInstall")
	public void onInstallClick(ClickEvent event) {

		if (selectedMirror == null) {
			return;
		}
//		if (dataProvider.getList().contains(sbox.getText())) {
//			resultInstall.getElement().getStyle().setColor("red");
//			resultInstall.setText(LabelsConstants.lblCnst.PackageAlreadyInstalled());
//			resultInstall.setVisible(true);
//			return;
//		}
		resultInstall.setText("");
		mainPanel.showWaitPart(true);
		String path = mainPanel.getUserSession().getRlibs();

		RScriptModel box = new RScriptModel();
		String script = "av <- length(installed.packages(lib=\"" + path + "\")[,\"Package\"])\n";
		script += "install.packages(\"" + sbox.getText() + "\", lib=\"" + path + "\", repos=\"" + selectedMirror.getUrl() + "\")\n";
		script += "ap <- length(installed.packages(lib=\"" + path + "\")[,\"Package\"])\n";
		script += "manual_result <- ap > av";
		box.setScript(script);
		box.setUserREnv(user.getLogin() + user.getId());
		box.setOutputs("manual_result".split(" "));

		SmartAirService.Connect.getInstance().executeScript(box, new AsyncCallback<RScriptModel>() {

			@Override
			public void onSuccess(RScriptModel result) {
				mainPanel.showWaitPart(false);
				if (Integer.parseInt(result.getOutputVarstoString().get(0).trim().split("\t")[0]) == 1) { // success
					resultInstall.getElement().getStyle().setColor("green");
				
					resultInstall.setText(LabelsConstants.lblCnst.PackageInstallationSuccessfull());
					
					resultInstall.setVisible(true);
					sbox.setText("");
				} 
				else if(dataProvider.getList().contains(sbox.getText()+";1")) {
					resultInstall.getElement().getStyle().setColor("green");
					resultInstall.setText(LabelsConstants.lblCnst.UpdatePackageSuccessfull());
					resultInstall.setVisible(true);
					sbox.setText("");
				} 
				else if(dataProvider.getList().contains(sbox.getText()+";0")) {
					resultInstall.getElement().getStyle().setColor("green");
					resultInstall.setText(LabelsConstants.lblCnst.UpdatePackageSuccessfull());
					resultInstall.setVisible(true);
					sbox.setText("");
				}
				else { // echec
					resultInstall.getElement().getStyle().setColor("red");
					resultInstall.setText(LabelsConstants.lblCnst.UnableToInstallPackage());
					resultInstall.setVisible(true);
				}
				mainPanel.getLogPanel().addLog(result.getOutputLog());
				loadInstalledPackages();

			}

			@Override
			public void onFailure(Throwable caught) {
				mainPanel.showWaitPart(false);
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToInstallPackageError());
			}
		});
	}

	private DataGrid<String> createGridData() {
		TextCell cell = new TextCell() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
				if (value != null) {
					sb.appendHtmlConstant("<a class='" + style.hyperlinkPackage() + "' onclick='helpLibrary2(\"" + value + "\")'>" + value + "</a>");
				}
			}
		};
		Column<String, String> nameColumn = new Column<String, String>(cell) {

			@Override
			public String getValue(String object) {
				return object.split(";")[0];
			}
		};
		nameColumn.setSortable(true);

		TextCell labelCell = new TextCell() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
				if (value != null) {
					if(!value.equals("")){
						sb.appendHtmlConstant("<a class='" + style.updatablePackage() + "' onclick='updLib(\"" + value + "\")'>" + LabelsConstants.lblCnst.AvailableUpdate() + "</a>");
					} else {
						sb.appendHtmlConstant("");
					}
					
				}
			}
		};
		Column<String, String> updateColumn = new Column<String, String>(labelCell) {

			@Override
			public String getValue(String object) {
				if(object.split(";")[1].equals("1")){
					return object.split(";")[0];
				} else {
					return "";
				}
				
			}
		};
		
		DataGrid<String> dataGrid = new DataGrid<String>(15);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("420px");

		dataGrid.addColumn(nameColumn, LabelsConstants.lblCnst.Name());
		dataGrid.addColumn(updateColumn, "");

		dataGrid.setEmptyTableWidget(new Label(LabelsConstants.lblCnst.NoResult()));

		dataProvider = new ListDataProvider<String>();
		dataProvider.addDataDisplay(dataGrid);

		sortHandler = new ListHandler<String>(new ArrayList<String>());
		sortHandler.setComparator(nameColumn, new Comparator<String>() {

			@Override
			public int compare(String m1, String m2) {
				return m1.compareToIgnoreCase(m2);
			}
		});

		dataGrid.addColumnSortHandler(sortHandler);

		selectionModel = new SingleSelectionModel<String>();
		selectionModel.addSelectionChangeHandler(selectionChangeHandler);
		dataGrid.setSelectionModel(selectionModel);

		// Create a Pager to control the table.
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		// pager.addStyleName(style.pager());
		pager.setDisplay(dataGrid);

		panelPager.setWidget(pager);

		return dataGrid;
	}

	private Handler selectionChangeHandler = new Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			selectedPackage = selectionModel.getSelectedObject().split(";")[0].trim();
			boolean isOld;
			if(selectionModel.getSelectedObject().split(";")[1].equals("1")){
				isOld = true;
			} else  {
				isOld = false;
			}
			
			btnDelete.setVisible(selectedPackage != null);
			btnUpdate.setVisible(selectedPackage != null && isOld);
		}
	};

	public void handleHelpLibrary2Click(String name) {
		
		RScriptModel box = new RScriptModel();
		String script = "library(help = '" + name.trim() + "')";
		box.setScript(script);
		box.setUserREnv(user.getLogin() + user.getId());
		mainPanel.showWaitPart(true);
		SmartAirService.Connect.getInstance().executeScript(box, new AsyncCallback<RScriptModel>() {

			@Override
			public void onSuccess(RScriptModel result) {
				mainPanel.showWaitPart(false);
				mainPanel.getLogPanel().addLog(result.getOutputLog());

				if (result.getOutputFiles() != null) {
					int nbfiles = result.getOutputFiles().length;
					if (nbfiles > 0) {
						for (int i = 0; i < nbfiles; i++) {
							helpPanel.clear();
							String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.VIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + "Help" + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + CommonConstants.FORMAT_TXT;
							Frame frame = new Frame(fullUrl);
							frame.setStyleName(style.frameMarkdown());
							helpPanel.add(frame);
						}
					}
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				mainPanel.showWaitPart(false);
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToExecuteScript());
			}
		});
	}

	@UiHandler("btnSave")
	public void onSaveClick(ClickEvent event) {
		mainPanel.showWaitPart(true);
		adminconfig.setIdMirror(selectedMirror.getId());
		SmartAirService.Connect.getInstance().addOrEditAdmin(adminconfig, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				mainPanel.showWaitPart(false);
			}

			@Override
			public void onFailure(Throwable caught) {
				mainPanel.showWaitPart(false);
				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToSaveConfig());
			}
		});
	}

	public void handleFilterGrid(String value) {
		List<String> list = new ArrayList<String>(installedPackagesList);
		for (Iterator<String> it = list.iterator(); it.hasNext();) {
			if (!it.next().split(";")[0].toLowerCase().contains(value.toLowerCase()))
				it.remove(); // NOTE: Iterator's remove method, not ArrayList's,
								// is used.
		}
		dataProvider.setList(list);

	}

	private final native void initJS(AdminPanel adminPanel) /*-{
		var adminPanel = adminPanel;
		$wnd.filterGrid = function() {
			var str = $doc.getElementById("filter").value;
			adminPanel.@bpm.smart.web.client.panels.resources.AdminPanel::handleFilterGrid(Ljava/lang/String;)(str);
			$wnd.setTimeout(function() {
				$doc.getElementById("filter").value = str;
			}, 5);
		};
		$wnd.helpLibrary2 = function(name) {
			adminPanel.@bpm.smart.web.client.panels.resources.AdminPanel::handleHelpLibrary2Click(Ljava/lang/String;)(name);
		};
		$wnd.updLib = function(name) {
			adminPanel.@bpm.smart.web.client.panels.resources.AdminPanel::handleUpdateLib(Ljava/lang/String;)(name);
		};
	}-*/;

	private final native void renderMap(AdminPanel adminPanel, String div, JSONObject obj) /*-{
		var adminPanel = adminPanel;	
		$wnd.clickMirror = function(feature){
			adminPanel.@bpm.smart.web.client.panels.resources.AdminPanel::handleMirrorClick(Ljava/lang/String;)(feature.get('url'));
		};
																	
		$wnd.loadMirrors(div, obj);
	}-*/;

	private final native void mapResize() /*-{
		$wnd.resizeMap();
	}-*/;
	
	private final native void selectMirror(String url) /*-{
		$wnd.selectMirror(url);
	}-*/;
	
	@UiHandler("btnDelete")
	public void onDeleteClick(ClickEvent ev){
		final InformationsDialog dialConfirm = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.ConfirmDeletePackage() + selectedPackage + " ?", true);
		dialConfirm.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dialConfirm.isConfirm()) {
					
					String path = mainPanel.getUserSession().getRlibs();
					RScriptModel box = new RScriptModel();
					String script = "remove.packages(\"" + selectedPackage + "\", lib=\"" + path + "\")";
					box.setScript(script);
					box.setUserREnv(user.getLogin() + user.getId());
					mainPanel.showWaitPart(true);
					SmartAirService.Connect.getInstance().executeScript(box, new AsyncCallback<RScriptModel>() {

						@Override
						public void onSuccess(RScriptModel result) {
							//showWaitPart(false);
							mainPanel.getLogPanel().addLog(result.getOutputLog());
							loadInstalledPackages();
							MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.DeletePackageSuccessfull());
						}

						@Override
						public void onFailure(Throwable caught) {
							mainPanel.showWaitPart(false);
							caught.printStackTrace();

							ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToDeletePackage());
						}
					});
					
				}
			}
		});
		dialConfirm.center();
		
	}
	
	@UiHandler("btnUpdate")
	public void onUpdateClick(ClickEvent ev){
		handleUpdateLib(selectedPackage);
	}
	
	public void handleUpdateLib(String valueLib) {
		String path = mainPanel.getUserSession().getRlibs();
		RScriptModel box = new RScriptModel();
		String script = "manual_result <- install.packages(\"" + valueLib + "\", lib=\"" + path + "\", repos=\"" + selectedMirror.getUrl() + "\")";
		box.setScript(script);
		box.setUserREnv(user.getLogin() + user.getId());
		box.setOutputs(new String[]{"manual_result"});
		mainPanel.showWaitPart(true);
		SmartAirService.Connect.getInstance().executeScript(box, new AsyncCallback<RScriptModel>() {

			@Override
			public void onSuccess(RScriptModel result) {
				//showWaitPart(false);
				mainPanel.getLogPanel().addLog(result.getOutputLog());
				loadInstalledPackages();
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.UpdatePackageSuccessfull());
			}

			@Override
			public void onFailure(Throwable caught) {
				mainPanel.showWaitPart(false);
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, caught.getMessage());
			}
		});

	}
	
	@UiHandler("btnReload")
	public void onReloadClick(ClickEvent ev){
		mainPanel.showWaitPart(true);
		loadInstalledPackages();
	}

	@Override
	public void onCollapse(boolean collapse) {
		if(this.isAttached()){
			Timer timer = new Timer() {
				public void run() {
					mapResize();
				}
		    };
		    timer.schedule(100);
		}
		
		
	}
}
