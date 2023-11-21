package bpm.architect.web.client.panels;

import bpm.vanilla.platform.core.beans.resources.ClassRule;

import com.google.gwt.user.client.ui.IsWidget;

public interface IRulePanel extends IsWidget {

	public ClassRule getClassRule();
}
