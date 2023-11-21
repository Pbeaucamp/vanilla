package bpm.gwt.commons.client.utils;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.shared.utils.ExportResult;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.PopupPanel;

public class MessageHelper {
	
	public static void openMessageError(String message, Exception caught){
		InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(), 
				LabelsConstants.lblCnst.ErrorDetails(), message, caught);
		dial.center();
	}
	
	public static void openMessageError(String message, Throwable caught){
		InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(), 
				LabelsConstants.lblCnst.ErrorDetails(), message, caught);
		dial.center();
	}
	
	public static void openMessageDialog(String title, String msg) { 
		InformationsDialog dial = new InformationsDialog(title, LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), msg, false);
		dial.center();
	}

	public static void openMessageErrorWithRedirect(final String title, final String msg, final String redirectString) { 
		InformationsDialog dial = new InformationsDialog(title, LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), msg, false);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
            	ToolsGWT.changeCurrURL(redirectString);
			}
		});
		dial.center();
	}
	
	public static void openMessageMailResult(String title, ExportResult result) {
		StringBuilder buf = new StringBuilder();

		if (result.hasErrors() || result.hasWarnings()) {
			if (result.getNbMailSuccess() > 0) {
				buf.append(LabelsConstants.lblCnst.NumberSuccessSendMail() + " : " + result.getNbMailSuccess() + "<br/>");
			}
			else {
				buf.append(LabelsConstants.lblCnst.FailedSendMail() + "<br/>");
			}

			if (result.hasWarnings()) {
				buf.append(LabelsConstants.lblCnst.Warning() + " : " + result.getNbMailWarning() + "<br/>");
				for (String warning : result.getWarnings()) {
					buf.append(" - " + warning + "<br/>");
				}
				buf.append("<br/>");
			}

			if (result.hasErrors()) {
				buf.append(LabelsConstants.lblCnst.Error() + " : " + result.getNbMailError() + "<br/>");
				for (String error : result.getErrors()) {
					buf.append(" - " + error + "<br/>");
				}
				buf.append("<br/>");
			}
		}
		else {
			buf.append(LabelsConstants.lblCnst.SuccessSendMail() + " : " + result.getNbMailSuccess());
		}
		SafeHtml html = SafeHtmlUtils.fromTrustedString(buf.toString());
		InformationsDialog dial = new InformationsDialog(title, LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), html, false);
		dial.center();
	}
	
	
	//
	/**
	 * Create and show an error dialog to display a message
	 * 
	 * @param title
	 * @param message
	 * @param lblConfirm
	 * @param lblCancel
	 * @param needConfirm
	 *            (Set to true if you need a confirm and cancel button. Add
	 *            OnCloseHandler to check which button has been selected.)
	 * @param width
	 *            (Leave null if you don't want to define a width (Default is
	 *            550px))
	 * @return the dialog created
	 */
	public static InformationsDialog openErrorDialog(String title, String message, String lblConfirm, String lblCancel, boolean needConfirm, Integer width) {
		InformationsDialog dial = new InformationsDialog(title, lblConfirm, lblCancel, message, needConfirm);
		if (width != null) {
			dial.setWidth(width);
		}
		dial.center();
		return dial;
	}
	
	/**
	 * Use for debugging purpose, do not use in production
	 * 
	 * @param message
	 */
	//TODO: 00 - Supprimer tout les references
	public static void debugTrace(String message) {
		InformationsDialog dial = new InformationsDialog("", LabelsConstants.lblCnst.Close(), LabelsConstants.lblCnst.Close(), message, false);
		dial.center();
	}
}
