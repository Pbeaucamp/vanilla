package bpm.gwt.commons.client.popup;

import java.util.List;

import bpm.gwt.commons.client.custom.IChangeGroup;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.vanilla.platform.core.beans.Group;

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

public class GroupPopup extends PopupPanel {

	private static GroupPopupUiBinder uiBinder = GWT.create(GroupPopupUiBinder.class);

	interface GroupPopupUiBinder extends UiBinder<Widget, GroupPopup> {
	}
	
	interface MyStyle extends CssResource {
		String labelType();
		String labelTypeEnd();
	}
	
	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelMenu;
	
	private IChangeGroup changeGroupPanel;
	
	public GroupPopup(IChangeGroup changeGroupPanel, List<Group> availableGroups) {
		setWidget(uiBinder.createAndBindUi(this));
		this.changeGroupPanel = changeGroupPanel;

		panelMenu.addStyleName(VanillaCSS.POPUP_PANEL);

		boolean first = true;
		for(Group group : availableGroups){
			addGroup(group, first);
			first = false;
		}
		
		this.setAnimationEnabled(true);
		this.setAutoHideEnabled(true);
	}
	
	private void addGroup(Group group, boolean first) {
		Label lblTheme = new Label(group.getName());
		lblTheme.addClickHandler(new GroupHandler(group));
		if(first) {
			lblTheme.addStyleName(style.labelType());
		}
		else {
			lblTheme.addStyleName(style.labelTypeEnd());
		}
		
		panelMenu.add(lblTheme);
	}
	
	private class GroupHandler implements ClickHandler {
		
		private Group group;

		public GroupHandler(Group group) {
			this.group = group;
		}
		
		@Override
		public void onClick(ClickEvent event) {
			changeGroupPanel.changeGroup(group);
			GroupPopup.this.hide();
		}
	}
}
