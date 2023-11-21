package bpm.gwt.commons.client.custom.v2;

/**
 * Use to manage a checkbox  cell with two state (enable and checked)
 */
public class CheckState {

	private boolean enable;
	private boolean check;

	public CheckState(boolean enable, boolean check) {
		this.enable = enable;
		this.check = check;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}
}