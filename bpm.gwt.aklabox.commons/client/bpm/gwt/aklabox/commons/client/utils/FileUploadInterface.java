package bpm.gwt.aklabox.commons.client.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import org.moxieapps.gwt.uploader.client.File;
import org.moxieapps.gwt.uploader.client.Uploader;
import org.moxieapps.gwt.uploader.client.events.FileDialogCompleteEvent;
import org.moxieapps.gwt.uploader.client.events.FileDialogCompleteHandler;
import org.moxieapps.gwt.uploader.client.events.FileDialogStartEvent;
import org.moxieapps.gwt.uploader.client.events.FileDialogStartHandler;
import org.moxieapps.gwt.uploader.client.events.FileQueueErrorEvent;
import org.moxieapps.gwt.uploader.client.events.FileQueueErrorHandler;
import org.moxieapps.gwt.uploader.client.events.FileQueuedEvent;
import org.moxieapps.gwt.uploader.client.events.FileQueuedHandler;
import org.moxieapps.gwt.uploader.client.events.UploadCompleteEvent;
import org.moxieapps.gwt.uploader.client.events.UploadCompleteHandler;
import org.moxieapps.gwt.uploader.client.events.UploadErrorEvent;
import org.moxieapps.gwt.uploader.client.events.UploadErrorHandler;
import org.moxieapps.gwt.uploader.client.events.UploadProgressEvent;
import org.moxieapps.gwt.uploader.client.events.UploadProgressHandler;
import org.moxieapps.gwt.uploader.client.events.UploadSuccessEvent;
import org.moxieapps.gwt.uploader.client.events.UploadSuccessHandler;

import bpm.aklabox.workflow.core.IAklaflowConstant;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.panels.ImageCroppingTool;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.upload.FileUploadItem;

import com.google.code.gwt.crop.client.GWTCropper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.ProgressBar;

public class FileUploadInterface extends Composite {

	private static FileUploadInterfaceUiBinder uiBinder = GWT.create(FileUploadInterfaceUiBinder.class);

	interface FileUploadInterfaceUiBinder extends UiBinder<Widget, FileUploadInterface> {
	}

	interface MyStyle extends CssResource {
		String btnUploader();

		String imageCropZoom();
	}

	@UiField
	HTMLPanel uploaderPanel, panel;
	
	@UiField
	FocusPanel dropPanel;
	
	@UiField
	MyStyle style;

	private static boolean hostedMode = !GWT.isScript() && GWT.isClient();

	private GWTCropper crop;
	private ImageCroppingTool imageTool;

	public FileUploadInterface() {
		initWidget(uiBinder.createAndBindUi(this));
		initUploader();
	}

	private void initUploader() {
		final HTMLPanel progressBarPanel = new HTMLPanel("");
		final Map<String, ProgressBar> progressBars = new LinkedHashMap<String, ProgressBar>();
		final Map<String, Image> cancelButtons = new LinkedHashMap<String, Image>();
		final Uploader uploader = new Uploader();
		uploader.setStyleName(style.btnUploader());
		uploader.setUploadURL("*.gupld").setButtonText(LabelsConstants.lblCnst.Upload()).setButtonWidth(133).setButtonHeight(22).setFileSizeLimit("50 MB").setButtonCursor(Uploader.Cursor.HAND).setButtonAction(Uploader.ButtonAction.SELECT_FILES).setFileQueuedHandler(new FileQueuedHandler() {
			public boolean onFileQueued(final FileQueuedEvent fileQueuedEvent) {
				// Create a Progress Bar for this file
				final ProgressBar progressBar = new ProgressBar(0.0, 1.0, 0.0, new CancelProgressBarTextFormatter());
				progressBar.setTitle(fileQueuedEvent.getFile().getName());
				progressBar.setHeight("18px");
				progressBar.setWidth("200px");
				progressBars.put(fileQueuedEvent.getFile().getId(), progressBar);

				return true;
			}
		}).setUploadProgressHandler(new UploadProgressHandler() {
			public boolean onUploadProgress(UploadProgressEvent uploadProgressEvent) {
				ProgressBar progressBar = progressBars.get(uploadProgressEvent.getFile().getId());
				progressBar.setProgress((double) uploadProgressEvent.getBytesComplete() / uploadProgressEvent.getBytesTotal());
				return true;
			}
		}).setUploadCompleteHandler(new UploadCompleteHandler() {
			public boolean onUploadComplete(UploadCompleteEvent uploadCompleteEvent) {
				cancelButtons.get(uploadCompleteEvent.getFile().getId()).removeFromParent();
				uploader.startUpload();
				return true;
			}
		}).setFileDialogStartHandler(new FileDialogStartHandler() {
			public boolean onFileDialogStartEvent(FileDialogStartEvent fileDialogStartEvent) {
				if (uploader.getStats().getUploadsInProgress() <= 0) {
					// Clear the uploads that have completed, if none are in
					// process
					progressBarPanel.clear();
					progressBars.clear();
					cancelButtons.clear();
				}
				return true;
			}
		}).setFileDialogCompleteHandler(new FileDialogCompleteHandler() {
			public boolean onFileDialogComplete(FileDialogCompleteEvent fileDialogCompleteEvent) {
				if (fileDialogCompleteEvent.getTotalFilesInQueue() > 0) {

					if (uploader.getStats().getUploadsInProgress() <= 0) {
						uploader.startUpload();
					}
				}
				return true;
			}
		}).setFileQueueErrorHandler(new FileQueueErrorHandler() {
			public boolean onFileQueueError(FileQueueErrorEvent fileQueueErrorEvent) {
				Window.alert(LabelsConstants.lblCnst.UploadOfFile() + " " + fileQueueErrorEvent.getFile().getName() + " " + LabelsConstants.lblCnst.FailedDueTo() + " [" + fileQueueErrorEvent.getErrorCode().toString() + "]: " + fileQueueErrorEvent.getMessage());
				return true;
			}
		}).setUploadErrorHandler(new UploadErrorHandler() {
			public boolean onUploadError(UploadErrorEvent uploadErrorEvent) {
				cancelButtons.get(uploadErrorEvent.getFile().getId()).removeFromParent();
				Window.alert(LabelsConstants.lblCnst.UploadOfFile() + " " + uploadErrorEvent.getFile().getName() + " " + LabelsConstants.lblCnst.FailedDueTo() + " [" + uploadErrorEvent.getErrorCode().toString() + "]: " + uploadErrorEvent.getMessage());
				return true;
			}
		}).setUploadSuccessHandler(new UploadSuccessHandler() {

			@Override
			public boolean onUploadSuccess(UploadSuccessEvent uploadSuccessEvent) {
				String filePath = uploadSuccessEvent.getServerData();
				addCropPanel(filePath);
				return true;
			}
		});

		if (Uploader.isAjaxUploadWithProgressEventsSupported()) {
			dropPanel.addDragOverHandler(new DragOverHandler() {
				public void onDragOver(DragOverEvent event) {
					if (!uploader.getButtonDisabled()) {
						// DRAGOVER
					}
				}
			});
			dropPanel.addDragLeaveHandler(new DragLeaveHandler() {
				public void onDragLeave(DragLeaveEvent event) {
					// DRAG LEAVE
				}
			});
			dropPanel.addDropHandler(new DropHandler() {
				public void onDrop(DropEvent event) {
					// DRAG DROP
					if (uploader.getStats().getUploadsInProgress() <= 0) {
						progressBarPanel.clear();
						progressBars.clear();
						cancelButtons.clear();
					}

					uploader.addFilesToQueue(Uploader.getDroppedFiles(event.getNativeEvent()));
					event.preventDefault();
				}
			});
			uploaderPanel.add(dropPanel);
		}
		uploaderPanel.add(uploader);
	}

	public void addCropPanel(String url) {
		imageTool.getResource().setBaseImage(url);
		panel.clear();
		crop = new GWTCropper(getRightPath(url));
		crop.addStyleName(style.imageCropZoom());
		crop.setInitialSelection(0, 0, 100, 100);
		imageTool.getCelLFieldPanel().setVisible(true);
		panel.add(crop);
		panel.add(crop);
		imageTool.updateUI();
	}

	protected class CancelProgressBarTextFormatter extends ProgressBar.TextFormatter {
		@Override
		protected String getText(ProgressBar bar, double curProgress) {
			if (curProgress < 0) {
				return LabelsConstants.lblCnst.Cancelled();
			}
			return ((int) (100 * bar.getPercent())) + "%";
		}
	}

	public GWTCropper getCrop() {
		return crop;
	}

	public void setCrop(GWTCropper crop) {
		this.crop = crop;
	}

	public ImageCroppingTool getImageTool() {
		return imageTool;
	}

	public void setImageTool(ImageCroppingTool imageTool) {
		this.imageTool = imageTool;
	}

	@UiHandler("btnScan")
	void onScan(ClickEvent e) {
		AklaCommonService.Connect.getService().scan(new AsyncCallback<String>() {

			@Override
			public void onSuccess(final String result) {
				WaitDialog.showWaitPart(true);
				imageTool.setScannedDoc(result);
				Timer t = new Timer() {

					@Override
					public void run() {
						checkIfScannedIsExist(result, this);
					}
				};
				t.scheduleRepeating(3000);
			}

			private void checkIfScannedIsExist(final String filePath, final Timer t) {
				AklaCommonService.Connect.getService().isScannedImageReady(filePath, new AsyncCallback<Boolean>() {

					@Override
					public void onSuccess(Boolean result) {
						if (result) {
							WaitDialog.showWaitPart(false);
							imageTool.getResource().setBaseImage(filePath);
							addCropPanel(filePath);
							t.cancel();
						}
					}

					@Override
					public void onFailure(Throwable caught) { }
				});
			}

			@Override
			public void onFailure(Throwable caught) {
				new DefaultResultDialog(caught.getMessage(), "failure").show();
			}
		});
	}

	public HTMLPanel getPanel() {
		return panel;
	}

	public void setPanel(HTMLPanel panel) {
		this.panel = panel;
	}

	public static String getRightPath(String path) {
		path = path.replace("\\", "/");
		if (hostedMode) {
			return path;
		}
		else {
			return path.replace("webapps/", "../");
		}

	}

}
