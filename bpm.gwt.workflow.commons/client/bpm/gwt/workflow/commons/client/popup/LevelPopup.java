package bpm.gwt.workflow.commons.client.popup;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.workflow.commons.beans.Log.Level;
import bpm.workflow.commons.beans.WorkflowInstance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class LevelPopup extends PopupPanel {

	private static LevelPopupUiBinder uiBinder = GWT.create(LevelPopupUiBinder.class);

	interface LevelPopupUiBinder extends UiBinder<Widget, LevelPopup> {
	}

	interface MyStyle extends CssResource {
		String labelType();

		String labelTypeEnd();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelMenu;

	private ISelectLevel selector;
	private WorkflowInstance instance;

	public LevelPopup(ISelectLevel selector, WorkflowInstance instance) {
		setWidget(uiBinder.createAndBindUi(this));
		this.selector = selector;
		this.instance = instance;

		panelMenu.addStyleName(VanillaCSS.POPUP_PANEL);

		boolean first = true;
		for (Level level : Level.values()) {
			String levelLabel = LabelsCommon.lblCnst.All();
			if (level != Level.ALL) {
				levelLabel = level.toString();
			}

			addFormat(levelLabel, level, first);
			first = false;
		}

		this.setAnimationEnabled(true);
		this.setAutoHideEnabled(true);
	}

	private void addFormat(String levelLabel, Level level, boolean first) {
		Label lblTheme = new Label(levelLabel);
		lblTheme.addClickHandler(new LevelHandler(level));
		if (first) {
			lblTheme.addStyleName(style.labelType());
		}
		else {
			lblTheme.addStyleName(style.labelTypeEnd());
		}

		panelMenu.add(lblTheme);
	}

	private class LevelHandler implements ClickHandler {

		private Level level;

		public LevelHandler(Level level) {
			this.level = level;
		}

		@Override
		public void onClick(ClickEvent event) {
			selector.selectLevel(instance, level);
			LevelPopup.this.hide();
		}
	}
}
