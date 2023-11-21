package bpm.vanilla.portal.client.dialog.properties;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.dialog.ButtonTab;
import bpm.gwt.commons.client.dialog.ITabDialog;
import bpm.gwt.commons.client.listeners.TabSwitchHandler;
import bpm.gwt.commons.client.viewer.dialog.ItemMetadataLinkPanel;
import bpm.gwt.commons.shared.repository.PortailRepositoryDirectory;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.panels.ContentDisplayPanel;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class PropertyDialog extends AbstractDialogBox implements ITabDialog {
	private static PropertyDialogUiBinder uiBinder = GWT.create(PropertyDialogUiBinder.class);

	interface PropertyDialogUiBinder extends UiBinder<Widget, PropertyDialog> {
	}
	
	interface MyStyle extends CssResource {
		String contentPanelMax();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	HTMLPanel panelTab;
	
	@UiField
	SimplePanel panelContent;
	
	private ButtonTab selectedButton;

	public PropertyDialog(ContentDisplayPanel parent, PortailRepositoryItem item) {
		super(ToolsGWT.lblCnst.Properties(), true, false);
		setWidget(uiBinder.createAndBindUi(this));
		
		addTab(ToolsGWT.lblCnst.Properties(), new ItemPropertiesView(item), true);
		if(biPortal.get().getInfoUser().getUser().isSuperUser() && item.getType() != IRepositoryApi.FA_CUBE_TYPE) {
			addTab(ToolsGWT.lblCnst.GroupManagement(), new ItemRightView(this, item.getId(), item.getType(), false), false);
			
			if(item.isReport()) {
				addTab(ToolsGWT.lblCnst.AccessHistoric(), new HistoricIndexView(this, item), false);
			}
		}
		
		addTab(ToolsGWT.lblCnst.Comments(), new CommentView(item), false);
		
		if(item.isReport()){
			addTab(ToolsGWT.lblCnst.History(), new HistoryView(this, parent, item), false);
			addTab(ToolsGWT.lblCnst.MetadataLink(), new ItemMetadataLinkPanel(this, biPortal.get().getInfoUser(), item.getId()), false);
		}
		
		addTab(ToolsGWT.lblCnst.LinkedDocuments(), new LinkedDocumentView(this, parent, item), false);
	}

	public PropertyDialog(PortailRepositoryDirectory directory) {
		super(ToolsGWT.lblCnst.Properties(), true, false);
		setWidget(uiBinder.createAndBindUi(this));

		addTab(ToolsGWT.lblCnst.Properties(), new ItemPropertiesView(directory), true);
		if(biPortal.get().getInfoUser().getUser().isSuperUser()) {
			addTab(ToolsGWT.lblCnst.GroupManagement(), new ItemRightView(this, directory.getId(), -1, true), false);
		}
	}
	
	public void addTab(String buttonName, Widget widget, boolean selectTab) {
		ButtonTab button = new ButtonTab(buttonName);
		button.addClickHandler(new TabSwitchHandler(this, button, widget));
		panelTab.add(button);
		
		if(selectTab) {
			switchViewer(button, widget);
		}
	}
	
	@Override
	public void switchViewer(ButtonTab button, Widget widget) {
		if(selectedButton != null) {
			this.selectedButton.select(false);
		}
		button.select(true);
		
		this.selectedButton = button;
		
		panelContent.setWidget(widget);
	}

	@Override
	public void maximize(boolean maximize) {
		if(maximize) {
			contentPanel.addStyleName(style.contentPanelMax());
		}
		else {
			contentPanel.removeStyleName(style.contentPanelMax());
		}
	}

}
