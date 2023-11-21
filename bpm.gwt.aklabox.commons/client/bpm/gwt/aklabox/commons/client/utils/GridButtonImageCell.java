package bpm.gwt.aklabox.commons.client.utils;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;

public class GridButtonImageCell<T> extends ActionCell<T> {
	
	protected ImageResource resource;
	protected String title;
	protected String styleName;
	
	private int clientX;
	private int clientY;
	
	public GridButtonImageCell(ImageResource resource, Delegate<T> delegate) {
		this(resource, "", null, delegate);
	}
	
	public GridButtonImageCell(ImageResource resource, String title, Delegate<T> delegate) {
		this(resource, title, null, delegate);
	}
	
	public GridButtonImageCell(ImageResource resource, String title, String styleName, Delegate<T> delegate) {
		super("", delegate);
		this.resource = resource;
		this.title = title;
		this.styleName = styleName != null ? styleName : ThemeCSS.IMG_BUTTON_GRID;
	}
	
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, T value, SafeHtmlBuilder sb) {
		Image img = new Image(resource);
		img.setTitle(title);
		img.addStyleName(styleName);
		
		SafeHtml html = SafeHtmlUtils.fromTrustedString(img.toString());
		sb.append(html);
	}
	
	@Override
	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, T value, NativeEvent event, ValueUpdater<T> valueUpdater) {
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