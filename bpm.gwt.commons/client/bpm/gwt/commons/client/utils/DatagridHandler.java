package bpm.gwt.commons.client.utils;

import com.google.gwt.dom.client.NativeEvent;

public interface DatagridHandler<T> {

	public void onRightClick(T item, NativeEvent event);

	public void onDoubleClick(T item);
}
