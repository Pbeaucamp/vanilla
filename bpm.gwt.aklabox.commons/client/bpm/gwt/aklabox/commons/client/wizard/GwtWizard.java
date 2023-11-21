package bpm.gwt.aklabox.commons.client.wizard;

import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.dialogs.AbstractDialogBox;
import bpm.gwt.aklabox.commons.client.loading.GreyAbsolutePanel;
import bpm.gwt.aklabox.commons.client.loading.WaitAbsolutePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class GwtWizard extends AbstractDialogBox implements IGwtWizard {

	private static GwtWizardUiBinder uiBinder = GWT.create(GwtWizardUiBinder.class);

	interface GwtWizardUiBinder extends UiBinder<Widget, GwtWizard> {
	}

	interface MyStyle extends CssResource {
		String disabled();
	}

	@UiField
	MyStyle style;

	@UiField
	protected HTMLPanel contentPanel;

	@UiField
	SimplePanel panelWizard;

	private Button btnBack, btnNext, btnFinish;

	private GreyAbsolutePanel greyPanel;
	private WaitAbsolutePanel waitPanel;
	private boolean isCharging;

	private int indexCharging = 0;
	
	private boolean isConfirm = false;

	public GwtWizard(String title) {
		super(title, false, true);
		setWidget(uiBinder.createAndBindUi(this));

		createButton(LabelsConstants.lblCnst.Cancel(), cancelHandler);
		btnFinish = createButton(LabelsConstants.lblCnst.Confirmation(), confirmHandler);
		btnNext = createButton(LabelsConstants.lblCnst.Next() + " >", nextHandler);
		btnBack = createButton("< " + LabelsConstants.lblCnst.Back(), backHandler);
	}

	public ClickHandler backHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			onBackClick();
		}
	};

	public ClickHandler nextHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			onNextClick();
		}
	};

	public ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			isConfirm = false;
			GwtWizard.this.hide();
		}
	};

	public ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			isConfirm = true;
			onClickFinish();
		}
	};

	protected void setContentPanel(Composite content) {
		panelWizard.setWidget(content);
	}

	protected void setBtnBackState(boolean enabled) {
		this.btnBack.setEnabled(enabled);
		updateButtonState(btnBack, enabled);
	}

	protected void setBtnFinishState(boolean enabled) {
		this.btnFinish.setEnabled(enabled);
		updateButtonState(btnFinish, enabled);
	}

	protected void setBtnNextState(boolean enabled) {
		this.btnNext.setEnabled(enabled);
		updateButtonState(btnNext, enabled);
	}

	private void updateButtonState(Button button, boolean enabled) {
		if (enabled) {
			button.removeStyleName(style.disabled());
		}
		else {
			button.addStyleName(style.disabled());
		}
	}

	@Override
	public void showWaitPart(boolean visible) {

		if (visible) {
			indexCharging++;

			if (!isCharging) {
				isCharging = true;

				greyPanel = new GreyAbsolutePanel();
				waitPanel = new WaitAbsolutePanel();

				contentPanel.add(greyPanel);
				contentPanel.add(waitPanel);

				int height = panelWizard.getOffsetHeight();
				int width = panelWizard.getOffsetWidth();

				if (height != 0) {
					DOM.setStyleAttribute(waitPanel.getElement(), "top", ((height / 2) - 50) + "px");
				}
				else {
					DOM.setStyleAttribute(waitPanel.getElement(), "top", 42 + "%");
				}

				if (width != 0) {
					DOM.setStyleAttribute(waitPanel.getElement(), "left", ((width / 2) - 100) + "px");
				}
				else {
					DOM.setStyleAttribute(waitPanel.getElement(), "left", 35 + "%");
				}
			}
		}
		else if (!visible) {
			indexCharging--;

			if (isCharging && indexCharging == 0) {
				isCharging = false;

				contentPanel.remove(greyPanel);
				contentPanel.remove(waitPanel);
			}
		}
	}
	
	public void updateTitle(String title) {
		super.setTitle(title);
	}
	
	public boolean isConfirm() {
		return isConfirm;
	}

	protected abstract void onClickFinish();

	protected abstract void onNextClick();

	protected abstract void onBackClick();
}
