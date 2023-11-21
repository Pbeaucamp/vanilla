package bpm.vanilla.portal.client.panels.center;

import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.panels.WidgetPanel;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class WelcomePanel extends Tab {

	private static WelcomePanelUiBinder uiBinder = GWT.create(WelcomePanelUiBinder.class);

	interface WelcomePanelUiBinder extends UiBinder<Widget, WelcomePanel> {
	}

	@UiField
	HTML lblAdminMessage;
	
	@UiField
	HTMLPanel mainPanel;

	public WelcomePanel(TabManager tabManager) {
		super(tabManager, ToolsGWT.lblCnst.Home(), true);
		this.add(uiBinder.createAndBindUi(this));
		
		if(biPortal.get().getInfoUser().getUser().isWidget()) {
			mainPanel.clear();
			mainPanel.add(new WidgetPanel());
		}
		else {
			String mess = biPortal.get().getInfoUser().getGroup().getCustom1();
			lblAdminMessage.setHTML(mess);
			lblAdminMessage.addAttachHandler(new AttachEvent.Handler() {
	
				@Override
				public void onAttachOrDetach(AttachEvent event) {
					loadLocale();
				}
			});
		}
	}

	public static native void loadLocale() /*-{
		var locale = $wnd.gup('locale');
		if (locale == 'fr_FR' && $doc.getElementById('conteneur') != undefined) {
			$doc.getElementById('conteneur').className = 'fr';
		} else if ($doc.getElementById('conteneur') != undefined) {
			$doc.getElementById('conteneur').className = 'en';
		}
	}-*/;
}
