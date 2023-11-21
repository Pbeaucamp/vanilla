package bpm.gwt.commons.client.dialog;

import java.util.HashMap;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.tree.CmisTree;
import bpm.gwt.commons.shared.cmis.CmisFolder;
import bpm.gwt.commons.shared.cmis.CmisInformations;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class CmisDialog extends AbstractDialogBox {

	private static CmisDialogUiBinder uiBinder = GWT.create(CmisDialogUiBinder.class);

	interface CmisDialogUiBinder extends UiBinder<Widget, CmisDialog> {
	}

	@UiField
	HTMLPanel mainPanel;

	private CmisTree tree;

	private boolean confirm;

	private CmisFolder selectedDirectory;

	public CmisDialog() {
		super("", true, true);
		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		CmisInformations cmisInfos = createDummy();
		
		tree = new CmisTree(cmisInfos);
		mainPanel.add(tree);
	}

	private CmisInformations createDummy() {
		HashMap<String, String> properties = new HashMap<>();
		properties.put(CmisInformations.LOGIN, "system");
		properties.put(CmisInformations.PASSWORD, "system");
		properties.put(CmisInformations.BINDING_BROWSER_URL, "http://localhost:8080/bpm.vanilla.cmis/browser");
		
		return new CmisInformations(properties, true);
	}

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			confirm = true;
			hide();
		}
	};

	public boolean isConfirm() {
		return confirm;
	}

	public boolean folderSelected() {
		return selectedDirectory != null;
	}
}
