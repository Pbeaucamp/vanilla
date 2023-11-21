package bpm.vanilla.platform.core.runtime.components.listener;

import bpm.vanilla.platform.core.listeners.IVanillaEvent;
import bpm.vanilla.platform.core.listeners.IVanillaListener;

public class DummyListener implements IVanillaListener{
	private String name;
	public DummyListener(String name){
		this.name = name;
	}
	@Override
	public void handleEvent(IVanillaEvent event) {
		StringBuilder b = new StringBuilder();
		b.append("Event Handled by " + name + ":\n");
		b.append(" - id:" + event.getEventSourceComponent().getComponentId() + "\n");
		b.append(" - type:" + event.getEventTypeName() + "\n");
		
		
	}
	@Override
	public String[] getListenedEventTypes() {
		return new String[]{"bpm.vanilla.platform.core.listeners.event.impl.ObjectExecutedEvent"};
	}

}
