package bpm.gwt.commons.client.viewer.fmdtdriller.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.vanilla.platform.core.beans.fmdt.FmdtFilter;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class PromptsValueDialog extends AbstractDialogBox {

	private static PromptsValueDialogUiBinder uiBinder = GWT.create(PromptsValueDialogUiBinder.class);

	interface PromptsValueDialogUiBinder extends UiBinder<Widget, PromptsValueDialog> {
	}

	interface MyStyle extends CssResource {

	}

	@UiField
	HTMLPanel contentPanel;

	@UiField
	SimplePanel panelDatagrid;

	private final CellTable<PromptValue> promptsValues = new CellTable<PromptValue>();
	private List<PromptValue> promptValueList;
	private List<FmdtFilter> prompts;
	Boolean confirm = false;

	public PromptsValueDialog(List<FmdtFilter> prompts) {
		super(LabelsConstants.lblCnst.Filter(), false, true);
		setWidget(uiBinder.createAndBindUi(this));

		promptsValues.setAutoHeaderRefreshDisabled(true);
		promptsValues.setAutoFooterRefreshDisabled(true);

		initPromptValueTable();
		initPromptValueList(prompts);
		promptsValues.setRowData(0, promptValueList);

		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

	}

	public void initPromptValueTable() {

		TextColumn<PromptValue> promptName = new TextColumn<PromptValue>() {
			@Override
			public String getValue(PromptValue prompt) {
				return prompt.name;
			}
		};

		TextInputCell textcell = new TextInputCell();
		Column<PromptValue, String> promptValue = new Column<PromptValue, String>(textcell) {
			@Override
			public String getValue(PromptValue object) {
				return object.value == null ? "" : object.value;
			}
		};

		promptValue.setFieldUpdater(new FieldUpdater<PromptValue, String>() {
			@Override
			public void update(int index, PromptValue object, String value) {
				object.value = value;
			}
		});

		promptsValues.addColumn(promptName, LabelsConstants.lblCnst.Label());
		promptsValues.setColumnWidth(promptName, 30, Unit.PC);
		promptsValues.addColumn(promptValue, LabelsConstants.lblCnst.Value());
		promptsValues.setColumnWidth(promptValue, 70, Unit.PC);

		panelDatagrid.setWidget(promptsValues);
	}

	private void initPromptValueList(List<FmdtFilter> prompts) {
		promptValueList = new ArrayList<PromptsValueDialog.PromptValue>();
		for (FmdtFilter filter : prompts) {
			promptValueList.add(new PromptValue(filter.getLabel(), filter));
		}
	}

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			confirm = true;
			prompts = new ArrayList<FmdtFilter>();
			for (PromptValue value : promptsValues.getVisibleItems()) {
				FmdtFilter prompt = value.getFilter();
				prompt.setValues(value.getValues());
				prompts.add(prompt);
			}
			hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	class PromptValue {
		private String name;
		private String value;
		private FmdtFilter filter;

		public PromptValue(String name, FmdtFilter filter) {
			super();

			this.name = name;
			this.filter = filter;
			this.value = "";
		}

		public PromptValue() {
			super();
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<String> getValues() {
			List<String> values = new ArrayList<String>();
			if (value.contains(",")) {
				String[] val = value.split(",");
				for (String v : val)
					values.add(v);
			} else
				values.add(value);

			return values;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public FmdtFilter getFilter() {
			return filter;
		}

		public void setFilter(FmdtFilter filter) {
			this.filter = filter;
		}

	}

	public List<FmdtFilter> getPrompts() {
		return prompts;
	}

	public Boolean isConfirm() {
		return confirm;
	}

}
