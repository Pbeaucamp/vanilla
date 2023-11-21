package bpm.faweb.client.projection.dialog;

import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.client.projection.ProjectionMeasure;
import bpm.faweb.client.projection.ProjectionMeasureCondition;
import bpm.faweb.client.projection.panel.ProjectionConditionPanel;
import bpm.faweb.client.projection.panel.ProjectionFormulaPanel;
import bpm.faweb.client.projection.panel.ProjectionMeasureFormulaPanel;
import bpm.faweb.client.tree.DimensionTreePanel;
import bpm.faweb.shared.infoscube.ItemMes;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.loading.GreyAbsolutePanel;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.loading.WaitAbsolutePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ProjectionMeasureFormulaDialog extends AbstractDialogBox implements IWait {

	private static ProjectionMeasureFormulaDialogUiBinder uiBinder = GWT.create(ProjectionMeasureFormulaDialogUiBinder.class);

	interface ProjectionMeasureFormulaDialogUiBinder extends UiBinder<Widget, ProjectionMeasureFormulaDialog> {
	}

	@UiField
	HTMLPanel contentPanel;

	@UiField
	HTMLPanel formulaPanel, treePanel, filterPanel;

	private MainPanel mainPanel;

	private WaitAbsolutePanel waitPanel;
	private GreyAbsolutePanel greyPanel;
	private boolean isCharging = false;

	private Image imgAdd;
	private ItemMes measure;

	private ProjectionMeasureFormulaPanel projectionMeasureFormulaPanel;

	public ProjectionMeasureFormulaDialog(MainPanel mainPanel, ItemMes measure, ProjectionMeasureFormulaPanel projectionMeasureFormulaPanel) {
		super(FreeAnalysisWeb.LBL.ProjectionMeasureFormula() + " " + measure.getName(), true, true);
		this.mainPanel = mainPanel;
		this.projectionMeasureFormulaPanel = projectionMeasureFormulaPanel;
		this.measure = measure;

		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		treePanel.add(new DimensionTreePanel(mainPanel, this, null));

		formulaPanel.add(new ProjectionFormulaPanel(measure));
		createConditionsPanels(projectionMeasureFormulaPanel.getProjectionMeasure());
	}

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			ProjectionMeasure proj = createProjectionMeasure();

			boolean isValid = true;
			if (!testProjectionValidity(proj)) {
				Window.alert(FreeAnalysisWeb.LBL.ProjectionBadCondtions());
				isValid = false;
			}

			if (isValid) {
				projectionMeasureFormulaPanel.setProjectionMeasure(proj);

				for (int i = 0; i < filterPanel.getWidgetCount(); i++) {
					if (filterPanel.getWidget(i) instanceof ProjectionConditionPanel) {
						delete((ProjectionConditionPanel) filterPanel.getWidget(i));
					}
				}

				ProjectionMeasureFormulaDialog.this.hide();
			}
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			for (int i = 0; i < filterPanel.getWidgetCount(); i++) {
				if (filterPanel.getWidget(i) instanceof ProjectionConditionPanel) {
					delete((ProjectionConditionPanel) filterPanel.getWidget(i));
				}
			}
			ProjectionMeasureFormulaDialog.this.hide();
		}
	};

	private ClickHandler handler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (event.getSource().equals(imgAdd)) {
				ProjectionConditionPanel pan = new ProjectionConditionPanel(mainPanel, ProjectionMeasureFormulaDialog.this, false);
				filterPanel.add(pan);
			}
		}
	};

	private void createConditionsPanels(ProjectionMeasure projectionMeasure) {

		HTMLPanel addPanel = new HTMLPanel("");
		imgAdd = new Image(CommonImages.INSTANCE.add_24());
		Label lblAdd = new Label(FreeAnalysisWeb.LBL.ProjectionAddCondition());
		imgAdd.getElement().getStyle().setFloat(Float.LEFT);
		imgAdd.getElement().getStyle().setMarginLeft(10, Unit.PX);
		lblAdd.getElement().getStyle().setFloat(Float.LEFT);
		lblAdd.getElement().getStyle().setMarginTop(5, Unit.PX);

		imgAdd.addClickHandler(handler);

		addPanel.add(lblAdd);
		addPanel.add(imgAdd);

		filterPanel.add(addPanel);

		ProjectionConditionPanel cond = new ProjectionConditionPanel(mainPanel, ProjectionMeasureFormulaDialog.this, true);
		DOM.setStyleAttribute(cond.getElement(), "clear", "both");
		cond.getListBox().setEnabled(false);
		cond.getListBox().addItem(FreeAnalysisWeb.LBL.ProjectionDefaultCondition());

		cond.getCaptionPanel().getElement().getStyle().setBackgroundColor("#BBBBBB");
		filterPanel.add(cond);

		if (projectionMeasure != null) {
			for (ProjectionMeasureCondition condi : projectionMeasure.getConditions()) {
				if (condi.getMemberUnames().get(0).equals("[Default]")) {
					cond.setFormula(condi.getFormula());
				}
				else {
					ProjectionConditionPanel pan = new ProjectionConditionPanel(mainPanel, ProjectionMeasureFormulaDialog.this, false, condi);
					filterPanel.add(pan);
				}
			}
		}
		else {
			cond.setFormula(measure.getUname() + " * 1");
		}

		((ProjectionFormulaPanel) formulaPanel.getWidget(0)).setFormula(cond.getFormula());
	}

	protected boolean testProjectionValidity(ProjectionMeasure proj) {

		List<ProjectionMeasureCondition> conditions = proj.getConditions();

		// compare the filter members
		// the conditions need to have at least one common dimension
		// we start at 1 to skip the default condition
		for (int i = 1; i < conditions.size(); i++) {
			for (int j = 1; j < conditions.size(); j++) {
				if (i == j) {
					continue;
				}
				boolean isConditionValid = false;
				ProjectionMeasureCondition cond1 = conditions.get(i);
				ProjectionMeasureCondition cond2 = conditions.get(j);

				for (String cond1Uname : cond1.getMemberUnames()) {
					for (String cond2Uname : cond2.getMemberUnames()) {
						String[] part1 = cond1Uname.split("\\]\\.\\[");
						String[] part2 = cond2Uname.split("\\]\\.\\[");
						if (part1[0].equals(part2[0])) {
							isConditionValid = true;
							break;
						}
					}
					if (isConditionValid) {
						break;
					}
				}
				if (!isConditionValid) {
					return false;
				}

			}
			if (conditions.get(i).getFormula() == null || conditions.get(i).getFormula().equals("")) {
				return false;
			}
		}

		return true;
	}

	protected ProjectionMeasure createProjectionMeasure() {
		ProjectionMeasure projMes = new ProjectionMeasure();
		for (int i = 0; i < filterPanel.getWidgetCount(); i++) {
			Widget w = filterPanel.getWidget(i);
			if (w instanceof ProjectionConditionPanel) {
				ProjectionConditionPanel pan = (ProjectionConditionPanel) w;

				if (pan.getCaptionPanel().getElement().getStyle().getBackgroundColor().equalsIgnoreCase("rgb(187, 187, 187)")) {
					pan.setFormula(((ProjectionFormulaPanel) formulaPanel.getWidget(0)).getFormula());
				}

				ProjectionMeasureCondition cond = pan.getCondition();
				if (cond != null) {
					if (cond.isDefault()) {
						projMes.setFormula(cond.getFormula());
						projMes.addCondition(cond);
					}
					else {
						projMes.addCondition(cond);
					}
				}
			}
		}

		projMes.setUname(measure.getUname());

		return projMes;
	}

	public void select(ProjectionConditionPanel projectionConditionPanel) {

		for (int i = 0; i < filterPanel.getWidgetCount(); i++) {
			Widget w = filterPanel.getWidget(i);
			if (w instanceof ProjectionConditionPanel) {
				ProjectionConditionPanel pan = (ProjectionConditionPanel) w;
				if (pan.getCaptionPanel().getElement().getStyle().getBackgroundColor().equalsIgnoreCase("rgb(187, 187, 187)")) {
					pan.setFormula(((ProjectionFormulaPanel) formulaPanel.getWidget(0)).getFormula());
				}
			}
		}

		for (int i = 0; i < filterPanel.getWidgetCount(); i++) {
			Widget w = filterPanel.getWidget(i);
			if (w instanceof ProjectionConditionPanel) {
				ProjectionConditionPanel pan = (ProjectionConditionPanel) w;
				if (pan.equals(projectionConditionPanel)) {
					pan.getCaptionPanel().getElement().getStyle().setBackgroundColor("#BBBBBB");

					((ProjectionFormulaPanel) formulaPanel.getWidget(0)).setFormula(pan.getFormula());
				}
				else {
					pan.getCaptionPanel().getElement().getStyle().setBackgroundColor("#FFFFFF");
				}
			}
		}

	}

	public void delete(ProjectionConditionPanel projectionConditionPanel) {
		mainPanel.getDragController().unregisterDropController(projectionConditionPanel.getDropCtrl());
		filterPanel.remove(projectionConditionPanel);
	}

	@Override
	public void showWaitPart(boolean visible) {
		if (visible && !isCharging) {
			isCharging = true;

			int height = treePanel.getOffsetHeight();
			int width = treePanel.getOffsetWidth();

			greyPanel = new GreyAbsolutePanel();
			waitPanel = new WaitAbsolutePanel();

			treePanel.add(greyPanel);
			treePanel.add(waitPanel);

			DOM.setStyleAttribute(waitPanel.getElement(), "top", ((height / 2) - 50) + "px");
			DOM.setStyleAttribute(waitPanel.getElement(), "left", ((width / 2) - 100) + "px");
		}
		else if (!visible && isCharging) {
			isCharging = false;

			treePanel.remove(greyPanel);
			treePanel.remove(waitPanel);
		}
	}

}
