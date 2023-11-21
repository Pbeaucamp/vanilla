package bpm.fd.web.client.utils;

import bpm.fd.web.client.I18N.Labels;
import bpm.fd.web.client.images.DashboardImage;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;

public class DimensionMeasureImageCell extends ButtonCell {
	
	public static final String DIMENSION = "Dimension";
	public static final String MEASURE = "Measure";
	
	private String styleName;
	
	public DimensionMeasureImageCell(String styleName) {
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
		case DIMENSION:
			return new Image(DashboardImage.INSTANCE.dimension());
		case MEASURE:
			return new Image(DashboardImage.INSTANCE.obj_measure());
		default:
			break;
		}
		
		return new Image(DashboardImage.INSTANCE.dimension());
	}
	
	private String findTitle(String value) {
		switch (value) {
		case DIMENSION:
			return Labels.lblCnst.Dimension();
		case MEASURE:
			return Labels.lblCnst.Measure();
		default:
			break;
		}
		
		return "";
	}
}
