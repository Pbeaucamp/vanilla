package bpm.fd.web.client.panels;

import java.util.List;

import bpm.gwt.commons.client.panels.CollapsePanel;
import bpm.gwt.commons.client.panels.StackHeader;
import bpm.gwt.commons.client.panels.StackNavigationPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ToolBoxCategory extends StackNavigationPanel {

	private static ToolBoxCategoryUiBinder uiBinder = GWT.create(ToolBoxCategoryUiBinder.class);

	interface ToolBoxCategoryUiBinder extends UiBinder<Widget, ToolBoxCategory> {
	}
	
	@UiField
	HTMLPanel panelContent;

	private CollapsePanel collapsePanel;
	
	private String title;
	private boolean showCollapse;
	
	public ToolBoxCategory(CollapsePanel collapsePanel, String title, boolean showCollapse, List<ToolBox> tools) {
		initWidget(uiBinder.createAndBindUi(this));
		this.collapsePanel = collapsePanel;
		this.title = title;
		this.showCollapse = showCollapse;
		
		if (tools != null) {
			for(ToolBox tool : tools) {
				panelContent.add(tool);
			}
		}
	}

	@Override
	public StackHeader buildHeader() {
		return new StackHeader(collapsePanel, title, showCollapse, 0, null);
	}
}
