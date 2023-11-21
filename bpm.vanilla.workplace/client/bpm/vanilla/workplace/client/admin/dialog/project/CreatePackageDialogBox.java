package bpm.vanilla.workplace.client.admin.dialog.project;

import java.util.Date;

import bpm.vanilla.workplace.client.admin.dialog.DialogInformation;
import bpm.vanilla.workplace.client.services.AdminService;
import bpm.vanilla.workplace.shared.model.PlaceWebPackage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

public class CreatePackageDialogBox extends DialogBox {
	private static final String CSS_HORIZONTAL_PANEL = "panelIndexFile";
	private static final String CSS_FORM_PANEL = "formPanel";
	
	private FormPanel formPanel;
	
	private FileUpload fileUpload;
	private TextBox txtName;
	private TextBox txtVersion;
	private TextBox txtVanillaVersion;
	private TextBox txtDocUrl;
	private TextBox txtSiteWebUrl;
	private TextBox txtPrerequisite;

	private int projectId;
	private int userId;
	
	public CreatePackageDialogBox(int projectId, int userId) {
		super();
		this.projectId = projectId;
		this.userId = userId;
		this.setText("Create a Package");
		
		FlexTable table = createMainPart();
		
		formPanel = new FormPanel();
		formPanel.setStylePrimaryName(CSS_FORM_PANEL);
		formPanel.setAction(GWT.getHostPageBaseURL() +  "bpm_vanilla_workplace/IndexPackageServlet");
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_POST);
		formPanel.addSubmitCompleteHandler(submitCompleteHandler);
		formPanel.setWidget(table);
		
		HTMLPanel mainPanel = new HTMLPanel("");
		mainPanel.add(formPanel);
		mainPanel.add(buildBottomPanel());
		
		this.add(mainPanel);
	}

	private FlexTable createMainPart() {
		txtName = new TextBox();
		txtName.setMaxLength(150);
		
		txtVersion = new TextBox();
		txtVersion.setMaxLength(150);
		
		txtVanillaVersion = new TextBox();
		txtVanillaVersion.setMaxLength(150);
		
		txtDocUrl = new TextBox();
		
		txtSiteWebUrl = new TextBox();
		
		txtPrerequisite = new TextBox();
		
		fileUpload = new FileUpload();
		fileUpload.setName("file");
	    fileUpload.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				String file = fileUpload.getFilename();
			}
		});
	    
	    HorizontalPanel container = new HorizontalPanel();
	    container.addStyleName(CSS_HORIZONTAL_PANEL);
	    container.add(fileUpload);
		
		FlexTable table = new FlexTable();
		table.setText(0, 0, "Name");
		table.setWidget(0, 1, txtName);
		table.setText(1, 0, "Version");
		table.setWidget(1, 1, txtVersion);
		table.setText(2, 0, "Vanilla Version");
		table.setWidget(2, 1, txtVanillaVersion);
		table.setText(3, 0, "Documentation URL");
		table.setWidget(3, 1, txtDocUrl);
		table.setText(4, 0, "Website");
		table.setWidget(4, 1, txtSiteWebUrl);
		table.setText(5, 0, "Prerequisite");
		table.setWidget(5, 1, txtPrerequisite);
		table.setWidget(6, 0, container);
		
		FlexCellFormatter formatter = table.getFlexCellFormatter();
		formatter.setColSpan(6, 0, 2);
		
		return table;
	}
	
	private AbsolutePanel buildBottomPanel(){
		Button btnCancel = new Button("Cancel");
		btnCancel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				CreatePackageDialogBox.this.hide();
			}
		});
		
		Button btnSubmit = new Button("Confirm");
		btnSubmit.getElement().getStyle().setMarginLeft(5, Unit.PX);
		btnSubmit.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				String name = txtName.getText();
				String version = txtVersion.getText();
				String vanillaVersion = txtVanillaVersion.getText();
				String docUrl = txtDocUrl.getText();
				String website = txtSiteWebUrl.getText();
				String prerequisite = txtPrerequisite.getText();
				
				
				if(name.isEmpty()){
					DialogInformation dial = new DialogInformation("You need to fill the package name.");
					dial.setGlassEnabled(true);
					dial.center();
					return;
				}
				
				PlaceWebPackage pack = new PlaceWebPackage();
				pack.setName(name);
				pack.setVersion(version);
				pack.setVanillaVersion(vanillaVersion);
				pack.setType(2);
				pack.setProjectId(projectId);
				pack.setCreationDate(new Date());
				pack.setCreatorId(userId);
				pack.setDocumentationUrl(docUrl);
				pack.setSiteWebUrl(website);
				pack.setPrerequisUrl(prerequisite);
				pack.setValid(true);
				pack.setCertified(true);
				
				AdminService.Connect.getInstance().addPackage(pack, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();

						CreatePackageDialogBox.this.hide();
					
						DialogInformation info = new DialogInformation("An error happend");
						info.setGlassEnabled(true);
						info.center();
					}

					@Override
					public void onSuccess(Void result) {
						formPanel.submit();
					}
				});
			}
		});
		
		HorizontalPanel btnPanel = new HorizontalPanel();
		btnPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
		btnPanel.getElement().getStyle().setRight(5, Unit.PX);
		btnPanel.getElement().getStyle().setMarginTop(10, Unit.PX);
		btnPanel.getElement().getStyle().setMarginBottom(5, Unit.PX);
		btnPanel.add(btnCancel);
		btnPanel.add(btnSubmit);
		
		AbsolutePanel bottomPanel = new AbsolutePanel();
		bottomPanel.setSize("100%", "40px");
		bottomPanel.add(btnPanel);
		return bottomPanel;
	}
	
	private SubmitCompleteHandler submitCompleteHandler = new SubmitCompleteHandler() {
		
		public void onSubmitComplete(SubmitCompleteEvent event) {
			CreatePackageDialogBox.this.hide();
			
			DialogInformation info = new DialogInformation("Creation package is a success");
			info.setGlassEnabled(true);
			info.center();
		}
	};
}
