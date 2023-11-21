package bpm.architect.web.client.panels;

import bpm.architect.web.client.dialogs.dataviz.BaseRuleDialog;
import bpm.data.viz.core.preparation.PreparationRule;
import bpm.data.viz.core.preparation.PreparationRule.RuleType;
import bpm.data.viz.core.preparation.PreparationRuleAddChar;
import bpm.data.viz.core.preparation.PreparationRuleCalc;
import bpm.data.viz.core.preparation.PreparationRuleFilter;
import bpm.data.viz.core.preparation.PreparationRuleFormat;
import bpm.data.viz.core.preparation.PreparationRuleMinMax;
import bpm.data.viz.core.preparation.PreparationRuleRecode;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class DataVizRuleDisplayPanel extends Composite {

	private static DataVizRuleDisplayPanelUiBinder uiBinder = GWT.create(DataVizRuleDisplayPanelUiBinder.class);

	interface DataVizRuleDisplayPanelUiBinder extends UiBinder<Widget, DataVizRuleDisplayPanel> {}

	@UiField
	Label lblRowCol, lblOn, lblRule;

	private PreparationRule rule;
	private DataVizDesignPanel parent;

	public DataVizRuleDisplayPanel(PreparationRule rule, DataVizDesignPanel parent) {
		initWidget(uiBinder.createAndBindUi(this));

		this.rule = rule;
		this.parent = parent;

		if(rule.getColumn() != null) {
			lblOn.setText("sur");
			String txt = "";
			boolean first = true;
			for(DataColumn col : rule.getColumns()) {
				if(first) {
					first = false;
				}
				else {
					txt += "/";
				}
				txt += col.getColumnLabel();
			}
			lblRowCol.setText(txt);
		}
		else if(rule.getType() == RuleType.CALC){
			lblOn.setText("sur");
			lblRowCol.setText(rule.getNewColumnName());
//			lblOn.setText("la ligne");
//			lblRowCol.setText(rule.getRowNumber() + "");
		}

		lblRule.setText(getRuleText());
	}

	private String getRuleText() {
		switch(rule.getType()) {
			case LOWER_CASE:
				return "Passer en minuscule";
			case UPPER_CASE:
				return "Passer en majuscule";
			case RECODE:
				return "Recoder (" + ((PreparationRuleRecode) rule).getOriginFormula() + " => " + ((PreparationRuleRecode) rule).getResultFormula() + ")";
			case SORT:
				return "Trier";
			case NORMALIZE:
				return "Normaliser";
			case ADD_CHAR:
				return "Ajouter un caractère (" + ((PreparationRuleAddChar)rule).getCharToAdd() + ")";
			case ROUND:
				return "Arrodir";
			case FORMAT_NUMBER:
				return "Formatter (" + ((PreparationRuleFormat)rule).getPattern() + ")";
			case MAX:
				return "Maximum (" + ((PreparationRuleMinMax)rule).getValue() + ")";
			case MIN:
				return "Minimum (" + ((PreparationRuleMinMax)rule).getValue() + ")";
			case DATE_TO_AGE:
				return "Transformation date en âge";
			case DEDOUBLON:
				return "Dédoublonner";
			case CALC:
				return "Calculer (" + ((PreparationRuleCalc)rule).getFormula() + ")";
			case FILTER:
				return "Filtrer (" + ((PreparationRuleFilter)rule).getFilter() + ")";
			case GROUP:
				return "Grouper";
			case AFFECTER:
				return "Affectation par tanche effectuer";
			case LIBREOFFICE:
				return "Libre Office";
		}
		return null;
	}

	@UiHandler("imgDelete")
	public void onDelete(ClickEvent event) {
		parent.getDataPreparation().getRules().remove(rule);
		parent.getDataPanel().refresh(parent.getDataPreparation());
		parent.getRulePanel().refreshRulePanel();
		parent.getDataPanel().getVisualPanel().deleteBox(rule);
	}

	@UiHandler("imgEdit")
	public void onEdit(ClickEvent event) {
		if(rule.getType().equals(RuleType.LIBREOFFICE)) {
			return;
		}
		final BaseRuleDialog dial = new BaseRuleDialog(parent.getDataPreparation(), rule, parent.getDataPanel());
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if(dial.isConfirm()) {
					parent.getDataPanel().refresh(parent.getDataPreparation());
					parent.getRulePanel().refreshRulePanel();
					parent.getDataPanel().getVisualPanel().editBox(rule);
				}
			}
		});
		dial.center();
	}

}
