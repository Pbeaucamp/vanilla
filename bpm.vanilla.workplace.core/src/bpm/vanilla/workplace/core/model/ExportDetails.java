package bpm.vanilla.workplace.core.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Role;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ExportDetails {

	private String name, desc, location;
	private Date date = Calendar.getInstance().getTime();

	private boolean allContent;

	private String exporterId = "";

	private boolean includeReports, includeLogs, includeCalendar;
	private boolean includeGroups, includeRoles, includeGrants;
	private boolean includeDataSources;
	private boolean replaceOld;
	private boolean ownerExporter;
	private boolean applyOwnerToOld;

	/**
	 * map between old groupId and old ObjectId
	 */

	private HashMap<Integer, List<Integer>> groupsItem = new HashMap<Integer, List<Integer>>();
	private HashMap<Integer, List<Integer>> groupsRole = new HashMap<Integer, List<Integer>>();

	private List<ImportItem> importItems = new ArrayList<ImportItem>();

	private List<Group> groups = new ArrayList<Group>();
	private List<Role> roles = new ArrayList<Role>();
	private List<ReportBean> reports = new ArrayList<ReportBean>();

	public ExportDetails() {
	}

	public String getExporterId() {
		return exporterId;
	}

	public Date getDate() {
		return date;
	}

	public void setExporterId(String exporterId) {
		this.exporterId = exporterId;
	}

	public void setDate(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
		try {
			this.date = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public List<ReportBean> getReportsBeans() {
		return reports;
	}

	public void addReportBean(ReportBean bean) {
		reports.add(bean);
	}

	public HashMap<Integer, List<Integer>> getGroupsRoles() {
		return groupsRole;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void addRole(Role r) {
		roles.add(r);
	}

	public void addGroupItem(String groupId, String dirItId) {
		if (groupsItem.get(Integer.parseInt(groupId)) == null) {
			groupsItem.put(Integer.parseInt(groupId), new ArrayList<Integer>());
		}
		groupsItem.get(Integer.parseInt(groupId)).add(Integer.parseInt(dirItId));
	}

	public void addGroupRole(String groupId, String roleId) {
		if (groupsRole.get(Integer.parseInt(groupId)) == null) {
			groupsRole.put(Integer.parseInt(groupId), new ArrayList<Integer>());
		}
		groupsRole.get(Integer.parseInt(groupId)).add(Integer.parseInt(roleId));
	}

	public void addGroup(Group g) {
		groups.add(g);
	}

	public void addImportItem(ImportItem item) {
		importItems.add(item);
	}

	public List<ImportItem> getImportItems() {
		return importItems;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setAllContent(boolean allContent) {
		this.allContent = allContent;
	}

	public void setAllContent(String allContent) {
		this.allContent = Boolean.parseBoolean(allContent);
	}

	public void addItem(RepositoryItem item, String path) {
		ImportItem it = new ImportItem();
		it.setDirectoryId(item.getDirectoryId());
		it.setName(item.getItemName());
		it.setType(item.getType());
		it.setPath(path);
		it.setId(item.getId());
		importItems.add(it);
	}

	public void setIncludeReports(boolean includeReports) {
		this.includeReports = includeReports;
	}

	public void setIncludeReports(String includeReports) {
		this.includeReports = Boolean.parseBoolean(includeReports);
	}

	public void setIncludeLogs(boolean includeLogs) {
		this.includeLogs = includeLogs;
	}

	public void setIncludeLogs(String includeLogs) {
		this.includeLogs = Boolean.parseBoolean(includeLogs);
	}

	public void setIncludeCalendar(boolean includeCalendar) {
		this.includeCalendar = includeCalendar;
	}

	public void setIncludeCalendar(String includeCalendar) {
		this.includeCalendar = Boolean.parseBoolean(includeCalendar);
	}

	public void setIncludeGroups(boolean includeGroups) {
		this.includeGroups = includeGroups;
	}

	public void setIncludeGroups(String includeGroups) {
		this.includeGroups = Boolean.parseBoolean(includeGroups);
	}

	public void setIncludeRoles(boolean includeRoles) {
		this.includeRoles = includeRoles;
	}

	public void setIncludeRoles(String includeRoles) {
		this.includeRoles = Boolean.parseBoolean(includeRoles);
	}

	public void setIncludeGrants(boolean includeGrants) {
		this.includeGrants = includeGrants;
	}

	public void setIncludeGrants(String includeGrants) {
		this.includeGrants = Boolean.parseBoolean(includeGrants);
	}

	public void setIncludeDataSources(boolean includeDataSources) {
		this.includeDataSources = includeDataSources;
	}

	public void setIncludeDataSources(String includeDataSources) {
		this.includeDataSources = Boolean.parseBoolean(includeDataSources);
	}

	public void setReplaceOld(boolean replaceOld) {
		this.replaceOld = replaceOld;
	}

	public void setReplaceOld(String replaceOld) {
		this.replaceOld = Boolean.parseBoolean(replaceOld);
	}

	public void setOwnerExporter(boolean ownerExporter) {
		this.ownerExporter = ownerExporter;
	}

	public void setOwnerExporter(String ownerExporter) {
		this.ownerExporter = Boolean.parseBoolean(ownerExporter);
	}

	public void setApplyOwnerToOld(boolean applyOwnerToOld) {
		this.applyOwnerToOld = applyOwnerToOld;
	}

	public void setApplyOwnerToOld(String applyOwnerToOld) {
		this.applyOwnerToOld = Boolean.parseBoolean(applyOwnerToOld);
	}

	private void lookForDependancies(List<ImportItem> toAdd, IRepositoryApi sock) throws Exception {
		for (ImportItem i : toAdd) {
			for (RepositoryItem ii : sock.getRepositoryService().getNeededItems(i.getId())) {
				ImportItem r = new ImportItem();
				r.setId(ii.getId());
				r.setName(ii.getItemName());
				r.setType(ii.getType());

				i.addNeeded(r.getId());

				boolean contained = false;

				for (ImportItem iii : importItems) {
					if (iii.getId() == r.getId()) {
						contained = true;
						break;
					}
				}

				for (ImportItem iii : toAdd) {
					if (iii.getId() == r.getId()) {
						contained = true;
						break;
					}
				}

				if (!contained) {
					r.setPath(getFullPath(sock, r.getId()));
					List<ImportItem> _toAdd = new ArrayList<ImportItem>();
					_toAdd.addAll(toAdd);
					_toAdd.add(r);
					lookForDependancies(_toAdd, sock);
				}
			}
		}
	}

	public String getFullPath(IRepositoryApi repositoryApi, int directoryItemId) throws Exception {
		RepositoryItem it = repositoryApi.getRepositoryService().getDirectoryItem(directoryItemId);
		if (it == null) {
			throw new Exception("No DirectoryItem with this id");
		}
		String s = "";

		RepositoryDirectory d = repositoryApi.getRepositoryService().getDirectory(it.getDirectoryId());

		if (d != null) {
			s = "/" + d.getName();
		}

		while (d != null && d.getParentId() != 0) {
			d = repositoryApi.getRepositoryService().getDirectory(d.getParentId());
			s = "/" + d.getName();
		}

		if (d == null) {
			throw new Exception("Unable to find a parent Directory ");
		}

		return s;
	}

	public void findDepedencies(IRepositoryApi sock) throws Exception {
		List<ImportItem> toAdd = new ArrayList<ImportItem>();

		for (ImportItem i : importItems) {
			for (RepositoryItem ii : sock.getRepositoryService().getNeededItems(i.getId())) {
				ImportItem r = new ImportItem();
				r.setId(ii.getId());
				r.setName(ii.getItemName());
				r.setType(ii.getType());

				i.addNeeded(r.getId());

				boolean contained = false;

				for (ImportItem iii : importItems) {
					if (iii.getId() == r.getId()) {
						contained = true;
						break;
					}
				}
				for (ImportItem iii : toAdd) {
					if (iii.getId() == r.getId()) {
						contained = true;
						break;
					}
				}

				if (!contained) {
					r.setPath(getFullPath(sock, r.getId()));
					RepositoryItem it = sock.getRepositoryService().getDirectoryItem(r.getId());
					if (it != null) {
						r.setDirectoryId(it.getDirectoryId());
					}

					toAdd.add(r);
				}
			}

		}
		lookForDependancies(toAdd, sock);
		importItems.addAll(toAdd);
	}

	public void purgeItems() {
		importItems.clear();
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public String getLocation() {
		return location;
	}

	public boolean isAllContent() {
		return allContent;
	}

	public boolean isIncludeReports() {
		return includeReports;
	}

	public boolean isIncludeLogs() {
		return includeLogs;
	}

	public boolean isIncludeCalendar() {
		return includeCalendar;
	}

	public boolean isIncludeGroups() {
		return includeGroups;
	}

	public boolean isIncludeRoles() {
		return includeRoles;
	}

	public boolean isIncludeGrants() {
		return includeGrants;
	}

	public boolean isIncludeDataSources() {
		return includeDataSources;
	}

	public boolean isReplaceOld() {
		return replaceOld;
	}

	public boolean isOwnerExporter() {
		return ownerExporter;
	}

	public boolean isApplyOwnerToOld() {
		return applyOwnerToOld;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public HashMap<Integer, List<Integer>> getGroupsItem() {
		return groupsItem;
	}

	public Group getGroupById(int groupId) {
		if (groups != null) {
			for (Group gr : groups) {
				if (gr.getId() == groupId) {
					return gr;
				}
			}
		}

		return null;
	}

}
