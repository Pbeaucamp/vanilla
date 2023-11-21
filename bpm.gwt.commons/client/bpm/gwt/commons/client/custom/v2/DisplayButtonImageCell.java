package bpm.gwt.commons.client.custom.v2;

import bpm.vanilla.map.core.design.MapLayer;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;

/**
 * Boutons d'action pour les DataGrids de {@link MapLayer}
 */
public class DisplayButtonImageCell extends bpm.gwt.commons.client.custom.ButtonImageCell<MapLayer> {

	private ImageResource resourceNotDisplay;
	private String titleNotDisplay	;

	public DisplayButtonImageCell(ImageResource resource, String title, Delegate<MapLayer> delegate) {
		super(resource, title, null, delegate);
	}

	public DisplayButtonImageCell(ImageResource ressourceDisplay, String titleDisplay, ImageResource resourceNotDisplay, String titleNotDisplay, Delegate<MapLayer> delegate) {
		super(ressourceDisplay, titleDisplay, null, delegate);
		this.resourceNotDisplay = resourceNotDisplay;
		this.titleNotDisplay = titleNotDisplay;
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, MapLayer value, SafeHtmlBuilder sb) {
		if (value.isSelected()) {
			render(sb, resource, title);
		}
		else {
			render(sb, resourceNotDisplay, titleNotDisplay);
		}
	}

	private void render(SafeHtmlBuilder sb, ImageResource resource, String title) {
		Image img = new Image(resource);
		img.setTitle(title);
		img.addStyleName(styleName);

		SafeHtml html = SafeHtmlUtils.fromTrustedString(img.toString());
		sb.append(html);
	}
}