package bpm.gwt.aklabox.commons.client.panels;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;

public interface GridHandler<T> extends EventHandler {

	  public void onItemsLoad(List<T> items);
}
