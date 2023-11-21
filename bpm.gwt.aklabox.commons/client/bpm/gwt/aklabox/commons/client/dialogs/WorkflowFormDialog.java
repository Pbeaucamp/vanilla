package bpm.gwt.aklabox.commons.client.dialogs;

import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.Form;
import bpm.gwt.aklabox.commons.client.utils.OrbeonHelper;

import com.gargoylesoftware.htmlunit.javascript.host.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

public class WorkflowFormDialog extends AbstractDialogBox {

	private static WorkflowFormDialogUiBinder uiBinder = GWT.create(WorkflowFormDialogUiBinder.class);

	interface WorkflowFormDialogUiBinder extends UiBinder<Widget, WorkflowFormDialog> {
	}
	
	@UiField
	Frame formFrame;

	public WorkflowFormDialog(Form form) {
		super("Visualiseur formulaire", true, false);
		setWidget(uiBinder.createAndBindUi(this));
		this.getDialog().onMaximiseClick(null);
		OrbeonHelper.openOrbeonForm(form, null, formFrame);
	}
	
	public WorkflowFormDialog(Documents doc) {
		super("Visualiseur formulaire", true, false);
		setWidget(uiBinder.createAndBindUi(this));
		this.getDialog().onMaximiseClick(null);
		OrbeonHelper.openOrbeonForm(doc, formFrame);
//		formFrame.addLoadHandler(new LoadHandler() {
//			
//			@Override
//			public void onLoad(LoadEvent event) {
//				try {
//					console("load");
//					Element velem = getValidateButton();
//					Event.sinkEvents(velem, Event.ONCLICK);
//					Event.setEventListener(velem, new EventListener() {
//
//					    @Override
//					    public void onBrowserEvent(Event event) {
//					       console("ok");
//					         if(Event.ONCLICK == event.getTypeInt()) {
//					        	 console("CLICK");
//					              hide();
//					         }
//
//					    }
//					});
//				} catch (Exception e) {
//					console(e.getMessage());
//					e.printStackTrace();
//				}
//			}
//		});
		
	}

	@Override
	public int getThemeColor() {
		return 5;
	}
	
	private final native Element getValidateButton() /*-{
	  return $doc.getElementById("iframe")[0].contentWindow.document.getElementsByClassName("fr-validate-button")[0].getElementsByClassName("btn")[0];
	}-*/;

	public static native void console(String text)
	/*-{
	   console.log(text);
	}-*/;
}
