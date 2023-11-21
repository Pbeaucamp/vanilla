package bpm.gwt.commons.client;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.services.exception.SecurityException;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.client.utils.MessageHelper;

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
						bpm.gwt.commons.client.utils.ToolsGWT.doRedirect(GWT.getHostPageBaseURL());
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
					dialSessionExpired.addCloseHandler(new CloseHandler<PopupPanel>() {

						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							if (dialSessionExpired.isConfirm()) {
								final String url = GWT.getHostPageBaseURL();
								bpm.gwt.commons.client.utils.ToolsGWT.changeCurrURL(url);
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
		else if (caught instanceof ServiceException && ((ServiceException) caught).hasCode()) {
			
			String titleError = null;
			String messageError = null;

			StringBuilder buf = new StringBuilder();
			
			ServiceException odessaaEx = (ServiceException) caught;
			switch (odessaaEx.getCode()) {
//			case CODE_SESSION_EXPIRED:
//				sessionExpired = true;
//				this.dialSessionExpired = buildAndShowSessionExpiredDialog();
//				break;
			case CODE_UPLOAD_DOCUMENT:
				buf = new StringBuilder();
				buf.append(LabelsConstants.lblCnst.UneErreurEstSurvenueLorsDeLUploadDuDocumentMessageErreur() + "<br/>");
				if (message != null && !message.isEmpty()) {
					buf.append(message);
				}
				else {
					buf.append("(" + caught.getMessage() + ")");
				}

				titleError = LabelsConstants.lblCnst.ErreurUploadDeDocument();
				messageError = buf.toString();
				break;
			case CODE_UPLOAD_BAD_FORMAT:
				buf = new StringBuilder();
				buf.append(LabelsConstants.lblCnst.CeFormatNEstPasAutoriseePourCeContrat() + "<br/>");

				titleError = LabelsConstants.lblCnst.ErreurFormat();
				messageError = buf.toString();
				break;
			default:
				buildAndShowUnknownErrorDialog(caught.getMessage());
				break;
			}

			if (titleError != null && messageError != null) {
				MessageHelper.openErrorDialog(titleError, messageError, LabelsConstants.lblCnst.Close(), null, false, 350);
			}
		}
		else {
			buildAndShowUnknownErrorDialog(caught.getMessage());
		}
	}

	private void buildAndShowUnknownErrorDialog(String exceptionMessage) {
		StringBuilder buf = new StringBuilder();
		buf.append(LabelsConstants.lblCnst.ErreurInconnuMessageVoirLeMessageSuivant() + "<br/>");
		buf.append("(" + exceptionMessage + ")");

		MessageHelper.openErrorDialog(LabelsConstants.lblCnst.ErreurInconnue(), buf.toString(), LabelsConstants.lblCnst.Close(), null, false, 350);
	}
}
