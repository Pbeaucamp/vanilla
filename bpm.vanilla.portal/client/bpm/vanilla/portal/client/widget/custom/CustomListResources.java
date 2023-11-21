package bpm.vanilla.portal.client.widget.custom;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.vanilla.portal.client.images.PortalImage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellList.Resources;
import com.google.gwt.user.cellview.client.CellList.Style;

public class CustomListResources implements Resources{
	
    private final CustomStyle style;

    public CustomListResources() {
    	Resources resource = GWT.create(Resources.class);
    	this.style = new CustomStyle(resource.cellListStyle());
    }

	@Override
	public ImageResource cellListSelectedBackground() {
		return PortalImage.INSTANCE.cellBackground();
	}

	@Override
	public Style cellListStyle() {
		return style;
	}

	/**
	 * A ClientBundle that provides images for this widget.
	 */
	public interface Resources extends ClientBundle {

	    /**
	     * The styles used in this widget.
	     */
	    @Source("CustomList.css")
	    Style cellListStyle();

		ImageResource cellListSelectedBackground();
	}
	
	private class CustomStyle implements CellList.Style {
		
		private final CellList.Style style;

	    public CustomStyle(CellList.Style style) {
	    	this.style = style;
	    }

		@Override
		public boolean ensureInjected() {
			return style.ensureInjected();
		}

		@Override
		public String getText() {
			return style.getText();
		}

		@Override
		public String getName() {
			return style.getName();
		}

		@Override
		public String cellListEvenItem() {
			return style.cellListEvenItem();
		}

		@Override
		public String cellListKeyboardSelectedItem() {
			return style.cellListKeyboardSelectedItem();
		}

		@Override
		public String cellListOddItem() {
			return VanillaCSS.DATA_GRID_ODD_ROW;
		}

		@Override
		public String cellListSelectedItem() {
			return VanillaCSS.DATA_GRID_SELECTED_ROW;
		}

		@Override
		public String cellListWidget() {
			return style.cellListWidget();
		}
	}

}
