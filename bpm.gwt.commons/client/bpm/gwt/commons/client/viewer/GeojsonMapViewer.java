package bpm.gwt.commons.client.viewer;

import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GeojsonMapViewer extends Viewer {

	public GeojsonMapViewer(VanillaViewer vanillaViewer, PortailRepositoryItem portailItem, Group selectedGroup, List<Group> availableGroups) {
		super(vanillaViewer);
		
		vanillaViewer.launchReport(this, portailItem, selectedGroup, false, false);
	}

	@Override
	public void runItem(LaunchReportInformations itemInformations) {
		
		
		ReportingService.Connect.getInstance().getForwardUrlForDataPreparation(itemInformations.getItem().getId(), new AsyncCallback<String>() {
			public void onSuccess(String arg0) {
				reportFrame.setUrl(arg0);
			}
			
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.FailedGetUrl());
			}
		});
		
//		ReportingService.Connect.getInstance().getGeojsonFromItem(itemInformations.getItem(), new GwtCallbackWrapper<String>(this, true) {
//			@Override
//			public void onSuccess(String result) {
//				
//				for(int i =0; i < ((HTMLPanel)reportPanel).getWidgetCount() ; i++) {
//					if(((HTMLPanel)reportPanel).getWidget(i) instanceof Frame) {
//						((HTMLPanel)reportPanel).remove(i);
//						break;
//					}
//				}
//			
//				
//				HTMLPanel popup = new HTMLPanel("");
//				popup.getElement().setId("popup");
//				HTMLPanel popupContent = new HTMLPanel("");
//				popupContent.getElement().setId("popup-content");
//				HTMLPanel popupCloser = new HTMLPanel("");
//				popupCloser.getElement().setId("popup-closer");
//				Anchor detail = new Anchor("Fiche détaillé");
//				detail.getElement().setId("detail");
//				
//				HTMLPanel pagination = new HTMLPanel("");
//				pagination.getElement().setId("pagination");
//				HTMLPanel previous = new HTMLPanel("");
//				previous.getElement().setId("previous");
//				HTMLPanel pager = new HTMLPanel("");
//				pager.getElement().setId("pager");
//				HTMLPanel next = new HTMLPanel("");
//				next.getElement().setId("next");
//			
//				pagination.add(previous);
//				pagination.add(pager);
//				pagination.add(next);
//				
//				popup.getElement().setClassName("ol-popup");
//				popupCloser.getElement().setClassName("ol-popup-closer");
//				
//				((HTMLPanel)reportPanel.getParent()).add(popup);
//				popup.add(popupContent);
//				popup.add(detail);
//				popup.add(popupCloser);
//				popup.add(pagination);
//				
//				int i = new Object().hashCode();
//				
//				reportPanel.getElement().setId("map" + i);
//				JsArrayMixed bigArray= JavaScriptObject.createArray().cast();
//				JsArrayMixed row= JavaScriptObject.createArray().cast();
//				bigArray.push(row);
//				renderDataPrepMap("map" + i, result, null, bigArray);
//			}
//		}.getAsyncCallback());
		
	}

	@Override
	public void defineToolbar(LaunchReportInformations itemInfo) {
		// TODO Auto-generated method stub
		
	}
	
	private final native void renderDataPrepMap(String div, String obj, String url, JsArrayMixed bigArray) /*-{		
		$wnd.renderGeoJsonMap(div, obj, url, bigArray);
	}-*/;
	
}
