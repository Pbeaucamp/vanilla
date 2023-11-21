package bpm.gwt.aklabox.commons.client.upload;

import org.moxieapps.gwt.uploader.client.events.UploadErrorEvent.ErrorCode;

import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.TypeProcess;
import bpm.gwt.aklabox.commons.client.customs.ProgressBar;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.aklabox.commons.client.upload.FileUploadComposite.IUploadDocumentManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class FileUploadItem extends Composite {

	private static FileUploadItemUiBinder uiBinder = GWT.create(FileUploadItemUiBinder.class);

	interface FileUploadItemUiBinder extends UiBinder<Widget, FileUploadItem> {
	}
	
	@UiField
	Label lblFileName;
	
	@UiField
	SimplePanel progressPanel, btnPanel;
	
	private ProgressBar progress;

	private FileUploadManager uploader;
	private IUploadDocumentManager manager;
	private FileUploadManagerPopup parent;
	private String fileId;
	
	private Documents uploadedDocument;
	
	public FileUploadItem(FileUploadManager uploader, IUploadDocumentManager manager, FileUploadManagerPopup parent, String fileId, String fileName) {
		initWidget(uiBinder.createAndBindUi(this));
		this.uploader = uploader;
		this.manager = manager;
		this.parent = parent;
		this.fileId = fileId;
		
		lblFileName.setText(fileName);
		
		progress = new ProgressBar(40);
		progressPanel.setWidget(progress);
	}
	
	public String getFileId() {
		return fileId;
	}

	public void setProgress(double progress) {
		if (progress <= 100) {
			this.progress.setProgress(progress);
		}
	}

	//If support cancel
	public void onCancel() {
		uploader.cancelUpload(fileId);
		progressPanel.setVisible(false);
	}

	public void manageError(org.moxieapps.gwt.uploader.client.events.FileQueueErrorEvent.ErrorCode errorCode, String message) {
		//TODO: Manage error onFileQueueError
//		Window.alert(LabelConstants.lblCnst.UploadOfFile() + " " + fileQueueErrorEvent.getFile().getName() + " " + LabelConstants.lblCnst.failedDueTo() + "[" + fileQueueErrorEvent.getErrorCode().toString() + "]: " + fileQueueErrorEvent.getMessage());
	}

	public void manageError(ErrorCode errorCode, String message) {
		//TODO: Manage error onUploadError
//		Window.alert(LabelConstants.lblCnst.UploadOfFile() + " " + uploadErrorEvent.getFile().getName() + " " + LabelConstants.lblCnst.failedDueTo() + "[" + uploadErrorEvent.getErrorCode().toString() + "]: " + uploadErrorEvent.getMessage());
	}

	public void manageError(String response) {
		Window.alert(response);
	}

	public void manageInfos(String response, TypeProcess process) {
		int documentId = Integer.parseInt(response);
		
		switch (process) {
		case UPLOAD_DOCUMENTS:
			AklaCommonService.Connect.getService().postProcessDocument(manager.getParentEnterprise(), documentId, new GwtCallbackWrapper<Documents>(null, false, false) {
				@Override
				public void onSuccess(Documents result) {
					uploadedDocument = result;

					parent.checkUploadAndUpdateUi();
					progressPanel.removeFromParent();
					btnPanel.setVisible(true);
					manager.refreshFolder();
				}
			}.getAsyncCallback());
			break;
		case CHECKIN:
			manager.onUploadComplete(null);
			manager.refreshFolder();
			break;
		case UPLOAD_ONE_DOCUMENT:
			AklaCommonService.Connect.getService().postProcessDocument(manager.getParentEnterprise(), documentId, new GwtCallbackWrapper<Documents>(null, false, false) {
				@Override
				public void onSuccess(Documents result) {
					uploadedDocument = result;

					manager.onUploadComplete(result);
				}
			}.getAsyncCallback());
			break;

		default:
			break;
		}
	}

	public void setUploadComplete() {
		//Do nothing, all the work is done in manageInfos
	}
	
	public boolean isComplete() {
		return uploadedDocument != null;
	}
	
	public Documents getUploadedDocument() {
		return uploadedDocument;
	}

	//Save document in servlet
//	private Documents registerDocuments(String infos, File file, final IObject parent) {
//		String[] info = infos.split(";");
//		int fileSize = Integer.parseInt(info[0]);
//		String thumbPath = info[1];
//		String filePath = info[2];
//		String type = info[3];
//		final String extension = info[4];
//
//		if (type.equals(DocumentUtils.AUDIO)) {
//			thumbPath = "webapps/aklabox_files/images/type_audio.png";
//		}
//		else if (type.equals(DocumentUtils.OTHERS)) {
//			thumbPath = "webapps/aklabox_files/images/type_others.png";
//		}
//		else if (type.equals(DocumentUtils.XAKL)) {
//			thumbPath = "webapps/aklabox_files/images/type_xakl.png";
//		}
//		else if (type.equals(DocumentUtils.ZIP)) {
//			thumbPath = "webapps/aklabox_files/images/type_zip.png";
//		}
//		else if (type.equals(DocumentUtils.TEXT)) {
//			thumbPath = "webapps/aklabox_files/images/type_text.png";
//		}
//
//		final Documents doc = new Documents();
//		doc.setAnnotation("");
//		doc.setAuthorName(UserEmail.getEmail());
//		doc.setCheckoutStatus(false);
//		doc.setCreationDate(new Date());
//		doc.setDeleted(false);
//		doc.setEncrypt(false);
//		doc.setEncryptPassword(null);
//		doc.setFileExtension(extension);
//		doc.setFileName(file.getName());
//		doc.setFileSize(fileSize);
//		doc.setFinished(true);
//		doc.setItemTypeId(0);
//		doc.setLastModified(new Date());
//		doc.setLastModifiedBy(UserEmail.getEmail());
//		doc.setName(file.getName());
//		doc.setOriginalPath(filePath);
//		doc.setSecurityStatus("Enterprise");
//		doc.setTreeType("Documents");
//		doc.setUploadDate(new Date());
//		doc.setThumbImage(thumbPath);
//		doc.setParentId(parent.getId());
//		doc.setFilePath(filePath);
//		doc.setType(type);
//		doc.setValidationStatus("Waiting");
//
//		AdminService.Connect.getService().getFolderForms(parent.getId(), new GwtCallbackWrapper<List<FormFolder>>(null, false, false) {
//			@Override
//			public void onSuccess(List<FormFolder> result) {
//				try {
//					formId = result.get(0).getFormId();
//				} catch (Exception e) {
//
//				}
//				AdminService.Connect.getService().getFileTypes(new GwtCallbackWrapper<List<FileType>>(null, false, false) {
//					@Override
//					public void onSuccess(List<FileType> result) {
//						try {
//							for (FileType ft : result) {
//								if (ft.getForms().get(0).getId() == formId) {
//									doc.setTypeSelected(ft);
//									break;
//								}
//							}
//						} catch (Exception e) {
//						}
//
//						long l = 0;
//						for (int i = 0; i < doc.getFileName().length(); i++)
//							l = l * 127 + doc.getFileName().charAt(i) + doc.getId();
//						doc.setUniqueCode(String.valueOf(Math.abs(l)));
//
//						final List<Documents> documentList = new ArrayList<Documents>();
//						documentList.add(doc);
//
//						if (parent instanceof Tree && ((Tree) parent).isAutoStamp()) {
//							AdminService.Connect.getService().getEnterprisesPerFiles(parent.getId(), new GwtCallbackWrapper<List<Enterprise>>(null, false, false) {
//
//								@Override
//								public void onSuccess(List<Enterprise> result) {
//									if (result != null && !result.isEmpty()) {
//										Enterprise selectedEnterprise = result.get(0);
//										if (selectedEnterprise != null) {
//											saveDocuments(documentList, extension, true, selectedEnterprise.getStamp(), selectedEnterprise.isDisplayDate());
//											return;
//										}
//									}
//
//									saveDocuments(documentList, extension, false, null, false);
//								}
//							}.getAsyncCallback());
//						}
//						else {
//							saveDocuments(documentList, extension, false, null, false);
//						}
//
//					}
//
//				}.getAsyncCallback());
//			}
//		}.getAsyncCallback());
//
//		return doc;
//	}
//
//	private void saveDocuments(List<Documents> documentList, String extension, final boolean addStamp, final String logoPath, final boolean displayDate) {
//
//		ItemService.Connect.getService().saveDocuments(documentList, new ArrayList<User>(), new ArrayList<Group>(), UserEmail.getEmail(), new ArrayList<FolderShare>(), 0, 0, new ArrayList<String>(), extension, "", "", new RMDocument(), userMain.getUser().getUserType(), new GwtCallbackWrapper<List<Documents>>(null, false, false) {
//
//			@Override
//			public void onSuccess(List<Documents> result) {
//				for (Documents doc : result) {
//					displayForm(doc);
//
//					VersionController versionController = new VersionController();
//					versionController.saveVersion(doc, Cookies.getCookie(AklaboxConstant.COOKIE_EMAIL), 0, "", userMain, addStamp, logoPath, displayDate, true, true);
//
//					Rate rate = new Rate();
//					rate.setDocId(doc.getId());
//					rate.setUser(Cookies.getCookie(AklaboxConstant.COOKIE_EMAIL));
//					ItemService.Connect.getService().saveRate(rate, userMain.getUser().getUserType(), new GwtCallbackWrapper<Void>(null, false, false) {
//						@Override
//						public void onSuccess(Void result) {
//						}
//					}.getAsyncCallback());
//				}
//				userMain.showWaitPart(false);
//
//				userMain.getExplorerPanel().refresh(false, true, true);
//			}
//		}.getAsyncCallback());
//	}

//	private void displayForm(final Documents doc) {
//		WaitDialog.showWaitPart(true);
//		AdminService.Connect.getService().getFormForFolder(parent.getId(), userMain.getUser().getUserId(), doc, new GwtCallbackWrapper<Form>(null, false, false) {
//
//			@Override
//			public void onSuccess(Form result) {
//				WaitDialog.showWaitPart(false);
//				new DefaultDialog("Form Definition", new FormDisplayPanel(result, true, doc, null)).show();
//			}
//		}.getAsyncCallback());
//	}
}
