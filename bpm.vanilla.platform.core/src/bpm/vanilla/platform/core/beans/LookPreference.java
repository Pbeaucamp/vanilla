package bpm.vanilla.platform.core.beans;


public class LookPreference {
	
	private int id;
	private boolean rightClic;
	private boolean toolBar;
	private int userId;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean isRightClic() {
		return rightClic;
	}
	public void setRightClic(boolean rightClic) {
		this.rightClic = rightClic;
	}
	public boolean isToolBar() {
		return toolBar;
	}
	public void setToolBar(boolean toolBar) {
		this.toolBar = toolBar;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public String getXml(){
		StringBuffer buf = new StringBuffer();
		
		buf.append("    <toolbar>\n");
		buf.append("    	<id>" + id + "</id>");
		buf.append("    	<rightClic>" + rightClic + "</rightClic>");
		buf.append("    	<toolBar>" + toolBar + "</toolBar>");
		buf.append("    	<userId>" + userId + "</userId>");
		buf.append("    </toolbar>\n");
		
		return buf.toString();
	}
	

}
