package bpm.sqldesigner.ui.editor;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IToolBarManager;

public class SQLDesignBarContributor extends ActionBarContributor {

	@Override
	protected void buildActions() {
//		addRetargetAction(new UndoRetargetAction());
//		addRetargetAction(new RedoRetargetAction());

		addRetargetAction(new ZoomInRetargetAction());
		addRetargetAction(new ZoomOutRetargetAction());
	}

	@Override
	protected void declareGlobalActionKeys() {

	}

	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
//		toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
//		toolBarManager.add(getAction(ActionFactory.REDO.getId()));

		toolBarManager.add(getAction(GEFActionConstants.ZOOM_IN));
		toolBarManager.add(getAction(GEFActionConstants.ZOOM_OUT));
		toolBarManager.add(new ZoomComboContributionItem(getPage()));
	}
}