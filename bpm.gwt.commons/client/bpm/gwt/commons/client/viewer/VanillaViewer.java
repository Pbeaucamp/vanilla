package bpm.gwt.commons.client.viewer;

import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.viewer.dialog.BirtOptionRunDialogBox;
import bpm.gwt.commons.client.viewer.dialog.RunDialog;
import bpm.gwt.commons.client.viewer.dialog.RunKpiDialog;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.repository.PortailItemCube;
import bpm.gwt.commons.shared.repository.PortailItemCubeFmdt;
import bpm.gwt.commons.shared.repository.PortailItemCubeView;
import bpm.gwt.commons.shared.repository.PortailItemReportsGroup;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.viewer.DisplayItem;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations.TypeRun;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.ReportBackground;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class VanillaViewer extends Tab {

	private static VanillaViewerUiBinder uiBinder = GWT.create(VanillaViewerUiBinder.class);

	interface VanillaViewerUiBinder extends UiBinder<Widget, VanillaViewer> {
	}

	interface MyStyle extends CssResource {
		String mainPanel();

		String displayNone();

		String panelViewer();

		String viewerFull();

		String panelTab();

		String tabSmall();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelViewer;

	private Group selectedGroup;
	private List<Group> availableGroups;

	public VanillaViewer(TabManager tabManager, Group selectedGroup, List<Group> availableGroups) {
		super(tabManager, "Empty until load", true);
		this.selectedGroup = selectedGroup;
		this.availableGroups = availableGroups;

		this.add(uiBinder.createAndBindUi(this));

		this.addStyleName(style.mainPanel());
	}

	public void openViewer(IRepositoryObject item, InfoUser infoUser, boolean isDisco, boolean forceOpen) {
		Viewer viewer = createViewer(item, infoUser, isDisco, forceOpen);
		panelViewer.add(viewer);

		setTabHeaderTitle(item.getName());
	}

	public Viewer createViewer(IRepositoryObject item, InfoUser infoUser, boolean isDisco, boolean forceOpen) {
		if (item instanceof PortailRepositoryItem) {
			PortailRepositoryItem portailItem = (PortailRepositoryItem) item;
			if (portailItem instanceof PortailItemCube || portailItem instanceof PortailItemCubeView || portailItem instanceof PortailItemCubeFmdt || portailItem.getType() == IRepositoryApi.FASD_TYPE || portailItem.getType() == IRepositoryApi.FAV_TYPE) {
				FaVanillaViewer viewer = new FaVanillaViewer(this, portailItem, selectedGroup, availableGroups, isDisco);
				return viewer;
			}
			else if (portailItem.isReport()) {
				ReportViewer viewer = new ReportViewer(this, portailItem, selectedGroup, infoUser, isDisco, forceOpen);
				return viewer;
			}
			else if (portailItem instanceof PortailItemReportsGroup) {
				ReportGroupViewer viewer = new ReportGroupViewer(this, portailItem, selectedGroup, infoUser, isDisco);
				return viewer;
			}
			else if (portailItem.getType() == IRepositoryApi.FD_TYPE) {
				FdVanillaViewer viewer = new FdVanillaViewer(this, portailItem, selectedGroup, infoUser, isDisco);
				return viewer;
			}
			else if (portailItem.getType() == IRepositoryApi.GTW_TYPE) {
				GtwVanillaViewer viewer = new GtwVanillaViewer(this, portailItem, selectedGroup, forceOpen);
				return viewer;
			}
			else if (portailItem.getType() == IRepositoryApi.BIW_TYPE) {
				WorkflowVanillaViewer viewer = new WorkflowVanillaViewer(this, portailItem, selectedGroup, forceOpen);
				return viewer;
			}
			else if (portailItem.getType() == IRepositoryApi.GED_ENTRY) {
				GedEntryViewer viewer = new GedEntryViewer(this, portailItem, selectedGroup, availableGroups);
				return viewer;
			}
			else if (portailItem.getType() == IRepositoryApi.EXTERNAL_DOCUMENT) {
				ExtDocViewer viewer = new ExtDocViewer(this, portailItem, selectedGroup, availableGroups);
				return viewer;
			}
			else if (portailItem.getType() == IRepositoryApi.EXTERNAL_DOCUMENT) {
				ExtDocViewer viewer = new ExtDocViewer(this, portailItem, selectedGroup, availableGroups);
				return viewer;
			}
			else if (portailItem.getType() == IRepositoryApi.FMDT_TYPE || portailItem.getType() == IRepositoryApi.FMDT_DRILLER_TYPE || portailItem.getType() == IRepositoryApi.FMDT_CHART_TYPE) {
				FmdtVanillaViewer viewer = new FmdtVanillaViewer(this, portailItem, selectedGroup, availableGroups, infoUser);
				return viewer;
			}
			else if (portailItem.getType() == IRepositoryApi.URL) {
				UrlViewer viewer = new UrlViewer(this, portailItem, selectedGroup, availableGroups);
				return viewer;
			}
			else if (portailItem.getType() == IRepositoryApi.R_MARKDOWN_TYPE) {
				if (portailItem.getItem().getSubtype() == IRepositoryApi.MARKDOWN_SUBTYPE) {
					MarkdownViewer viewer = new MarkdownViewer(this, portailItem, selectedGroup, availableGroups);
					return viewer;
				}
				else {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.NoViewer() + " " + portailItem.getSubType());
				}

			}
			else if (portailItem.getType() == IRepositoryApi.KPI_THEME) {
				KpiThemeViewer viewer = new KpiThemeViewer(this, portailItem, selectedGroup, availableGroups);
				return viewer;
			}
			else if (portailItem.getType() == IRepositoryApi.KPI_MAP) {
				KpiMapViewer viewer = new KpiMapViewer(this, portailItem, selectedGroup, availableGroups);
				return viewer;
			}
			else if (portailItem.getType() == IRepositoryApi.MAP_TYPE) {
				GeojsonMapViewer viewer = new GeojsonMapViewer(this, portailItem, selectedGroup, availableGroups);
				return viewer;
			}
			else if (portailItem.getType() == IRepositoryApi.FORM) {
				FormVanillaViewer viewer = new FormVanillaViewer(this, portailItem, selectedGroup, availableGroups);
				return viewer;
			}
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.NoViewer() + " " + portailItem.getType());
		}
		else if (item instanceof DisplayItem) {
			OtherItemViewer viewer = new OtherItemViewer(this, (DisplayItem) item, selectedGroup, availableGroups);
			return viewer;
		}
		else if (item instanceof ReportBackground) {
			ReportBackgroundItemViewer viewer = new ReportBackgroundItemViewer(this, (ReportBackground) item);
			return viewer;
		}

		MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.NoViewerClass() + " " + item.getClass());

		return null;
	}

	public void launchReport(final Viewer viewer, final PortailRepositoryItem item, Group selectedGroup, final boolean isDisco, final boolean forceOpen) {
		showWaitPart(true);

		ReportingService.Connect.getInstance().getLaunchReportInformations(item, selectedGroup, new AsyncCallback<LaunchReportInformations>() {

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToGetReportInformations());
			}

			@Override
			public void onSuccess(LaunchReportInformations itemInformations) {
				showWaitPart(false);

				if (forceOpen || isOpen()) {
					if (isDisco) {
						itemInformations.setTypeRun(TypeRun.DISCO);
					}

					boolean hasParam = itemInformations.getGroupParameters() != null && !itemInformations.getGroupParameters().isEmpty();

					if (viewer instanceof ReportViewer || (hasParam && (viewer instanceof WorkflowVanillaViewer || viewer instanceof GtwVanillaViewer))) {
						BirtOptionRunDialogBox birtParamWindow = new BirtOptionRunDialogBox(viewer, itemInformations);
						birtParamWindow.center();
					}
					else if (viewer instanceof ReportGroupViewer) {
						RunDialog runDialog = new RunDialog(viewer, itemInformations);
						runDialog.center();
					}
					else if (viewer instanceof KpiThemeViewer) {
						RunKpiDialog dial = new RunKpiDialog((KpiThemeViewer) viewer, itemInformations);
						dial.center();
					}
					else if (viewer instanceof MarkdownViewer && hasParam) {
						RunDialog runDialog = new RunDialog(viewer, itemInformations);
						runDialog.center();
					}
					else {
						viewer.runItem(itemInformations);
					}
				}
			}
		});
	}

	public void postProcess() {
		if (getTabManager() != null) {
			getTabManager().postProcess();
		}
	}

	// public void expandPanel(boolean expand) {
	// if(expand) {
	// panelTab.removeStyleName(style.tabSmall());
	// panelViewer.removeStyleName(style.viewerFull());
	// panelTabContent.removeStyleName(style.displayNone());
	// panelTab.removeStyleName(VanillaCSS.TAB_TOOLBAR);
	//
	// panelViewer.addStyleName(style.panelViewer());
	// }
	// else {
	// panelTab.addStyleName(style.tabSmall());
	// panelViewer.addStyleName(style.viewerFull());
	// panelTabContent.addStyleName(style.displayNone());
	// panelTab.addStyleName(VanillaCSS.TAB_TOOLBAR);
	//
	// panelViewer.removeStyleName(style.panelViewer());
	// }
	// }
	//
	// public void addViewer(ButtonViewer buttonViewer, Viewer viewer) {
	// if(selectedButton != null) {
	// this.selectedButton.select(false);
	// }
	// buttonViewer.select(true);
	//
	// if(this.selectedViewer != null) {
	// this.selectedViewer.addStyleName(style.displayNone());
	// }
	//
	// this.selectedButton = buttonViewer;
	// this.selectedViewer = viewer;
	//
	// panelViewer.add(viewer);
	// }
	//
	// @Override
	// public void switchViewer(ButtonViewer buttonViewer, Viewer viewer) {
	// if(selectedButton != null) {
	// this.selectedButton.select(false);
	// }
	// buttonViewer.select(true);
	//
	// if(selectedViewer != null) {
	// this.selectedViewer.addStyleName(style.displayNone());
	// }
	//
	// this.selectedButton = buttonViewer;
	// this.selectedViewer = viewer;
	//
	// this.selectedViewer.removeStyleName(style.displayNone());
	// }
	//
	// @Override
	// public void closeViewer(ButtonViewer button, IClose close, Widget viewer)
	// {
	// panelTabContent.remove(button);
	// panelViewer.remove(viewer);
	// }
	//
	// @UiHandler("btnExpand")
	// public void onExpandClick(ClickEvent event) {
	// expandPanel(!isExpand);
	// this.isExpand = !isExpand;
	//
	// if(isExpand) {
	// btnExpand.setResource(CommonImages.INSTANCE.ic_btn_sidebar_right_expand());
	// }
	// else {
	// btnExpand.setResource(CommonImages.INSTANCE.ic_btn_sidebar_right_collapse());
	// }
	// }

}
