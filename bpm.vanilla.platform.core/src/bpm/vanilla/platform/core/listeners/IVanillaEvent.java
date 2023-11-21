package bpm.vanilla.platform.core.listeners;

import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.listeners.event.impl.ItemVersionEvent;
import bpm.vanilla.platform.core.listeners.event.impl.ObjectExecutedEvent;
import bpm.vanilla.platform.core.listeners.event.impl.ReportExecutedEvent;
import bpm.vanilla.platform.core.listeners.event.impl.RepositoryItemEvent;
import bpm.vanilla.platform.core.listeners.event.impl.VanillaActionEvent;

public interface IVanillaEvent {
	
	public static final String EVENT_OBJECT_EXECUTED = ObjectExecutedEvent.class.getName();
	public static final String EVENT_REPOSITORY_ITEM_UPDATED = RepositoryItemEvent.class.getName();
	public static final String EVENT_REPORT_GENERATED = ReportExecutedEvent.class.getName();
	public static final String EVENT_VANILLA_ACTION = VanillaActionEvent.class.getName();
	public static final String EVENT_ITEM_VERSION_UPDATED = ItemVersionEvent.class.getName();
	
	
//	public Object getOldValue();
//	public Object getNewValue();
//	public String getPropertyChangedName();
	
	public IVanillaComponentIdentifier getEventSourceComponent();
	
	public String getSessionId();
	
	public String getEventTypeName();
	
}
