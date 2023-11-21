package bpm.vanilla.workplace.client.admin.dialog.project;

import java.util.Date;

import bpm.vanilla.workplace.client.admin.dialog.DialogInformation;
import bpm.vanilla.workplace.client.services.AdminService;
import bpm.vanilla.workplace.shared.model.PlaceWebProject;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;

public class CreateProjectDialogBox extends DialogBox {
	private TextBox txtName;
	private TextBox txtVersion;

	private int userId;
	
	public CreateProjectDialogBox(int userId) {
		super();
		this.userId = userId;
		this.setText("Create a Project");
		
		FlexTable table = createMainPart();
		
		HTMLPanel mainPanel = new HTMLPanel("");
		mainPanel.add(table);
		mainPanel.add(buildBottomPanel());
		
		this.add(mainPanel);
	}

	private FlexTable createMainPart() {
		txtName = new TextBox();
		txtName.setMaxLength(150);
		
		txtVersion = new TextBox();
		txtVersion.setMaxLength(150);
		
		FlexTable table = new FlexTable();
		table.setText(0, 0, "Name");
		table.setWidget(0, 1, txtName);
		table.setText(1, 0, "Version");
		table.setWidget(1, 1, txtVersion);
		
		return table;
	}
	
	private AbsolutePanel buildBottomPanel(){
		Button btnCancel = new Button("Cancel");
		btnCancel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				CreateProjectDialogBox.this.hide();
			}
		});
		
		Button btnSubmit = new Button("Confirm");
		btnSubmit.getElement().getStyle().setMarginLeft(5, Unit.PX);
		btnSubmit.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				String name = txtName.getText();
				String version = txtVersion.getText();
				
				if(name.isEmpty()){
					DialogInformation dial = new DialogInformation("You need to fill the project name.");
					dial.setGlassEnabled(true);
					dial.center();
					return;
				}
				
				PlaceWebProject project = new PlaceWebProject();
				project.setName(name);
				project.setVersion(version);
				project.setCreatorId(userId);
				project.setCreationDate(new Date());
				
				AdminService.Connect.getInstance().addProject(userId, project, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						
						CreateProjectDialogBox.this.hide();
					
						DialogInformation info = new DialogInformation("An error happend");
						info.setGlassEnabled(true);
						info.center();
					}

					@Override
					public void onSuccess(Void result) {
						CreateProjectDialogBox.this.hide();
					
						DialogInformation info = new DialogInformation("Project creation is a success.");
						info.setGlassEnabled(true);
						info.center();
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
}
