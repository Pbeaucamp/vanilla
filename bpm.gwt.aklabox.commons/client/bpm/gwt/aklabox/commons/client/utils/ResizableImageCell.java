package bpm.gwt.aklabox.commons.client.utils;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class ResizableImageCell extends AbstractCell<String> {

	interface Template extends SafeHtmlTemplates {
		@Template("<img src=\"{0}\" style=\"width:{1}; height:{2};\" />")
		SafeHtml img(String url, String width, String height);
	}

	private static Template template;
	private final String width;
	private final String height;

	public ResizableImageCell(String height, String width) {
		if (template == null) {
			template = GWT.create(Template.class);
		}
		this.width = width;
		this.height = height;
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
		if (value != null) {
			// The template will sanitize the URI.
			sb.append(template.img(value, this.width, this.height));
		}
	}

}
