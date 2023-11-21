package bpm.gwt.workflow.commons.client;

public interface IManager<T> {

	public void loadResources();

	public void loadResources(java.util.List<T> resources);
}