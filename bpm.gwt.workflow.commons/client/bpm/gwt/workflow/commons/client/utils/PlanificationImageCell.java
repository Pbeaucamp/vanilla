package bpm.gwt.workflow.commons.client.utils;

import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.images.Images;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;

public class PlanificationImageCell extends ButtonCell {
	
	public static final String PLANIFICATION = "Planification";
	public static final String ACTIVATE = "Activate";
	public static final String DEACTIVATE = "Deactivate";
	
	private String styleName;
	
	private int clientX;
	private int clientY;
	
	public PlanificationImageCell(String styleName) {
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
		case PLANIFICATION:
			return new Image(Images.INSTANCE.planification_gray());
		case ACTIVATE:
			return new Image(Images.INSTANCE.planification_red());
		case DEACTIVATE:
			return new Image(Images.INSTANCE.planification_green());
		default:
			break;
		}
		
		return new Image(Images.INSTANCE.planification_gray());
	}
	
	private String findTitle(String value) {
		switch (value) {
		case PLANIFICATION:
			return LabelsCommon.lblCnst.PlanWorkflow();
		case ACTIVATE:
			return LabelsCommon.lblCnst.ActivateWorkflow();
		case DEACTIVATE:
			return LabelsCommon.lblCnst.DeactivateWorkflow();
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
