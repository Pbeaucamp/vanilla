package bpm.architect.web.client.panels;

import com.google.gwt.user.client.ui.IsWidget;

import bpm.vanilla.platform.core.beans.resources.ClassRule;

public interface IRulePanel extends IsWidget {

	public ClassRule getClassRule();
}
