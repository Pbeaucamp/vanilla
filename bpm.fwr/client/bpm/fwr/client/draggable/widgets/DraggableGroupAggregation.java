package bpm.fwr.client.draggable.widgets;

import bpm.fwr.api.beans.dataset.GroupAgregation;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.dialogs.AggregationDialogBox;
import bpm.fwr.client.dialogs.TextDialogBox;
import bpm.fwr.client.dialogs.TextDialogBox.TypeDialog;
import bpm.gwt.commons.client.listeners.FinishListener;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

public class DraggableGroupAggregation extends FocusPanel {

	private AggregationDialogBox dialogParent;
	private GroupAgregation groupAgg;
	private HTML htmlAgg;
	private Label lblAggOn;
	private GroupAgregation gr;

	public DraggableGroupAggregation(GroupAgregation gr, AggregationDialogBox dialogParent, GroupAgregation groupAgg) {
		super();
		this.gr = gr;

		String grName = gr.getCustomLabel();
		String aggOn = " - (" + gr.getType() + " on " + gr.getColumn() + ")";

		htmlAgg = new HTML(grName);
		htmlAgg.addDoubleClickHandler(doubleClickHandler);

		lblAggOn = new Label(aggOn);

		FlowPanel mainPanel = new FlowPanel();
		mainPanel.add(htmlAgg);
		mainPanel.add(lblAggOn);

		this.add(mainPanel);

		this.setDialogParent(dialogParent);
		this.setGroupAgg(groupAgg);
	}

	public void setDialogParent(AggregationDialogBox dialogParent) {
		this.dialogParent = dialogParent;
	}

	public AggregationDialogBox getDialogParent() {
		return dialogParent;
	}

	public void setGroupAgg(GroupAgregation groupAgg) {
		this.groupAgg = groupAgg;
	}

	public GroupAgregation getGroupAgg() {
		return groupAgg;
	}

	private DoubleClickHandler doubleClickHandler = new DoubleClickHandler() {

		@Override
		public void onDoubleClick(DoubleClickEvent event) {
			showDialogText(finishListener);
		}
	};

	private FinishListener finishListener = new FinishListener() {

		@Override
		public void onFinish(Object result, Object source, String result1) {
			String txt = (String) result;
			if (!txt.isEmpty()) {
				htmlAgg.setHTML(txt);
				gr.setCustomLabel(txt);
			}
		}
	};

	public void showDialogText(FinishListener finishListener) {
		TextDialogBox dial = new TextDialogBox(Bpm_fwr.LBLW.Label(), Bpm_fwr.LBLW.TextFill(), htmlAgg.getHTML(), htmlAgg, TypeDialog.EDITABLE);
		dial.addFinishListener(finishListener);
		dial.center();
	}

	public FinishListener getFinishListener() {
		return finishListener;
	}
}
