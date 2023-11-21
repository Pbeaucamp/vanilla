package bpm.faweb.client.history;

import java.util.List;
import java.util.Stack;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.services.FaWebService;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class ModificationHistory {
	private Stack before = new Stack();
	private Stack after = new Stack();
	private MainPanel parent;

	public ModificationHistory(MainPanel parent) {
		this.parent = parent;
	}

	public void memo(Object gc) {
		before.push(gc);
		parent.setUndo(false);
	}

	public void memo() {
		// rien
	}

	public Object back() {
		Object o = null;
		try {
			o = before.pop();
			after.push(o);
			undoredo(1, parent);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (before.isEmpty()) {
			return null;
		}
		return before.pop();
	}

	public Object forward() {
		Object o = null;
		try {
			o = after.pop();
			undoredo(2, parent);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return o;
	}

	public Stack getBefore() {
		return before;
	}

	public void setBefore(Stack all) {
		this.before = all;
	}

	public void undoredo(int type, final MainPanel parent) {

		FaWebService.Connect.getInstance().UndoRedoService(parent.getKeySession(), type, new AsyncCallback<List<String>>() {
			
			@Override
			public void onSuccess(List<String> result) {
				parent.getDisplayPanel().getCubeViewerTab().clearFilters();
				for (String s : result) {
					parent.getDisplayPanel().getCubeViewerTab().addFilterItem(s);
				}

				parent.showWaitPart(false);
			}

			@Override
			public void onFailure(Throwable ex) {
				parent.showWaitPart(false);
			}
		});
	}

}
