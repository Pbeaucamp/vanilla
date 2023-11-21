package bpm.fd.api.core.model.components.definition.chart;

import java.awt.Color;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class ChartValueFormat {
	private Color backgroundColor;
	private Color fontColor;
	private boolean bold= false;
	private boolean italic= false;
	private boolean underline = false;
	private String font;
	private String size;
	/**
	 * @return the backgroundColor
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	/**
	 * @param backgroundColor the backgroundColor to set
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	/**
	 * @return the fontColor
	 */
	public Color getFontColor() {
		return fontColor;
	}
	/**
	 * @param fontColor the fontColor to set
	 */
	public void setFontColor(Color fontColor) {
		this.fontColor = fontColor;
	}
	/**
	 * @return the bold
	 */
	public boolean isBold() {
		return bold;
	}
	/**
	 * @param bold the bold to set
	 */
	public void setBold(boolean bold) {
		this.bold = bold;
	}
	/**
	 * @return the italic
	 */
	public boolean isItalic() {
		return italic;
	}
	/**
	 * @param italic the italic to set
	 */
	public void setItalic(boolean italic) {
		this.italic = italic;
	}
	/**
	 * @return the underline
	 */
	public boolean isUnderline() {
		return underline;
	}
	/**
	 * @param underline the underline to set
	 */
	public void setUnderline(boolean underline) {
		this.underline = underline;
	}
	/**
	 * @return the font
	 */
	public String getFont() {
		return font;
	}
	/**
	 * @param font the font to set
	 */
	public void setFont(String font) {
		this.font = font;
	}
	/**
	 * @return the size
	 */
	public String getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(String size) {
		this.size = size;
	}
	
	public Element getElement(){
		Element e = DocumentHelper.createElement("valueFormat");
		if (getBackgroundColor() != null){
			Element bckCol = e.addElement("backgroundColor");
			bckCol.addAttribute("red", getBackgroundColor().getRed() + "");
			bckCol.addAttribute("green", getBackgroundColor().getGreen() + "");
			bckCol.addAttribute("blue", getBackgroundColor().getBlue() + "");
		}
		if (getFont() != null){
			e.addElement("font").setText(getFont());
		}
		if (getFontColor() != null){
			Element bckCol = e.addElement("fontColor");
			bckCol.addAttribute("red", getFontColor().getRed() + "");
			bckCol.addAttribute("green", getFontColor().getGreen() + "");
			bckCol.addAttribute("blue", getFontColor().getBlue() + "");
		}
		if (getSize() != null){
			e.addElement("size").setText(getSize());
		}
		e.addElement("bold").setText(isBold() + "");
		e.addElement("italic").setText(isItalic()+ "");
		e.addElement("underline").setText(isUnderline()+ "");
		
		return e;
	}
}
