package bpm.vanilla.platform.core.beans;

public class WidgetPosition {

	private int positionX;
	private int positionY;
	private int width;
	private int height;
	private int user;
	private int idWidgetPosition;
	
	private int widgetId;
	private String widgetURL;
	private Widgets widget;
	
	public String getWidgetURL() {
		return widgetURL;
	}

	public void setWidgetURL(String widgetURL) {
		this.widgetURL = widgetURL;
	}

	public int getWidgetId() {
		return widgetId;
	}

	public void setWidgetId(int widgetId) {
		this.widgetId = widgetId;
	}

	public int getIdWidgetPosition() {
		return idWidgetPosition;
	}

	public void setIdWidgetPosition(int idWidgetPosition) {
		this.idWidgetPosition = idWidgetPosition;
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

	public void setWidget(Widgets widget) {
		this.widget = widget;
	}

	public Widgets getWidget() {
		return widget;
	}

}
