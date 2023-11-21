package bpm.gwt.aklabox.commons.client.tree;

import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.images.CommonImages;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TreeItem;

public class WaitTreeItem extends TreeItem {

	public WaitTreeItem() {
		super(new HTML(new Image(CommonImages.INSTANCE.loading()) + " " + LabelsConstants.lblCnst.Wait()));
	}
	
	@Override
	public void setSelected(boolean selected) {
//		if (!selected) {
//			this.getWidget().removeStyleName(VanillaCSS.TREE_ITEM_SELECTED);
//		}
//		else {
//			this.getWidget().addStyleName(VanillaCSS.TREE_ITEM_SELECTED);
//		}
		super.setSelected(selected);
	}
}
