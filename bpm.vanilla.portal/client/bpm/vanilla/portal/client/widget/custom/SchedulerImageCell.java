package bpm.vanilla.portal.client.widget.custom;

import bpm.vanilla.portal.client.images.PortalImage;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;

public class SchedulerImageCell extends ButtonCell {
	
	public static final int ACTIVATE = 0;
	public static final int DEACTIVATE = 1;
	
	private String styleName;
	
	private int clientX;
	private int clientY;
	
	public SchedulerImageCell(String styleName) {
		this.styleName = styleName;
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
		Image img = findImage(Integer.parseInt(value));
		img.setTitle(findTitle(Integer.parseInt(value)));
		img.addStyleName(styleName);
		
		SafeHtml html = SafeHtmlUtils.fromTrustedString(img.toString());
		sb.append(html);
	}
	
	private Image findImage(int value) {
		switch (value) {
		case ACTIVATE:
			return new Image(PortalImage.INSTANCE.planification_red());
		case DEACTIVATE:
			return new Image(PortalImage.INSTANCE.planification_green());
		default:
			break;
		}
		return new Image(PortalImage.INSTANCE.planification_red());
	}
	
	private String findTitle(int value) {
		switch (value) {
		case ACTIVATE:
			return ToolsGWT.lblCnst.ActivateJob();
		case DEACTIVATE:
			return ToolsGWT.lblCnst.DeactivateJob();
		default:
			break;
		}
		
		return "";
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
