package bpm.map.viewer.web.client.wizard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.fm.api.model.ComplexMap;
import bpm.fm.api.model.ComplexMapLevel;
import bpm.fm.api.model.ComplexMapMetric;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.map.viewer.web.client.UserSession;
import bpm.map.viewer.web.client.I18N.LabelsConstants;
import bpm.map.viewer.web.client.images.Images;
import bpm.map.viewer.web.client.utils.ColorPicker;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

public class AddMapParamsPage extends Composite implements IGwtPage {
	private static AddMapParamsPageUiBinder uiBinder = GWT.create(AddMapParamsPageUiBinder.class);
	
	public static LabelsConstants lblCnst = (LabelsConstants) GWT
			.create(LabelsConstants.class);
	public static Images images = (Images) GWT
			.create(Images.class);

	interface AddMapParamsPageUiBinder extends UiBinder<Widget, AddMapParamsPage> {}

	interface MyStyle extends CssResource {
		String selectedWidget();
		String clearTable();
	}

	@UiField
	SimplePanel gridLevel, gridMetric, commonLevel, commonMetric, uploadLevel, uploadMetric, typeMetric, colorLevel, colorMetric;
		
	@UiField
	FormPanel formLevel;
	
	@UiField
	MyStyle style;

	private IGwtWizard parent;
	private int index;
	private ComplexMapLevel selectedLevel;
	private ComplexMapMetric selectedMetric;
	private ComplexMap currentMap;
	
	private ListBox lstType =new ListBox();;
	private FlexTable flexLevel = new FlexTable();
	private FlexTable flexMetric = new FlexTable();
	private ColorPicker pickerLevel = new ColorPicker();
	private ColorPicker pickerMetric = new ColorPicker();
	private FileUpload fileUploadLevel = new FileUpload();
	private FileUpload fileUploadMetric = new FileUpload();

	private ListDataProvider<ComplexMapMetric> dataProviderMetric;
	private ListDataProvider<ComplexMapLevel> dataProviderLevel;
	private ListHandler<ComplexMapMetric> sortHandlerMetric;
	private ListHandler<ComplexMapLevel> sortHandlerLevel;
	private SingleSelectionModel<ComplexMapMetric> selectionModelMetric;
	private SingleSelectionModel<ComplexMapLevel> selectionModelLevel;

	private String webappUrl;
	
	public AddMapParamsPage(AddComplexMapWizard parent, int index) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.index = index;
		
		this.currentMap = parent.getCurrentComplexMap();
		
		gridLevel.add(LevelGridData());
		gridMetric.add(MetricGridData());
		
		this.webappUrl = UserSession.getInstance().getWebappUrl();
		
		initMetrics();
		initLevels();
		
		loadStatics();
		
		loadMetrics();
		loadLevels();
		
		formLevel.setAction(GWT.getHostPageBaseURL() + "bpm_map_viewer_web/UploadFileServlet");
		formLevel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formLevel.setMethod(FormPanel.METHOD_POST);
		formLevel.addSubmitCompleteHandler(submitCompleteHandler);
		formLevel.setWidget(uploadLevel);
		
		
	}


	private void loadStatics() {
		
		loadIcons();
		
		fileUploadLevel.setName("file");
		fileUploadLevel.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				fileUploadLevel.getFilename();
				formLevel.submit();
			}
		});
		uploadLevel.add(fileUploadLevel);
		
		uploadMetric.add(new FileUpload());
		
		lstType.addItem("Bulles");
		//lstType.addItem("Secteurs");
		lstType.addItem("Cartes de chaleurs");
		typeMetric.add(lstType);
		
		colorLevel.add(pickerLevel);
		colorMetric.add(pickerMetric);
	}
	private void loadIcons() {
		List<String> imgTab = UserSession.getInstance().getIconSet();
		commonLevel.clear();
		commonMetric.clear();
		int i = 0;
		for(String res: imgTab){
			
			if (res.contains("webapps")) {
				res = res.substring(res.indexOf("webapps") + "webapps".length(), res.length());
			}
			String url = webappUrl + res.replace("\\", "/");
			
			Image img = new Image(url);
			img.setHeight("18px");img.setWidth("18px");
			img.addClickHandler(new ClickHandler() {
			      @Override
			      public void onClick(ClickEvent event) {
			    	  selectedLevel.setIconUrl(((Image)event.getSource()).getUrl().split("KpiMap_Icons/")[1]);
			    	  for(int i = 0; i<flexLevel.getRowCount(); i++){
			    		  for(int j =0; j<flexLevel.getCellCount(i); j++){
			    			  flexLevel.getWidget(i, j).removeStyleName(style.selectedWidget());
			    		  }
			    	  }
			    	  ((Image)event.getSource()).addStyleName(style.selectedWidget());
			      }
			});
			int line = (int)(i/15);
			flexLevel.setWidget(line, i- line*15, img);
			i++;
			
			try {
				if(selectedLevel != null && url.contains(selectedLevel.getIconUrl())) {
					img.addStyleName(style.selectedWidget());
				}
			} catch(Exception e) {
			}
		}
		commonLevel.add(flexLevel);
		
		//grid = new FlexTable();
		i = 0;
		for(String res: imgTab){
			if (res.contains("webapps")) {
				res = res.substring(res.indexOf("webapps") + "webapps".length(), res.length());
			}
			String url = webappUrl + res.replace("\\", "/");
			
			Image img = new Image(url);
			//Image img = new Image(res.replace("\\", "/"));
			img.setHeight("18px");img.setWidth("18px");
			img.addClickHandler(new ClickHandler() {
			      @Override
			      public void onClick(ClickEvent event) {
			    	  selectedMetric.setIconUrl(((Image)event.getSource()).getUrl().split("KpiMap_Icons/")[1]);
			    	  for(int i = 0; i<flexMetric.getRowCount(); i++){
			    		  for(int j =0; j<flexMetric.getCellCount(i); j++){
			    			  flexMetric.getWidget(i, j).removeStyleName(style.selectedWidget());
			    		  }
			    	  }
			    	  ((Image)event.getSource()).setStyleName(style.selectedWidget());
			      }
			});
			int line = (int)(i/15);
			flexMetric.setWidget(line, i- line*15, img);
			i++;
			
			try {
				if(selectedMetric != null && url.contains(selectedMetric.getIconUrl())) {
					img.addStyleName(style.selectedWidget());
				}
			} catch(Exception e) {
			}
		}
		commonMetric.add(flexMetric);
	}

	private void initLevels() {
		dataProviderLevel.setList(currentMap.getComplexLevels());
		if(dataProviderLevel.getList().size()> 0){
			selectedLevel = dataProviderLevel.getList().get(0);
			selectionModelLevel.setSelected(dataProviderLevel.getList().get(0), true);
		}

	}

	private void initMetrics() {
		dataProviderMetric.setList(currentMap.getComplexMetrics());
		selectedMetric = dataProviderMetric.getList().get(0);
		selectionModelMetric.setSelected(dataProviderMetric.getList().get(0), true);

	}
	
	private void loadLevels() {
		if(selectedLevel.getColor() == ""){
			pickerLevel.setColor("ffffff");
		} else {
			pickerLevel.setColor(selectedLevel.getColor());
		}
		
		for(int i = 0; i<flexLevel.getRowCount(); i++){
		  for(int j =0; j<flexLevel.getCellCount(i); j++){
			  if(((Image)flexLevel.getWidget(i, j)).getUrl().equals(selectedLevel.getIconUrl())){
				  flexLevel.getWidget(i, j).addStyleName(style.selectedWidget());
			  } else {
				  flexLevel.getWidget(i, j).removeStyleName(style.selectedWidget());
			  }
		  }
  	  	}
	}

	private void loadMetrics() {
		lstType.setSelectedIndex(0);
		for(int i = 0; i<lstType.getItemCount(); i++){
			if(lstType.getItemText(i).equals(selectedMetric.getRepresentation())){
				lstType.setSelectedIndex(i);
			}
		}
		
		
		if(selectedMetric.getColor() == ""){
			pickerMetric.setColor("ffffff");
		} else {
			pickerMetric.setColor(selectedMetric.getColor());
		}
		
		for(int i = 0; i<flexMetric.getRowCount(); i++){
		  for(int j =0; j<flexMetric.getCellCount(i); j++){
			  if(((Image)flexMetric.getWidget(i, j)).getUrl().equals(selectedMetric.getIconUrl())){
				  flexMetric.getWidget(i, j).addStyleName(style.selectedWidget());
			  } else {
				  flexMetric.getWidget(i, j).removeStyleName(style.selectedWidget());
			  }
		  }
  	  	}
	}

	@Override
	public boolean canGoBack() {
//		saveComplexMetric();
//		saveComplexLevel();
		return true;
	}

	@Override
	public boolean canGoFurther() {
		return false;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public boolean isComplete() {
		return true;
	}
	
	private DataGrid<ComplexMapMetric> MetricGridData() {

		TextCell cell = new TextCell();

		Column<ComplexMapMetric, String> nameColumn = new Column<ComplexMapMetric, String>(cell) {

			@Override
			public String getValue(ComplexMapMetric object) {
				return object.getMetric().getName();
			}
		};
		nameColumn.setSortable(true);

		// DataGrid.Resources resources = new CustomResources();
		DataGrid<ComplexMapMetric> dataGrid = new DataGrid<ComplexMapMetric>(12);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");

		// Attention au label
		dataGrid.addColumn(nameColumn, lblCnst.MetricName());

		dataGrid.setEmptyTableWidget(new Label(lblCnst.NoResult()));

		dataProviderMetric = new ListDataProvider<ComplexMapMetric>();
		dataProviderMetric.addDataDisplay(dataGrid);

		sortHandlerMetric = new ListHandler<ComplexMapMetric>(new ArrayList<ComplexMapMetric>());
		sortHandlerMetric.setComparator(nameColumn, new Comparator<ComplexMapMetric>() {

			@Override
			public int compare(ComplexMapMetric m1, ComplexMapMetric m2) {
				return m1.getMetric().getName().compareTo(m2.getMetric().getName());
			}
		});

		dataGrid.addColumnSortHandler(sortHandlerMetric);

		// Add a selection model so we can select cells.
		selectionModelMetric = new SingleSelectionModel<ComplexMapMetric>();
		selectionModelMetric
				.addSelectionChangeHandler(selectionChangeHandlerMetric);
		dataGrid.setSelectionModel(selectionModelMetric);
		

		return dataGrid;
	}

	private DataGrid<ComplexMapLevel> LevelGridData() {

		TextCell cell = new TextCell();
		
		Column<ComplexMapLevel, String> nameColumn = new Column<ComplexMapLevel, String>(cell) {

			@Override
			public String getValue(ComplexMapLevel object) {
				return object.getLevel().getName();
			}
		};
		nameColumn.setSortable(true);

		
		// DataGrid.Resources resources = new CustomResources();
		DataGrid<ComplexMapLevel> dataGrid = new DataGrid<ComplexMapLevel>(12);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		
		// Attention au label
		dataGrid.addColumn(nameColumn, "Niveaux");

		dataGrid.setEmptyTableWidget(new Label(lblCnst.NoResult()));

		dataProviderLevel = new ListDataProvider<ComplexMapLevel>();
		dataProviderLevel.addDataDisplay(dataGrid);

		sortHandlerLevel = new ListHandler<ComplexMapLevel>(new ArrayList<ComplexMapLevel>());
		sortHandlerLevel.setComparator(nameColumn, new Comparator<ComplexMapLevel>() {

			@Override
			public int compare(ComplexMapLevel m1, ComplexMapLevel m2) {
				return m1.getLevel().getName().compareTo(m2.getLevel().getName());
			}
		});

		dataGrid.addColumnSortHandler(sortHandlerLevel);

		// Add a selection model so we can select cells.
		selectionModelLevel = new SingleSelectionModel<ComplexMapLevel>();
		selectionModelLevel
				.addSelectionChangeHandler(selectionChangeHandlerLevel);
		dataGrid.setSelectionModel(selectionModelLevel);


		return dataGrid;
	}
	
	private Handler selectionChangeHandlerMetric = new Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			saveComplexMetric();
			selectedMetric = selectionModelMetric.getSelectedObject();
			loadMetrics();
			
		}
	};
	
	private Handler selectionChangeHandlerLevel = new Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			saveComplexLevel();
			selectedLevel = selectionModelLevel.getSelectedObject();
			loadLevels();
			
		}
	};
	
	public void saveComplexMetric(){
		selectedMetric.setColor(pickerMetric.getTextBox().getText());
		selectedMetric.setRepresentation(lstType.getValue(lstType.getSelectedIndex()));
		
		List<ComplexMapMetric> list = dataProviderMetric.getList();
		for(ComplexMapMetric met : list){
			if(met.getIdMetric() == selectedMetric.getIdMetric()){
				int index = list.indexOf(met);
				list.remove(met);
				list.add(index, selectedMetric);
			}
		}
		dataProviderMetric.setList(list);
	}
	
	public void saveComplexLevel(){
		selectedLevel.setColor(pickerLevel.getTextBox().getText());
		
		List<ComplexMapLevel> list = dataProviderLevel.getList();
		if(dataProviderLevel.getList().size() > 0){
			for(ComplexMapLevel lev : list){
				if(lev.getIdLevel() == selectedLevel.getIdLevel()){
					int index = list.indexOf(lev);
					list.remove(lev);
					list.add(index, selectedLevel);
				}
			}
		} else {
			list.add(selectedLevel);
		}
		
		dataProviderLevel.setList(list);
	}
	
	public List<ComplexMapLevel> getComplexLevelList(){
		return new ArrayList<ComplexMapLevel>(dataProviderLevel.getList());
	}
	
	public List<ComplexMapMetric> getComplexMetricList(){
		return new ArrayList<ComplexMapMetric>(dataProviderMetric.getList());
	}
	
	private SubmitCompleteHandler submitCompleteHandler = new SubmitCompleteHandler() {
		
		public void onSubmitComplete(SubmitCompleteEvent event) {
			String res = event.getResults();
			res = res.subSequence(5, res.length()-6).toString();
			
			if(res != ""){
				List<String> set = UserSession.getInstance().getIconSet();
				set.add(res);
				UserSession.getInstance().setIconSet(set);
			}
			loadIcons();
		}
	};
}
