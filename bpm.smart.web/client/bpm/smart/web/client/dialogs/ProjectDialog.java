package bpm.smart.web.client.dialogs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.CommonService;
import bpm.smart.core.model.AirProject;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.images.Images;
import bpm.smart.web.client.panels.resources.NavigationPanel;
import bpm.smart.web.client.services.SmartAirService;
import bpm.vanilla.platform.core.beans.data.Dataset;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public class ProjectDialog extends AbstractDialogBox {
	private static ProjectUiBinder uiBinder = GWT
			.create(ProjectUiBinder.class);

	interface ProjectUiBinder extends
			UiBinder<Widget, ProjectDialog> {
	}

	interface MyStyle extends CssResource {

	}

	@UiField
	Label lblName, lblDate, lblAuthor, lblSubject, txtDate, lblStatus, lblDatasets;
	
	@UiField
	TextBox txtName, txtAuthor;
	@UiField
	Image overlay;
	@UiField
	TextArea txtSubject;
	@UiField
	SimplePanel panelUpload, panelStatus;
	@UiField
	HTMLPanel avatar, datasetsGrid;
	@UiField
	FormPanel formAvatar;
	

	@UiField
	MyStyle style;

	private NavigationPanel parent;
	private AirProject project;
	private FileUpload fileUploadAvatar = new FileUpload();
	private RadioButton publicRadio, privateRadio;

	private ListDataProvider<Dataset> dataProvider;
	private MultiSelectionModel<Dataset> selectionModel;
	private ListHandler<Dataset> sortHandler;
	
	private List<Dataset> datasets = new ArrayList<Dataset>();
	
	public static LabelsConstants lblCnst = (LabelsConstants) GWT
			.create(LabelsConstants.class);

	public ProjectDialog(NavigationPanel parent) {
		super(lblCnst.ProjectCreation(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.project = new AirProject();
		this.project.setIdUserCreator(parent.getIdUser());
		setLabels();
		DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG);
		txtDate.setText(format.format(new Date()));
		createButtonBar(lblCnst.Ok(), okHandler, lblCnst.Cancel(), cancelHandler);
		
		fileUploadAvatar.setName("file");
		fileUploadAvatar.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				fileUploadAvatar.getFilename();
				formAvatar.submit();
			}
		});
		panelUpload.add(fileUploadAvatar);
		
		formAvatar.setAction(GWT.getHostPageBaseURL() + "bpm_smart_web/UploadAvatarServlet");
		formAvatar.setEncoding(FormPanel.ENCODING_MULTIPART);
		formAvatar.setMethod(FormPanel.METHOD_POST);
		formAvatar.addSubmitCompleteHandler(submitCompleteHandler);
		formAvatar.setWidget(panelUpload);
		
		generateStatusPanel();
		
		privateRadio.setValue(true);
		
		datasetsGrid.add(createGrid());
		loadDatasets();
	}

	public ProjectDialog(NavigationPanel parent, AirProject project) {
		super(lblCnst.ProjectEdition(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.project = project;
		
		setLabels();
		DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG);
		txtName.setText(this.project.getName());
		txtDate.setText(format.format(this.project.getDate()));
		txtAuthor.setText(this.project.getAuthor());
//		SmartAirService.Connect.getInstance().getAvatarIconUrl(this.project.getAvatar(), new AsyncCallback<String>() {
//			@Override
//			public void onSuccess(String result) {
//				//avatar.setUrl(result);
//				avatar.getElement().getStyle().setBackgroundImage("url("+result+")");
//			}
//			@Override
//			public void onFailure(Throwable caught) {
//				
//			}
//		});
		avatar.getElement().getStyle().setBackgroundImage("url("+this.project.getUrlIcon()+")");
		txtSubject.setText(this.project.getSubject());
		
		createButtonBar(lblCnst.Ok(), okHandler, lblCnst.Cancel(), cancelHandler);
		
		fileUploadAvatar.setName("file");
		//fileUploadAvatar.add
		fileUploadAvatar.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				fileUploadAvatar.getFilename();
				formAvatar.submit();
			}
		});
		panelUpload.add(fileUploadAvatar);
		
		formAvatar.setAction(GWT.getHostPageBaseURL() + "bpm_smart_web/UploadAvatarServlet");
		formAvatar.setEncoding(FormPanel.ENCODING_MULTIPART);
		formAvatar.setMethod(FormPanel.METHOD_POST);
		formAvatar.addSubmitCompleteHandler(submitCompleteHandler);
		formAvatar.setWidget(panelUpload);
		
		generateStatusPanel();
		if(this.project.isPrivate()){
			privateRadio.setValue(true);
		} else {
			publicRadio.setValue(true);
		}
		
		datasetsGrid.add(createGrid());
		loadDatasets();
	}

	
	private void setLabels() {
		
		lblName.setText(lblCnst.Name());
		lblDate.setText(lblCnst.Date());
		lblAuthor.setText(lblCnst.Author());
		//lblIcon.setText(lblCnst.Icon());
		lblSubject.setText(lblCnst.Subject());
		lblStatus.setText(lblCnst.Status());
		lblDatasets.setText(lblCnst.LinkedDatasets());
	}

	@UiHandler("overlay")
	public void onAddClick(ClickEvent event) {
		fileUploadAvatar.getElement().<InputElement>cast().click();
	}
	
	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			
			if(txtName.getText() == null || txtAuthor.getText() == null || txtDate.getText() == null) {
				InformationsDialog dial = new InformationsDialog(lblCnst.Error(), lblCnst.Ok(), lblCnst.Cancel(), lblCnst.MissingInformation(), false);
				dial.center();
			}
			else {
				DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG);
				project.setAuthor(txtAuthor.getText());
				project.setName(txtName.getText());
//				if(avatar.getUrl() ==  null){
//					project.setAvatar("");
//				}else {
//					project.setAvatar(avatar.getUrl().split("vanilla_files/")[1]);
//				}
				if(avatar.getElement().getStyle().getBackgroundImage().equals(null) 
						|| avatar.getElement().getStyle().getBackgroundImage().equals("") 
						|| avatar.getElement().getStyle().getBackgroundImage().equals(GWT.getHostPageBaseURL())){
					project.setAvatar("");
				}else {
					try {
						String res = avatar.getElement().getStyle().getBackgroundImage();
						project.setAvatar(res.substring(4, res.length()-2).split("vanilla_files/")[1]);
					} catch (Exception e) {
					}
				}
				project.setDate(format.parse(txtDate.getText()));
				project.setPrivate(privateRadio.getValue());
				project.setSubject(txtSubject.getText());
				String ids = "";
				for(Dataset dts : datasets){
					ids += dts.getId() + ";";
				}
				if(ids.length() > 0)	ids = ids.substring(0, ids.length()-1);
				
				project.setLinkedDatasets(ids);
				SmartAirService.Connect.getInstance().addOrEditProject(project, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						parent.onRefreshClick(null);
						ProjectDialog.this.hide();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						InformationsDialog dial = new InformationsDialog(lblCnst.Error(), lblCnst.Ok(), lblCnst.UnableToAddAProject(), caught.getMessage(), caught);
						dial.center();
					}
				});
			}

		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			ProjectDialog.this.hide();
		}
	};
	
	private SubmitCompleteHandler submitCompleteHandler = new SubmitCompleteHandler() {
		
		public void onSubmitComplete(SubmitCompleteEvent event) {
			String res = event.getResults();
//			res = res.subSequence(5, res.length()-6).toString();
			
			if(res != ""){
			//	avatar.setUrl(res);
				avatar.getElement().getStyle().setBackgroundImage("url("+res+")");
			}
			
		}
	};
	
	private void generateStatusPanel(){
		HorizontalPanel hpanel = new HorizontalPanel();
		Image publicIcon = new Image(Images.INSTANCE.ic_public());
		Image privateIcon = new Image(Images.INSTANCE.ic_private());
		privateIcon.getElement().getStyle().setMarginLeft(50, Unit.PX);
		publicRadio = new RadioButton("statut", lblCnst.Public());
		privateRadio = new RadioButton("statut", lblCnst.Private());
		hpanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hpanel.add(publicIcon);
		hpanel.add(publicRadio);
		hpanel.setSpacing(10);
		hpanel.add(privateIcon);
		hpanel.add(privateRadio);
		
		panelStatus.add(hpanel);
	}
	
	private DataGrid<Dataset> createGrid() {
		
		TextCell cell = new TextCell();
		Column<Dataset, Boolean> checkboxColumn = new Column<Dataset, Boolean>(new CheckboxCell(true, true)) {

			@Override
			public Boolean getValue(Dataset object) {
				return selectionModel.isSelected(object);
			}
		};

		Column<Dataset, String> nameColumn = new Column<Dataset, String>(cell) {

			@Override
			public String getValue(Dataset object) {
				return object.getName();
			}
		};
		nameColumn.setSortable(true);

		checkboxColumn.setFieldUpdater(new FieldUpdater<Dataset, Boolean>() {

			@Override
			public void update(int index, Dataset object, Boolean value) {

				selectionModel.setSelected(object, value);
			}
		});

		// DataGrid.Resources resources = new CustomResources();
		DataGrid<Dataset> dataGrid = new DataGrid<Dataset>(99);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");

		// Attention au label
		dataGrid.addColumn(checkboxColumn, "");
		dataGrid.addColumn(nameColumn, lblCnst.Name());
		dataGrid.setColumnWidth(checkboxColumn, 20.0, Unit.PCT);
		dataGrid.setColumnWidth(nameColumn, 80.0, Unit.PCT);

		dataGrid.setEmptyTableWidget(new Label(lblCnst.NoResult()));

		dataProvider = new ListDataProvider<Dataset>();
		dataProvider.addDataDisplay(dataGrid);

		sortHandler = new ListHandler<Dataset>(new ArrayList<Dataset>());
		sortHandler.setComparator(nameColumn, new Comparator<Dataset>() {

			@Override
			public int compare(Dataset m1, Dataset m2) {
				return m1.getName().compareTo(m2.getName());
			}
		});

		dataGrid.addColumnSortHandler(sortHandler);

		// Add a selection model so we can select cells.
		selectionModel = new MultiSelectionModel<Dataset>();
		selectionModel.addSelectionChangeHandler(selectionChangeHandler);
		dataGrid.setSelectionModel(selectionModel);

		return dataGrid;
	}

	private Handler selectionChangeHandler = new Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			datasets.clear();
			datasets.addAll(selectionModel.getSelectedSet());
		}
	};
	
	private void loadDatasets() {
		showWaitPart(true);
		CommonService.Connect.getInstance().getPermittedDatasets(new AsyncCallback<List<Dataset>>() {

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToLoadDatasets());
			}

			@Override
			public void onSuccess(List<Dataset> result) {
				showWaitPart(false);
				dataProvider.setList(result);
				sortHandler.setList(dataProvider.getList());
				selectionModel.clear();
				if(project.getLinkedDatasets()!=null){
					for (Dataset dts : dataProvider.getList()) {
						for (String id : project.getLinkedDatasets().split(";")) {
							if (dts.getId() == Integer.parseInt(id)) {
								selectionModel.setSelected(dts, true);
							}

						}
					}
				}
				
			}

		});
	}
	
}
