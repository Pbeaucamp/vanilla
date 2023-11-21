package bpm.fm.designer.web.client.dialog;

import bpm.fm.api.model.Theme;
import bpm.fm.designer.web.client.Messages;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ThemeDialog extends AbstractDialogBox {

	private static ThemeDialogUiBinder uiBinder = GWT.create(ThemeDialogUiBinder.class);

	interface ThemeDialogUiBinder extends UiBinder<Widget, ThemeDialog> {
	}
	
	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	TextBox txtName;
	
	private Theme theme;
	
	private boolean confirm = true;

	public ThemeDialog(Theme theme) {
		super(Messages.lbl.addEditTheme(), false, true);
		
		setWidget(uiBinder.createAndBindUi(this));
		
		this.theme = theme;
		
		if(theme != null) {
			txtName.setText(theme.getName());
		}
		
		createButtonBar(Messages.lbl.Ok(), okHandler, Messages.lbl.Cancel(), cancelHandler);
	}

	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if(theme == null) {
				theme = new Theme();
			}
			theme.setName(txtName.getText());
			
			ThemeDialog.this.hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			confirm = false;
			ThemeDialog.this.hide();
		}
	};
	
	public Theme getTheme() {
		return theme;
	}
	
	public boolean isConfirm() {
		return confirm;
	}
}
