package bpm.fd.api.core.model.structure;

import java.awt.Point;

public interface IContainer {
	public static final String EVENT_LAYOUT = "layout";
	public Point getPosition();
	public void setPosition(int x, int y);
	public void setSize(int width, int height);
	public Point getSize();

}
