package bpm.vanilla.server.client.ui.clustering.menu.uolap.viewers;

import java.util.List;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import bpm.vanilla.designer.ui.common.dialogs.DialogGroupPickers;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.server.client.ui.clustering.menu.Activator;

public class GroupDialogCellEditor extends DialogCellEditor{

	public GroupDialogCellEditor(Composite parent) {
		super(parent);
	}

	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		List<Group> groups = null;
		
		if (getValue() != null){
			groups = (List<Group>)getValue();
		}
		DialogGroupPickers d = new DialogGroupPickers(cellEditorWindow.getShell(), 
				Activator.getDefault().getVanillaApi(), groups);
		
		if (d.open() == DialogGroupPickers.OK){
			return d.getGroups();
		}
		
		return null;
	}

}
