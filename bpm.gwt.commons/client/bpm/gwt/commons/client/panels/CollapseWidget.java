package bpm.gwt.commons.client.panels;

import java.util.List;

import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public abstract class CollapseWidget extends Composite {
	
	public void managePanel(boolean isCollapse) {
		List<Widget> widgets = getCollapseWidgets();
		if (widgets != null) {
			for (Widget widg : widgets) {
				widg.setVisible(isCollapse);
			}
		}
		
		Image imgExpand = getImgExpand();
		imgExpand.setVisible(!isCollapse);

		if (isCollapse) {
			this.removeStyleName(VanillaCSS.RIGHT_TOOLBAR);
		}
		else {
			this.addStyleName(VanillaCSS.RIGHT_TOOLBAR);
		}
		
		additionnalCollapseTreatment(isCollapse);
	}

	public abstract void onCollapseClick(ClickEvent event);
	
	public abstract void onExpandClick(ClickEvent event);
	
	public abstract Image getImgExpand();

	public abstract List<Widget> getCollapseWidgets();
	
	protected abstract void additionnalCollapseTreatment(boolean isCollapse);
}
