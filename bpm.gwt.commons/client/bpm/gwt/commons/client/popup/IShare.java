package bpm.gwt.commons.client.popup;

import bpm.gwt.commons.shared.InfoShare;
import bpm.gwt.commons.shared.utils.TypeShare;

public interface IShare {

	public void openShare(TypeShare typeShare);
	
	public void share(InfoShare infoShare);
}
