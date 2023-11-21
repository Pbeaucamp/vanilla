package bpm.vanilla.platform.core.beans;

import java.io.Serializable;

public class Widgets implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String widgetUrl = "";
	private int widgetId;
	private int positionX = 50;
	private int positionY = 50;
	private int width = 150;
	private int height = 150;
	private int user;
	
	public void setWidgetId(int widgetId) {
		this.widgetId = widgetId;
	}
	
	public int getWidgetId() {
		return widgetId;
	}

	public String getWidgetUrl() {
		return widgetUrl;
	}

	public void setWidgetUrl(String widgetUrl) {
		this.widgetUrl = widgetUrl;
	}

	public int getPositionX() {
		return positionX;
	}

	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}

	public int getPositionY() {
		return positionY;
	}

	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getUser() {
		return user;
	}

	public void setUser(int user) {
		this.user = user;
	}
	
}
