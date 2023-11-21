package bpm.gwt.commons.client.viewer;

import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;

public interface IReportViewer extends IWait {

	public void expendParam();
	
	public void expendComment();
	
	public void runItem(LaunchReportInformations itemInfo);
}
