package bpm.gwt.commons.client.utils;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;

public class ButtonImageCell extends ButtonCell {
	
	private ImageResource resource;
	private String title;
	private String styleName;
	
	private int clientX;
	private int clientY;
	
	public ButtonImageCell(ImageResource resource, String styleName) {
		this.resource = resource;
		this.title = "";
		this.styleName = styleName;
	}
	
	public ButtonImageCell(ImageResource resource, String title, String styleName) {
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
	
	@Override
	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
		super.onBrowserEvent(context, parent, value, event, valueUpdater);
		
		this.clientX = event.getClientX();
		this.clientY = event.getClientY();
	}
	
	public int getClientX() {
		return clientX;
	}
	
	public int getClientY() {
		return clientY;
	}
}