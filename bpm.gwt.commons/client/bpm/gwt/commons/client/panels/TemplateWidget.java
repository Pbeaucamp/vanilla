package bpm.gwt.commons.client.panels;

import bpm.vanilla.platform.core.beans.VanillaImage;
import bpm.vanilla.platform.core.repository.Template;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class TemplateWidget<T> extends Composite {

	private static TemplateWidgetUiBinder uiBinder = GWT.create(TemplateWidgetUiBinder.class);

	interface TemplateWidgetUiBinder extends UiBinder<Widget, TemplateWidget<?>> {
	}

	interface MyStyle extends CssResource {
		String selected();
	}
	
	@UiField
	MyStyle style;

	@UiField
	FocusPanel focus;

	@UiField
	Image img, btnDelete;

	@UiField
	Label lbl;

	private ITemplateManager<T> manager;
	private Template<T> template;

	public TemplateWidget(ITemplateManager<T> manager, Template<T> template) {
		initWidget(uiBinder.createAndBindUi(this));
		this.manager = manager;
		this.template = template;
		
		lbl.setText(template.getName());
		setImage(template.getImage());
//		img.setText(template.getName());
		
		if (template.getId() == 0) {
			btnDelete.removeFromParent();
		}
	}

	private void setImage(VanillaImage image) {
		if (image != null && image.getUrl() != null && !image.getUrl().isEmpty()) {
			String url = image.getUrl();
			url = url.replace("webapps", "..");
			
			img.setUrl(url);
		}
	}

	@UiHandler("focus")
	public void onSelect(ClickEvent event) {
		manager.select(this);
	}

	@UiHandler("btnDelete")
	public void onDelete(ClickEvent event) {
		manager.deleteTemplate(template);
	}
	
	public Template<T> getTemplate() {
		return template;
	}

	public void setSelected(boolean selected) {
		if (selected) {
			focus.addStyleName(style.selected());
		}
		else {
			focus.removeStyleName(style.selected());
		}
	}

	public interface ITemplateManager<T> {

		public void deleteTemplate(Template<T> template);

		public void select(TemplateWidget<T> widget);
	}
}
