package bpm.gwt.commons.client.custom.v2;

import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.utils.GlobalCSS;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;

/**
 * Header for datagrid with a Tooltip button image
 * 
 * @param <T>
 */
public class TooltipHeader<T> extends AbstractCell<T> {

	private String columnName;
	private Tooltip tooltip;
	
	public TooltipHeader(String columnName, String tooltip) {
		super("click");
		this.columnName = columnName;
		if (tooltip != null && !tooltip.isEmpty()) {
			this.tooltip = new Tooltip();
			this.tooltip.setHTML(tooltip);
		}
	}
	
	@Override
	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, T value, NativeEvent event, ValueUpdater<T> valueUpdater) {
		super.onBrowserEvent(context, parent, value, event, valueUpdater);
		if ("click".equals(event.getType())) {
			if (tooltip != null) {
				tooltip.setPopupPosition(event.getClientX(), event.getClientY());
				tooltip.show();
			}
		}
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, T value, SafeHtmlBuilder sb) {
		sb.appendHtmlConstant("<div class=\"flex\">");
		sb.appendHtmlConstant("<p>" + columnName + "</p>");
		
		Image img = new Image(CommonImages.INSTANCE.info_24());
		img.addStyleName(GlobalCSS.TOOLTIP_HEADER);
		
		SafeHtml html = SafeHtmlUtils.fromTrustedString(img.toString());
		sb.append(html);
		sb.appendHtmlConstant("</div>");
	}
}
