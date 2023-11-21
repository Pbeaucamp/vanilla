package bpm.faweb.client.services;

import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.panels.center.HasFilter;
import bpm.faweb.shared.infoscube.InfosReport;
import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.utils.MessageHelper;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CubeServices {
	
	public static void filter(final List<String> filters, final MainPanel parent, final HasFilter viewerParent) {
		parent.showWaitPart(true);
		FaWebService.Connect.getInstance().filterService(parent.getKeySession(), filters, new AsyncCallback<InfosReport>() {
			
			@Override
			public void onSuccess (InfosReport result){
				if (result != null) {
					for(String f : filters) {
						viewerParent.addFilterItem(f);
					}
					parent.setGridFromRCP(result);
					
				}
				else {
					MessageHelper.openMessageDialog(FreeAnalysisWeb.LBL.Error(), "filter service failed");
				}
				
				parent.showWaitPart(false);
			}

			@Override
			public void onFailure (Throwable ex){
				ex.printStackTrace();
				
				ExceptionManager.getInstance().handleException(ex, "Error while adding filter.");

				parent.showWaitPart(false);
			}
		});	
	}
	
	public static void filter(String uname, final MainPanel parent) {
		parent.showWaitPart(true);
		FaWebService.Connect.getInstance().filterService(parent.getKeySession(), uname, new AsyncCallback<InfosReport>() {
			
			@Override
			public void onSuccess (InfosReport result){
				if (result != null) {
					parent.setGridFromRCP(result);
				}
				else {
					MessageHelper.openMessageDialog(FreeAnalysisWeb.LBL.Error(), "filter service failed");
				}
				parent.showWaitPart(false);
			}

			@Override
			public void onFailure (Throwable ex){
				ex.printStackTrace();

				parent.showWaitPart(false);
			}
		});	
	}

	public static void removefilter(String uname, final MainPanel parent) {
		parent.showWaitPart(true);
		FaWebService.Connect.getInstance().removefilterService(parent.getKeySession(), uname, new AsyncCallback<InfosReport>() {

			@Override
			public void onSuccess (InfosReport result){
				if (result != null) {
					parent.setGridFromRCP(result);
				}
				else {
					MessageHelper.openMessageDialog(FreeAnalysisWeb.LBL.Error(), "removefilter service failed");
				}
				parent.showWaitPart(false);
			}

			@Override
			public void onFailure (Throwable ex){
				ex.printStackTrace();

				parent.showWaitPart(false);
			}
		});	
	}

	public static void add(List<String> items, boolean col, final MainPanel parent) {
		parent.showWaitPart(true);
		FaWebService.Connect.getInstance().addService(parent.getKeySession(), items, col, new AsyncCallback<InfosReport>() {
			
			@Override
			public void onSuccess (InfosReport result){
				if (result != null) {
					parent.setGridFromRCP(result);
					parent.getNavigationPanel().clearItemSelected();
				}
				else {
					parent.getNavigationPanel().clearItemSelected();
					
					MessageHelper.openMessageDialog(FreeAnalysisWeb.LBL.Error(), "add service failed");
				}
				parent.showWaitPart(false);
			}

			@Override
			public void onFailure (Throwable ex){
				ex.printStackTrace();
				
				parent.getNavigationPanel().clearItemSelected();
				parent.showWaitPart(false);
			}
		});
		parent.getNavigationPanel().clearItemSelected();
	}

	public static void remove(List<String> items, final MainPanel parent) {
		boolean removeOk = true;
		
		if(removeOk) {
			parent.showWaitPart(true);
			FaWebService.Connect.getInstance().removeService(parent.getKeySession(), items, new AsyncCallback<InfosReport>() {

				@Override
				public void onSuccess (InfosReport result){
					if (result != null) {
						if(result.getFailedUnames() != null && result.getFailedUnames().size() > 0) {
							String elements = "";
							for(String s : result.getFailedUnames()) {
								elements += "\n" + s;
							}
							Window.alert(FreeAnalysisWeb.LBL.ElementCannotBeRemove() + " : " + elements);
						}
						parent.setGridFromRCP(result);
					}
					else {
						MessageHelper.openMessageDialog(FreeAnalysisWeb.LBL.Error(), "remove service failed");
						parent.showWaitPart(false);
					}	
				}

				@Override
				public void onFailure (Throwable ex){
					ex.printStackTrace();

					parent.showWaitPart(false);
				}
			});
		}
		
		else {
			MessageHelper.openMessageDialog(FreeAnalysisWeb.LBL.Error(), FreeAnalysisWeb.LBL.DelError());
		}
		
	}

	public static void removeLvl(int row, int cell, final MainPanel parent) {
		parent.showWaitPart(true);
		FaWebService.Connect.getInstance().removeService(parent.getKeySession(), row, cell, new AsyncCallback<InfosReport>() {

			@Override
			public void onSuccess (InfosReport infosReport){
				if (infosReport != null) {
					parent.setGridFromRCP(infosReport);
				}
				else {
					MessageHelper.openMessageDialog(FreeAnalysisWeb.LBL.Error(), "removelvl service failed");
				}
				parent.showWaitPart(false);
			}
			
			@Override
			public void onFailure (Throwable ex){
				ex.printStackTrace();
				parent.showWaitPart(false);
			}
		});			
	}
	
	
}
