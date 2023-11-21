package bpm.es.sessionmanager.gef.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.model.IWorkbenchAdapter;

public class FieldModel {
	
	private UserModel parent;

	private List<Link> sourceLinks = new ArrayList<Link>();
	private List<Link> targetLinks = new ArrayList<Link>();
	
	public FieldModel() {

	}
	
	public void setParent(UserModel m) {
		this.parent = m;
	}
	
	public String getFieldName() {
		return "test";
	}
	
	public UserModel getParent() {
		return parent;
	}
	
//	public Object getAdapter(Class adapter) {
//		if (adapter == IWorkbenchAdapter.class){
//			return this;
//		}
//		
//		return null;
//	}
//	
	///////////////////////////////////////////////////////////
	// for connections
	///////////////////////////////////////////////////////////
	
	public void addLink(Link conn) throws Exception{
		if (conn == null || conn.getSource() == conn.getTarget()) {
			throw new IllegalArgumentException();
		}

//		DBColumn target = conn.getTarget().getModel();
//		
//		mapping.addMapping(target.getName(), model);
//		Log.info("Added a mapping between DB : " + model.getFullName() + " and vanilla : " + target.getFullName());
//		//model.getDBTable().getDBSchema()
//		
//		if (conn.getSource() == this && ! sourceLinks.contains(conn)) {
//			sourceLinks.add(conn);
//			getListeners().firePropertyChange(SOURCE_CONNECTIONS_PROP, null, conn);
//		} else if (conn.getTarget() == this && !targetLinks.contains(conn)) {
//			targetLinks.add(conn);
//			getListeners().firePropertyChange(TARGET_CONNECTIONS_PROP, null, conn);
//			
//		}
		
	
	}
	
//	public DBColumn getModel() {
//		return model;
//	}
	
	public void removeLink(Link conn) {
		if (conn == null) {
			throw new IllegalArgumentException();
		}
//		NodeLinkerHelper.remove(conn);
		if (conn.getSource() == this) {
			sourceLinks.remove(conn);
			//getListeners().firePropertyChange(SOURCE_CONNECTIONS_PROP, null, conn);
		} else if (conn.getTarget() == this) {
			targetLinks.remove(conn);
			//getListeners().firePropertyChange(TARGET_CONNECTIONS_PROP, null, conn);
		}
		
//		NodeLinkerHelper.remove(conn);
	}
	
	public List<Link> getSourceLink() {
		return sourceLinks;
	}

	public List<Link> getTargetLink() {
		return targetLinks;
	}
}
