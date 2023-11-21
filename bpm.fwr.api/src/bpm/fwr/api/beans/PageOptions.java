package bpm.fwr.api.beans;

import java.io.Serializable;

/**
 * 
 * For a wysiwyg Report, I use the text has the left text and the textRight as the right text
 * Same for boolean with page number
 * 
 * @author patrickbeaucamp
 *
 */
public class PageOptions implements Serializable {

	private String text;
	private String textRight;

	private boolean number;
	private boolean numberRight;
	
	public PageOptions(){
		super();
	}
	
	public PageOptions(String t, boolean b){
		this.text = t;
		this.number = b;
	}

	public boolean isNumber() {
		return number;
	}
	
	public void setNumber(String number) {
		if (number.equalsIgnoreCase("true")) {
			this.number = true;
		}
		else {
			this.number = false;
		}
	}

	public void setNumber(boolean number) {
		this.number = number;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setNumberRight(boolean numberRight) {
		this.numberRight = numberRight;
	}

	public boolean isNumberRight() {
		return numberRight;
	}
	
	public void setNumberRight(String numberRight) {
		if (numberRight.equalsIgnoreCase("true")) {
			this.numberRight = true;
		}
		else {
			this.numberRight = false;
		}
	}

	public void setTextRight(String textRight) {
		this.textRight = textRight;
	}

	public String getTextRight() {
		return textRight;
	}
}
