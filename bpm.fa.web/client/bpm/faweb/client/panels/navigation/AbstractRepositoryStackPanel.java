package bpm.faweb.client.panels.navigation;

import bpm.faweb.client.MainPanel;
import bpm.gwt.commons.client.loading.StackLayoutWaitPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractRepositoryStackPanel extends StackLayoutWaitPanel {

	private static AbstractRepositoryStackPanelUiBinder uiBinder = GWT.create(AbstractRepositoryStackPanelUiBinder.class);

	interface AbstractRepositoryStackPanelUiBinder extends UiBinder<Widget, AbstractRepositoryStackPanel> {
	}

	interface MyStyle extends CssResource {
		String parentPanel();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel panelContent;

	protected MainPanel mainPanel;

	private TypeViewer typeViewer;

	public AbstractRepositoryStackPanel(MainPanel mainPanel, TypeViewer typeViewer) {
		super("");
		add(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		this.typeViewer = typeViewer;

		this.addStyleName(style.parentPanel());
	}

	public TypeViewer getTypeViewer() {
		return typeViewer;
	}

	public abstract void refresh();
}
