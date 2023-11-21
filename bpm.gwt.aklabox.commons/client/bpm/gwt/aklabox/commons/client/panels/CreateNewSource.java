package bpm.gwt.aklabox.commons.client.panels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bpm.document.management.core.model.SourceConnection;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.utils.ChildDialogComposite;
import bpm.gwt.aklabox.commons.client.utils.DefaultDialog;
import bpm.gwt.aklabox.commons.client.utils.MessageDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;

public class CreateNewSource extends ChildDialogComposite {

	private static CreateNewSourceUiBinder uiBinder = GWT
			.create(CreateNewSourceUiBinder.class);

	interface CreateNewSourceUiBinder extends UiBinder<Widget, CreateNewSource> {
	}
	
	interface MyStyle extends CssResource {
		String lst();
	}
	
	@UiField
	TextBox txtName, txtUrl;
	
	@UiField
	PasswordTextBox txtUser, txtPassword;
	
	@UiField
	SimplePanel listDrivers;
	
	@UiField
	Button btnSave, btnCancel, btnConnection;
	
	@UiField
	MyStyle style;
	
	ValueListBox<String> drivers = new ValueListBox<String>(new Renderer<String>() {
		@Override
		public String render(String object) {
			return object != null ? object: "";
		}

		@Override
		public void render(String object, Appendable appendable)
				throws IOException {
			appendable.append(render(object));			
		}
	});
	
	private Boolean confirm=false;
	private SourceConnection source;

	public CreateNewSource() {
		initWidget(uiBinder.createAndBindUi(this));
		drivers.addStyleName(style.lst());
		listDrivers.add(drivers);
		initDriver();
	}
	
	private void initDriver(){
		try{
			AklaCommonService.Connect.getService().getLisDriver(new AsyncCallback<List<String>>() {

				@Override
				public void onFailure(Throwable caught) {
					//new SessionController().checkSession(caught);		
				}

				@Override
				public void onSuccess(List<String> result) {
					Set<String> set =new HashSet<String>(result);
					result=new ArrayList<String>(set);
			//		drivers.setValue(result.get(0));
					drivers.setAcceptableValues(result);			
				}
			});	
			
		} catch(Exception e){
			e.printStackTrace();
		}	
	}
	
	
	@UiHandler("btnCancel")
	void onCancel(ClickEvent e){	
		closeParent();
	}
	
	@UiHandler("btnSave")
	void onSave(ClickEvent e){
		try{
			if(!txtName.getText().isEmpty() && !txtUrl.getText().isEmpty() && !txtPassword.getText().isEmpty() &&
					!txtUser.getText().isEmpty() && drivers.getValue()!=null){
			
				source=new SourceConnection();
				source.setName(txtName.getText());
				source.setDriver((String)drivers.getValue());
				source.setLogin(txtUser.getText());
				source.setPassword(txtPassword.getText());
				source.setUrl(txtUrl.getText());
				
				AklaCommonService.Connect.getService().saveSourceConnection(source, new AsyncCallback<SourceConnection>(){
					@Override
					public void onFailure(Throwable caught) {
						//new SessionController().checkSession(caught);			
					}

					@Override
					public void onSuccess(SourceConnection result) {
						confirm=true;
						source=result;
						closeParent();			
					}			
				});
			}
			else 
			{
				MessageDialog message = new MessageDialog(LabelsConstants.lblCnst.FillFieldsErrorMes(),false);
				DefaultDialog defaultDialog = new DefaultDialog(LabelsConstants.lblCnst.FillFieldsErrorTitle(), message, 400, 210, 10);
				defaultDialog.center();
			}
		} catch(Exception ex){
			
		}		
	}
	
	@UiHandler("btnConnection")
	void onConnection(ClickEvent e){	
		try{
			if(!txtName.getText().isEmpty() && !txtUrl.getText().isEmpty() && !txtPassword.getText().isEmpty() &&
					!txtUser.getText().isEmpty()){
			
				SourceConnection sourceTest =new SourceConnection();
				sourceTest.setName(txtName.getText());
				sourceTest.setDriver((String)drivers.getValue());
				sourceTest.setLogin(txtUser.getText());
				sourceTest.setPassword(txtPassword.getText());
				sourceTest.setUrl(txtUrl.getText());
				
				AklaCommonService.Connect.getService().testConnection(sourceTest, new AsyncCallback<Boolean>(){
					@Override
					public void onFailure(Throwable caught) {
						//new SessionController().checkSession(caught);	
						
						MessageDialog message = new MessageDialog(LabelsConstants.lblCnst.ConnectionFailedMessage(),false);
						DefaultDialog defaultDialog = new DefaultDialog(LabelsConstants.lblCnst.ConnectionTitle(), message, 400, 210, 10);
						defaultDialog.center();
					}

					@Override
					public void onSuccess(Boolean result) {
						if(result){
							MessageDialog message = new MessageDialog(LabelsConstants.lblCnst.ConnectionSuccessfulMessage(),false);
							DefaultDialog defaultDialog = new DefaultDialog(LabelsConstants.lblCnst.ConnectionTitle(), message, 400, 210, 10);
							defaultDialog.center();
						}
						else{
							MessageDialog message = new MessageDialog(LabelsConstants.lblCnst.ConnectionFailedMessage(),false);
							DefaultDialog defaultDialog = new DefaultDialog(LabelsConstants.lblCnst.ConnectionTitle(), message, 400, 210, 10);
							defaultDialog.center();
						}
					}			
				});
			}
			else 
			{
				MessageDialog message = new MessageDialog(LabelsConstants.lblCnst.FillFieldsErrorMes(),false);
				DefaultDialog defaultDialog = new DefaultDialog(LabelsConstants.lblCnst.FillFieldsErrorTitle(), message, 400, 210, 10);
				defaultDialog.center();
			}
		} catch(Exception ex){
			
		}	
	}
	

	public Boolean isConfirm() {
		return confirm;
	}
	

	public SourceConnection getSource() {
		return source;
	}
	
	
	

}
