package bpm.fd.web.client.handlers;

import java.util.List;

public interface HasComponentSelectionHandler {
	
	public void addComponentSelectionHandler(IComponentSelectionHandler handler);
	
	public void removeComponentSelectionHandler(IComponentSelectionHandler handler);
	
	public List<IComponentSelectionHandler> getComponentSelectionHandlers();
	
	public void setModified(boolean isModified);
}