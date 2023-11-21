package bpm.es.web.client.utils;

import bpm.es.web.client.I18N.Labels;
import bpm.es.web.client.images.Images;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;

public class ActivationImageCell extends ButtonCell {
	
	public static final String ACTIVATE = "Activate";
	public static final String DEACTIVATE = "Deactivate";
	
	private String styleName;
	
	private int clientX;
	private int clientY;
	
	public ActivationImageCell(String styleName) {
		this.styleName = styleName;
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
		Image img = findImage(value);
		img.setTitle(findTitle(value));
		img.addStyleName(styleName);
		
		SafeHtml html = SafeHtmlUtils.fromTrustedString(img.toString());
		sb.append(html);
	}
	
	private Image findImage(String value) {
		switch (value) {
		case ACTIVATE:
			return new Image(Images.INSTANCE.activated());
		case DEACTIVATE:
			return new Image(Images.INSTANCE.deactivated());
		default:
			break;
		}

		return new Image(Images.INSTANCE.planification_red());
	}
	
	private String findTitle(String value) {
		switch (value) {
		case ACTIVATE:
			return Labels.lblCnst.DeactivateUser();
		case DEACTIVATE:
			return Labels.lblCnst.ActivateUser();
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
