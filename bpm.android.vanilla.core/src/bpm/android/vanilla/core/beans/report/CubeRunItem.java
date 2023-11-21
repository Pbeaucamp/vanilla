package bpm.android.vanilla.core.beans.report;

import bpm.android.vanilla.core.beans.AndroidCube;

public class CubeRunItem implements IRunItem {

	private static final long serialVersionUID = 1L;
	
	private String outputFormat;
	private String html;
	
	private AndroidCube cube;
	
	public CubeRunItem() { }
	
	public CubeRunItem(String outputFormat, String html, AndroidCube cube) {
		this.outputFormat = outputFormat;
		this.html = html;
		this.cube = cube;
	}
	
	@Override
	public int getItemId() {
		return cube.getId();
	}
	
	public String getOutputFormat() {
		return outputFormat;
	}
	
	public void setHtml(String html) {
		this.html = html;
	}
	
	public String getHtml() {
		return html;
	}
	
	public AndroidCube getCube() {
		return cube;
	}
}
