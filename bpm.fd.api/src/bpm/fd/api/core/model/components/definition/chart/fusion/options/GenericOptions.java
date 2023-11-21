package bpm.fd.api.core.model.components.definition.chart.fusion.options;

import java.awt.Color;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.components.definition.chart.ChartNature;

public class GenericOptions implements IComponentOptions {
	private static final int KEY_CAPTION = 0;
	private static final int KEY_SUB_CAPTION = 1;
	private static final int KEY_SHOW_LABELS = 0;
	private static final int KEY_SHOW_VALUES = 1;
	private static final int KEY_BACKGROUND_COLOR = 2;
	private static final int KEY_SHOW_BORDER = 3;
	private static final int KEY_BORDER_COLOR = 4;
	private static final int KEY_BORDER_THICKNESS = 5;
	private static final int KEY_BG_ALPHA = 6;
	private static final int KEY_BG_SWF_ALPHA = 7;
	private static final int MULTILINELABEL = 8;
	private static final int LABELCOLOR = 9;
	private static final int BASEFONTSIZE = 10;
	private static final int DYNAMICLEGEND = 11;
	private static final int EXPORT = 12;

	private static final String[] i18nKeys = new String[] { "caption", "subCaption" };
	private static final String[] standardKeys = new String[] { "showLabel", "showValues", "bgColor", "showBorder", "borderColor", "borderThickness", "bgAlpha", "bgSWFAlpha", "multiLineLabels", "baseFontColor", "baseFontSize", "setAdaptiveYMin", "exportEnabled" };

	private int bgAlpha = 0;
	private int bgSWFAlpha = 0;

	private String caption = "";
	private String subCaption = "";
	private boolean showLabel = true;
	private boolean showValues = true;
	//Bar
	private boolean separationBar = true ;
	
	//Histpgramme
	private boolean density = true;
	private int bins=30;
	
	
	private Color bgColor = null;

	private boolean showBorder = true;
	private Color borderColor;
	private int borderThickness = 1;

	private boolean multiLineLabels = false;

	private Color baseFontColor = new Color(0, 0, 0);
	private int baseFontSize = 10;

	private boolean dynamicLegend = false;
	private int labelSize = 0;

	private boolean exportEnable = false;

	public int getBgAlpha() {
		return bgAlpha;
	}

	public void setBgAlpha(int bgAlpha) {
		this.bgAlpha = bgAlpha;
	}

	public int getBgSWFAlpha() {
		return bgSWFAlpha;
	}

	public void setBgSWFAlpha(int bgSWFAlpha) {
		this.bgSWFAlpha = bgSWFAlpha;
	}

	/**
	 * @return the showBorder
	 */
	public boolean isShowBorder() {
		return showBorder;
	}

	/**
	 * @param showBorder
	 *            the showBorder to set
	 */
	public void setShowBorder(boolean showBorder) {
		this.showBorder = showBorder;
	}

	/**
	 * @return the borderColor
	 */
	public Color getBorderColor() {
		return borderColor;
	}

	/**
	 * @param borderColor
	 *            the borderColor to set
	 */
	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	/**
	 * @return the borderThickness
	 */
	public int getBorderThickness() {
		return borderThickness;
	}

	/**
	 * @param borderThickness
	 *            the borderThickness to set
	 */
	public void setBorderThickness(int borderThickness) {
		this.borderThickness = borderThickness;
	}

	/**
	 * @return the backgroundColor
	 */
	public Color getBgColor() {
		return bgColor;
	}

	/**
	 * @param backgroundColor
	 *            the backgroundColor to set
	 */
	public void setBgColor(Color backgroundColor) {
		this.bgColor = backgroundColor;
	}

	/**
	 * @return the caption
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * @param caption
	 *            the caption to set
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * @return the subCaption
	 */
	public String getSubCaption() {
		return subCaption;
	}

	/**
	 * @param subCaption
	 *            the subCaption to set
	 */
	public void setSubCaption(String subCaption) {
		this.subCaption = subCaption;
	}

	/**
	 * @return the showLabel
	 */
	public boolean isShowLabel() {
		return showLabel;
	}

	/**
	 * @param showLabel
	 *            the showLabel to set
	 */
	public void setShowLabel(boolean showLabel) {
		this.showLabel = showLabel;
	}

	/**
	 * @return the showValues
	 */
	public boolean isShowValues() {
		return showValues;
	}

	/**
	 * @param showValues
	 *            the showValues to set
	 */
	public void setShowValues(boolean showValues) {
		this.showValues = showValues;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("genericOptions");

		e.addAttribute("caption", getCaption());
		e.addAttribute("subCaption", getSubCaption());
		e.addAttribute("showValues", isShowValues() + "");
		e.addAttribute("showLabels", isShowLabel() + "");
		e.addAttribute("baseFontSize", baseFontSize + "");

		if (getBgColor() != null) {
			Element bckCol = e.addElement("backgroundColor");
			bckCol.addAttribute("red", getBgColor().getRed() + "");
			bckCol.addAttribute("green", getBgColor().getGreen() + "");
			bckCol.addAttribute("blue", getBgColor().getBlue() + "");
		}
		if (getBaseFontColor() != null) {
			Element bckCol = e.addElement("baseFontColor");
			bckCol.addAttribute("red", getBaseFontColor().getRed() + "");
			bckCol.addAttribute("green", getBaseFontColor().getGreen() + "");
			bckCol.addAttribute("blue", getBaseFontColor().getBlue() + "");
		}

		e.addAttribute("DynamicLegend", dynamicLegend + "");
		e.addAttribute("exportEnable", exportEnable + "");
		e.addAttribute("LabelSize", labelSize + "");

		e.addAttribute("multiLineLabels", multiLineLabels + "");

		return e;
	}

	public IComponentOptions getAdapter(Object type) {
		if (type instanceof ChartNature) {
			ChartNature nature = (ChartNature) type;
			try {
				if (nature == ChartNature.getNature(ChartNature.PIE) || nature == ChartNature.getNature(ChartNature.PIE_3D)) {
					return new PieGenericOptions(this);
				}
			} catch (Exception e) {
				// TODO
			}

		}
		return this;
	}

	public String getDefaultLabelValue(String key) {
		if (i18nKeys[KEY_CAPTION].equals(key)) {
			return getCaption();
		}
		else if (i18nKeys[KEY_SUB_CAPTION].equals(key)) {
			return getSubCaption();
		}
		return null;
	}

	public String[] getInternationalizationKeys() {
		return i18nKeys;
	}

	public String[] getNonInternationalizationKeys() {
		return standardKeys;
	}

	public String getValue(String key) {
		if (standardKeys[KEY_SHOW_LABELS].equals(key)) {
			return isShowLabel() ? "1" : "0";
		}
		else if (standardKeys[KEY_SHOW_VALUES].equals(key)) {
			return isShowValues() ? "1" : "0";
		}
		else if (standardKeys[KEY_BACKGROUND_COLOR].equals(key)) {
			if (getBgColor() == null) {
				return "FFFFFF";
			}

			String r = Integer.toHexString(bgColor.getRed());
			if (r.length() == 1) {
				r = "0" + r;
			}
			String g = Integer.toHexString(bgColor.getGreen());
			if (g.length() == 1) {
				g = "0" + g;
			}
			String b = Integer.toHexString(bgColor.getBlue());
			if (b.length() == 1) {
				b = "0" + b;
			}
			return r + g + b;

		}
		else if (standardKeys[KEY_BORDER_COLOR].equals(key)) {
			if (getBorderColor() == null) {
				return "FFFFFF";
			}

			String r = Integer.toHexString(borderColor.getRed());
			if (r.length() == 1) {
				r = "0" + r;
			}
			String g = Integer.toHexString(borderColor.getGreen());
			if (g.length() == 1) {
				g = "0" + g;
			}
			String b = Integer.toHexString(borderColor.getBlue());
			if (b.length() == 1) {
				b = "0" + b;
			}

			return r + g + b;

		}
		else if (standardKeys[KEY_BORDER_THICKNESS].equals(key)) {
			return getBorderThickness() + "";
		}
		else if (standardKeys[KEY_SHOW_BORDER].equals(key)) {
			return isShowBorder() ? "1" : "0";
		}
		else if (standardKeys[KEY_BG_ALPHA].equals(key)) {
			return getBgAlpha() + "";
		}
		else if (standardKeys[KEY_BG_SWF_ALPHA].equals(key)) {
			return getBgSWFAlpha() + "";
		}

		else if (standardKeys[MULTILINELABEL].equals(key)) {
			return multiLineLabels + "";
		}
		else if (standardKeys[LABELCOLOR].equals(key)) {

			if (getBaseFontColor() == null) {
				return "FFFFFF";
			}

			String r = Integer.toHexString(baseFontColor.getRed());
			if (r.length() == 1) {
				r = "0" + r;
			}
			String g = Integer.toHexString(baseFontColor.getGreen());
			if (g.length() == 1) {
				g = "0" + g;
			}
			String b = Integer.toHexString(baseFontColor.getBlue());
			if (b.length() == 1) {
				b = "0" + b;
			}

			return r + g + b;

			// if(getBaseFontColor() != null) {
			// return Integer.toHexString(getBaseFontColor().getRGB());
			// // return getBaseFontColor().getRed() + "" +
			// getBaseFontColor().getGreen() + "" +
			// getBaseFontColor().getBlue();
			// }
			// return "";
		}
		else if (standardKeys[BASEFONTSIZE].equals(key)) {
			return baseFontSize + "";
		}
		else if (i18nKeys[KEY_CAPTION].equals(key)) {
			return getCaption();
		}
		else if (i18nKeys[KEY_SUB_CAPTION].equals(key)) {
			return getSubCaption();
		}

		else if (standardKeys[DYNAMICLEGEND].equals(key)) {
			return isDynamicLegend() ? "1" : "0";
		}

		else if (standardKeys[EXPORT].equals(key)) {
			return isExportEnable() ? "1" : "0";
		}

		return null;
	}

	public void setMultiLineLabels(boolean multiLineLabels) {
		this.multiLineLabels = multiLineLabels;
	}

	public boolean isMultiLineLabels() {
		return multiLineLabels;
	}

	public void setBaseFontColor(Color labelColor) {
		this.baseFontColor = labelColor;
	}

	public Color getBaseFontColor() {
		return baseFontColor;
	}

	public void setBaseFontSize(int baseFontSize) {
		this.baseFontSize = baseFontSize;
	}

	public int getBaseFontSize() {
		return baseFontSize;
	}

	public void setDynamicLegend(boolean dynamicLegend) {
		this.dynamicLegend = dynamicLegend;
	}

	public boolean isDynamicLegend() {
		return dynamicLegend;
	}

	public void setLabelSize(int labelSize) {
		this.labelSize = labelSize;
	}

	public int getLabelSize() {
		return labelSize;
	}

	@Override
	public IComponentOptions copy() {
		GenericOptions copy = new GenericOptions();

		copy.setBaseFontColor(baseFontColor);
		copy.setBaseFontSize(baseFontSize);
		copy.setBgAlpha(bgAlpha);
		copy.setBgColor(bgColor);
		copy.setBgSWFAlpha(bgSWFAlpha);
		copy.setBorderColor(borderColor);
		copy.setBorderThickness(borderThickness);
		copy.setCaption(caption);
		copy.setDynamicLegend(dynamicLegend);
		copy.setLabelSize(labelSize);
		copy.setMultiLineLabels(multiLineLabels);
		copy.setShowBorder(showBorder);
		copy.setShowLabel(showLabel);
		copy.setShowValues(showValues);
		copy.setSubCaption(subCaption);
		copy.setExportEnable(exportEnable);

		return copy;
	}

	public boolean isExportEnable() {
		return exportEnable;
	}

	public void setExportEnable(boolean exportEnable) {
		this.exportEnable = exportEnable;
	}

	public boolean isSeparationBar() {
		return separationBar;
	}

	public void setSeparationBar(boolean separationBar) {
		this.separationBar = separationBar;
	}

	public boolean isDensity() {
		return density;
	}

	public void setDensity(boolean density) {
		this.density = density;
	}

	public int getBins() {
		return bins;
	}

	public void setBins(int bins) {
		this.bins = bins;
	}

}
