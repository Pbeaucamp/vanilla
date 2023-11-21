package bpm.gwt.aklabox.commons.client.wizard;

public interface IGwtPage {
	
	public boolean canGoBack();
	
	public boolean isComplete();
	
	public boolean canGoFurther();
	
	public int getIndex();
}
