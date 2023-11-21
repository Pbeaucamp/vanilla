package bpm.android.vanilla.core.beans.report;

public class DashboardRunItem implements IRunItem {

	private static final long serialVersionUID = 1L;
	
	private int itemId;
	private String outputFormat;
	private String dashboardUrl;
	
	public DashboardRunItem() { }
	
	public DashboardRunItem(int itemId, String outputFormat, String dashboardUrl) {
		this.itemId = itemId;
		this.outputFormat = outputFormat;
		this.dashboardUrl = dashboardUrl;
	}
	
	@Override
	public int getItemId() {
		return itemId;
	}
	
	public String getOutputFormat() {
		return outputFormat;
	}
	
	public String getHtml() {
		return dashboardUrl;
	}
}
