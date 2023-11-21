package bpm.fwr.client.utils;

public class GroupComboParameter {
	
	private String paramName;
	
	private CustomListBoxWithWait actualBox;
	private CustomListBoxWithWait childBox;
	
	private boolean isLast = false;
	
	public GroupComboParameter(String paramName, 
			CustomListBoxWithWait actualBox, CustomListBoxWithWait childBox, boolean isLast) {

		this.paramName = paramName;
		this.actualBox = actualBox;
		this.childBox = childBox;
		this.isLast = isLast;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public CustomListBoxWithWait getActualBox() {
		return actualBox;
	}

	public void setActualBox(CustomListBoxWithWait actualBox) {
		this.actualBox = actualBox;
	}

	public CustomListBoxWithWait getChildBox() {
		return childBox;
	}

	public void setChildBox(CustomListBoxWithWait childBox) {
		this.childBox = childBox;
	}

	public boolean isLast() {
		return isLast;
	}

	public void setLast(boolean isLast) {
		this.isLast = isLast;
	}
}
