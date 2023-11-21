package bpm.architect.web.client.panels;

import bpm.architect.web.client.utils.DocumentHelper;
import bpm.mdm.model.supplier.Contract;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;

/**
 * ActionCell of type <T> with a image as button
 *
 * @param <T>
 */
public class FormatImageCell extends ActionCell<Contract> {
	
	protected String styleName;
	
	public FormatImageCell() {
		super("", null);
	}
	
	public FormatImageCell(String styleName) {
		super("", null);
		this.styleName = styleName;
	}
	
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, Contract value, SafeHtmlBuilder sb) {
		Image img = new Image(DocumentHelper.getImageFormat(value));
		img.setTitle(DocumentHelper.getFormat(value));
		if (styleName != null) {
			img.addStyleName(styleName);
		}
		
		SafeHtml html = SafeHtmlUtils.fromTrustedString(img.toString());
		sb.append(html);
	}
}