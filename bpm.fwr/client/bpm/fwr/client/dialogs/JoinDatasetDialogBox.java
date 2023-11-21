package bpm.fwr.client.dialogs;

import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.JoinDataSet;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.images.WysiwygImage;
import bpm.fwr.client.utils.JoinConditionConstantes;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.listeners.FinishListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class JoinDatasetDialogBox extends AbstractDialogBox implements ICustomDialogBox {

	private static JoinDatasetDialogBoxUiBinder uiBinder = GWT.create(JoinDatasetDialogBoxUiBinder.class);

	interface JoinDatasetDialogBoxUiBinder extends UiBinder<Widget, JoinDatasetDialogBox> {
	}
	
	interface MyStyle extends CssResource {
		String lblDataset();
		String lstDataset();
		String lstColumns();
		String addColumn();
		String lblCondition();
		String tblType();
		String lstType();
		String tblOperator();
		String lstOperator();
		String lblActualCondition();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel contentPanel;
	
	private Button confirmBtn;

	private Image imgAddColumnDatasetLeft;
	private Image imgAddColumnDatasetRight;

	// join dataset
	private ListBox leftCombo;
	private ListBox rightCombo;
	private ListBox leftLstCol;
	private ListBox rightLstCol;
	private ListBox txtJoinType;
	private ListBox txtJoinOperator;

	private HTML lblPrev;

	private boolean complete = false;

	private DataSet datasetLeft;
	private DataSet datasetRight;

	public JoinDatasetDialogBox(DataSet datasetLeft, DataSet datasetRight) {
		super(Bpm_fwr.LBLW.CreateJoinDataset(), false, true);
		this.datasetLeft = datasetLeft;
		this.datasetRight = datasetRight;
		
		setWidget(uiBinder.createAndBindUi(this));

		createButton(Bpm_fwr.LBLW.Cancel(), cancelHandler);
		confirmBtn = createButton(Bpm_fwr.LBLW.Ok(), okHandler);
		
		VerticalPanel mainPanel = new VerticalPanel();

		VerticalPanel contentPanel = new VerticalPanel();
		HorizontalPanel middlePanel = new HorizontalPanel();

		// the panel which contains the left dataset
		VerticalPanel leftDsPanel = new VerticalPanel();
		Label lblDsLeft = new Label(Bpm_fwr.LBLW.DatasetLeftDs());
		lblDsLeft.addStyleName(style.lblDataset());
		
		leftCombo = new ListBox(false);
		leftCombo.setEnabled(false);
		leftCombo.addStyleName(style.lstDataset());
		
		leftLstCol = new ListBox(true);
		leftLstCol.addStyleName(style.lstColumns());
		HorizontalPanel panelAddColumnLeft = createPanelAddColumn(true);
		leftDsPanel.add(lblDsLeft);
		leftDsPanel.add(leftCombo);
		leftDsPanel.add(leftLstCol);
		leftDsPanel.add(panelAddColumnLeft);

		// the panel which contains the join informations
		VerticalPanel joinPanel = new VerticalPanel();
		HorizontalPanel joinTypePanel = new HorizontalPanel();
		joinTypePanel.addStyleName(style.tblType());
		
		HorizontalPanel joinOperatorPanel = new HorizontalPanel();
		joinOperatorPanel.addStyleName(style.tblOperator());
		
		Label lblJoinType = new Label(Bpm_fwr.LBLW.DatasetJoinCondType());
		Label lblJoinOperator = new Label(Bpm_fwr.LBLW.DatasetJoinCondOperator());
		txtJoinType = new ListBox(false);
		txtJoinType.addStyleName(style.lstType());
		
		txtJoinOperator = new ListBox(false);
		txtJoinOperator.addStyleName(style.lstOperator());
		
		joinTypePanel.add(lblJoinType);
		joinTypePanel.add(txtJoinType);
		
		joinOperatorPanel.add(lblJoinOperator);
		joinOperatorPanel.add(txtJoinOperator);
		
		Label lblCondition = new Label("Join conditions");
		lblCondition.addStyleName(style.lblCondition());
		
		joinPanel.add(lblCondition);
		joinPanel.add(joinTypePanel);
		joinPanel.add(joinOperatorPanel);

		// the panel with the preview of the joincondition
		VerticalPanel previewJoinPanel = new VerticalPanel();
		Label lblPrevTitle = new Label(Bpm_fwr.LBLW.JoinPreviewTitle());
		lblPrevTitle.addStyleName(style.lblActualCondition());
		
		lblPrev = new HTML();
		previewJoinPanel.add(lblPrevTitle);
		previewJoinPanel.add(lblPrev);

		// the panel which contains the right dataset
		VerticalPanel rightDsPanel = new VerticalPanel();
		Label lblDsRight = new Label(Bpm_fwr.LBLW.DatasetRightDs());
		lblDsRight.addStyleName(style.lblDataset());
		
		rightCombo = new ListBox(false);
		rightCombo.setEnabled(false);
		rightCombo.addStyleName(style.lstDataset());
		
		rightLstCol = new ListBox(true);
		rightLstCol.addStyleName(style.lstColumns());
		
		HorizontalPanel panelAddColumnRight = createPanelAddColumn(false);
		rightDsPanel.add(lblDsRight);
		rightDsPanel.add(rightCombo);
		rightDsPanel.add(rightLstCol);
		rightDsPanel.add(panelAddColumnRight);

		fillJoinLists();

		middlePanel.add(leftDsPanel);
		middlePanel.add(joinPanel);
		middlePanel.add(rightDsPanel);
		contentPanel.add(middlePanel);
		contentPanel.add(previewJoinPanel);
		mainPanel.add(contentPanel);

		this.contentPanel.add(mainPanel);

		leftLstCol.addChangeHandler(joinConditionChangeHandler);
		rightLstCol.addChangeHandler(joinConditionChangeHandler);
		txtJoinOperator.addChangeHandler(joinConditionChangeHandler);
		txtJoinType.addChangeHandler(joinConditionChangeHandler);

		updateBtn();
	}

	private HorizontalPanel createPanelAddColumn(boolean isLeft) {
		HorizontalPanel bottomPanel = new HorizontalPanel();
		bottomPanel.addStyleName(style.addColumn());

		if (isLeft) {
			imgAddColumnDatasetLeft = new Image(WysiwygImage.INSTANCE.add());
			imgAddColumnDatasetLeft.addClickHandler(btnClickHandler);

			bottomPanel.add(imgAddColumnDatasetLeft);
		}
		else {
			imgAddColumnDatasetRight = new Image(WysiwygImage.INSTANCE.add());
			imgAddColumnDatasetRight.addClickHandler(btnClickHandler);

			bottomPanel.add(imgAddColumnDatasetRight);
		}

		Label lblAddColumn = new Label(Bpm_fwr.LBLW.AddColumn());
		bottomPanel.add(lblAddColumn);

		return bottomPanel;
	}

	private void fillJoinLists() {
		// fill joinCondition combos
		for (String type : JoinConditionConstantes.types) {
			txtJoinType.addItem(type, type);
		}
		for (String op : JoinConditionConstantes.operators) {
			txtJoinOperator.addItem(op, op);
		}

		leftCombo.addItem(datasetLeft.getName(), datasetLeft.getName());
		rightCombo.addItem(datasetRight.getName(), datasetRight.getName());

		refreshListColumn();
	}

	private void refreshListColumn() {
		leftLstCol.clear();
		rightLstCol.clear();

		for (Column col : datasetLeft.getColumns()) {
			leftLstCol.addItem(col.getTitle(datasetLeft.getLanguage()), col.getName());
		}

		for (Column col : datasetRight.getColumns()) {
			rightLstCol.addItem(col.getTitle(datasetRight.getLanguage()), col.getName());
		}
	}

	public void majPreview() {
		String lblRight = rightLstCol.getSelectedIndex() != -1 ? rightLstCol.getItemText(rightLstCol.getSelectedIndex()) : "";
		String lblLeft = leftLstCol.getSelectedIndex() != -1 ? leftLstCol.getItemText(leftLstCol.getSelectedIndex()) : "";

		if (!lblRight.equals("") && !lblLeft.equals("")) {
			complete = true;
		}

		String prevMess = Bpm_fwr.LBLW.PrevJoinType() + txtJoinType.getItemText(txtJoinType.getSelectedIndex()) + "<br />" + leftCombo.getItemText(leftCombo.getSelectedIndex()) + "::" + lblLeft + " = " + rightCombo.getItemText(rightCombo.getSelectedIndex()) + "::" + lblRight;
		lblPrev.setHTML(prevMess);

		updateBtn();
	}

	private JoinDataSet createBaseForJoinDataset() {
		// create the dataset
		String joinDatasetName = "JoinDataset_" + System.currentTimeMillis();

		JoinDataSet joinDs = new JoinDataSet();
		joinDs.setName(joinDatasetName);
		joinDs.setLeftDatasetName(leftCombo.getValue(leftCombo.getSelectedIndex()));
		joinDs.setRightDatasetName(rightCombo.getValue(rightCombo.getSelectedIndex()));
		joinDs.setLeftExpression("dataSetRow[\"" + leftLstCol.getValue(leftLstCol.getSelectedIndex()) + "\"]");
		joinDs.setRightExpression("dataSetRow[\"" + rightLstCol.getValue(rightLstCol.getSelectedIndex()) + "\"]");
		joinDs.setOperator(txtJoinOperator.getValue(txtJoinOperator.getSelectedIndex()));
		joinDs.setType(txtJoinType.getValue(txtJoinType.getSelectedIndex()));
		joinDs.addDataset(datasetLeft);
		joinDs.addDataset(datasetRight);

		datasetLeft.setParent(joinDs);
		datasetRight.setParent(joinDs);

		return joinDs;
	}

	private ChangeHandler joinConditionChangeHandler = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			majPreview();
		}
	};

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			JoinDataSet joinDataset = createBaseForJoinDataset();
			finish(joinDataset, JoinDatasetDialogBox.this, null);
			JoinDatasetDialogBox.this.hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			JoinDatasetDialogBox.this.hide();
		}
	};

	private ClickHandler btnClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (event.getSource().equals(imgAddColumnDatasetLeft)) {
				AddColumnToDatasetDialogBox dial = new AddColumnToDatasetDialogBox(datasetLeft);
				dial.addFinishListener(finishListener);
				dial.center();
			}
			else if (event.getSource().equals(imgAddColumnDatasetRight)) {
				AddColumnToDatasetDialogBox dial = new AddColumnToDatasetDialogBox(datasetRight);
				dial.addFinishListener(finishListener);
				dial.center();
			}
		}
	};

	private FinishListener finishListener = new FinishListener() {

		@Override
		public void onFinish(Object result, Object source, String result1) {
			refreshListColumn();
		}
	};

	@Override
	public void updateBtn() {
		confirmBtn.setEnabled(complete);
	}
}