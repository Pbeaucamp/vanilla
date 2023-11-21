package bpm.fd.core.component;

import java.io.Serializable;

import bpm.fd.api.core.model.components.definition.chart.fusion.options.TypeTarget;

public class ChartDrill implements Serializable {

	private static final long serialVersionUID = 1L;
	private String url;
	private boolean categorieAsParameter = true;

	private TypeTarget typeTarget = TypeTarget.Parameter;
	private int zoomWidth = 500;
	private int zoomHeight = 500;

	private String targetPage;

	private boolean keepColor;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isCategorieAsParameter() {
		return categorieAsParameter;
	}

	public void setCategorieAsParameter(boolean categorieAsParameter) {
		this.categorieAsParameter = categorieAsParameter;
	}

	public TypeTarget getTypeTarget() {
		return typeTarget;
	}

	public void setTypeTarget(TypeTarget typeTarget) {
		this.typeTarget = typeTarget;
	}

	public int getZoomWidth() {
		return zoomWidth;
	}

	public void setZoomWidth(int zoomWidth) {
		this.zoomWidth = zoomWidth;
	}

	public int getZoomHeight() {
		return zoomHeight;
	}

	public void setZoomHeight(int zoomHeight) {
		this.zoomHeight = zoomHeight;
	}

	public String getTargetPage() {
		return targetPage;
	}

	public void setTargetPage(String targetPage) {
		this.targetPage = targetPage;
	}

	public boolean isKeepColor() {
		return keepColor;
	}

	public void setKeepColor(boolean keepColor) {
		this.keepColor = keepColor;
	}

}
