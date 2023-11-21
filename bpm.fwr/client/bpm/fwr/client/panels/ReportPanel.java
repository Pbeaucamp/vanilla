package bpm.fwr.client.panels;

import java.util.LinkedList;
import java.util.List;

import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.DataSource;
import bpm.fwr.api.beans.dataset.DynamicPrompt;
import bpm.fwr.api.beans.dataset.FWRFilter;
import bpm.fwr.api.beans.dataset.FwrPrompt;
import bpm.fwr.api.beans.dataset.FwrRelationStrategy;
import bpm.fwr.api.beans.dataset.GroupPrompt;
import bpm.fwr.api.beans.dataset.IResource;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.WysiwygPanel;
import bpm.fwr.client.action.Action;
import bpm.fwr.client.action.ActionAddResource;
import bpm.fwr.client.action.ActionTrashResource;
import bpm.fwr.client.action.ActionType;
import bpm.fwr.client.dialogs.CreatePromptDialog;
import bpm.fwr.client.draggable.HasBin;
import bpm.fwr.client.draggable.dropcontrollers.FilterPromptDropController;
import bpm.fwr.client.draggable.widgets.DraggableResourceHTML;
import bpm.fwr.client.images.WysiwygImage;
import bpm.fwr.client.utils.SizeComponentConstants;
import bpm.fwr.client.utils.WidgetType;
import bpm.fwr.client.widgets.ReportWidget;
import bpm.gwt.commons.shared.InfoUser;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ReportPanel extends Composite implements HasBin {
	private static final int SIZE_FILTER_PROMPT_PANEL = 155;

	private static final String CSS_REPORT = "report";
	private static final String CSS_IMG_FILTER_PROMPT = "imgFilterPrompt";
	private static final String CSS_LABEL_FILTER_PROMPT = "lblFilterPrompt";
	private static final String CSS_RESOURCE = "resourceHtml";

	public enum DropTargetType {
		FILTER, PROMPT, RELATION
	}

	private static ReportPanelUiBinder uiBinder = GWT.create(ReportPanelUiBinder.class);

	interface ReportPanelUiBinder extends UiBinder<Widget, ReportPanel> {
	}

	@UiField
	HTMLPanel rootPanel, filterPromptPanel;

	@UiField
	FocusPanel panelClickPromptFilter;

	@UiField
	SimplePanel reportSheetPanel;

	@UiField
	Image imgPromptFilter;

	@UiField
	Label lblPromptFilter;

	@UiField
	ListBox listComponents;

	@UiField
	AbsolutePanel filterDropPanel, promptDropPanel, relationDropPanel;
	
	@UiField
	TextBox txtHeight, txtWidth;

	private PickupDragController resourceDragController, dragController, dataDragController;
	private FilterPromptDropController filterDropController, promptDropController, relationDropController, dynamicPromptDropController;

	private ReportSheet reportSheet;
	private WysiwygPanel panelparent;

	private boolean isOpenFilter = false;
	private boolean hasComponent = false;
	private boolean hasFilter = false;
	private boolean hasPrompt = false;
	private boolean hasRelation = false;

	private Label labelFilter, labelPrompt, labelRelation;

	private LinkedList<Action> actionUndo = new LinkedList<Action>();
	private LinkedList<Action> actionRedo = new LinkedList<Action>();

	public ReportPanel(WysiwygPanel panelParent, PickupDragController reportWidgDC, PickupDragController paletteDragController, PickupDragController dragController, 
			PickupDragController dataDragController, PickupDragController groupDragController, PickupDragController detailDragController, 
			PickupDragController resourceDragController, PickupDragController thirdBinDragController) {
		initWidget(uiBinder.createAndBindUi(this));
		this.setPanelparent(panelParent);

		this.resourceDragController = resourceDragController;
		this.dragController = dragController;
		this.dataDragController = dataDragController;

		panelClickPromptFilter.addClickHandler(filterPromptClickHandler);

		imgPromptFilter.setResource(WysiwygImage.INSTANCE.filter());
		imgPromptFilter.addStyleName(CSS_IMG_FILTER_PROMPT);

		lblPromptFilter.setText(Bpm_fwr.LBLW.ShowFilterPromptChoice());

		filterPromptPanel.setWidth(SizeComponentConstants.WIDTH_REPORT + "px");
		filterPromptPanel.setHeight((SIZE_FILTER_PROMPT_PANEL + 1) + "px");
		DOM.setStyleAttribute(filterPromptPanel.getElement(), "marginTop", -((SIZE_FILTER_PROMPT_PANEL + 1) + 1) + "px");

		reportSheet = new ReportSheet(reportWidgDC, dataDragController, groupDragController, detailDragController, thirdBinDragController, paletteDragController, this);
		reportSheet.addStyleName(CSS_REPORT);
		reportSheetPanel.add(reportSheet);

		// Filter and Prompt part
		labelFilter = new Label(Bpm_fwr.LBLW.DropFilter());
		labelFilter.addStyleName(CSS_LABEL_FILTER_PROMPT);
		filterDropPanel.add(labelFilter);
		filterDropPanel.setWidth(SizeComponentConstants.WIDTH_REPORT - 30 + "px");
		filterDropPanel.setTitle(Bpm_fwr.LBLW.DropFilter());

		labelPrompt = new Label(Bpm_fwr.LBLW.DropPrompt());
		labelPrompt.addStyleName(CSS_LABEL_FILTER_PROMPT);
		promptDropPanel.add(labelPrompt);
		promptDropPanel.setWidth(SizeComponentConstants.WIDTH_REPORT - 30 + "px");
		promptDropPanel.setTitle(Bpm_fwr.LBLW.DropPrompt());

		labelRelation = new Label(Bpm_fwr.LBLW.DropRelation());
		labelRelation.addStyleName(CSS_LABEL_FILTER_PROMPT);
		relationDropPanel.add(labelRelation);
		relationDropPanel.setWidth(SizeComponentConstants.WIDTH_REPORT - 30 + "px");
		relationDropPanel.setTitle(Bpm_fwr.LBLW.DropPrompt());

		filterDropController = new FilterPromptDropController(this, filterDropPanel, DropTargetType.FILTER);
		promptDropController = new FilterPromptDropController(this, promptDropPanel, DropTargetType.PROMPT);
		relationDropController = new FilterPromptDropController(this, relationDropPanel, DropTargetType.RELATION);
		dynamicPromptDropController = new FilterPromptDropController(this, promptDropPanel, DropTargetType.PROMPT);

		resourceDragController.registerDropController(filterDropController);
		resourceDragController.registerDropController(promptDropController);
		resourceDragController.registerDropController(relationDropController);
		dataDragController.registerDropController(dynamicPromptDropController);

		refreshListBoxComponents();
		listComponents.addChangeHandler(changeHandler);

		txtWidth.setText(SizeComponentConstants.WIDTH_REPORT + "");
		txtHeight.setText(SizeComponentConstants.HEIGHT_REPORT + "");
	}
	
	@UiHandler("btnApplySize")
	public void onHeightApply(ClickEvent event) {
		int height = SizeComponentConstants.HEIGHT_REPORT;
		try {
			height = Integer.parseInt(txtHeight.getText());
		} catch(Exception e) {
			txtHeight.setText(SizeComponentConstants.HEIGHT_REPORT + "");
		}
		
		int width = SizeComponentConstants.WIDTH_REPORT;
		try {
			width = Integer.parseInt(txtWidth.getText());
		} catch(Exception e) {
			txtWidth.setText(SizeComponentConstants.WIDTH_REPORT + "");
		}
		
		reportSheet.setSize(width, height);
	}

	@Override
	protected void onDetach() {
		resourceDragController.unregisterDropController(filterDropController);
		resourceDragController.unregisterDropController(promptDropController);
		resourceDragController.unregisterDropController(relationDropController);
		dataDragController.unregisterDropController(dynamicPromptDropController);
		super.onDetach();
	}

	public void refreshListBoxComponents() {
		listComponents.clear();

		List<ReportWidget> reportComponents = reportSheet.getReportComponentsForFilterPrompt();
		if (reportComponents != null && !reportComponents.isEmpty()) {
			int listIndex = 1;
			int chartIndex = 1;
			int crosstabIndex = 1;
			for (ReportWidget widg : reportComponents) {
				String widgetName = "";
				if (widg.getType() == WidgetType.CHART) {
					widgetName = Bpm_fwr.LBLW.Chart() + " " + chartIndex;
					chartIndex++;
				}
				else if (widg.getType() == WidgetType.CROSSTAB) {
					widgetName = Bpm_fwr.LBLW.CrossTab() + " " + crosstabIndex;
					crosstabIndex++;
				}
				else if (widg.getType() == WidgetType.LIST) {
					widgetName = Bpm_fwr.LBLW.List() + " " + listIndex;
					listIndex++;
				}
				else if (widg.getType() == WidgetType.VANILLA_MAP) {
					widgetName = Bpm_fwr.LBLW.VanillaMap() + " " + listIndex;
					listIndex++;
				}
				listComponents.addItem(widgetName);
			}

			refreshFilterPromptPart(reportComponents.get(0));

			hasComponent = true;
		}
		else {
			listComponents.addItem(Bpm_fwr.LBLW.NoComponentInReport());
			hasComponent = false;
		}
	}

	private void refreshFilterPromptPart(ReportWidget widg) {
		filterDropPanel.clear();
		promptDropPanel.clear();
		relationDropPanel.clear();

		if (widg.hasFilter()) {
			int i = 0;
			for (FWRFilter filter : widg.getFilters()) {
				DraggableResourceHTML htmlResource = new DraggableResourceHTML(filter.getName(), filter, this, DropTargetType.FILTER, i);
				htmlResource.addStyleName(CSS_RESOURCE);
				filterDropPanel.add(htmlResource);
				dragController.makeDraggable(htmlResource);

				i++;
			}

			hasFilter = true;
		}
		else {
			filterDropPanel.add(labelFilter);
		}

		if (widg.hasPrompt()) {
			int j = 0;
			for (IResource prompt : widg.getPrompts()) {
				DraggableResourceHTML htmlResource = new DraggableResourceHTML(prompt.getName(), prompt, this, DropTargetType.PROMPT, j);
				htmlResource.addStyleName(CSS_RESOURCE);
				promptDropPanel.add(htmlResource);
				dragController.makeDraggable(htmlResource);

				j++;
			}

			hasPrompt = true;
		}
		else {
			promptDropPanel.add(labelPrompt);
		}

		if (widg.hasRelations()) {
			int j = 0;
			for (FwrRelationStrategy relation : widg.getRelations()) {
				DraggableResourceHTML htmlResource = new DraggableResourceHTML(relation.getName(), relation, this, DropTargetType.RELATION, j);
				htmlResource.addStyleName(CSS_RESOURCE);
				relationDropPanel.add(htmlResource);
				dragController.makeDraggable(htmlResource);

				j++;
			}

			hasRelation = true;
		}
		else {
			relationDropPanel.add(labelRelation);
		}
	}

	private ChangeHandler changeHandler = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			if (hasComponent) {
				refreshFilterPromptPart(reportSheet.getReportComponentsForFilterPrompt().get(listComponents.getSelectedIndex()));
			}
		}
	};

	private ClickHandler filterPromptClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			Animation slideAnimation = new Animation() {

				@Override
				protected void onUpdate(double progress) {
					if (!isOpenFilter) {
						refreshListBoxComponents();
						int progressValue = (int) (-((SIZE_FILTER_PROMPT_PANEL + 1) + 1) * (1 - progress));
						DOM.setStyleAttribute(filterPromptPanel.getElement(), "marginTop", progressValue + "px");
					}
					else {
						int progressValue = (int) (-((SIZE_FILTER_PROMPT_PANEL + 1) + 1) * (progress));
						DOM.setStyleAttribute(filterPromptPanel.getElement(), "marginTop", progressValue + "px");
					}
				}

				protected void onComplete() {
					isOpenFilter = !isOpenFilter;
					if (isOpenFilter) {
						lblPromptFilter.setText(Bpm_fwr.LBLW.HideFilterPromptChoice());
					}
					else {
						lblPromptFilter.setText(Bpm_fwr.LBLW.ShowFilterPromptChoice());
					}
				};
			};
			slideAnimation.run(1000);
		}
	};

	private ReportWidget getWidgetSelectedFromListBox() {
		return reportSheet.getReportComponentsForFilterPrompt().get(listComponents.getSelectedIndex());
	}

	public void manageWidget(Column column, DropTargetType type, ActionType actionType) {
		if (hasComponent) {
			if (type == DropTargetType.PROMPT) {
				IResource resource = createDynamicResource(column);

				CreatePromptDialog dial = new CreatePromptDialog(this, resource, type, actionType, true);
				dial.center();
			}
		}
	}
	
	private IResource createDynamicResource(Column column) {

		InfoUser infoUser = Bpm_fwr.getInstance().getInfoUser();

		// Datasource creation
		DataSource dataS = new DataSource();
		dataS.setBusinessModel(column.getBusinessModelParent());
		dataS.setBusinessPackage(column.getBusinessPackageParent());
		dataS.setConnectionName("Default");
		dataS.setGroup(infoUser.getGroup().getName());
		dataS.setItemId(column.getMetadataId());
		dataS.setRepositoryId(infoUser.getRepository().getId());
		dataS.setName("datasource_" + System.currentTimeMillis());
		dataS.setPassword(infoUser.getUser().getPassword());
		dataS.setUrl(infoUser.getRepository().getUrl());
		dataS.setUser(infoUser.getUser().getName());
		dataS.setOnOlap(false);

		DataSet dataset = new DataSet();
		dataset.setLanguage("fr");
		dataset.setDatasource(dataS);
		dataset.setName("dataset_" + System.currentTimeMillis());
		dataset.addColumn(column);

		DynamicPrompt resource = new DynamicPrompt();
		resource.setName("Dyn_Prt_" + new Object().hashCode());
		resource.setMetadataId(column.getMetadataId());
		resource.setModelParent(column.getBusinessModelParent());
		resource.setPackageParent(column.getBusinessPackageParent());
		resource.setOriginDataStreamName(column.getName());
		resource.setColumn(column);
		resource.setDataset(dataset);
		return resource;
	}

	public void manageWidget(IResource resource, DropTargetType type, ActionType actionType, boolean definePrompt) {
		if (hasComponent) {
			if (type == DropTargetType.FILTER && resource instanceof FWRFilter) {
				ReportWidget widgetSelected = getWidgetSelectedFromListBox();
				int indexFilter = widgetSelected.getFilters().size();
				widgetSelected.addFilter((FWRFilter) resource);

				if (!hasFilter) {
					filterDropPanel.clear();
				}

				DraggableResourceHTML htmlResource = new DraggableResourceHTML(resource.getName(), resource, this, type, indexFilter);
				htmlResource.addStyleName(CSS_RESOURCE);
				filterDropPanel.add(htmlResource);
				dragController.makeDraggable(htmlResource);

				hasFilter = true;

				if (actionType == ActionType.DEFAULT) {
					ActionAddResource action = new ActionAddResource(ActionType.ADD_FILTER, this, htmlResource, widgetSelected);
					addActionToUndoAndClearRedo(action);
				}
				else if (actionType == ActionType.REDO) {
					ActionAddResource action = new ActionAddResource(ActionType.ADD_FILTER, this, htmlResource, widgetSelected);
					replaceLastActionToUndo(action);
				}
				else if (actionType == ActionType.UNDO) {
					ActionTrashResource action = new ActionTrashResource(ActionType.TRASH_FILTER, this, htmlResource, widgetSelected);
					replaceFirstActionToRedo(action);
				}
			}
			else if (type == DropTargetType.PROMPT && (resource instanceof FwrPrompt || resource instanceof GroupPrompt)) {
				if(definePrompt && !(resource instanceof GroupPrompt)) {
					CreatePromptDialog dial = new CreatePromptDialog(this, resource, type, actionType, false);
					dial.center();
				}
				else {
					ReportWidget widgetSelected = getWidgetSelectedFromListBox();
					int indexPrompt = widgetSelected.getPrompts().size();
					widgetSelected.addPromptResource(resource);
	
					if (!hasPrompt) {
						promptDropPanel.clear();
					}
	
					DraggableResourceHTML htmlResource = new DraggableResourceHTML(resource.getName(), resource, this, type, indexPrompt);
					htmlResource.addStyleName(CSS_RESOURCE);
					promptDropPanel.add(htmlResource);
					dragController.makeDraggable(htmlResource);
	
					hasPrompt = true;
	
					if (actionType == ActionType.DEFAULT) {
						ActionAddResource action = new ActionAddResource(ActionType.ADD_PROMPT, this, htmlResource, widgetSelected);
						addActionToUndoAndClearRedo(action);
					}
					else if (actionType == ActionType.REDO) {
						ActionAddResource action = new ActionAddResource(ActionType.ADD_PROMPT, this, htmlResource, widgetSelected);
						replaceLastActionToUndo(action);
					}
					else if (actionType == ActionType.UNDO) {
						ActionTrashResource action = new ActionTrashResource(ActionType.TRASH_PROMPT, this, htmlResource, widgetSelected);
						replaceFirstActionToRedo(action);
					}
				}
			}
			else if (type == DropTargetType.RELATION && resource instanceof FwrRelationStrategy) {
				ReportWidget widgetSelected = getWidgetSelectedFromListBox();
				int indexRelation = widgetSelected.getRelations().size();
				widgetSelected.addRelation((FwrRelationStrategy)resource);

				if (!hasRelation) {
					relationDropPanel.clear();
				}

				DraggableResourceHTML htmlResource = new DraggableResourceHTML(resource.getName(), resource, this, type, indexRelation);
				htmlResource.addStyleName(CSS_RESOURCE);
				relationDropPanel.add(htmlResource);
				dragController.makeDraggable(htmlResource);

				hasRelation = true;

				if (actionType == ActionType.DEFAULT) {
					ActionAddResource action = new ActionAddResource(ActionType.ADD_RELATION, this, htmlResource, widgetSelected);
					addActionToUndoAndClearRedo(action);
				}
				else if (actionType == ActionType.REDO) {
					ActionAddResource action = new ActionAddResource(ActionType.ADD_RELATION, this, htmlResource, widgetSelected);
					replaceLastActionToUndo(action);
				}
				else if (actionType == ActionType.UNDO) {
					ActionTrashResource action = new ActionTrashResource(ActionType.TRASH_RELATION, this, htmlResource, widgetSelected);
					replaceFirstActionToRedo(action);
				}
			}
		}
	}

	@Override
	public void widgetToTrash(Object widget) {
		ReportWidget widgetSelected = getWidgetSelectedFromListBox();
		DraggableResourceHTML resource = (DraggableResourceHTML) widget;
		removeWidget(widgetSelected, resource, false, ActionType.DEFAULT, false);
	}

	public void removeWidget(ReportWidget widgetSelected, DraggableResourceHTML resource, boolean removeFromParent, ActionType actionType, boolean deleteFromPanel) {

		ActionType typeForTrash = null;
		ActionType typeForAdd = null;

		if (resource.getResource() instanceof FWRFilter) {
			typeForTrash = ActionType.TRASH_FILTER;
			typeForAdd = ActionType.ADD_FILTER;

			int indexOf = widgetSelected.indexOfResource((FWRFilter) resource.getResource());

			widgetSelected.removeFilter((FWRFilter) resource.getResource());

			filterDropPanel.remove(indexOf);

			if (!widgetSelected.hasFilter()) {
				hasFilter = false;

				filterDropPanel.add(labelFilter);
			}
		}
		else if (resource.getResource() instanceof FwrPrompt || resource.getResource() instanceof GroupPrompt) {
			typeForTrash = ActionType.TRASH_PROMPT;
			typeForAdd = ActionType.ADD_PROMPT;

			int indexOf = widgetSelected.indexOfResource(resource.getResource());

			widgetSelected.removePromptResource(resource.getResource());

			promptDropPanel.remove(indexOf);

			if (!widgetSelected.hasPrompt()) {
				hasPrompt = false;

				promptDropPanel.add(labelPrompt);
			}
		}
		else if (resource.getResource() instanceof FwrRelationStrategy) {
			typeForTrash = ActionType.TRASH_RELATION;
			typeForAdd = ActionType.ADD_RELATION;

			int indexOf = widgetSelected.indexOfResource(resource.getResource());

			widgetSelected.removeRelation((FwrRelationStrategy)resource.getResource());

			relationDropPanel.remove(indexOf);

			if (!widgetSelected.hasRelations()) {
				hasRelation = false;

				relationDropPanel.add(labelRelation);
			}
		}

		if (actionType == ActionType.DEFAULT) {
			ActionTrashResource action = new ActionTrashResource(typeForTrash, this, resource, widgetSelected);
			addActionToUndoAndClearRedo(action);
		}
		else if (actionType == ActionType.REDO) {
			ActionTrashResource action = new ActionTrashResource(typeForTrash, this, resource, widgetSelected);
			replaceLastActionToUndo(action);
		}
		else if (actionType == ActionType.UNDO) {
			ActionAddResource action = new ActionAddResource(typeForAdd, this, resource, widgetSelected);
			replaceFirstActionToRedo(action);
		}
	}

	public ReportSheet getReportSheet() {
		return reportSheet;
	}

	public void setPanelparent(WysiwygPanel panelparent) {
		this.panelparent = panelparent;
	}

	public WysiwygPanel getPanelparent() {
		return panelparent;
	}

	public HTMLPanel getReportSheetPanel() {
		return rootPanel;
	}

	/*
	 * Undo and redo gestion Part
	 */
	public void addActionToUndoAndClearRedo(Action action) {
		actionRedo.clear();

		addActionToUndo(action);
	}

	private void addActionToUndo(Action action) {
		int actionUndoSize = actionUndo.size();
		if (actionUndoSize < 10) {
			actionUndo.addLast(action);
		}
		else {
			actionUndo.removeFirst();
			actionUndo.addLast(action);
		}
	}

	private void addActionToRedo(Action action) {
		int actionRedoSize = actionRedo.size();
		if (actionRedoSize < 10) {
			actionRedo.addFirst(action);
		}
		else {
			actionRedo.addFirst(action);
			actionRedo.removeLast();
		}
	}

	public void replaceLastActionToUndo(Action action) {
		actionUndo.addLast(action);
	}

	public void replaceFirstActionToRedo(Action action) {
		actionRedo.removeFirst();
		actionRedo.addFirst(action);
	}

	public void undo() {
		if (actionUndo.size() > 0) {
			Action action = actionUndo.getLast();
			actionUndo.removeLast();
			addActionToRedo(action);

			action.executeUndo();
		}
	}

	public void redo() {
		if (actionRedo.size() > 0) {
			Action action = actionRedo.getFirst();
			actionRedo.removeFirst();

			action.executeRedo();
		}
	}
}
