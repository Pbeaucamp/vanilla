package bpm.faweb.client.projection.panel;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.client.projection.ProjectionMeasureCondition;
import bpm.faweb.client.projection.dialog.ProjectionMeasureFormulaDialog;
import bpm.faweb.client.projection.panel.impl.ClickableCaptionPanel;
import bpm.faweb.client.projection.panel.impl.ConditionDropController;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class ProjectionConditionPanel extends Composite {

	private static ProjectionConditionPanelUiBinder uiBinder = GWT.create(ProjectionConditionPanelUiBinder.class);

	interface ProjectionConditionPanelUiBinder extends UiBinder<Widget, ProjectionConditionPanel> {
	}

	@UiField
	Image imgDelete;

	@UiField
	ListBox lstMembers;

	@UiField
	ClickableCaptionPanel captionPanel;

	@UiField
	HTMLPanel mainPanel;

	private ProjectionMeasureFormulaDialog parent;

	private ProjectionMeasureCondition condition;

	private boolean isDefault;

	private ConditionDropController dropCtrl;

	public ProjectionConditionPanel(MainPanel mainCompPanel, ProjectionMeasureFormulaDialog projectionMeasureFormulaDialog, boolean isDefault) {
		initWidget(uiBinder.createAndBindUi(this));

		this.isDefault = isDefault;

		this.parent = projectionMeasureFormulaDialog;

		imgDelete.setResource(FaWebImage.INSTANCE.delete());
		lstMembers.setMultipleSelect(true);

		captionPanel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				parent.select(ProjectionConditionPanel.this);
			}
		});

		imgDelete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				parent.delete(ProjectionConditionPanel.this);
			}
		});

		if (isDefault) {
			mainPanel.remove(imgDelete);
		}

		else {
			dropCtrl = new ConditionDropController(lstMembers);
			mainCompPanel.getDragController().registerDropController(dropCtrl);
		}
	}

	public ProjectionConditionPanel(MainPanel mainCompPanel, ProjectionMeasureFormulaDialog projectionMeasureFormulaDialog, boolean b, ProjectionMeasureCondition condi) {
		this(mainCompPanel, projectionMeasureFormulaDialog, b);
		condition = condi;
		for (String uname : condi.getMemberUnames()) {
			lstMembers.addItem(uname, uname);
		}
	}

	public ListBox getListBox() {
		return lstMembers;
	}

	public CaptionPanel getCaptionPanel() {
		return captionPanel;
	}

	public void setFormula(String formula) {
		if (condition == null) {
			condition = new ProjectionMeasureCondition();
		}
		condition.setFormula(formula);
	}

	public String getFormula() {
		if (condition == null) {
			return "";
		}
		return condition.getFormula();
	}

	public ProjectionMeasureCondition getCondition() {
		if (condition != null) {
			if (isDefault) {
				condition.setDefault(true);
				if (!condition.getMemberUnames().contains("[Default]")) {
					condition.addMemberUname("[Default]");
				}
			}
			else {
				for (int i = 0; i < lstMembers.getItemCount(); i++) {
					String uname = lstMembers.getValue(i);
					if (!condition.getMemberUnames().contains(uname)) {
						condition.addMemberUname(uname);
					}
				}
			}
		}
		return condition;
	}

	public ConditionDropController getDropCtrl() {
		return dropCtrl;
	}

}
