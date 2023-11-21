package bpm.fd.api.core.model.components.definition.maps;

import org.dom4j.Element;

public interface IMapInfo {
	public int getWidth();
	public void setWidth(int width);
	public int getHeight();
	public void setHeight(int height);
	public void updateValues(IMapInfo info);
	
	public Element getElement();
}
