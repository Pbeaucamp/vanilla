package bpm.gateway.ui.ext;

import bpm.gateway.core.AbstractTransformation;

public class TransformationUIExtension {

	private Class<? extends AbstractTransformation>transformationClass;
	
	private String icon32;
	private String icon16;
	private String paletteDrawerId;
	
	/**
	 * @return the transformationClass
	 */
	public Class<? extends AbstractTransformation> getTransformationClass() {
		return transformationClass;
	}
	/**
	 * @param transformationClass the transformationClass to set
	 */
	public void setTransformationClass(Class<? extends AbstractTransformation> transformationClass) {
		this.transformationClass = transformationClass;
	}
	/**
	 * @return the icon32
	 */
	public String getIcon32() {
		return icon32;
	}
	/**
	 * @param icon32 the icon32 to set
	 */
	public void setIcon32(String icon32) {
		this.icon32 = icon32;
	}
	/**
	 * @return the icon16
	 */
	public String getIcon16() {
		return icon16;
	}
	/**
	 * @param icon16 the icon16 to set
	 */
	public void setIcon16(String icon16) {
		this.icon16 = icon16;
	}
	/**
	 * @return the paletteDrawerId
	 */
	public String getPaletteDrawerId() {
		return paletteDrawerId;
	}
	/**
	 * @param paletteDrawerId the paletteDrawerId to set
	 */
	public void setPaletteDrawerId(String paletteDrawerId) {
		this.paletteDrawerId = paletteDrawerId;
	}
	
	
	
}
