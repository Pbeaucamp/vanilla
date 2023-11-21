package bpm.gwt.commons.client.viewer.dialog;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.client.viewer.VanillaViewer;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.IRepositoryObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ViewerDialog extends AbstractDialogBox {

	private static ViewerDialogUiBinder uiBinder = GWT.create(ViewerDialogUiBinder.class);

	interface ViewerDialogUiBinder extends UiBinder<Widget, ViewerDialog> {
	}
	
	interface MyStyle extends CssResource {
		String max();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	SimplePanel panelContent;

	public ViewerDialog(IRepositoryObject item) {
		super(LabelsConstants.lblCnst.ReportViewer(), true, false);
		
		buildContent(item, null);
	}

	public ViewerDialog(IRepositoryObject item, Group selectedGroup) {
		super(LabelsConstants.lblCnst.ReportViewer(), true, false);
		
		buildContent(item, selectedGroup);
	}
	
	private void buildContent(IRepositoryObject item, Group selectedGroup) {
		setWidget(uiBinder.createAndBindUi(this));
	
		loadViewer(item, selectedGroup);
	}

	private void loadViewer(final IRepositoryObject item, final Group selectedGroup) {
		showWaitPart(true);
		LoginService.Connect.getInstance().getInfoUser(new GwtCallbackWrapper<InfoUser>(this, true) {

			@Override
			public void onSuccess(InfoUser infoUser) {
				VanillaViewer vanillaViewer = new VanillaViewer(null, selectedGroup != null ? selectedGroup : infoUser.getGroup(), infoUser.getAvailableGroups());
				vanillaViewer.openViewer(item, infoUser, false, true);
				
				panelContent.setWidget(vanillaViewer);
			}
		}.getAsyncCallback());
	}
	
	@Override
	public void maximize(boolean maximize) {
		if (maximize) {
			panelContent.addStyleName(style.max());
		}
		else {
			panelContent.removeStyleName(style.max());
		}
	}

}
