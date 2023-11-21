package bpm.gwt.workflow.commons.client.workflow;

import java.util.List;

import bpm.gwt.workflow.commons.client.images.Images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ToolBoxFolder extends Composite {

	private static final int MARGIN = 20;

	private static ToolBoxFolderUiBinder uiBinder = GWT.create(ToolBoxFolderUiBinder.class);

	interface ToolBoxFolderUiBinder extends UiBinder<Widget, ToolBoxFolder> {
	}

	@UiField
	Image imgOpen;

	@UiField
	Label lblName;

	@UiField
	HTMLPanel panelContent, panelTool;

	private List<ToolBoxFolder> folders;
	private List<ToolBox> tools;

	private boolean isOpen = false;

	public ToolBoxFolder(String name, List<ToolBoxFolder> folders, List<ToolBox> tools, int level) {
		initWidget(uiBinder.createAndBindUi(this));
		this.folders = folders;
		this.tools = tools;

		lblName.setText(name);
		lblName.setTitle(name);

		panelContent.getElement().getStyle().setMarginLeft(MARGIN * level, Unit.PX);
	}

	@UiHandler("focus")
	public void onOpen(ClickEvent event) {
		this.isOpen = !isOpen;
		open(isOpen);
	}

	private void open(boolean isOpen) {
		if (isOpen) {
			if (folders != null) {
				for (ToolBoxFolder folder : folders) {
					panelContent.add(folder);
				}
			}

			if (tools != null) {
				for (ToolBox tool : tools) {
					panelContent.add(tool);
				}
			}

			if ((tools != null && !tools.isEmpty()) || (folders != null && !folders.isEmpty())) {
				imgOpen.setVisible(true);
				imgOpen.setResource(Images.INSTANCE.ic_remove_black_24dp());
			}
			else {
				imgOpen.setVisible(false);
			}
		}
		else if (!isOpen) {
			panelContent.clear();

			if ((tools != null && !tools.isEmpty()) || (folders != null && !folders.isEmpty())) {
				imgOpen.setVisible(true);
				imgOpen.setResource(Images.INSTANCE.ic_add_black_24dp());
			}
			else {
				imgOpen.setVisible(false);
			}
		}
		else {
			imgOpen.setVisible(false);
		}
	}

}
