package bpm.fd.web.client.panels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.fd.core.Dashboard;
import bpm.fd.core.DashboardComponent;
import bpm.fd.core.DashboardPage;
import bpm.fd.core.component.ComponentType;
import bpm.fd.core.component.EventType;
import bpm.fd.web.client.I18N.Labels;
import bpm.fd.web.client.actions.ActionManager;
import bpm.fd.web.client.actions.WidgetAction;
import bpm.fd.web.client.dialogs.DashboardOptionsDialog;
import bpm.fd.web.client.handlers.HasComponentSelectionHandler;
import bpm.fd.web.client.handlers.IComponentSelectionHandler;
import bpm.fd.web.client.images.DashboardImage;
import bpm.fd.web.client.popup.SaveTypePopup;
import bpm.fd.web.client.services.DashboardService;
import bpm.fd.web.client.utils.PageManager;
import bpm.fd.web.client.utils.ToolHelper;
import bpm.fd.web.client.utils.WidgetAlignHelper;
import bpm.fd.web.client.utils.WidgetAlignHelper.AlignmentType;
import bpm.fd.web.client.utils.WidgetAlignHelper.ResizeType;
import bpm.fd.web.client.utils.WidgetDropHelper;
import bpm.fd.web.client.utils.WidgetManager;
import bpm.fd.web.client.widgets.ContainerWidget;
import bpm.fd.web.client.widgets.DashboardWidget;
import bpm.fd.web.client.widgets.WidgetComposite;
import bpm.fd.web.client.wizard.TemplatePropertiesWizard;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.RepositorySaveDialog;
import bpm.gwt.commons.client.dialog.RepositorySaveDialog.SaveHandler;
import bpm.gwt.commons.client.dialog.SaveAsTemplateDialog;
import bpm.gwt.commons.client.dialog.SaveAsTemplateDialog.TemplateHandler;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.panels.AbstractTabHeader;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.repository.SaveItemInformations;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.VanillaImage;
import bpm.vanilla.platform.core.repository.IDashboard;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.Template;
import bpm.vanilla.platform.core.repository.Template.TypeTemplate;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class WorkspacePanel extends Composite implements TabManager, DragOverHandler, DragLeaveHandler, DropHandler, IDropPanel, HasComponentSelectionHandler, SaveHandler, TemplateHandler {

	private static final int HEADER_HIDDEN = 0;
	private static final int CONTENT_TAB_HIDDEN = 25;
	private static final int CONTENT_PANEL_HIDDEN = 55;

	private static final int HEADER_MIDDLE = 120;
	private static final int CONTENT_TAB_MIDDLE = 160;
	private static final int CONTENT_PANEL_MIDDLE = 190;

	private static final int HEADER_BIG = 400;
	private static final int CONTENT_TAB_BIG = 440;
	private static final int CONTENT_PANEL_BIG = 470;

	private enum HeaderPosition {
		HIDDEN, MIDDLE, BIG
	}

	private static WorkspacePanelUiBinder uiBinder = GWT.create(WorkspacePanelUiBinder.class);

	interface WorkspacePanelUiBinder extends UiBinder<Widget, WorkspacePanel> {
	}

	interface MyStyle extends CssResource {
		String focus();

		String displayNone();

		String dragOver();

		String grid();
	}

	@UiField
	MyStyle style;

	@UiField
	FocusPanel headerFocusPanel, btnHeaderLeft, btnHeaderRight;

	@UiField
	HTMLPanel mainPanel, designPanel, previewPanel, contentPanel, toolbarPanel, headerContentPanel;

	@UiField
	FlowPanel tabHeaderPanel;

	@UiField
	SimplePanel panelBtnHeader;

	@UiField
	Frame previewFrame;

	@UiField
	Label btnDesigner, btnPreview, lblHeaderLeft, lblHeaderRight;

	@UiField
	Image imgHeaderLeft, imgHeaderRight;

	private CreationPanel parent;
	private RightPanel rightPanel;
	private ActionManager actionManager;
	private WidgetManager widgetManager;
	private PageManager pageManager;

	private AbstractTabHeader selectedBtn;
	private Tab selectedPage;

	private List<AbstractTabHeader> openTabs = new ArrayList<AbstractTabHeader>();
	private List<IComponentSelectionHandler> componentHandlers = new ArrayList<IComponentSelectionHandler>();

	private boolean displayGrid = false;
	private boolean isPreview = false;
	private HeaderPosition headerPosition = HeaderPosition.MIDDLE;

	private List<WidgetComposite> items;

	private Dashboard dashboard;

	public WorkspacePanel(CreationPanel parent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.actionManager = new ActionManager();
		this.widgetManager = new WidgetManager();
		this.pageManager = new PageManager();

		toolbarPanel.addStyleName(VanillaCSS.TAB_TOOLBAR);

		this.rightPanel = new RightPanel(parent);
		mainPanel.add(rightPanel);
		addComponentSelectionHandler(rightPanel);

		headerFocusPanel.addDragOverHandler(this);
		headerFocusPanel.addDragLeaveHandler(this);
		headerFocusPanel.addDropHandler(this);

		updateUi(headerPosition);
	}

	public void loadDashboard(Dashboard dashboard) {
		this.dashboard = dashboard;

		rightPanel.getCssPanel().setCss(dashboard.getCss());
		rightPanel.getEventsPanel().loadDashboardEvents(dashboard.getEventScript());

		List<DashboardComponent> globalComponents = dashboard.getComponents();
		if (globalComponents != null) {
			for (DashboardComponent component : globalComponents) {
				ComponentType typeTool = component.getType();
				if (typeTool == null) {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), component.getClass() + " " + Labels.lblCnst.IsNotSupported());
					continue;
				}

				WidgetComposite widget = buildWidget(parent, this, actionManager, widgetManager, typeTool, component);
				addWidget(widget, component.getLeft(), component.getTop());
			}
		}

		List<DashboardPage> pages = dashboard.getPages();
		if (pages != null) {
			for (DashboardPage page : pages) {
				PagePanel pagePanel = createPage(page.getName(), page.getLabel());

				if (page.getComponents() != null) {
					for (DashboardComponent component : page.getComponents()) {
						ComponentType typeTool = component.getType();
						if (typeTool == null) {
							MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), component.getClass() + " " + Labels.lblCnst.IsNotSupported());
							continue;
						}

						WidgetComposite widget = buildWidget(parent, this, actionManager, widgetManager, typeTool, component);
						pagePanel.addWidget(widget, component.getLeft(), component.getTop());
					}
				}
			}
		}
	}

	public void loadTemplate(Template<IDashboard> template) {
		this.dashboard = (Dashboard) template.getItem();

		rightPanel.getCssPanel().setCss(dashboard.getCss());
		rightPanel.getEventsPanel().loadDashboardEvents(dashboard.getEventScript());

		List<DashboardWidget> widgets = new ArrayList<>();

		List<DashboardComponent> globalComponents = dashboard.getComponents();
		if (globalComponents != null) {
			for (DashboardComponent component : globalComponents) {
				ComponentType typeTool = component.getType();
				if (typeTool == null) {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), component.getClass() + " " + Labels.lblCnst.IsNotSupported());
					continue;
				}

				WidgetComposite widget = buildWidget(parent, this, actionManager, widgetManager, typeTool, component);
				addWidget(widget, component.getLeft(), component.getTop());

				if (widget instanceof DashboardWidget) {
					widgets.add((DashboardWidget) widget);
				}
			}
		}

		List<DashboardPage> pages = dashboard.getPages();
		if (pages != null) {
			for (DashboardPage page : pages) {
				PagePanel pagePanel = createPage(null, page.getLabel());

				if (page.getComponents() != null) {
					for (DashboardComponent component : page.getComponents()) {
						ComponentType typeTool = component.getType();
						if (typeTool == null) {
							MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), component.getClass() + " " + Labels.lblCnst.IsNotSupported());
							continue;
						}

						WidgetComposite widget = buildWidget(parent, this, actionManager, widgetManager, typeTool, component);
						pagePanel.addWidget(widget, component.getLeft(), component.getTop());

						if (widget instanceof DashboardWidget) {
							widgets.add((DashboardWidget) widget);
						}
					}
				}
			}
		}
		else {
			createPage(null, "Page1");
		}

		if (widgets != null && !widgets.isEmpty()) {
			TemplatePropertiesWizard propertiesWizard = new TemplatePropertiesWizard(widgets);
			propertiesWizard.center();
		}
	}

	private WidgetComposite buildWidget(IWait waitPanel, HasComponentSelectionHandler componentSelectionHandler, ActionManager actionManager, WidgetManager widgetManager, ComponentType typeTool, DashboardComponent component) {
		if (ToolHelper.isContentWidget(typeTool)) {
			return new ContainerWidget(waitPanel, componentSelectionHandler, actionManager, widgetManager, component);
		}
		else {
			return new DashboardWidget(this, waitPanel, componentSelectionHandler, widgetManager, component);
		}
	}

	public Dashboard buildDashboard(boolean isTemplate) {
		String name = parent.getDashboardName();
		String description = parent.getDashboardDescription();

		String css = rightPanel.getCssPanel().getCss();
		HashMap<EventType, String> dashboardScripts = rightPanel.getEventsPanel().getDashboardScripts();

		List<DashboardComponent> components = new ArrayList<>();
		if (items != null) {
			for (WidgetComposite widget : items) {
				DashboardComponent component = widget.getComponent();
				if (isTemplate) {
					component.clear();
				}
				components.add(component);
			}
		}

		List<DashboardPage> pages = new ArrayList<>();
		if (openTabs != null) {
			for (AbstractTabHeader tabHeader : openTabs) {
				PagePanel page = (PagePanel) tabHeader.getTab();
				pages.add(page.buildPage(isTemplate));
			}
		}

		if (dashboard == null) {
			dashboard = new Dashboard();
		}
		dashboard.setName(name);
		dashboard.setDescription(description);
		dashboard.setCss(css);
		dashboard.setComponents(components);
		dashboard.setPages(pages);
		dashboard.setEventScript(dashboardScripts);
		return dashboard;
	}

	public Template<IDashboard> buildTemplate(String name, VanillaImage img) {
		Dashboard dashboard = buildDashboard(true);

		Template<IDashboard> template = new Template<>(name, TypeTemplate.DASHBOARD);
		template.setCreatorId(parent.getInfoUser().getUser().getId());
		template.setDateCreation(new Date());
		template.setImage(img);
		template.setItem(dashboard);
		return template;
	}

	public PagePanel createPage(String pageName, String title) {
		PagePanel creationPanel = new PagePanel(parent, this, actionManager, widgetManager, pageManager, pageName, title);
		changePage(creationPanel, true);
		return creationPanel;
	}

	@UiHandler("btnSave")
	public void onSave(ClickEvent event) {
		onSave(event, true, false);
	}

	public void onSave(ClickEvent event, boolean proposeSaveAs, boolean close) {
		if (dashboardIsSaved()) {
			if (proposeSaveAs) {
				SaveTypePopup popup = new SaveTypePopup(this, close);
				if (event != null) {
					popup.setPopupPosition(event.getClientX(), event.getClientY());
					popup.show();
				}
				else {
					popup.center();
				}
			}
			else {
				saveDashboard(getItemInfos(), true, close);
			}
		}
		else {
			saveDashboard(close);
		}
	}

	public boolean dashboardIsSaved() {
		return dashboard != null && dashboard.getItemId() != null;
	}

	public void saveDashboard(boolean close) {
		String name = parent.getDashboardName();
		String description = parent.getDashboardDescription();

		List<Group> availableGroups = parent.getInfoUser().getAvailableGroups();

		RepositorySaveDialog<Dashboard> dial = new RepositorySaveDialog<Dashboard>(this, null, IRepositoryApi.FD_TYPE, name, description, availableGroups, close);
		dial.center();
	}

	@Override
	public void saveItem(SaveItemInformations itemInfos, boolean close) {
		saveDashboard(itemInfos, false, close);
	}

	public void saveDashboard(SaveItemInformations itemInfos, final boolean update, final boolean close) {
		parent.showWaitPart(true);

		parent.refreshDashboardOptions(itemInfos.getName(), itemInfos.getDescription());

		Dashboard dashboard = buildDashboard(false);
		DashboardService.Connect.getInstance().save(itemInfos, dashboard, update, new GwtCallbackWrapper<Integer>(parent, true) {

			@Override
			public void onSuccess(Integer result) {
				if (update) {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), Labels.lblCnst.DashboardUpdatedSuccess());
				}
				else {
					if (result != null) {
						WorkspacePanel.this.dashboard.setItemId(result);
					}
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), Labels.lblCnst.DashboardSavedSuccess());
				}
				setModified(false);
				if (close && parent.getTabManager() != null) {
					((DashboardsPanel) parent.getTabManager()).close(parent.getTabHeader());
				}
			}
		}.getAsyncCallback());
	}

	@UiHandler("btnSaveAsTemplate")
	public void onSaveAsTemplate(ClickEvent event) {
		SaveAsTemplateDialog dial = new SaveAsTemplateDialog(this);
		dial.center();
	}

	@Override
	public void saveTemplate(String name, VanillaImage img) {
		parent.showWaitPart(true);

		Template<IDashboard> template = buildTemplate(name, img);
		DashboardService.Connect.getInstance().addTemplate(template, new GwtCallbackWrapper<Void>(parent, true) {

			@Override
			public void onSuccess(Void result) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.TemplateSavedSuccess());
			}
		}.getAsyncCallback());
	}

	@UiHandler("btnAddPage")
	public void onAddPage(ClickEvent event) {
		createPage(null, Labels.lblCnst.MyPage() + "_" + openTabs.size());
	}

	@UiHandler("btnUndo")
	public void onUndo(ClickEvent event) {
		actionManager.undoAction();
	}

	@UiHandler("btnRedo")
	public void onRedo(ClickEvent event) {
		actionManager.redoAction();
	}

	@UiHandler("btnPreferences")
	public void onOptions(ClickEvent event) {
		DashboardOptionsDialog dial = new DashboardOptionsDialog(parent);
		dial.center();
	}

	@UiHandler("btnGrid")
	public void onGrid(ClickEvent event) {
		this.displayGrid = !displayGrid;

		if (displayGrid) {
			headerContentPanel.addStyleName(style.grid());
		}
		else {
			headerContentPanel.removeStyleName(style.grid());
		}
		if (openTabs != null) {
			for (AbstractTabHeader tabHeader : openTabs) {
				PagePanel page = (PagePanel) tabHeader.getTab();
				page.displayGrid(displayGrid, style.grid());
			}
		}
	}

	@UiHandler("btnAlignTop")
	public void onAlignTop(ClickEvent event) {
		WidgetAlignHelper.align(widgetManager.getSelectedWidgets(), AlignmentType.TOP);
	}

	@UiHandler("btnAlignBottom")
	public void onAlignBottom(ClickEvent event) {
		WidgetAlignHelper.align(widgetManager.getSelectedWidgets(), AlignmentType.BOTTOM);
	}

	@UiHandler("btnAlignLeft")
	public void onAlignLeft(ClickEvent event) {
		WidgetAlignHelper.align(widgetManager.getSelectedWidgets(), AlignmentType.LEFT);
	}

	@UiHandler("btnAlignRight")
	public void onAlignRight(ClickEvent event) {
		WidgetAlignHelper.align(widgetManager.getSelectedWidgets(), AlignmentType.RIGHT);
	}

	@UiHandler("btnResizeWidth")
	public void onResizeWidth(ClickEvent event) {
		WidgetAlignHelper.resize(widgetManager.getSelectedWidgets(), ResizeType.WIDTH);
	}

	@UiHandler("btnResizeHeight")
	public void onResizeHeight(ClickEvent event) {
		WidgetAlignHelper.resize(widgetManager.getSelectedWidgets(), ResizeType.HEIGHT);
	}

	@UiHandler("btnHeaderLeft")
	public void onHeaderLeftClick(ClickEvent event) {
		if (headerPosition == HeaderPosition.HIDDEN) {
			headerPosition = HeaderPosition.MIDDLE;
		}
		else if (headerPosition == HeaderPosition.MIDDLE) {
			headerPosition = HeaderPosition.HIDDEN;
		}
		else if (headerPosition == HeaderPosition.BIG) {
			headerPosition = HeaderPosition.HIDDEN;
		}

		updateUi(headerPosition);
	}

	@UiHandler("btnHeaderRight")
	public void onHeaderRightClick(ClickEvent event) {
		if (headerPosition == HeaderPosition.HIDDEN) {
			headerPosition = HeaderPosition.BIG;
		}
		else if (headerPosition == HeaderPosition.MIDDLE) {
			headerPosition = HeaderPosition.BIG;
		}
		else if (headerPosition == HeaderPosition.BIG) {
			headerPosition = HeaderPosition.MIDDLE;
		}

		updateUi(headerPosition);
	}

	private void updateUi(HeaderPosition position) {
		String labelLeft = "";
		String labelRight = "";

		ImageResource imgLeft = null;
		ImageResource imgRight = null;

		if (headerPosition == HeaderPosition.HIDDEN) {
			labelLeft = Labels.lblCnst.ShowHeader();
			labelRight = Labels.lblCnst.ExpandHeader();

			imgLeft = DashboardImage.INSTANCE.arrow_down_24();
			imgRight = DashboardImage.INSTANCE.arrow_double_down_24();
		}
		else if (headerPosition == HeaderPosition.MIDDLE) {
			labelLeft = Labels.lblCnst.HideHeader();
			labelRight = Labels.lblCnst.ExpandHeader();

			imgLeft = DashboardImage.INSTANCE.arrow_up_24();
			imgRight = DashboardImage.INSTANCE.arrow_down_24();
		}
		else if (headerPosition == HeaderPosition.BIG) {
			labelLeft = Labels.lblCnst.HideHeader();
			labelRight = Labels.lblCnst.ReduceHeader();

			imgLeft = DashboardImage.INSTANCE.arrow_double_up_24();
			imgRight = DashboardImage.INSTANCE.arrow_up_24();
		}

		headerFocusPanel.getElement().getStyle().setHeight(headerPosition == HeaderPosition.HIDDEN ? HEADER_HIDDEN : headerPosition == HeaderPosition.MIDDLE ? HEADER_MIDDLE : HEADER_BIG, Unit.PX);
		panelBtnHeader.getElement().getStyle().setTop(headerPosition == HeaderPosition.HIDDEN ? HEADER_HIDDEN : headerPosition == HeaderPosition.MIDDLE ? HEADER_MIDDLE : HEADER_BIG, Unit.PX);

		tabHeaderPanel.getElement().getStyle().setTop(headerPosition == HeaderPosition.HIDDEN ? CONTENT_TAB_HIDDEN : headerPosition == HeaderPosition.MIDDLE ? CONTENT_TAB_MIDDLE : CONTENT_TAB_BIG, Unit.PX);
		contentPanel.getElement().getStyle().setTop(headerPosition == HeaderPosition.HIDDEN ? CONTENT_PANEL_HIDDEN : headerPosition == HeaderPosition.MIDDLE ? CONTENT_PANEL_MIDDLE : CONTENT_PANEL_BIG, Unit.PX);

		lblHeaderLeft.setText(labelLeft);
		lblHeaderRight.setText(labelRight);
		imgHeaderLeft.setResource(imgLeft);
		imgHeaderRight.setResource(imgRight);
	}

	@Override
	public void addWidget(WidgetComposite widget, int positionLeft, int positionTop) {
		widget.setDropPanel(this);
		headerContentPanel.add(widget);

		widget.getElement().getStyle().setTop(positionTop, Unit.PX);
		widget.getElement().getStyle().setLeft(positionLeft, Unit.PX);

		if (items == null) {
			items = new ArrayList<WidgetComposite>();
		}
		items.add(widget);

		widget.updatePosition(positionLeft, positionTop);
		setModified(true);
	}

	@Override
	public void removeWidget(WidgetComposite widget, boolean addAction) {
		if (addAction) {
			WidgetAction action = new WidgetAction(widgetManager, this, widget, widget.getLeft(), widget.getTop());
			actionManager.launchAction(action, true);
		}
		else {
			widget.removeFromParent();
			items.remove(widget);
		}
		setModified(true);
	}

	@UiHandler("btnDesigner")
	public void onDesigner(ClickEvent event) {
		if (isPreview) {
			switchView(isPreview);
		}
	}

	@UiHandler("btnPreview")
	public void onPreview(ClickEvent event) {
		if (!isPreview) {
			switchView(isPreview);
		}
	}

	private void switchView(boolean preview) {
		this.isPreview = !preview;

		designPanel.setVisible(!isPreview);
		previewPanel.setVisible(isPreview);
		rightPanel.setVisible(!isPreview);

		if (isPreview) {
			btnDesigner.removeStyleName(style.focus());
			btnPreview.addStyleName(style.focus());

			loadPreview();
		}
		else {
			btnPreview.removeStyleName(style.focus());
			btnDesigner.addStyleName(style.focus());
		}
	}

	private void loadPreview() {
		parent.showWaitPart(true);

		Dashboard dashboard = buildDashboard(false);
		DashboardService.Connect.getInstance().preview(dashboard, new GwtCallbackWrapper<String>(parent, true) {

			@Override
			public void onSuccess(String result) {
				if (result != null && !result.isEmpty()) {
					parent.showWaitPart(true);
					previewFrame.setUrl(result);
				}
				else {
					switchView(false);
				}
			}
		}.getAsyncCallback());
	}

	@UiHandler("previewFrame")
	public void onLoad(LoadEvent event) {
		parent.showWaitPart(false);
	}

	private void changePage(Tab selectedPanel, boolean select) {
		if (select && selectedBtn != null) {
			this.selectedBtn.setSelected(false);
		}

		if (select && this.selectedPage != null) {
			this.selectedPage.addStyleName(style.displayNone());
		}

		AbstractTabHeader header = selectedPanel.buildTabHeader();
		if (!header.isOpen()) {
			tabHeaderPanel.add(header);
			openTabs.add(header);
		}

		if (select) {
			this.selectedBtn = header;
			this.selectedBtn.setSelected(true);

			this.selectedPage = selectedPanel;
			this.selectedPage.removeStyleName(style.displayNone());

			if (!selectedPanel.isOpen()) {
				header.setOpen(true);
				this.contentPanel.add(selectedPanel);
			}

			selectedPanel.doActionAfterSelection();
		}
	}

	@Override
	public void closeTab(AbstractTabHeader tabHeader) {
		int index = openTabs.indexOf(tabHeader);
		openTabs.remove(index);
		tabHeaderPanel.remove(tabHeader);
		contentPanel.remove(tabHeader.getTab());

		if (selectedBtn == tabHeader && !openTabs.isEmpty()) {
			if (index > 0) {
				changePage(openTabs.get(index - 1).getTab(), true);
			}
			else {
				changePage(openTabs.get(0).getTab(), true);
			}
		}
	}

	@Override
	public void selectTab(AbstractTabHeader tabHeader) {
		changePage(tabHeader.getTab(), true);
	}

	@Override
	public void openViewer(IRepositoryObject item) {
	}

	@Override
	public int getIndex(AbstractTabHeader tabHeader) {
		return openTabs != null ? openTabs.indexOf(tabHeader) : -1;
	}

	@Override
	public void updatePosition(String tabId, int index) {
		PageHeader header = pageManager.getWidget(tabId);
		int currentIndex = openTabs.indexOf(header);
		Collections.swap(openTabs, currentIndex, index);

		header.removeFromParent();
		tabHeaderPanel.insert(header, index);
	}

	@Override
	public void onDrop(DropEvent event) {
		event.preventDefault();
		headerContentPanel.removeStyleName(style.dragOver());

		WidgetDropHelper.dropWidget(event, headerContentPanel, this, this, actionManager, widgetManager, parent, this, true, false);
	}

	@Override
	public void onDragLeave(DragLeaveEvent event) {
		headerContentPanel.removeStyleName(style.dragOver());
	}

	@Override
	public void onDragOver(DragOverEvent event) {
		String data = event.getData(ToolHelper.TYPE_TOOL);
		if (data != null && !data.isEmpty()) {
			Integer typeToolIndex = Integer.parseInt(data);
			ComponentType typeTool = ComponentType.valueOf(typeToolIndex);

			if (!isAllowedInHeader(typeTool)) {
				return;
			}

			headerContentPanel.addStyleName(style.dragOver());
		}

		String generatedId = event.getData(DashboardWidget.WIDGET_ID);
		if (generatedId != null && !generatedId.isEmpty()) {
			WidgetComposite widget = widgetManager.getWidget(generatedId);
			ComponentType typeTool = widget.getComponent().getType();

			if (!isAllowedInHeader(typeTool)) {
				return;
			}

			headerContentPanel.addStyleName(style.dragOver());
		}
	}

	private boolean isAllowedInHeader(ComponentType typeTool) {
		if (typeTool != ComponentType.IMAGE && typeTool != ComponentType.LABEL && typeTool != ComponentType.BUTTON && typeTool != ComponentType.FILTER) {
			return false;
		}
		return true;
	}

	@Override
	public void addComponentSelectionHandler(IComponentSelectionHandler handler) {
		componentHandlers.add(handler);
	}

	@Override
	public void removeComponentSelectionHandler(IComponentSelectionHandler handler) {
		componentHandlers.remove(handler);
	}

	@Override
	public List<IComponentSelectionHandler> getComponentSelectionHandlers() {
		return componentHandlers;
	}

	public CreationPanel getCreationPanel() {
		return parent;
	}

	public SaveItemInformations getItemInfos() {
		return new SaveItemInformations(dashboard.getName(), dashboard.getDescription(), null, 0, null, null);
	}

	@Override
	public void setModified(boolean isModified) {
		parent.setModified(isModified);
	}

	@Override
	public ActionManager getActionManager() {
		return actionManager;
	}

	@Override
	public void postProcess() {
		// TODO Auto-generated method stub
		
	}
}
