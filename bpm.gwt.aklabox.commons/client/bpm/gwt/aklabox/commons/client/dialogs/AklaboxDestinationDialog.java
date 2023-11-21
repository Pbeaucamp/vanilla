package bpm.gwt.aklabox.commons.client.dialogs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.aklabox.workflow.core.model.resources.AkLadExportObject;
import bpm.document.management.core.model.AkladematAdminEntity;
import bpm.document.management.core.model.Chorus;
import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.Tasks;
import bpm.document.management.core.model.Tasks.TaskStatus;
import bpm.document.management.core.model.User;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.images.CommonImages;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.tree.AklaboxTreeManager;
import bpm.gwt.aklabox.commons.client.tree.DirectoryTreeItem;
import bpm.gwt.aklabox.commons.client.utils.DefaultResultDialog;
import bpm.gwt.aklabox.commons.client.utils.MessageHelper;
import bpm.gwt.aklabox.commons.client.utils.DefaultResultDialog.TypeMessage;
import bpm.gwt.aklabox.commons.client.utils.WaitDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class AklaboxDestinationDialog extends AbstractDialogBox {

	private static AklaboxDestinationDialogUiBinder uiBinder = GWT.create(AklaboxDestinationDialogUiBinder.class);

	interface AklaboxDestinationDialogUiBinder extends UiBinder<Widget, AklaboxDestinationDialog> {
	}

	@UiField
	Tree destinationTree;
	@UiField Image imgDoc, imgDatePicker;
	@UiField Label lblName, lblType, lblDateTitle;
	@UiField HTMLPanel delegatePanel;
	@UiField ListBox listUser;
	
	private AkLadExportObject doc;
	private boolean isMail;
	private String login;
	private boolean sendToAccounting;
	
	private List<AkLadExportObject> docs;
	
	private DateTimePickerDialog datePickerDialog;
	
	private ExportAkladInterface parent;

	public AklaboxDestinationDialog(String login, AkLadExportObject doc, final boolean isMail, boolean sendToAccounting, ExportAkladInterface parent) {
		super(LabelsConstants.lblCnst.PushToAklabox(), false, true);
		this.isMail = isMail;
		this.doc = doc;
		this.login = login;
		this.sendToAccounting = sendToAccounting;
		this.parent = parent;
		setWidget(uiBinder.createAndBindUi(this));
		
		lblName.setText(LabelsConstants.lblCnst.Name() + " : " + doc.getDoc().getName());
		lblType.setText(LabelsConstants.lblCnst.Type() + " : " + ((doc.getDoc().getTypeSelected()!=null)?doc.getDoc().getTypeSelected().getLabel() : LabelsConstants.lblCnst.None()) );
		
		imgDoc.setUrl(getRightPath(doc.getDoc().getThumbImage()));
		
		if(doc.getPagesMeta().get(0).getSelectedType() != null && doc.getPagesMeta().get(0).getSelectedType().getIdFolderParent() != 0 && !isMail){
			doc.getDoc().setParentId(doc.getPagesMeta().get(0).getSelectedType().getIdFolderParent());
			if(doc.getDoc().getUserId()==0){ //protection fake demo
            	doc.getDoc().setUserId(1);
            }
            
            List<AkLadExportObject> docus = new ArrayList<>();
            docus.add(doc);
			WaitDialog.showWaitPart(true);
			AklaCommonService.Connect.getService().saveDocumentsWithAnalyse(docus, sendToAccounting, new AsyncCallback<Map<AkLadExportObject, String>>() {
				
				@Override
				public void onSuccess(Map<AkLadExportObject, String> resultDoc) {
					WaitDialog.showWaitPart(false);
					manageExportDocumentResult(resultDoc);
//					if(isMail){
//						Documents doc = resultDoc.get(0);
//						doc.setValidationStatus("Waiting");
//						//doc.setStatus(DocumentStatus);
//						doc.setNature("leaving");
//						delegate(listUser.getValue(listUser.getSelectedIndex()), datePickerDialog.getSelectedDate(), doc);
//					}
//					hide();
//					DefaultResultDialog dial = new DefaultResultDialog(LabelsConstants.lblCnst.Success(), TypeMessage.success.name());
//					dial.show();
				}

				@Override
				public void onFailure(Throwable caught) {
					WaitDialog.showWaitPart(false);
					DefaultResultDialog dial = new DefaultResultDialog(LabelsConstants.lblCnst.Error() + " : " + caught.getMessage(), TypeMessage.failure.name());
					dial.show();
				}
			});
		}
		
		createButtonBar(LabelsConstants.lblCnst.Ok(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		initTree();
	}
	
	public AklaboxDestinationDialog(String login, List<AkLadExportObject> docs, final boolean isMail, boolean sendToAccounting, ExportAkladInterface parent) {
		super(LabelsConstants.lblCnst.PushToAklabox(), false, true);
		this.isMail = isMail;
		this.docs = docs;
		this.login = login;
		this.sendToAccounting = sendToAccounting;
		this.parent = parent;
		setWidget(uiBinder.createAndBindUi(this));
		
		lblName.setText(LabelsConstants.lblCnst.Name() + " : " + LabelsConstants.lblCnst.ManyDocuments());
		lblType.setText(LabelsConstants.lblCnst.Type() + " : " + LabelsConstants.lblCnst.ManyTypes() );
		
		imgDoc.setResource(CommonImages.INSTANCE.ic_many_docs());
		
		boolean allDocsHaveDestination = true;
		for(AkLadExportObject aeo : this.docs){
			if(aeo.getPagesMeta().get(0).getSelectedType() == null || (aeo.getPagesMeta().get(0).getSelectedType() != null && aeo.getPagesMeta().get(0).getSelectedType().getIdFolderParent() == 0) || isMail){
				allDocsHaveDestination = false;this.center();
				break;
			}
		}
		if(allDocsHaveDestination/* || sendToAccounting*/){
			for(AkLadExportObject aeo : this.docs){
				aeo.getDoc().setParentId(aeo.getPagesMeta().get(0).getSelectedType().getIdFolderParent());
				if(aeo.getDoc().getUserId()==0){ //protection fake demo
					aeo.getDoc().setUserId(1);
	            }
			}
			
            
			WaitDialog.showWaitPart(true);
			AklaCommonService.Connect.getService().saveDocumentsWithAnalyse(this.docs, sendToAccounting, new AsyncCallback<Map<AkLadExportObject, String>>() {
				
				@Override
				public void onSuccess(Map<AkLadExportObject, String> result) {
					
					WaitDialog.showWaitPart(false);
					
					manageExportDocumentResult(result);
					
				}

				@Override
				public void onFailure(Throwable caught) {
					WaitDialog.showWaitPart(false);
					DefaultResultDialog dial = new DefaultResultDialog(LabelsConstants.lblCnst.Error() + " : " + caught.getMessage(), TypeMessage.failure.name());
					dial.show();
				}
			});
		}
		
		
		createButtonBar(LabelsConstants.lblCnst.Ok(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		initTree();
	}
	
	private void initTree(){
		if (isMail) {
			AklaboxTreeManager.loadMail(null, 0, destinationTree, null);
			
			delegatePanel.setVisible(true);
			Date initialDate = new Date();
			lblDateTitle.setText(DateTimeFormat.getFormat("dd MMMM yyyy HH:mm").format(initialDate));
			
			datePickerDialog = new DateTimePickerDialog(initialDate, new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					Date date = datePickerDialog.getSelectedDate();
					String dateString = DateTimeFormat.getFormat("dd MMMM yyyy HH:mm").format(date);
					lblDateTitle.setText(dateString);
				}
			});
		}
		else {
			AklaboxTreeManager.loadEnterprise(null, login, destinationTree);
			delegatePanel.setVisible(false);
		}
	}

	@UiHandler("destinationTree")
	void onTreeOpen(OpenEvent<TreeItem> e) {
		
		if (isMail) {
			DirectoryTreeItem item = (DirectoryTreeItem) e.getTarget();
			int folderId = item.getDirectory().getId();
			AklaboxTreeManager.loadMail(null, folderId, destinationTree, item);
		}
	}
	
	@UiHandler("destinationTree")
	void onTreeClick(SelectionEvent<TreeItem> e) {
		
		if (isMail) {
			DirectoryTreeItem item = (DirectoryTreeItem) e.getSelectedItem();
			int folderId = item.getDirectory().getId();
			AklaCommonService.Connect.getService().getUsersFromOrganigramme(folderId, new AsyncCallback<List<User>>() {

				@Override
				public void onSuccess(List<User> result) {
					listUser.clear();
					listUser.addItem(LabelsConstants.lblCnst.SelectUserToDelegate());
					for(User user : result){
						listUser.addItem(user.getFirstName() + " "+ user.getLastName(), user.getEmail());
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
			});
		}
	}
	
	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			DirectoryTreeItem item = (DirectoryTreeItem) destinationTree.getSelectedItem();
            int folder = item.getDirectory().getId();
            
            if(listUser.getSelectedIndex() == 0){
            	DefaultResultDialog dial = new DefaultResultDialog(LabelsConstants.lblCnst.SelectUserToDelegate(), TypeMessage.warning.name());
				dial.show();
				return;
            }

            if(doc != null){ //doc par doc
            	if(doc.getDoc().getUserId()==0){ //protection fake demo
                	doc.getDoc().setUserId(1);
                }
                doc.getDoc().setParentId(folder);
                if(sendToAccounting && doc.getPagesMeta().get(0).getSelectedType() != null && doc.getPagesMeta().get(0).getSelectedType().getIdFolderParent() == 0){
                	doc.getPagesMeta().get(0).getSelectedType().setIdFolderParent(folder);
                }
                
                List<AkLadExportObject> docus = new ArrayList<>();
                docus.add(doc);
                WaitDialog.showWaitPart(true);
    			AklaCommonService.Connect.getService().saveDocumentsWithAnalyse(docus, sendToAccounting, new AsyncCallback<Map<AkLadExportObject, String>>() {
    				
    				@Override
    				public void onSuccess(Map<AkLadExportObject, String> resultDoc) {
    					WaitDialog.showWaitPart(false);
    					manageExportDocumentResult(resultDoc);
//    					if(isMail){
//    						Documents doc = resultDoc.get(0);
//    						doc.setValidationStatus("Waiting");
//    						//doc.setStatus(DocumentStatus);
//    						doc.setNature("leaving");
//    						delegate(listUser.getValue(listUser.getSelectedIndex()), datePickerDialog.getSelectedDate(), doc);
//    					}
//						hide();
//						DefaultResultDialog dial = new DefaultResultDialog(LabelsConstants.lblCnst.Success(), TypeMessage.success.name());
//						dial.show();
    				}

    				@Override
    				public void onFailure(Throwable caught) {
    					WaitDialog.showWaitPart(false);
    					DefaultResultDialog dial = new DefaultResultDialog(LabelsConstants.lblCnst.Error() + " : " + caught.getMessage(), TypeMessage.failure.name());
    					dial.show();
    				}
    			});
            } else { //liste de doc
            	
            	for(AkLadExportObject docu : docs){
            		if(docu.getDoc().getUserId()==0){ //protection fake demo
            			docu.getDoc().setUserId(1);
                    }
            		if(docu.getPagesMeta().get(0).getSelectedType() != null && docu.getPagesMeta().get(0).getSelectedType().getIdFolderParent() != 0){
            			docu.getDoc().setParentId(docu.getPagesMeta().get(0).getSelectedType().getIdFolderParent());
            		} else {
            			docu.getDoc().setParentId(folder);
            			if(sendToAccounting && docu.getPagesMeta().get(0).getSelectedType() != null && docu.getPagesMeta().get(0).getSelectedType().getIdFolderParent() == 0){
            				docu.getPagesMeta().get(0).getSelectedType().setIdFolderParent(folder);
                        }
            		}
            	}
            	
                WaitDialog.showWaitPart(true);
    			AklaCommonService.Connect.getService().saveDocumentsWithAnalyse(docs, sendToAccounting, new AsyncCallback<Map<AkLadExportObject, String>>() {
    				
    				@Override
    				public void onSuccess(Map<AkLadExportObject, String> result) {
    					
    					WaitDialog.showWaitPart(false);
    					manageExportDocumentResult(result);
//    					if(isMail){
//    						for(Documents doc : result){
//    							doc.setValidationStatus("Waiting");
//        						//doc.setStatus(DocumentStatus);
//        						doc.setNature("leaving");
//    							delegate(listUser.getValue(listUser.getSelectedIndex()), datePickerDialog.getSelectedDate(), doc);
//    						}
//    						
//    					}
//						hide();
//						DefaultResultDialog dial = new DefaultResultDialog(LabelsConstants.lblCnst.Success(), TypeMessage.success.name());
//						dial.show();
    				}

    				@Override
    				public void onFailure(Throwable caught) {
    					WaitDialog.showWaitPart(false);
    					DefaultResultDialog dial = new DefaultResultDialog(LabelsConstants.lblCnst.Error() + " : " + caught.getMessage(), TypeMessage.failure.name());
    					dial.show();
    				}
    			});
            }
            
            
		}
	};

	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
	
	private static boolean hostedMode = !GWT.isScript() && GWT.isClient();
	public String getRightPath(String path) {
		path = path.replace("\\", "/");
		if (hostedMode) {
			return path;
		}
		else {
			return path.replace("webapps/", "../");
		}

	}
	@Override
	public int getThemeColor() {
		return 5;
	}
	
	@UiHandler("imgDatePicker")
	void onChooseDate(ClickEvent e){
		datePickerDialog.center();
	}
	
	public void delegate(String email, Date dueDate, Documents doc) {
		showWaitPart(true);

		doc.setActivateDoc(false);

		Tasks delegateTask = new Tasks();
		delegateTask.setUserEmail(email);
		delegateTask.setStatus(TaskStatus.DELEGATED);
		delegateTask.setActive(true);
		delegateTask.setIndexTask(0);
		delegateTask.setDueDate(dueDate);
		delegateTask.setTaskGiverEmail(login);

		List<Tasks> tasks = new ArrayList<Tasks>();
		tasks.add(delegateTask);

		AklaCommonService.Connect.getService().saveDocument(doc, tasks, null, null, new AsyncCallback<Documents>() {

			@Override
			public void onSuccess(Documents doc) {
				
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				showWaitPart(false);
			}
		});
	}
	
	public void manageExportDocumentResult(Map<AkLadExportObject, String> result){
		List<AkLadExportObject> sent = new ArrayList<>();
		Map<AkLadExportObject, String> notSent = new HashMap<>();
		for(AkLadExportObject aeo : result.keySet()){
			if(result.get(aeo) == null){
				sent.add(aeo);
			} else {
				notSent.put(aeo, result.get(aeo));
			}
		}
		
		
		if(notSent != null && notSent.size() > 0){
			
			StringBuffer buf = new StringBuffer();
			buf.append(LabelsConstants.lblCnst.SendSummary() +"<br><br>");
			if(sent.size() > 0){
				buf.append(LabelsConstants.lblCnst.BillsSentSuccesfully() +":<br>");
				for(AkLadExportObject succ : sent){
					buf.append("- " + succ.getDoc().getName() + "<br>");
				}
				buf.append("<br>");
			}
			
			buf.append(LabelsConstants.lblCnst.BillsNotSent()+":<br>");
			
			for(AkLadExportObject err : notSent.keySet()){
				buf.append("- " + err.getDoc().getName() + "<br>");
				buf.append(LabelsConstants.lblCnst.Cause()+": " + notSent.get(err)+ "<br>");
			}
			
//			buf.append(LabelConstants.lblCnst.Sirets() + " " + sirets + " "+ LabelConstants.lblCnst.AreUnknownInAccounting());
			hide();
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), buf.toString());
			
		} else {
			hide();
			DefaultResultDialog dial = new DefaultResultDialog(LabelsConstants.lblCnst.Success(), TypeMessage.success.name());
			dial.show();
		}
		
		if(isMail){
			for(AkLadExportObject doc : sent){
				doc.getDoc().setValidationStatus("Waiting");
				//doc.setStatus(DocumentStatus);
				doc.getDoc().setNature("leaving");
				delegate(listUser.getValue(listUser.getSelectedIndex()), datePickerDialog.getSelectedDate(), doc.getDoc());
			}
			
		}
		
		parent.manageSentDocuments(sent);
	}
}