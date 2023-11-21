package bpm.gwt.aklabox.commons.client.workflows;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.aklabox.workflow.core.model.ActivityLog;
import bpm.aklabox.workflow.core.model.IInstanceStatus;
import bpm.aklabox.workflow.core.model.Instance;
import bpm.aklabox.workflow.core.model.Workflow.Type;
import bpm.aklabox.workflow.core.model.activities.Activity;
import bpm.aklabox.workflow.core.model.activities.AlertSignatureActivity;
import bpm.aklabox.workflow.core.model.activities.IActivity;
import bpm.aklabox.workflow.core.model.activities.OrbeonActivity;
import bpm.aklabox.workflow.core.model.activities.ScanActivity;
import bpm.aklabox.workflow.core.model.activities.UnzipFileActivity;
import bpm.aklabox.workflow.core.model.activities.UploadActivity;
import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.Enterprise.TypeUser;
import bpm.document.management.core.model.Tasks;
import bpm.document.management.core.model.Tasks.TaskStatus;
import bpm.document.management.core.model.Tree;
import bpm.document.management.core.model.User;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.aklabox.commons.client.upload.UploadDocumentDialog;
import bpm.gwt.aklabox.commons.client.utils.ActivityHelper;
import bpm.gwt.aklabox.commons.client.utils.DefaultDialog;
import bpm.gwt.aklabox.commons.client.utils.DefaultResultDialog;
import bpm.gwt.aklabox.commons.client.utils.ImageHelper;
import bpm.gwt.aklabox.commons.client.utils.WaitDialog;
import bpm.gwt.aklabox.commons.shared.Log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class InstanceTableItem extends Composite {

	private static InstanceTableItemUiBinder uiBinder = GWT.create(InstanceTableItemUiBinder.class);

	interface InstanceTableItemUiBinder extends UiBinder<Widget, InstanceTableItem> {
	}

	@UiField
	HTMLPanel panel, actionPanel;
	@UiField
	Label lblName, lblVersionNumber, lblStatus, lblUser, lblInstanceDate, lblActivityTime;
	@UiField 
	HTML lblCurrentActivity, lblCurrentAssignedUser;
	@UiField
	Image btnStart, btnStop, btnRestart, btnStandBy, imgWorkflow, btnView;
	@UiField
	CheckBox cbSelect;
	private Instance instance;
	private InstanceTable table;
//	private Workflow workflow;
	private List<Activity> currentActivities;

	public InstanceTableItem(Instance instance, InstanceTable table) {
		initWidget(uiBinder.createAndBindUi(this));
		this.instance = instance;
		this.table = table;
		
		initInstance();
		
//		AklaCommonService.Connect.getService().getWorkflow(instance.getWorkflowId(), new GwtCallbackWrapper<Workflow>(null, false, false) {
//
//			@Override
//			public void onSuccess(Workflow workflow) {
//				InstanceTableItem.this.workflow = workflow;
//				initInstance(InstanceTableItem.this.instance);
//				
//			}
//		}.getAsyncCallback());
	}

	@SuppressWarnings("deprecation")
	private void initInstance() {
		panel.removeStyleName("tableColumnSuccess");
		panel.removeStyleName("tableColumnFailure");
		panel.removeStyleName("tableColumnWarning");
		actionPanel.clear();
		if (instance.getInstanceStatus().equals(IInstanceStatus.RUNNING)) {
			panel.addStyleName("tableColumnSuccess");
			if(table.getUser().getSuperUser()){
				actionPanel.add(btnRestart);
				actionPanel.add(btnStop);
				actionPanel.add(btnStandBy);
			}
			
			getCurrentState();
		}
		else if (instance.getInstanceStatus().equals(IInstanceStatus.STOPED)) {
			panel.addStyleName("tableColumnFailure");
			if(table.getUser().getSuperUser()){
				actionPanel.add(btnRestart);
				actionPanel.add(btnStart);
			}
			
		}
		else if (instance.getInstanceStatus().equals(IInstanceStatus.STAND_BY)) {
			panel.addStyleName("tableColumnWarning");
			if(table.getUser().getSuperUser()){
				actionPanel.add(btnRestart);
				actionPanel.add(btnStart);
				actionPanel.add(btnStop);
			}
			
		}
		else if (instance.getInstanceStatus().equals(IInstanceStatus.FINISH)) {
			panel.addStyleName("tableColumnFinish");
			if(table.getUser().getSuperUser()){
				actionPanel.add(btnRestart);
				actionPanel.add(btnStart);
			}
			
		}
		actionPanel.add(btnView);
		lblName.setText(instance.getInstanceName());
		lblVersionNumber.setText(String.valueOf(instance.getVersionNumber()));
		lblStatus.setText(instance.getInstanceStatus());
		lblUser.setText(instance.getUserEmail());
		
		lblInstanceDate.setText(DateTimeFormat.getFormat("dd MMM yyyy hh:mm").format(instance.getInstanceDate()));
		
		try {
			imgWorkflow.setResource(ImageHelper.findResource(instance.getWorkflow()));
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}

	private void getCurrentState() {
		currentActivities = new ArrayList<>();
		if(instance.getWorkflow().getTypeWorkflow() == Type.ORBEON){
			List<Activity> nexts = new ArrayList<>();
			for (ActivityLog al : instance.getActivityLogs()) {
				if(al.getEndDate() == null) nexts.add((Activity) al.getActivity());
			}
//			AklaCommonService.Connect.getService().getOrbeonNextActivity(instance.getWorkflow(), instance, new GwtCallbackWrapper<List<Activity>>(null, false, false) {
//	
//				@Override
//				public void onSuccess(List<Activity> nexts) {
					currentActivities = nexts;
					String currentAct = ""; 
					String currentUser = "";
					for(Activity nextActivity : nexts){
						currentAct += nextActivity.getActivityName() + "<br>";
						
						currentUser += ActivityHelper.getAssignedUserOrbeon(nextActivity)+ "<br>";
//						currentUser += nextActivity.getOrgaElement().getName() + " - " + LabelHelper.functionToLabel(nextActivity.getOrgaFunction()) + "<br>";
					}
					if(!nexts.isEmpty()){
						currentAct = currentAct.substring(0, currentAct.length()-4);
						currentUser = currentUser.substring(0, currentUser.length()-4);
					}
					lblCurrentActivity.setHTML(currentAct);
					lblCurrentAssignedUser.setHTML(currentUser);
//					
//				}
//			}.getAsyncCallback());
			String lastDate = "";
			if(instance.getActivityLogs().size() > 0){
				try {
//					lastDate = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM).format(instance.getActivityLogs().get(instance.getActivityLogs().size() - 1).getEndDate());
					lastDate = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM).format(instance.getActivityLogs().get(instance.getActivityLogs().size() - 1).getStartDate());
				} catch (Exception e) {
//					e.printStackTrace();
				}
			}
			lblActivityTime.setText(lastDate);
		} else {
			for (ActivityLog al : instance.getActivityLogs()) {
				if (al.getEndDate() == null) {
					
					currentActivities.add((Activity) al.getActivity());
					lblCurrentActivity.setText(al.getActivityName());
					lblCurrentAssignedUser.setText(al.getAssignedUser());
					// String date =
					// DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM).format(new
					// Date(new Date().getTime() - al.getStartDate().getTime()));
					lblActivityTime.setText(DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM).format(al.getStartDate()));
					break;
				}
			}
		}
		
	}

	public void updateInstance(Instance result) {
		WaitDialog.showWaitPart(true);
		AklaCommonService.Connect.getService().updateInstance(result, new AsyncCallback<List<Log>>() {

			@Override
			public void onSuccess(List<Log> result) {
				table.getConsole().window.clear();
				table.getConsole().minimize(false);
				for (final Log log : result) {
					if (log.getActivity() instanceof UploadActivity || log.getActivity() instanceof ScanActivity) {

						if (log.getActivity() instanceof UploadActivity) {
							showUploadInterface(log.getActivity(), result, result.get(result.indexOf(log) + 2), "Upload");
						}
						else if (log.getActivity() instanceof ScanActivity) {
							showUploadInterface(log.getActivity(), result, result.get(result.indexOf(log) + 2), "Scan");
						}
					}
					else if (log.getActivity() instanceof UnzipFileActivity) {
						AklaCommonService.Connect.getService().extractZipFromAklaFlow((UnzipFileActivity) log.getActivity(), instance.getVersionNumber(), new AsyncCallback<List<Documents>>() {

							@Override
							public void onFailure(Throwable caught) {
								new DefaultResultDialog(caught.getMessage(), "failure").show();
							}

							@Override
							public void onSuccess(List<Documents> result) {
								
							}
						});
					}
					else if (log.getActivity() instanceof OrbeonActivity) {
						// TODO executeOrbeonForm((OrbeonActivity) log.getActivity());
					}
//					else if (log.getActivity() instanceof AssignTaskActivityPrompt) {
//						AddTask2 at = new AddTask2();
//
//						at.center();
//					}

//					instance.setInstanceStatus(IInstanceStatus.FINISH);
					initInstance();
					table.getConsole().window.add(new LogMessage(log));
				}
				initInstance();
				WaitDialog.showWaitPart(false);
			}

			@Override
			public void onFailure(Throwable caught) {
				new DefaultResultDialog(caught.getMessage(), "failure").show();
				WaitDialog.showWaitPart(false);
			}
		});
	}

	private void showUploadInterface(IActivity activity, final List<Log> listActivities, final Log nextActivity, final String interfaceValue) {
		WaitDialog.showWaitPart(true);
		AklaCommonService.Connect.getService().getAllAklaboxFiles((Activity) activity, instance.getVersionNumber(), new AsyncCallback<Tree>() {

			@Override
			public void onFailure(Throwable caught) {
				new DefaultResultDialog(caught.getMessage(), "failure").show();
				WaitDialog.showWaitPart(false);
			}

			@Override
			public void onSuccess(final Tree result) {
				// TODO: REFACTOR RIGHT - LATER - (Mail Management) Disable for
				// now
				// if (UserMain.getInstance().getExplorerPanel().getTreeType()
				// == ItemTreeType.MAIL_MANAGEMENT) {
				// CreateUploadDocuments uploadDocument;
				// uploadDocument = new CreateUploadDocuments(listActivities,
				// table.getUserMain(), result, result.getFolderStatus(),
				// interfaceValue, nextActivity);
				// uploadDocument.show();
				// WaitDialog.showWaitPart(false);
				// }
				// else {
				WaitDialog.showWaitPart(false);

				//TODO: REFACTOR RIGHT - LATER - (Aklaflow) Disable for now - Use UploadDocumentDialog somedays
//				UploadClassicDocument uploadDocument = new UploadClassicDocument(listActivities, table.getUserMain(), result, interfaceValue, nextActivity);
//				uploadDocument.show();
				
				final UploadDocumentDialog dial = new UploadDocumentDialog(null, LabelsConstants.lblCnst.UploadDocument(), result);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if(nextActivity.getActivity() instanceof AlertSignatureActivity) {
							
							AklaCommonService.Connect.getService().getEnterpriseUsers(result.getId(), new GwtCallbackWrapper<HashMap<User, List<TypeUser>>>(null, false, false) {
								@Override
								public void onSuccess(HashMap<User, List<TypeUser>> result) {
									for(User u : result.keySet()) {
										for(TypeUser tu : result.get(u)) {
											if(tu == TypeUser.SIGNER) {
												Date date = new Date();
												Tasks t = new Tasks();
												t.setDueDate(date);
												t.setTaskTitle("Sign Document");
												t.setTaskGiverEmail(table.getUser().getEmail());
												t.setStatus(TaskStatus.SIGN);
												t.setUserEmail(u.getEmail());
												
												
												
												t.setDocId(dial.getDocument().get(0).getId());
												t.setTaskStatus("New");

												String message = "";//LabelConstants.lblCnst.ANewTaskHasBeenAssignedToYou() + ": " + LabelConstants.lblCnst.Sign() + " " + LabelConstants.lblCnst.TheDocument().toLowerCase() + " '" + document.getName() + "'.";
												
												
												
												AklaCommonService.Connect.getService().addTask(dial.getDocument().get(0), u, t, message, new GwtCallbackWrapper<Void>(null, false, false) {

													@Override
													public void onSuccess(Void result) {
													}
												}.getAsyncCallback());
											}
										}
									}
									
								}
								
							}.getAsyncCallback());

						}
					}
				});
				
				dial.center();
				
				// }
			}
		});

	}

	@UiHandler("btnStart")
	public void onStartInstance(ClickEvent e) {
		instance.setInstanceStatus(IInstanceStatus.RUNNING);
		updateInstance(instance);
	}

	@UiHandler("btnStop")
	void onStopInstance(ClickEvent e) {
		instance.setInstanceStatus(IInstanceStatus.STOPED);
		updateInstance(instance);
	}

	@UiHandler("btnRestart")
	void onRestartInstance(ClickEvent e) {
		instance.setInstanceStatus(IInstanceStatus.RUNNING);
		updateInstance(instance);
	}

	@UiHandler("btnStandBy")
	void onStandByInstance(ClickEvent e) {
		instance.setInstanceStatus(IInstanceStatus.STAND_BY);
		updateInstance(instance);
	}
	
	@UiHandler("btnView")
	void onViewInstance(ClickEvent e) {
		WorkflowViewPanel view = new WorkflowViewPanel(instance);
		DefaultDialog dial = new DefaultDialog(LabelsConstants.lblCnst.View(), view, 1000, 0, 10);
		
		dial.show();
	}
	
	public void setSelected(boolean selected){
		cbSelect.setValue(selected);
		table.manageDelegation();
	}
	
	public boolean isSelected(){
		return cbSelect.getValue();
	}

	public void delegateInstance(final String userId) {
		if(instance.getInstanceStatus().equals(IInstanceStatus.RUNNING)){
			if(instance.getWorkflow().getTypeWorkflow() == Type.ORBEON){
				if(currentActivities != null){
					AklaCommonService.Connect.getService().getInstanceById(instance.getId(), new GwtCallbackWrapper<Instance>(null, false, false) {
						@Override
						public void onSuccess(Instance result) {
							for(Activity activity : currentActivities){
								result.addDelegation(activity.getActivityId(), Integer.parseInt(userId));
							}
							updateInstance(result);
							AklaCommonService.Connect.getService().sendDelegationNotification(Integer.parseInt(userId), result, new GwtCallbackWrapper<Void>(null, false, false) {

								@Override
								public void onSuccess(Void result) {}
							}.getAsyncCallback());
						}
						
					}.getAsyncCallback());
					

				}
			} 
//			else {
//				DefaultResultDialog d = new DefaultResultDialog(LabelsConstants.lblCnst.ThisFunctionNotSupportedForWorkflow() + " " + instance.getWorkflow().getTypeWorkflow().name(), TypeMessage.warning.name());
//				d.show();
//			}
		}
	
	}
	
	public String getAssignedUser(){
		return lblCurrentAssignedUser.getText();
	}
	
	public Instance getInstance(){
		return instance;
	}
}
