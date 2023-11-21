package bpm.gwt.commons.client.viewer;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.IChangeGroup;
import bpm.gwt.commons.client.custom.IChangeReport;
import bpm.gwt.commons.client.custom.ImageButton;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.loading.HtmlWaitPanel;
import bpm.gwt.commons.client.popup.IShare;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.InfoShare;
import bpm.gwt.commons.shared.InfoShareCmis;
import bpm.gwt.commons.shared.InfoShareMail;
import bpm.gwt.commons.shared.utils.ExportResult;
import bpm.gwt.commons.shared.utils.TypeShare;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class Viewer extends HtmlWaitPanel implements IChangeReport, IChangeGroup, IShare {

	private static ViewerUiBinder uiBinder = GWT.create(ViewerUiBinder.class);

	interface ViewerUiBinder extends UiBinder<Widget, Viewer> {
	}

	interface MyStyle extends CssResource {
		String mainPanel();

		String frame();

		String btnToolbar();

		String focus();

		String reportPanel();
		
		String lblToolbar();

	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel toolbarPanel, reportPanel;

	@UiField
	SimplePanel paramPanel, commentPanel;

	@UiField
	Frame reportFrame;

	@UiField
	Image btnRun, btnSave, btnExport, btnPrint, btnHistorize, btnOpenDashInNewTab, btnBurst, btnShare, btnComment, btnAdd, btnDelete, btnUpdate, btnPreview, btnDesigner, btnGraphe, btnCube, btnReport, btnCubefaweb, btnAnalysis;

	@UiField
	Label lblReloadReport;

	@UiField(provided = true)
	ImageButton btnReports;

	@UiField(provided = true)
	ImageButton btnGroups;

	@UiField(provided = true)
	ImageButton btnFormats;

	protected int tab = DESIGNER;
	protected final static int DESIGNER = 1;
	protected final static int PREVIEW = 2;
	protected final static int GRAPHE = 3;
	protected final static int CUBE = 4;
	protected final static int ANALYSIS = 5;

	private VanillaViewer viewer;
	
	public @UiConstructor
	Viewer(VanillaViewer vanillaViewer) {
		this.viewer = vanillaViewer;
		btnReports = new ImageButton(CommonImages.INSTANCE.arrow_down());
		btnGroups = new ImageButton(CommonImages.INSTANCE.arrow_down());
		btnFormats = new ImageButton(CommonImages.INSTANCE.arrow_down());
		add(uiBinder.createAndBindUi(this));

		reportFrame.addStyleName(style.frame());

		toolbarPanel.addStyleName(VanillaCSS.TAB_TOOLBAR);
		this.addStyleName(style.mainPanel());

		btnRun.setVisible(false);
		btnSave.setVisible(false);
		btnExport.setVisible(false);
		btnPrint.setVisible(false);
		btnHistorize.setVisible(false);
		btnOpenDashInNewTab.setVisible(false);
		btnBurst.setVisible(false);
		btnShare.setVisible(false);
		btnComment.setVisible(false);
		btnReports.setVisible(false);
		btnGroups.setVisible(false);
		btnFormats.setVisible(false);

		btnDesigner.addStyleName(style.focus());

		btnDesigner.setVisible(false);
		btnPreview.setVisible(false);
		btnGraphe.setVisible(false);
		btnCube.setVisible(false);
		btnAnalysis.setVisible(false);

		btnAdd.setVisible(false);
		btnDelete.setVisible(false);

		btnUpdate.setVisible(false);
		btnReport.setVisible(false);
		btnCubefaweb.setVisible(false);

		btnAnalysis.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				btnAnalysis.setResource(CommonImages.INSTANCE.ic_analysis_black());
			}
		});
		btnAnalysis.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				if (tab != ANALYSIS)
					btnAnalysis.setResource(CommonImages.INSTANCE.ic_analysis_gray());
			}
		});

		btnGraphe.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				btnGraphe.setResource(CommonImages.INSTANCE.metadataexplorer_chart());
			}
		});
		btnGraphe.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				if (tab != GRAPHE)
					btnGraphe.setResource(CommonImages.INSTANCE.metadataexplorer_chart_clair());
			}
		});

		btnCube.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				btnCube.setResource(CommonImages.INSTANCE.metadataexplorer_cube());
			}
		});
		btnCube.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				if (tab != CUBE)
					btnCube.setResource(CommonImages.INSTANCE.metadataexplorer_cube_clair());
			}
		});

		btnPreview.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				btnPreview.setResource(CommonImages.INSTANCE.metadataexplorer_results());
			}
		});
		btnPreview.addMouseOutHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				if (tab != PREVIEW)
					btnPreview.setResource(CommonImages.INSTANCE.metadataexplorer_results_clair());
			}
		});
		btnDesigner.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				btnDesigner.setResource(CommonImages.INSTANCE.metadataexplorer_conception());
			}
		});
		btnDesigner.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				if (tab != DESIGNER)
					btnDesigner.setResource(CommonImages.INSTANCE.metadataexplorer_conception_clair());
			}
		});

	}
	
	public VanillaViewer getViewer() {
		return viewer;
	}

	public void addButtonToolbar(ImageResource resource, String title, ClickHandler clickHandler) {
		Image imgToolbar = new Image(resource);
		imgToolbar.setTitle(title);
		imgToolbar.addClickHandler(clickHandler);
		imgToolbar.addStyleName(style.btnToolbar());

		toolbarPanel.add(imgToolbar);
	}

	public void addMessageToolbar(String message) {
		Label lblToolbar = new Label(message);
		lblToolbar.addStyleName(style.lblToolbar());

		toolbarPanel.add(lblToolbar);
	}

	/**
	 * Override if you want to support run in the viewer
	 * 
	 * @param event
	 */
	@UiHandler("btnRun")
	public void onRunClick(ClickEvent event) {
		// Do nothing
	}

	/**
	 * Override if you want to support run in the viewer
	 * 
	 * @param event
	 */
	@UiHandler("btnSave")
	public void onSaveClick(ClickEvent event) {
		// Do nothing
	}

	/**
	 * Override if you want to support export in the viewer
	 * 
	 * @param event
	 */
	@UiHandler("btnExport")
	public void onExportClick(ClickEvent event) {
		// Do nothing
	}

	/**
	 * Override if you want to support run in the viewer
	 * 
	 * @param event
	 */
	@UiHandler("btnReport")
	public void onReportClick(ClickEvent event) {
		// Do nothing
	}

	/**
	 * Override if you want to support print in the viewer
	 * 
	 * @param event
	 */
	@UiHandler("btnPrint")
	public void onPrintClick(ClickEvent event) {
		// Do nothing
	}

	/**
	 * Override if you want to support historize in the viewer
	 * 
	 * @param event
	 */
	@UiHandler("btnHistorize")
	public void onHistorizeClick(ClickEvent event) {
		// Do nothing
	}

	/**
	 * Override if you want to support the open in a new tab
	 * 
	 * @param event
	 */
	@UiHandler("btnOpenDashInNewTab")
	public void onOpenDashInNewTabClick(ClickEvent event) {
		// Do nothing
	}

	/**
	 * Override if you want to support share in the viewer
	 * 
	 * @param event
	 */
	@UiHandler("btnShare")
	public void onShareClick(ClickEvent event) {
		// Do nothing
	}

	/**
	 * Override if you want to support comment in the viewer
	 * 
	 * @param event
	 */
	@UiHandler("btnComment")
	public void onCommentClick(ClickEvent event) {
		// Do nothing
	}

	/**
	 * Override if you want to support report change in the viewer
	 * 
	 * @param event
	 */
	@UiHandler("btnReports")
	public void onReportChange(ClickEvent event) {
		// Do nothing
	}

	/**
	 * Override if you want to support group change in the viewer
	 * 
	 * @param event
	 */
	@UiHandler("btnGroups")
	public void onGroupChange(ClickEvent event) {
		// Do nothing
	}

	/**
	 * Override if you want to support format change in the viewer
	 * 
	 * @param event
	 */
	@UiHandler("btnFormats")
	public void onFormatChange(ClickEvent event) {
		// Do nothing
	}

	/**
	 * Override if you want to support burst in the viewer
	 * 
	 * @param event
	 */
	@UiHandler("btnBurst")
	public void onBurstClick(ClickEvent event) {
		// Do nothing
	}

	@UiHandler("btnDesigner")
	public void onDesigner(ClickEvent event) {

	}

	@UiHandler("btnPreview")
	public void onPreview(ClickEvent event) {
	}

	@UiHandler("btnGraphe")
	public void onGraphe(ClickEvent event) {
	}

	@UiHandler("btnCube")
	public void onCube(ClickEvent event) {
	}

	@UiHandler("btnAnalysis")
	public void onAnalysis(ClickEvent event) {
	}

	@UiHandler("btnAdd")
	public void onAddClick(ClickEvent event) {
		// Do nothing
	}

	@UiHandler("btnDelete")
	public void onDeleteClick(ClickEvent event) {
		// Do nothing
	}

	@UiHandler("btnUpdate")
	public void onUpdateClick(ClickEvent event) {
		update();
	}

	@UiHandler("btnCubefaweb")
	public void onCubefawebClick(ClickEvent event) {
	}

	protected void switchView(int newtab) {
	}

	protected void update() {

	}

	/**
	 * Override if you want to support report change in the viewer
	 * 
	 * @param composite
	 * 
	 * @param event
	 */
	@Override
	public void changeReport(LaunchReportInformations itemInfo) {
		// Do nothing
	}

	/**
	 * Override if you want to support group change in the viewer
	 * 
	 * @param event
	 */
	@Override
	public void changeGroup(Group selectedGroup) {
		// Do nothing
	}

	/**
	 * Override if you want to support share in the viewer
	 * 
	 * @param event
	 */
	@Override
	public void openShare(TypeShare typeShare) {
		// Do nothing
	}

	@Override
	public void share(InfoShare infoShare) {
		if (infoShare instanceof InfoShareMail) {
			showWaitPart(true);

			InfoShareMail infoShareReport = (InfoShareMail) infoShare;

			ReportingService.Connect.getInstance().sendEmail(infoShareReport, new GwtCallbackWrapper<ExportResult>(this, true) {

				@Override
				public void onSuccess(ExportResult result) {
					MessageHelper.openMessageMailResult(LabelsConstants.lblCnst.Information(), result);
				}
				
			}.getAsyncCallback());
		}
		else if (infoShare instanceof InfoShareCmis) {
			ReportingService.Connect.getInstance().shareCmis((InfoShareCmis) infoShare, new GwtCallbackWrapper<ExportResult>(this, true, true) {

				@Override
				public void onSuccess(ExportResult result) {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.SuccessDocumentCreation());
				}
				
			}.getAsyncCallback());
		}
	}

	/**
	 * Override if you want to set item info in the viewer
	 * 
	 * @param event
	 */
	public void setItemInfo(LaunchReportInformations itemInfo) {
		// Do nothing
	}

	public abstract void runItem(LaunchReportInformations itemInformations);

	public abstract void defineToolbar(LaunchReportInformations itemInfo);
}
