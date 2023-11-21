package bpm.gwt.aklabox.commons.client.customs;

import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.services.exceptions.SecurityException;
import bpm.gwt.aklabox.commons.client.utils.MessageHelper;
import bpm.gwt.aklabox.commons.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;

public class ExceptionManager {
	
	private static ExceptionManager instance;

	// Static boolean in case of session expired
	private InformationsDialog dialSessionExpired;
	private boolean sessionExpired = false;
	
	public static ExceptionManager getInstance() {
		if(instance == null) {
			instance = new ExceptionManager();
		}
		return instance;
	}

	public void handleException(Throwable caught, String message) {
		if (sessionExpired) {
			if (dialSessionExpired == null) {
				dialSessionExpired = new InformationsDialog(LabelsConstants.lblCnst.SessionExpiredTitle(), LabelsConstants.lblCnst.HomePage(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.SessionExpiredMessage(), true);
				dialSessionExpired.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						ToolsGWT.doRedirect(GWT.getHostPageBaseURL());
					}
				});
				dialSessionExpired.center();
			}
			else if (!dialSessionExpired.isShowing()) {
				dialSessionExpired.center();
			}
		}
		else if (caught instanceof SecurityException) {
			SecurityException secEx = (SecurityException) caught;
			if (secEx.getErrorType() == SecurityException.ERROR_TYPE_SESSION_EXPIRED) {
				sessionExpired = true;

				if (dialSessionExpired == null) {
					dialSessionExpired = new InformationsDialog(LabelsConstants.lblCnst.SessionExpiredTitle(), LabelsConstants.lblCnst.HomePage(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.SessionExpiredMessage(), true);
					dialSessionExpired.setGlassEnabled(true);
					dialSessionExpired.addCloseHandler(new CloseHandler<PopupPanel>() {

						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							if (dialSessionExpired.isConfirm()) {
								final String url = GWT.getHostPageBaseURL();
								ToolsGWT.changeCurrURL(url);
							}
						}
					});
					dialSessionExpired.center();
				}
				else if (!dialSessionExpired.isShowing()) {
					dialSessionExpired.center();
				}
			}
		}
		else {
			MessageHelper.openMessageError(message, caught);
		}
	}
}
