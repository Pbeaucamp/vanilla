package bpm.freemetrics.api.manager;

import java.util.ArrayList;
import java.util.List;

import bpm.freemetrics.api.organisation.application.Application;
import bpm.freemetrics.api.organisation.application.ApplicationDAO;
import bpm.freemetrics.api.organisation.group.Group;

public class DynamicAxeLoading implements IDynamicAxeLoading {

	private IManager manager;
	
	private ApplicationDAO dao;
	
	private int userId;
	private boolean isSuperUser;

	public DynamicAxeLoading(IManager manager) {
		this.manager = manager;
		dao = manager.getApplicationDAO();
	}
	
	@Override
	public List<Application> getApplicationForFilter(Object parent, String filter, int start, int end) throws Exception {
		return dao.getApplicationForFilter(parent, filter, start, end);
	}

	@Override
	public List<Group> getGroups(int userId, boolean isSuperUser) throws Exception {
		this.userId = userId;
		this.isSuperUser = isSuperUser;
		return manager.getAllowedGroupsForUser(userId, isSuperUser);
	}

	@Override
	public List<Application> getParentApplications(Application child, int start, int end) throws Exception {
		
		if(child.getParent() == null) {
			return loadChildren(child.getParentGroup(), start, end);
		}
		else {
			return loadChildren(child.getParent(), start, end);
		}
	}

	@Override
	public List<Application> loadChildren(Object parent, int start, int end) throws Exception {
		List<Application> apps = new ArrayList<Application>();

		if(parent instanceof Group) {
			apps = dao.getChildApplicationForGroup((Group) parent, start, end);
		}
		else {
			apps = dao.getChildApplicationForApp((Application) parent, start, end);
		}
		
		return apps;
	}

	@Override
	public int getChildrenCount(Object parent) throws Exception {
		if(parent == null) {
			return getGroups(userId, isSuperUser).size();
		}
		return dao.getChildrenCount(parent);
	}

	@Override
	public int getChildrenCount(Object previousParent, String text) {
		return dao.getChildrenCount(previousParent, text);
	}

	public void resetCache() {
		dao.resetCache();
	}
	
}
