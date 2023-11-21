package bpm.gwt.aklabox.commons.client.utils;

import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.customs.InformationsDialog;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;


public class MessageHelper {
	
	public static void openMessageError(String message, Exception caught){
		InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(), 
				LabelsConstants.lblCnst.ErrorDetails(), message, caught);
		dial.setGlassEnabled(true);
		dial.center();
	}
	
	public static void openMessageError(String message, Throwable caught){
		InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(), 
				LabelsConstants.lblCnst.ErrorDetails(), message, caught);
		dial.setGlassEnabled(true);
		dial.show();
		
		int left = (Window.getClientWidth() - dial.getDialog().getOffsetWidth()) / 2 - 50;
		int top = (Window.getClientHeight() - dial.getDialog().getOffsetHeight()) / 2 - 20;
		dial.setPopupPosition(left, top);
	}
	
	public static void openMessageDialog(String title, String msg) { 
		InformationsDialog dial = new InformationsDialog(title, LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), msg, false);
		dial.setGlassEnabled(true);
		dial.show();
		
		int left = (Window.getClientWidth() - dial.getDialog().getOffsetWidth()) / 2 - 50;
		int top = (Window.getClientHeight() - dial.getDialog().getOffsetHeight()) / 2 - 20;
		dial.setPopupPosition(left, top); 
		
		
	}

	public static void openMessageErrorWithRedirect(final String title, final String msg, final String redirectString) { 
		InformationsDialog dial = new InformationsDialog(title, LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), msg, false);
		dial.setGlassEnabled(true);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
            	ToolsGWT.changeCurrURL(redirectString);
			}
		});
		dial.show();
		
		int left = (Window.getClientWidth() - dial.getDialog().getOffsetWidth()) / 2 - 50;
		int top = (Window.getClientHeight() - dial.getDialog().getOffsetHeight()) / 2 - 20;
		dial.setPopupPosition(left, top);
	}
}
