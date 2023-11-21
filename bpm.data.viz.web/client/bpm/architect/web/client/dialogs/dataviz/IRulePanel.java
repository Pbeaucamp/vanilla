package bpm.architect.web.client.dialogs.dataviz;

import bpm.data.viz.core.preparation.PreparationRule;
import bpm.vanilla.platform.core.beans.data.DataColumn;

public interface IRulePanel {

	public PreparationRule getRule();
	
	public void changeColumnSelection(DataColumn column);
	
}
