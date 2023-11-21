package bpm.architect.web.client.utils;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.dialogs.dataviz.BaseRuleDialog;
import bpm.architect.web.client.dialogs.dataviz.TyperColonne;
import bpm.architect.web.client.panels.DataVizDataPanel;
import bpm.data.viz.core.preparation.PreparationRule.RuleType;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class HeaderPopup extends PopupPanel {

	private static HeaderPopupUiBinder uiBinder = GWT.create(HeaderPopupUiBinder.class);

	interface HeaderPopupUiBinder extends UiBinder<Widget, HeaderPopup> {}

	interface MyStyle extends CssResource {
		String labelType();

		String labelTypeEnd();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelMenu;

	public HeaderPopup(final DataColumn col, final DataVizDataPanel dataPanel) {
		setWidget(uiBinder.createAndBindUi(this));
		panelMenu.addStyleName(VanillaCSS.POPUP_PANEL);

		Label lblFilter = new Label(Labels.lblCnst.Filter());
		Label lblTyper = new Label(Labels.lblCnst.Typer());
		lblFilter.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final BaseRuleDialog dial = new BaseRuleDialog(dataPanel.getLastResult().getPreparation(), RuleType.FILTER, dataPanel);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if(dial.isConfirm()) {

							dataPanel.refresh(dataPanel.getLastResult().getPreparation());
						}
					}
				});
				dial.center();

				HeaderPopup.this.hide();
			}
		});

		lblTyper.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				final TyperColonne dial = new TyperColonne(col, dataPanel.getDataPreparation());
				
				dial.center();
				HeaderPopup.this.hide();

			}

		});

		lblFilter.addStyleName(style.labelType());
		panelMenu.add(lblFilter);
		lblTyper.addStyleName(style.labelType());
		panelMenu.add(lblTyper);

		this.setAnimationEnabled(true);
		this.setAutoHideEnabled(true);
	}

}
