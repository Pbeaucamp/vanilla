package bpm.fd.web.client.widgets;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.core.DashboardComponent;
import bpm.fd.core.component.ContainerComponent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ContentWidget extends Composite {

	private static ContentWidgetUiBinder uiBinder = GWT.create(ContentWidgetUiBinder.class);

	interface ContentWidgetUiBinder extends UiBinder<Widget, ContentWidget> {
	}

	@UiField
	HTMLPanel contentPanel;

	private List<WidgetComposite> items;

	public ContentWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public ContainerComponent getContainer() {
		List<DashboardComponent> components = new ArrayList<>();
		if (items != null) {
			for (WidgetComposite widget : items) {
				components.add(widget.getComponent());
			}
		}
		return new ContainerComponent(components);
	}

	public void addWidget(WidgetComposite widget) {
		contentPanel.add(widget);
		
		if (items == null) {
			items = new ArrayList<WidgetComposite>();
		}
		items.add(widget);
	}
}
