package bpm.architect.web.client.utils;

import java.util.ArrayList;
import java.util.List;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.images.Images;
import bpm.architect.web.client.panels.DataVizDesignPanel;
import bpm.architect.web.client.panels.ToolBox;
import bpm.architect.web.client.panels.ToolBoxCategory;
import bpm.data.viz.core.preparation.PreparationRule.RuleType;
import bpm.gwt.commons.client.panels.CollapsePanel;
import bpm.gwt.commons.client.panels.StackNavigationPanel;

import com.google.gwt.resources.client.ImageResource;

public class ToolHelper {

	protected static String TYPE_TOOL;

	public static List<StackNavigationPanel> createCategories(CollapsePanel collapsePanel, DataVizDesignPanel parent) {
		List<StackNavigationPanel> cats = new ArrayList<>();
		
		List<ToolBox> tools = new ArrayList<>();
		
		tools.add(new ToolBox(RuleType.LOWER_CASE, parent));
		tools.add(new ToolBox(RuleType.UPPER_CASE, parent));
		tools.add(new ToolBox(RuleType.RECODE, parent));
		tools.add(new ToolBox(RuleType.SORT, parent));
		tools.add(new ToolBox(RuleType.ADD_CHAR, parent));
		tools.add(new ToolBox(RuleType.NORMALIZE, parent));
		
		cats.add(new ToolBoxCategory(collapsePanel, Labels.lblCnst.RuleCatText(), false, tools, "#F0F8FF"));
		
		List<ToolBox> numberTools = new ArrayList<>();
		numberTools.add(new ToolBox(RuleType.FORMAT_NUMBER, parent));
		numberTools.add(new ToolBox(RuleType.MAX, parent));
		numberTools.add(new ToolBox(RuleType.MIN, parent));
		numberTools.add(new ToolBox(RuleType.ROUND, parent));
		
		cats.add(new ToolBoxCategory(collapsePanel, Labels.lblCnst.RuleCatNumber(), false, numberTools, "#98FB98"));
		
		List<ToolBox> dateTools = new ArrayList<>();
		dateTools.add(new ToolBox(RuleType.DATE_TO_AGE, parent));
		
		cats.add(new ToolBoxCategory(collapsePanel, Labels.lblCnst.RuleCatDate(), false, dateTools, "#FFFFE0"));
		
		List<ToolBox> calcTools = new ArrayList<>();
		calcTools.add(new ToolBox(RuleType.CALC, parent));
		
		cats.add(new ToolBoxCategory(collapsePanel, Labels.lblCnst.RuleCatCalc(), false, calcTools, "#DCDCDC"));
		
		List<ToolBox> advancedTools = new ArrayList<>();
		advancedTools.add(new ToolBox(RuleType.DEDOUBLON, parent));
		advancedTools.add(new ToolBox(RuleType.FILTER, parent));
		advancedTools.add(new ToolBox(RuleType.GROUP, parent));
		advancedTools.add(new ToolBox(RuleType.AFFECTER, parent));
		
		cats.add(new ToolBoxCategory(collapsePanel, Labels.lblCnst.RuleCatAdvanced(), false, advancedTools, "#E6E6FA"));
		return cats;
	}

	public static ImageResource getImage(RuleType type) {
		switch(type) {
			case LOWER_CASE:
				return Images.INSTANCE.add_version();
				
			case UPPER_CASE:
				return Images.INSTANCE.add_version();

			default:
				break;
		}
		return null;
	}

	public static String getLabel(RuleType type) {
		switch(type) {
			case LOWER_CASE:
				return Labels.lblCnst.RuleTypeLowerCase();
				
			case UPPER_CASE:
				return Labels.lblCnst.RuleTypeUpperCase();
				
			case RECODE:
				return Labels.lblCnst.RuleTypeRecode();
				
			case SORT:
				return Labels.lblCnst.RuleTypeSort();
				
			case ADD_CHAR:
				return Labels.lblCnst.RuleTypeAddChar();
				
			case NORMALIZE:
				return Labels.lblCnst.RuleTypeNormalize();
				
			case FORMAT_NUMBER:
				return Labels.lblCnst.FormatNumber();
				
			case MIN:
				return Labels.lblCnst.Min();
				
			case MAX:
				return Labels.lblCnst.Max();
				
			case ROUND:
				return Labels.lblCnst.Round();

			case CALC:
				return Labels.lblCnst.Calculated();
				
			case DATE_TO_AGE:
				return Labels.lblCnst.DateToAge();
				
			case DEDOUBLON:
				return "Supprimer doublons";
				
			case FILTER:
				return "Filtrer";
				
			case GROUP:
				return "Groupe de classification";
				
			case AFFECTER:
				return "Affectation par tranche";
				
			
			default:
				break;
		}
		return null;
	}

}
