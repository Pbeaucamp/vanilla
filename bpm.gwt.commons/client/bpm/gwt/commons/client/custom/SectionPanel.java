package bpm.gwt.commons.client.custom;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SectionPanel extends Composite implements HasWidgets {

	private static SectionPanelUiBinder uiBinder = GWT.create(SectionPanelUiBinder.class);

	interface SectionPanelUiBinder extends UiBinder<Widget, SectionPanel> {
	}
	
	@UiField
	Label lblTitle;
	
	@UiField
	HTMLPanel panelContent;

	public SectionPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setText(String text) {
		lblTitle.setText(text);
	}
	
	@Override
	public void add(Widget widget) {
		panelContent.add(widget);
	}

	@Override
	public void clear() {
		panelContent.clear();
	}

	@Override
	public Iterator<Widget> iterator() {
		return panelContent.iterator();
	}

	@Override
	public boolean remove(Widget w) {
		return panelContent.remove(w);
	}
}
