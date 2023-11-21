package bpm.gwt.commons.client.custom.v2;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler to catch with the items of a grid are loaded
 *
 * @param <T>
 */
public interface GridHandler<T> extends EventHandler {

	  public void onItemsLoad(List<T> items);
}
