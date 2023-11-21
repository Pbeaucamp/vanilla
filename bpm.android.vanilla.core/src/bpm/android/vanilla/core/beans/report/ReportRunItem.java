package bpm.android.vanilla.core.beans.report;

public class ReportRunItem implements IRunItem {

	private static final long serialVersionUID = 1L;
	
	private int itemId;
	private String outputFormat;
	private String html;
	
	public ReportRunItem() { }
	
	public ReportRunItem(int itemId, String outputFormat, String html) {
		this.itemId = itemId;
		this.outputFormat = outputFormat;
		this.html = html;
	}
	
	@Override
	public int getItemId() {
		return itemId;
	}
	
	public String getOutputFormat() {
		return outputFormat;
	}
	
	public String getHtml() {
		return html;
	}
}
