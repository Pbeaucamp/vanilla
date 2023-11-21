package bpm.master.core.remote;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bpm.master.core.IMasterService.ActionType;

public class RemoteAction implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private ActionType action;
	private List<Object> params;

	public RemoteAction(ActionType action, Object... params) {
		this.action = action;
		this.params = new ArrayList<>();
		if (params != null) {
			for (Object param : params) {
				this.params.add(param);
			}
		}
	}
	
	public ActionType getAction() {
		return action;
	}
	
	public Object getParam(int index) {
		return params != null && params.size() > index ? params.get(index) : null;
	}
}
