package bpm.gwt.aklabox.commons.client.viewers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.aklabox.workflow.core.model.resources.StandardForm;
import bpm.document.management.core.model.Comments;
import bpm.document.management.core.model.Comments.CommentStatus;
import bpm.document.management.core.model.DocPages;
import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.User;
import bpm.document.management.core.model.Versions;
import bpm.document.management.core.model.XaklFiles;
import bpm.document.management.core.utils.AklaboxConstant;
import bpm.document.management.core.utils.DocumentUtils;
import bpm.document.management.core.utils.TreatmentImageObject;
import bpm.document.management.core.utils.TreatmentImageObject.Type;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.images.CommonImages;
import bpm.gwt.aklabox.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.aklabox.commons.client.utils.CommentBubble;
import bpm.gwt.aklabox.commons.client.utils.DefaultDialog;
import bpm.gwt.aklabox.commons.client.utils.DefaultResultDialog;
import bpm.gwt.aklabox.commons.client.utils.PathHelper;
import bpm.gwt.aklabox.commons.client.utils.WaitDialog;
import bpm.gwt.aklabox.commons.client.viewers.PagesViewer.Orientation;
import bpm.gwt.aklabox.commons.shared.OCRSearchResult;

import com.bramosystems.oss.player.core.client.AbstractMediaPlayer;
import com.bramosystems.oss.player.core.client.PlayerUtil;
import com.bramosystems.oss.player.core.client.Plugin;
import com.bramosystems.oss.player.core.client.PluginNotFoundException;
import com.bramosystems.oss.player.core.client.PluginVersionException;
import com.bramosystems.oss.player.core.client.ui.VLCPlayer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DocumentViewer extends CompositeWaitPanel {

	private static DocumentViewerUiBinder uiBinder = GWT.create(DocumentViewerUiBinder.class);

	interface DocumentViewerUiBinder extends UiBinder<Widget, DocumentViewer> {
	}
	
	interface MyStyle extends CssResource {
		String transparent();
	}
	
	@UiField
	MyStyle style;

	@UiField
	HTMLPanel searchDocContent, panelMessagePreview, contentPanel;

	@UiField
	Image imgPreview, imgCheck, /* btnZoom, */btnEditComment, btnValid, btnGrey, btnRotateCounterClockWise, btnRotateClockWise, imgOriginal, btnReset;

	@UiField
	HTMLPanel previewPanel, preview, commentsBubblePanel, mainImgToolbar, treatImgToolbar, resetPanel, originalPanel;
	
	// @UiField
	// Button btnNext, btnPrev;

	@UiField
	TextBox txtSearchDoc;

	@UiField
	SimplePanel pagePreview, contrastPanel, brightnessPanel;
	
	@UiField
	Label lblMessagePreview;

	public enum TypeViewer {
		AKLABOX_DOCUMENT, AKLAD_VIGNETTE, AKLABOX_XAKL, GROUP_DOCUMENT
	}

	private Documents document;
	private String audioThumb;
	private List<DocPages> docPages;
	private int docPageCounter = 1;
	private User user;
	private boolean isLocked = false;
	private int versionNumber = 0;
	private List<StandardForm> listForms = new ArrayList<StandardForm>();
	private List<OCRSearchResult> searchResults = new ArrayList<OCRSearchResult>();
	private int pageNumber = 1;
	private TypeViewer integration;
	private List<Comments> comments;
	private ParentDocViewer parent;

	private SliderPanel sliderContrast;
	private SliderPanel sliderBright;

	private TreatmentImageObject imageTreatment;
	
	private boolean showSearch = false;
	private boolean pageImageAvailable = true;
	
	private String keyId;
	

	public DocumentViewer(Documents document, User user, ParentDocViewer parent, TypeViewer type) {
		this(document, user, new ArrayList<Comments>(), parent, type, true, false, true, null);
	}

	public DocumentViewer(Documents document, User user, ParentDocViewer parent, TypeViewer type, boolean simpleDisplay, boolean showSearch) {
		this(document, user, new ArrayList<Comments>(), parent, type, simpleDisplay, showSearch, true, null);
	}

	public DocumentViewer(Documents document, User user, ParentDocViewer parent, TypeViewer type, boolean simpleDisplay, boolean showSearch, boolean integratedPager) {
		this(document, user, new ArrayList<Comments>(), parent, type, simpleDisplay, showSearch, integratedPager, null);
	}
	
	public DocumentViewer(Documents document, User user, List<Comments> comments, ParentDocViewer parent, TypeViewer type, boolean simpleDisplay, boolean showSearch, boolean integratedPagers) {
		this(document, user, comments, parent, type, simpleDisplay, showSearch, integratedPagers, null);
	}

	public DocumentViewer(XaklFiles document, ParentDocViewer parent, TypeViewer type) {
		this.parent = parent;
		// TODO
	}
	
	public DocumentViewer(Documents document, User user, ParentDocViewer parent, TypeViewer type, List<DocPages> pages) {
		this(document, user, new ArrayList<Comments>(), parent, type, true, false, true, pages);
	}

	public DocumentViewer(Documents document, User user, List<Comments> comments, ParentDocViewer parent, TypeViewer type, boolean simpleDisplay, boolean showSearch, boolean integratedPager, List<DocPages> pages) {
		initWidget(uiBinder.createAndBindUi(this));
		this.document = document;
		this.user = user;
		this.comments = comments;
		this.parent = parent;
		this.integration = type;
		this.showSearch = showSearch;
		this.docPages = pages;

		if (type.equals(TypeViewer.GROUP_DOCUMENT)) {
			return; // TODO
		}

		keyId = new Date().getTime()+"0";
		imgPreview.getElement().setId("image"+keyId);
		imgPreview.getElement().getStyle().setMarginTop(50, Unit.PX);
		commentsBubblePanel.getElement().setId("coms"+keyId);

		if (document.getTreeItemType().equals(AklaboxConstant.LINKED)) {
			user.setUserType(AklaboxConstant.READER);
		}

		// imageSettings.setVisible(false);
		searchDocContent.setVisible(false);
		// btnZoom.setVisible(false);
		treatImgToolbar.setVisible(false);

		resetPanel.addDomHandler(new MouseOverHandler() {

			@Override
			public void onMouseOver(MouseOverEvent event) {
				btnReset.setVisible(false);
				originalPanel.setVisible(true);
			}
		}, MouseOverEvent.getType());
		resetPanel.addDomHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				btnReset.setVisible(true);
				originalPanel.setVisible(false);
			}
		}, MouseOutEvent.getType());
		originalPanel.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				returnToOriginal();
			}
		}, ClickEvent.getType());
		resetPanel.setVisible(false);
		originalPanel.setVisible(false);

		imgPreview.addLoadHandler(new LoadHandler() {

			@Override
			public void onLoad(LoadEvent event) {
				if (!imgPreview.getUrl().startsWith("data:image")) {
					initJs(DocumentViewer.this, keyId);
				}
			}
		});
		
		loadDoc(document, comments);

		if (simpleDisplay) {
			mainImgToolbar.setVisible(false);
			resetPanel.setVisible(false);
			treatImgToolbar.setVisible(false);

			contentPanel.getElement().getStyle().setTop(0, Unit.PX);
		}

		pagePreview.setVisible(integratedPager/* && !integration.equals(TypeViewer.AKLAD_VIGNETTE)*/);
		
		if (!integratedPager) {
			contentPanel.getElement().getStyle().setBottom(0, Unit.PX);
		}
		
		/*commentsBubblePanel.addDomHandler(new MouseWheelHandler() {
			
			@Override
			public void onMouseWheel(MouseWheelEvent event) {
				imgPreview.fireEvent(event);
			}
		}, MouseWheelEvent.getType());*/
	}
	
	public void loadDoc(Documents document, List<Comments> comments) {
		this.document = document;
		this.comments = comments;

		if (imageTreatment == null) {
			imageTreatment = new TreatmentImageObject();
		}
		else {
			imageTreatment.clear();
		}
		sliderContrast = new SliderPanel(imgPreview, CommonImages.INSTANCE.ic_contrast(), 50, Type.CONTRAST, imageTreatment);
		sliderBright = new SliderPanel(imgPreview, CommonImages.INSTANCE.ic_brightness(), 50, Type.BRIGHTNESS, imageTreatment);
		contrastPanel.setWidget(sliderContrast);
		brightnessPanel.setWidget(sliderBright);
		
		DOM.setElementProperty(imgPreview.getElement(), "style", "");

		if (integration.equals(TypeViewer.AKLABOX_DOCUMENT)) {
			loadVersion(document);
		}
		else if (integration.equals(TypeViewer.AKLAD_VIGNETTE)) {
			loadPage(document.getThumbImage(), false);
			if(docPages != null && docPages.size() > 1){
				pagePreview.setWidget(new PagesViewer(DocumentViewer.this, docPages, Orientation.HORIZONTAL));
			}
			//pagePreview.setVisible(false);
		}
	}

//	public void refreshDoc(Documents newDoc, List<Comments> newComments) {
	//TODO: imgOriginal ?
//		String path = ToolsGWT.getRightPath(document.getThumbImage()), name, extension;
//		name = path.substring(0, path.lastIndexOf("."));
//		extension = path.substring(path.lastIndexOf("."));
//
//		imgOriginal.addErrorHandler(new ErrorHandler() {
//			@Override
//			public void onError(ErrorEvent event) {
//				resetPanel.setVisible(false);
//			}
//		});
//		imgOriginal.addLoadHandler(new LoadHandler() {
//			@Override
//			public void onLoad(LoadEvent event) {
//				resetPanel.setVisible(true);
//			}
//		});
//		imgOriginal.setUrl(name + "OLD" + extension);
//	}

	public void changePage(String status, String pageImagePath, int page) {
		// if (status.equals("office")) {
		// if (parent != null) {
		// ((OpenDocument)
		// parent).previewPage(DocumentManagement.getRightPath(pageImagePath));
		// }
		// else {
		// if (groupDocument != null) {
		// groupDocument.previewPage(DocumentManagement.getRightPath(pageImagePath));
		// }
		// else {
		// xaklDocPreview.previewPage(DocumentManagement.getRightPath(pageImagePath));
		// }
		// }
		// }
		this.docPageCounter = page;
		loadPage(pageImagePath, false);
		//parent.previewPage(DocumentManagement.getRightPath(pageImagePath));
		// ((OpenDocument) parent).updateRightUIData();
	}

	public void loadVersion(final Documents doc) {
		AklaCommonService.Connect.getService().getVersions(doc, new GwtCallbackWrapper<List<Versions>>(this, true, true, true) {
			@Override
			public void onSuccess(List<Versions> result) {
				versionNumber = 0;
				for (Versions v : result) {
					if (!v.isHide()) {
						versionNumber = v.getVersionNumber();
					}
				}

				previewDocInfo(document, versionNumber);
			}
		}.getAsyncCallback());
	}

	public void previewDocInfo(final Documents document, final int versionNumber) {
		final String docType = document.getType();
		if (docType.equals(DocumentUtils.OFFICE) || docType.equals(DocumentUtils.DWG)) {
			if (this.document.isFinished()) {
				AklaCommonService.Connect.getService().getPages(document.getId(), versionNumber, new GwtCallbackWrapper<List<DocPages>>(this, true, true, true) {

					@Override
					public void onSuccess(List<DocPages> result) {
						DocumentViewer.this.docPages = result;
						pagePreview.setWidget(new PagesViewer(DocumentViewer.this, docPages, Orientation.HORIZONTAL));
					}

				}.getAsyncCallback());
			}
			else {
				loadPage(null, true);
			}
		}
		else {
			// btnNext.setVisible(false);
			// btnPrev.setVisible(false);

			Versions v = new Versions();
			v.setDocId(document.getId());
			v.setVersionNumber(versionNumber);

			AklaCommonService.Connect.getService().getSpecificVersion(v, user.getEmail(), false, new GwtCallbackWrapper<Versions>(this, true, true, true) {

				@Override
				public void onSuccess(Versions result) {
					if (docType.equals(DocumentUtils.TEXT)) {
						String f = result.getDocRelPath();
						preview.clear();
						preview.add(new TextViewer(f));
					}
					else if (docType.equals(DocumentUtils.IMAGE)) {
						if (DocumentViewer.this.document.isFinished()) {
							loadPage(result.getDocRelPath(), false);
						}
						else {
							loadPage(null, true);
						}
					}
					else if (docType.equals(DocumentUtils.VIDEO)) {
						String f = PathHelper.getRightPath(result.getDocRelPath());
						SimplePanel panel = new SimplePanel();
						AbstractMediaPlayer player = null;
						preview.clear();
						try {
							player = new VLCPlayer(f, true, "500", "500");
							player.getElement().getStyle().setWidth(500, Unit.PX);
							player.getElement().setAttribute("style", "margin: auto !important;");

							panel.setWidget(player);
						} catch (PluginVersionException e) {
							panel.setWidget(new HTML(".. some nice message telling the user to download plugin first .."));
						} catch (PluginNotFoundException e) {
							panel.setWidget(PlayerUtil.getMissingPluginNotice(Plugin.VLCPlayer));
						}

						preview.add(panel);
						pagePreview.setWidget(new VideoThumbCreator(DocumentViewer.this, document, versionNumber, user));

						AklaCommonService.Connect.getService().getPages(document.getId(), versionNumber, new GwtCallbackWrapper<List<DocPages>>(DocumentViewer.this, true, true, true) {

							@Override
							public void onSuccess(List<DocPages> result) {
								if (result != null && !result.isEmpty()) {
									for (DocPages p : result) {
										preview.add(new VideoThumbItem(p));
									}
								}
							}
						}.getAsyncCallback());
					}
					else if (docType.equals(DocumentUtils.AUDIO)) {
						String f = PathHelper.getRightPath(result.getDocRelPath());
						SimplePanel panel = new SimplePanel();
						AbstractMediaPlayer player = null;
						preview.clear();
						try {
							player = new VLCPlayer(f, true, "500", "500");
							player.getElement().getStyle().setWidth(500, Unit.PX);
							player.getElement().setAttribute("style", "margin: auto !important;");

							panel.setWidget(player);
						} catch (PluginVersionException e) {
							panel.setWidget(new HTML(".. some nice message telling the user to download plugin first .."));
						} catch (PluginNotFoundException e) {
							panel.setWidget(PlayerUtil.getMissingPluginNotice(Plugin.VLCPlayer));
						}
						preview.add(panel);
					}
					else {
						if (DocumentViewer.this.document.isFinished()) {
							loadPage(document.getThumbImage(), false);
						}
						else {
							loadPage(null, true);
						}
					}
				}
			}.getAsyncCallback());
		}
	}
	
	public void loadPage(String pagePath, boolean waitForDocumentToLoad) {
		searchDocContent.setVisible(false);
		
		if (waitForDocumentToLoad) {
			lblMessagePreview.setText(LabelsConstants.lblCnst.ThumbnailsAreGenerating());
			refreshDocumentThumbnail();
		}
		else {
			parent.previewPage(pagePath);
			pagePath = PathHelper.getRightPath(pagePath);
//			pagePath = "https://www.clicfacture.com/wp-content/uploads/2016/05/Exemple-de-facture-eBay.png";
//			pagePath = pagePath.indexOf("C:/BPM/workspaces/workspace_aklabox_60/bpm.document.management.web/war/") >= 0 ? pagePath.split("C:/BPM/workspaces/workspace_aklabox_60/bpm.document.management.web/war/")[1] : pagePath;
			loadPageUrl(pagePath);
			updateComments(comments);
		}
	}
	
	private void refreshDocumentThumbnail() {
		Timer timer = new Timer() {

			@Override
			public void run() {
				AklaCommonService.Connect.getService().getDocInfo(document.getId(), new GwtCallbackWrapper<Documents>(null, false, false) {

					@Override
					public void onSuccess(Documents result) {
						document = result;
						if (result.isFinished()) {
							loadVersion(result);
						}
						else {
							refreshDocumentThumbnail();
						}
					}
				}.getAsyncCallback());
			}
		};
		timer.schedule(3000);
	}

	private void loadPageUrl(final String pageUrl) {
		if (pageUrl != null && !pageUrl.isEmpty()) {
	 		pageImageAvailable = true;
			
			imgCheck.setUrl(pageUrl);
			imgCheck.addErrorHandler(new ErrorHandler() {
				@Override
				public void onError(ErrorEvent event) {
					pageImageAvailable = false;
					imgPreview.setAltText(pageUrl);
					
	//				lblMessagePreview.setText(LabelsConstants.lblCnst.ThumbnailsAreGenerating());
				}
			});
			imgCheck.addLoadHandler(new LoadHandler() {
	
				@Override
				public void onLoad(LoadEvent event) {
					if (pageImageAvailable) {
						searchDocContent.setVisible(showSearch);
						
						panelMessagePreview.removeFromParent();
						imgPreview.addStyleName(style.transparent());
						
						Timer timer = new Timer() {
	
							@Override
							public void run() {
								imgPreview.setUrl(pageUrl);
								//parent.previewPage(pageUrl);
								imgPreview.removeStyleName(style.transparent());
							}
						};
						timer.schedule(200);
					}
					else {
						imgPreview.setAltText(pageUrl);
						lblMessagePreview.setText(LabelsConstants.lblCnst.ThumbnailsNotAvailable());
					}
				}
			});
		}
		else {
			lblMessagePreview.setText(LabelsConstants.lblCnst.ThumbnailsNotAvailable());
		}
	}

	// // TODO Search Highlighting on Documents
	// @UiHandler("imgPreview")
	// void onZoom(ClickEvent e) {
	// /*
	// * if (zoomValue == true) {
	// * previewPanel.getElement().setAttribute("style", "width: 100%;");
	// * preview.setStyleName("previewZoom"); zoomValue = false;
	// * informationPanel.getElement().setAttribute("style", "width: 0%;");
	// * for (Widget w : preview) { if (w instanceof Image) {
	// * w.setWidth("50%"); } } } else {
	// * previewPanel.getElement().removeAttribute("style");
	// * preview.setStyleName("preview"); zoomValue = true;
	// * informationPanel.getElement().setAttribute("style", "width: 60%;");
	// * for (Widget w : preview) { if (w instanceof Image) {
	// * w.setWidth("100%"); } } } return zoomValue;
	// */
	// ZoomImageViewer viewer = null;
	// if (document.getType().equals("office")) {
	// viewer = new ZoomImageViewer(imgPreview.getUrl(), searchResults,
	// getDocPageCounter());
	// }
	// else {
	// viewer = new ZoomImageViewer(imgPreview.getUrl());
	// }
	// previewPanel.add(viewer);
	//
	// }
	//
	// @UiHandler("btnZoom")
	// void onZoomButton(ClickEvent e) {
	// onZoom(e);
	// }

	// @UiHandler("btnNext")
	// void onNextPreview(ClickEvent e) {
	// if (docPageCounter < docPages.size()) {
	// docPageCounter++;
	// imgPreview.setUrl(DocumentManagement.getRightPath(docPages.get(docPageCounter-1).getImagePath()));
	// }
	// }
	//
	// @UiHandler("btnPrev")
	// void onPrevPreview(ClickEvent e) {
	// if (docPageCounter > 1) {
	// docPageCounter--;
	// imgPreview.setUrl(DocumentManagement.getRightPath(docPages.get(docPageCounter-1).getImagePath()));
	// }
	// }

	// TODO OCRSearchResult
	@UiHandler("btnSearchOCRResult")
	void onSearchOCRResult(ClickEvent e) {
		searchOCRResult();
	}

	@UiHandler("txtSearchDoc")
	void onPressEnterSearch(KeyUpEvent e) {
		if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			searchOCRResult();
		}
	}

	private void searchOCRResult() {
		String query = txtSearchDoc.getText().toLowerCase();
		WaitDialog.showWaitPart(true);
		AklaCommonService.Connect.getService().searchDocContent(query, document.getId(), versionNumber, new GwtCallbackWrapper<List<OCRSearchResult>>(this, true, true, true) {

			@Override
			public void onSuccess(List<OCRSearchResult> result) {
				DocumentViewer.this.searchResults = result;
				WaitDialog.showWaitPart(false);
				for (Widget w : pagePreview) {
					if(w instanceof PagesViewer){
						for(PageResult pageResult : ((PagesViewer)w).getPages()){
							//PageResult pageResult = (PageResult) w;
							boolean matched = false;
							for (OCRSearchResult ocr : result) {

								if (ocr.getPageNumber() == pageResult.getPageNumber()) {
									matched = true;
									break;
								}
							}

							if (matched) {
								pageResult.blueStar.setVisible(true);
							}
							else {
								pageResult.blueStar.setVisible(false);
							}
						}
					}
					
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				new DefaultResultDialog(caught.getMessage(), "failure").show();
				WaitDialog.showWaitPart(false);
			}
		}.getAsyncCallback());
	}

	public void loadComments() {
		AklaCommonService.Connect.getService().getComments(document.getId(), CommentStatus.ALL, new GwtCallbackWrapper<List<Comments>>(this, true, true, true) {

			@Override
			public void onSuccess(List<Comments> result) {
				updateComments(result);
			}
		}.getAsyncCallback());
	}

	private final native void initJs(DocumentViewer panel, String keyId) /*-{
		var panel = panel;
		$wnd.wheelzoom($doc.getElementById("image"+keyId), $doc.getElementById("coms"+keyId));
	}-*/;

	@UiHandler("btnEditComment")
	public void onEditComment(ClickEvent c) {
		// if (currentVignette == null) {
		// DefaultResultDialog drd = new
		// DefaultResultDialog(LabelsConstants.lblCnst.PleaseChooseOrUploadAFile(),
		// DefaultResultDialog.TypeMessage.warning.toString());
		// drd.center();
		// }
		if (document == null) {
			return;
		}
		else {
			final CommentField commentfield = new CommentField(document, comments, user, CommentStatus.ALL, docPageCounter);

			DefaultDialog dialog = new DefaultDialog(LabelsConstants.lblCnst.Comment());
			dialog.getContent().add(commentfield);
			dialog.center();
			dialog.addCloseHandler(new CloseHandler<PopupPanel>() {

				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					updateComments(commentfield.getComments());
				}
			});
		}
	}

	public void updateComments(List<Comments> comments) {
		// currentVignette.setComments(comments);

		this.comments = comments;
		parent.onCommentUpdated(comments);
		
		commentsBubblePanel.clear();
		for (Comments com : comments) {
			if(com.getPage() == docPageCounter){
				commentsBubblePanel.add(new CommentBubble(com));
			}
			
		}
	}

	@UiHandler("btnSettings")
	public void onImageSettings(ClickEvent e) {
		mainImgToolbar.setVisible(false);
		treatImgToolbar.setVisible(true);
		// imageSettings.setVisible(false);
	}

	@UiHandler("btnValid")
	public void onValidImageTreatment(ClickEvent e) {
		WaitDialog.showWaitPart(true);
		AklaCommonService.Connect.getService().treatImage(imageTreatment, document, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				WaitDialog.showWaitPart(false);
				mainImgToolbar.setVisible(true);
				treatImgToolbar.setVisible(false);
				// imageSettings.setVisible(true);
				// showVignette(currentVignette);
				parent.onValidateImageTreatment();
			}

			@Override
			public void onFailure(Throwable caught) {
				WaitDialog.showWaitPart(false);
				caught.printStackTrace();
			}
		});
	}

	@UiHandler("btnCancel")
	public void onCancelImageTreatment(ClickEvent e) {
		mainImgToolbar.setVisible(true);
		treatImgToolbar.setVisible(false);
		// imageSettings.setVisible(true);
		imageTreatment.clear();

	}

	@UiHandler("btnGrey")
	public void onGrayScale(ClickEvent e) {
		imageTreatment.addTreatment(Type.GRAYFSCALE, 100); // 100 ou 0 est géré en interne
		imgPreview.getElement().getStyle().setProperty("filter", imageTreatment.renderCssFilterProperty());
		imgPreview.getElement().getStyle().setProperty("WebkitFilter", imageTreatment.renderCssFilterProperty());
	}

	@UiHandler("btnRotateClockWise")
	public void rotateLeft(ClickEvent e) {
		if (imageTreatment.getTreatments().get(Type.ROTATE) == null) {
			imageTreatment.addTreatment(Type.ROTATE, 0);
		}
		else {
			imageTreatment.addTreatment(Type.ROTATE, imageTreatment.getTreatments().get(Type.ROTATE) + 10);
		}
		imgPreview.getElement().getStyle().setProperty("transform", imageTreatment.renderCssTransformProperty());
		imgPreview.getElement().getStyle().setProperty("WebkitTransform", imageTreatment.renderCssTransformProperty());
		// RegExp p = RegExp.compile("(((?:\\d)|(?:-)|(?:\\.)))+");
		// MatchResult m =
		// p.exec(commentsBubblePanel.getElement().getStyle().getMarginTop());
		// int marginTop = Integer.parseInt(m.getGroup(0));
		// String marginTop =
		// commentsBubblePanel.getElement().getStyle().getMarginTop();
		// commentsBubblePanel.getElement().getStyle().setMarginTop(0, Unit.PX);
		// commentsBubblePanel.getElement().getStyle().setProperty("transform",
		// imageTreatment.renderCssTransformProperty());
		// commentsBubblePanel.getElement().getStyle().setProperty("WebkitTransform",
		// imageTreatment.renderCssTransformProperty());
		// commentsBubblePanel.getElement().getStyle().setProperty("marginTop",
		// marginTop);
		commentsBubblePanel.setVisible(false);
	}

	@UiHandler("btnRotateCounterClockWise")
	public void rotateRight(ClickEvent e) {
		if (imageTreatment.getTreatments().get(Type.ROTATE) == null) {
			imageTreatment.addTreatment(Type.ROTATE, 0);
		}
		else {
			imageTreatment.addTreatment(Type.ROTATE, imageTreatment.getTreatments().get(Type.ROTATE) - 10);
		}
		imgPreview.getElement().getStyle().setProperty("transform", imageTreatment.renderCssTransformProperty());
		imgPreview.getElement().getStyle().setProperty("WebkitTransform", imageTreatment.renderCssTransformProperty());
		// commentsBubblePanel.getElement().getStyle().setProperty("transform",
		// imageTreatment.renderCssTransformProperty());
		// commentsBubblePanel.getElement().getStyle().setProperty("WebkitTransform",
		// imageTreatment.renderCssTransformProperty());
		commentsBubblePanel.setVisible(false);
	}

	public void returnToOriginal() {
		WaitDialog.showWaitPart(true);
		AklaCommonService.Connect.getService().returnToOriginal(document, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				WaitDialog.showWaitPart(false);
				mainImgToolbar.setVisible(true);
				treatImgToolbar.setVisible(false);
				// imageSettings.setVisible(true);
				// showVignette(currentVignette);
				parent.onReturnToOriginal();
			}

			@Override
			public void onFailure(Throwable caught) {
				WaitDialog.showWaitPart(false);
				caught.printStackTrace();
			}
		});
	}

	public int getDocPageCounter() {
		return docPageCounter;
	}

	public boolean isInImageTreatment() {
		return treatImgToolbar.isVisible();
	}

	public List<DocPages> getDocPages() {
		return docPages;
	}

}