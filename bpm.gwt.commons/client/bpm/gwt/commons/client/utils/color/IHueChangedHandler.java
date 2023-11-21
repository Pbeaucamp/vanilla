package bpm.gwt.commons.client.utils.color;

import com.google.gwt.event.shared.EventHandler;

public interface IHueChangedHandler extends EventHandler {
	void hueChanged(HueChangedEvent event);
}
