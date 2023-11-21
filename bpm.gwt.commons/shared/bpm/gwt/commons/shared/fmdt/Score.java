package bpm.gwt.commons.shared.fmdt;

import java.io.Serializable;


public class Score implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String value;
	private String count;

	public Score() {
	}

	public Score(String value, String count) {
		super();
		this.value = value;
		this.count = count;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}
}
