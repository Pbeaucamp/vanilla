package bpm.vanilla.platform.core.runtime.alerts;

import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.alerts.AlertConstants;
import bpm.vanilla.platform.core.beans.alerts.IAlertRuntime;
import bpm.vanilla.platform.core.listeners.event.impl.ItemVersionEvent;
import bpm.vanilla.platform.core.listeners.event.impl.RepositoryItemEvent;

public class AlertUpdateRuntime implements IAlertRuntime {

	private RepositoryItemEvent triggerEventRepositoryItem;
	private ItemVersionEvent triggerEventItemVersion;
	private Alert alert;

	public AlertUpdateRuntime(RepositoryItemEvent triggerEvent, Alert alert) {
		this.triggerEventRepositoryItem = triggerEvent;
		this.alert = alert;
	}

	public AlertUpdateRuntime(ItemVersionEvent triggerEvent, Alert alert) {
		this.triggerEventItemVersion = triggerEvent;
		this.alert = alert;
	}

	@Override
	public boolean checkAlert() throws Exception {
		if (alert.getState() != AlertConstants.ACTIVE) {
			return false;
		}

		if (triggerEventRepositoryItem != null && triggerEventRepositoryItem.getPropertyName().equals(RepositoryItemEvent.EVENT_REPOSITORY_ITEM_UPDATED)) {
			return true;
		}
		else if (triggerEventItemVersion != null && triggerEventItemVersion.getPropertyName().equals(ItemVersionEvent.EVENT_ITEM_VERSION_UPDATED)) {
			return true;
		}

		return false;
	}
}
