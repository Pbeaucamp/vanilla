package bpm.fd.design.ui.editors.structure.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.action.Action;

import bpm.fd.api.core.model.structure.FactoryStructure;
import bpm.fd.api.core.model.structure.Table;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.structure.gef.editparts.TablePart;

public class ActionInsertRow extends Action{
	public static final String ID = "bpm.fd.design.ui.editors.structure.actions.ActionInsertRow"; //$NON-NLS-1$
	public static final String LABEL = Messages.ActionInsertRow_1;

	private List<EditPart> editParts = new ArrayList<EditPart>();
	private FactoryStructure factory;
	public ActionInsertRow(FactoryStructure factory, List<EditPart> editParts) {
		super(LABEL);
		this.editParts = editParts;
		this.factory = factory;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#getId()
	 */
	@Override
	public String getId() {
		
		return super.getId();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		for(EditPart e : editParts){
			if (e instanceof TablePart){
				((Table)((TablePart)e).getModel()).addDetailsRow(factory);
			}
		}
	}

	
	
	
}
