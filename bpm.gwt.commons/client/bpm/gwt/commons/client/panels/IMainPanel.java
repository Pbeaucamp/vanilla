package bpm.gwt.commons.client.panels;

import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.popup.IChangeView;
import bpm.gwt.commons.shared.InfoUser;

public interface IMainPanel extends IChangeView, IWait {

	public void showAbout();
	
	public void setInfoUser(InfoUser infoUser);
}
