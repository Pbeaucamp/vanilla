package bpm.architect.web.client.utils;

import bpm.architect.web.client.panels.DataVizDataPanel;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class ImageHeaderCell extends AbstractEditableCell<String, String> {

	private static String html = "<div><label  title={2} style=\"overflow:hidden;color:#6666CC;position:relative;cursor:pointer;float:left;vertical-align:top;width:68px;\">{0}</label><img style=\"cursor:pointer;vertical-align:top;\" src=\"{1}\"/></div>";

	private String imageUrl;
	private DataColumn column;
	private DataVizDataPanel dataVizDataPanel;

	public ImageHeaderCell(String value, String url, DataColumn col, DataVizDataPanel dataVizDataPanel) {
		super(BrowserEvents.CLICK);
		this.imageUrl = url;
		this.column = col;
		this.dataVizDataPanel = dataVizDataPanel;
	}

	@Override
	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
		DivElement div = (DivElement) parent.getChild(0);
		ImageElement img = (ImageElement) div.getChild(1);
		if(event.getClientX() > img.getAbsoluteLeft() && event.getClientY() < img.getAbsoluteRight()) {
			event.preventDefault();
			event.stopPropagation();
			HeaderPopup p = new HeaderPopup(column, dataVizDataPanel);
			p.setPopupPosition(event.getClientX(), event.getClientY());
			p.show();
		}
		else {
			dataVizDataPanel.sortColumn(column);
		}
	}

	@Override
	public boolean isEditing(com.google.gwt.cell.client.Cell.Context context, Element parent, String value) {
		
		return false;
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {

		String value1 = value;
		if(value1.length() >= 10) {

			value1 = value1.substring(0, 8) + "...";

			//System.out.println(value1);
			String res = html.replace("{0}", value1).replace("{1}", imageUrl).replace("{2}", value);
			SafeHtml sh = SafeHtmlUtils.fromTrustedString(res);
			sb.append(sh);

		}
		else {
			String res = html.replace("{0}", value1).replace("{1}", imageUrl).replace("{2}", value);
			SafeHtml sh = SafeHtmlUtils.fromTrustedString(res);
			sb.append(sh);

		}
	}
}
