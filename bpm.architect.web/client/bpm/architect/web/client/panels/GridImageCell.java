package bpm.architect.web.client.panels;

import com.google.gwt.cell.client.ImageCell;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;

public class GridImageCell extends ImageCell {
	
	private ImageResource resource;
	private String title;
	private String styleName;
	
	public GridImageCell(ImageResource resource, String title, String styleName) {
		this.resource = resource;
		this.title = title;
		this.styleName = styleName;
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
		Image img = new Image(resource);
		img.setTitle(title);
		img.addStyleName(styleName);
		
		SafeHtml html = SafeHtmlUtils.fromTrustedString(img.toString());
		sb.append(html);
	}
}