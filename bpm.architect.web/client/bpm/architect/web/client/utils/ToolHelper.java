package bpm.architect.web.client.utils;

import java.util.ArrayList;
import java.util.List;

import bpm.architect.web.client.images.Images;
import bpm.architect.web.client.panels.ToolBox;
import bpm.architect.web.client.panels.ToolBoxCategory;
import bpm.gwt.commons.client.panels.CollapsePanel;
import bpm.gwt.commons.client.panels.StackNavigationPanel;
import bpm.vanilla.platform.core.beans.forms.FormField.TypeField;

import com.google.gwt.resources.client.ImageResource;

public class ToolHelper {

	protected static String TYPE_TOOL;

	public static List<StackNavigationPanel> createCategories(CollapsePanel collapsePanel) {
		List<StackNavigationPanel> cats = new ArrayList<>();
		
		List<ToolBox> tools = new ArrayList<>();
		
		tools.add(new ToolBox(TypeField.TEXTBOX));
		tools.add(new ToolBox(TypeField.LISTBOX));
		tools.add(new ToolBox(TypeField.CHECKBOX));
		
		cats.add(new ToolBoxCategory(collapsePanel, "Elements", true, tools));
		return cats;
	}

	public static ImageResource getImage(TypeField type) {
		switch(type) {
			case TEXTBOX:
				return Images.INSTANCE.add_version();
				
			case CHECKBOX:
				return Images.INSTANCE.add_version();
				
			case LISTBOX:
				return Images.INSTANCE.add_version();
				

			default:
				break;
		}
		return null;
	}

	public static String getLabel(TypeField type) {
		switch(type) {
			case TEXTBOX:
				return "Textbox";
				
			case CHECKBOX:
				return "Checkbox";
				
			case LISTBOX:
				return "Listbox";

			default:
				break;
		}
		return null;
	}

}
