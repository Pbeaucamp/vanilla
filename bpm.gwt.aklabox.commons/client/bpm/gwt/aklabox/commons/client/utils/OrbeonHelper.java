package bpm.gwt.aklabox.commons.client.utils;

import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.Form;
import bpm.gwt.aklabox.commons.client.dialogs.WorkflowFormDialog;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.services.GwtCallbackWrapper;

import com.google.gwt.user.client.ui.Frame;

public class OrbeonHelper {
	
	private final static String ORBEON_SERVLET = "/AklaBox/orbeonServlet";
//	private final static String ORBEON_SERVLET = "/orbeonServlet";

	public static void openOrbeonForm(Form form, Documents doc, final Frame frame) {
//		AklaCommonService.Connect.getService().getOrbeonInstance(form, doc, new GwtCallbackWrapper<OrbeonFormInstance>(null, false,false) {
//			@Override
//			public void onSuccess(OrbeonFormInstance result) {
				StringBuilder buf = new StringBuilder(ORBEON_SERVLET);
//				buf.append("?action=" + result != null ? "show" : "new");
				buf.append("?action=new");
				buf.append("&app=");
				buf.append(form.getOrbeonApp());
				buf.append("&name=");
				buf.append(form.getOrbeonName());
//				if(result != null) {
//					buf.append("&instance=");
//					buf.append(result.getOrbeonInstanceId());
//				}
				
				//TODO security on section
				
				frame.setUrl(buf.toString());
				
//			}
//		}.getAsyncCallback());
	}
	
	public static void openOrbeonForm(Documents doc, final Frame frame) {
		AklaCommonService.Connect.getService().getOrbeonUrl(doc, new GwtCallbackWrapper<String>(null,false,false) {
			@Override
			public void onSuccess(String result) {
				frame.setUrl(result);
			}
		}.getAsyncCallback());
	}

	public static void openOrbeonForm(Documents doc) {
		WorkflowFormDialog dial = new WorkflowFormDialog(doc);
		dial.center();
	}
	
}
