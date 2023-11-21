package bpm.faweb.server.security;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import bpm.fa.api.olap.OLAPMember;
import bpm.fa.api.olap.unitedolap.UnitedOlapLoaderFactory;
import bpm.fa.api.repository.FaApiHelper;
import bpm.faweb.server.beans.InfosOlap;
import bpm.faweb.shared.drill.DrillthroughFilter;
import bpm.faweb.shared.drill.DrillthroughStock;
import bpm.fd.api.core.model.FdProject;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.gwt.commons.shared.analysis.DrillInformations;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class FaWebSession extends CommonSession {

	private HashMap<Integer, SessionContent> sessions = new HashMap<Integer, SessionContent>();

	private HashMap<Integer, DrillthroughStock> stockDrills = new HashMap<Integer, DrillthroughStock>();

	private String location;

	public FaWebSession() {
		super();
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Integer initSession() {
		if (sessions == null) {
			sessions = new HashMap<Integer, SessionContent>();
		}

		int newKey = 1;
		if (!sessions.isEmpty()) {
			newKey = Collections.max(sessions.keySet()) + 1;
		}

		sessions.put(newKey, new SessionContent());

		return newKey;
	}

	public FaApiHelper getFaApiHelper(int key) {
		return sessions.get(key).getFaApiHelper();
	}

	public void setFaApiHelper(int key, FaApiHelper repository) {
		sessions.get(key).setFaApiHelper(repository);
	}

	public InfosOlap getInfosOlap(int key) {
		return sessions.get(key).getInfosOlap();
	}
	
	public InfosOlap getInfosOlap() {
		return sessions.values().iterator().next().getInfosOlap();
	}

	public void addInfosOlap(int key, InfosOlap infos) {
		sessions.get(key).addInfosOlap(infos);
	}

	public List<String> getCreatedFiles(int key) {
		return sessions.get(key).getCreatedFiles();
	}

	public void setCreatedFiles(int key, List<String> createdFiles) {
		sessions.get(key).setCreatedFiles(createdFiles);
	}

	public void addCreatedFile(int key, String file) {
		sessions.get(key).addCreatedFile(file);
	}

	public void setCubeViews(int key, List<RepositoryItem> cubeViews) {
		sessions.get(key).setCubeViews(cubeViews);
	}

	public void removeSession(int keySession) {
		sessions.remove(keySession);
	}

	public List<RepositoryItem> getCubeViews(int key) {
		return sessions.get(key).getCubeViews();
	}

	public void setSelectedDatasource(int key, String selectedDatasource) {
		sessions.get(key).setSelectedDatasource(selectedDatasource);
	}

	public String getSelectedDatasource(int key) {
		return sessions.get(key).getSelectedDatasource();
	}

	public void setFdProject(int key, FdProject fdProject) {
		sessions.get(key).setFdProject(fdProject);
	}

	public FdProject getFdProject(int key) {
		return sessions.get(key).getFdProject();
	}

	public void setOlapMembers(int key, List<OLAPMember> olapMembers) {
		sessions.get(key).setOlapMembers(olapMembers);
	}

	public List<OLAPMember> getOlapMembers(int key) {
		return sessions.get(key).getOlapMembers();
	}

	public void addOlapMember(int key, OLAPMember olapMember) {
		sessions.get(key).addOlapMember(olapMember);
	}

	public void setReport(int key, String reportKey, ByteArrayInputStream in) {
		sessions.get(key).setReport(reportKey, in);
	}

	public ByteArrayInputStream getReport(int key, String name) {
		return sessions.get(key).getReport(name);
	}

	@Override
	public String getApplicationId() {
		return IRepositoryApi.FAWEB;
	}

	public class SessionContent {

		private FaApiHelper repository;

		private String selectedDatasource;
		private FdProject fdProject;
		private InfosOlap olaps;

		private List<RepositoryItem> cubeViews = new ArrayList<RepositoryItem>();
		private List<OLAPMember> olapMembers = new ArrayList<OLAPMember>();
		private List<String> createdFiles = new ArrayList<String>();

		private HashMap<String, ByteArrayInputStream> reports;

		public SessionContent() {
			olaps = new InfosOlap();
		}

		public FaApiHelper getFaApiHelper() {
			if (repository == null) {
				repository = new FaApiHelper(getVanillaRuntimeUrl(), UnitedOlapLoaderFactory.getLoader());
			}
			return repository;
		}

		public void setFaApiHelper(FaApiHelper repository) {
			this.repository = repository;
		}

		public InfosOlap getInfosOlap() {
			return olaps;
		}

		public void addInfosOlap(InfosOlap infos) {
			this.olaps = infos;
		}

		public List<String> getCreatedFiles() {
			return createdFiles;
		}

		public void setCreatedFiles(List<String> createdFiles) {
			this.createdFiles = createdFiles;
		}

		public void addCreatedFile(String file) {
			this.createdFiles.add(file);
		}

		public void setCubeViews(List<RepositoryItem> cubeViews) {
			this.cubeViews = cubeViews;
		}

		public List<RepositoryItem> getCubeViews() {
			return cubeViews;
		}

		public InfosOlap getOlaps() {
			return olaps;
		}

		public void setSelectedDatasource(String selectedDatasource) {
			this.selectedDatasource = selectedDatasource;
		}

		public String getSelectedDatasource() {
			return selectedDatasource;
		}

		public void setFdProject(FdProject fdProject) {
			this.fdProject = fdProject;
		}

		public FdProject getFdProject() {
			return fdProject;
		}

		public void setOlapMembers(List<OLAPMember> olapMembers) {
			this.olapMembers = olapMembers;
		}

		public List<OLAPMember> getOlapMembers() {
			return olapMembers;
		}

		public void addOlapMember(OLAPMember olapMember) {
			this.olapMembers.add(olapMember);
		}

		public void setReport(String key, ByteArrayInputStream in) {
			if (reports == null) {
				reports = new HashMap<String, ByteArrayInputStream>();
			}
			reports.put(key, in);
		}

		public ByteArrayInputStream getReport(String name) {
			return reports.get(name);
		}
	}

	public void stockDrill(int key, List<List<String>> drill) {
		stockDrills.put(key, new DrillthroughStock(drill));
	}

	public List<List<String>> getDrill(int key) {
		return stockDrills.get(key).getWorkingDrill() != null ? stockDrills.get(key).getWorkingDrill() : stockDrills.get(key).getOriginalDrill();
	}

	public DrillInformations applyDrillFilter(DrillInformations drillInfo, List<DrillthroughFilter> filters) {
		DrillthroughStock drill = stockDrills.get(drillInfo.getKey());
		if (drill != null) {
			List<List<String>> workingDrill = drill.getOriginalDrill();
			
			if(filters != null && !filters.isEmpty()) {
				workingDrill = filterDrill(drill.getOriginalDrill(), filters);
			}
			
			drill.setWorkingDrill(workingDrill);
			
			drillInfo.setSize(workingDrill.size());
		}
		return drillInfo;
	}

	public List<List<String>> getRange(int key, int start, int end, Integer indexSort, boolean ascending, boolean changed) {
		DrillthroughStock drill = stockDrills.get(key);
		if (drill != null) {
			List<List<String>> workingDrill = drill.getWorkingDrill() != null ? drill.getWorkingDrill() : drill.getOriginalDrill();

			if (changed) {
				if (indexSort != null) {
					sortDrill(workingDrill, indexSort, ascending);
				}
				
				drill.setWorkingDrill(workingDrill);
			}

			try {
				if (end <= 0) {
					return new ArrayList<List<String>>();
				}

				if (workingDrill.size() < end) {
					end = workingDrill.size();
				}

				List<List<String>> sublist = new ArrayList<List<String>>(workingDrill.subList(start, end));
				return sublist;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new ArrayList<List<String>>();
	}

	private List<List<String>> filterDrill(List<List<String>> drill, List<DrillthroughFilter> filters) {
		List<List<String>> workingDrill = new ArrayList<List<String>>();
		
		for(List<String> row : drill) {
			
			boolean addRow = true;
			for(DrillthroughFilter filter : filters) {
				String value = row.get(filter.getColumnIndex());
				if(!checkFilter(value, filter)) {
					addRow = false;
					break;
				}
			}
			
			if(addRow) {
				workingDrill.add(row);
			}
		}
		
		return workingDrill;
	}

	private boolean checkFilter(String value, DrillthroughFilter filter) {
		switch (filter.getType()) {
		case CONTAINS:
			return value.contains(filter.getValue());
		case DOES_NOT_CONTAINS:
			return !value.contains(filter.getValue());
		case STARTS_WITH:
			return value.startsWith(filter.getValue());
		case ENDS_WITH:
			return value.endsWith(filter.getValue());
		case GREATER_THAN:
			if (filter.getValue().matches("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?")) {
				try {
					double filterGreater = Double.parseDouble(filter.getValue());
					double valueGreater = Double.parseDouble(value);
					
					return valueGreater > filterGreater;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			else {
				return false;
			}
		case LESS_THAN:
			if (filter.getValue().matches("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?")) {
				try {
					double filterGreater = Double.parseDouble(filter.getValue());
					double valueGreater = Double.parseDouble(value);
					
					return valueGreater < filterGreater;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			else {
				return false;
			}
		case GREATER_THAN_OR_EQUAL_TO:
			if (filter.getValue().matches("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?")) {
				try {
					double filterGreaterOrEqual = Double.parseDouble(filter.getValue());
					double valueGreaterOrEqual = Double.parseDouble(value);
					
					return valueGreaterOrEqual >= filterGreaterOrEqual;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			else {
				return false;
			}
		case LESS_THAN_OR_EQUAL_TO:
			if (filter.getValue().matches("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?")) {
				try {
					double filterLessOrEqual = Double.parseDouble(filter.getValue());
					double valueLessOrEqual = Double.parseDouble(value);
					
					return valueLessOrEqual <= filterLessOrEqual;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			else {
				return false;
			}
		case BETWEEN:
			try {
				String filterBetween = filter.getValue();
				
				double startBetween = Double.parseDouble(filterBetween.substring(0, filterBetween.indexOf("-")));
				double endBetween = Double.parseDouble(filterBetween.substring(filterBetween.indexOf("-") + 1, filterBetween.length()));
				
				double valueBetween = Double.parseDouble(value);
				
				return valueBetween >= startBetween && valueBetween <= endBetween;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		case NOT_BETWEEN:
			try {
				String filterNotBetween = filter.getValue();
				
				double startNotBetween = Double.parseDouble(filterNotBetween.substring(0, filterNotBetween.indexOf("-")));
				double endNotBetween = Double.parseDouble(filterNotBetween.substring(filterNotBetween.indexOf("-") + 1, filterNotBetween.length()));
				
				double valueNotBetween = Double.parseDouble(value);
				
				return valueNotBetween < startNotBetween || valueNotBetween > endNotBetween;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		case IS_EMPTY:
			return value.isEmpty();
		case NOT_IS_EMPTY:
			return !value.isEmpty();
		default:
			break;
		}
		return false;
	}

	private void sortDrill(List<List<String>> drill, final int indexSort, final boolean ascending) {
		Collections.sort(drill, new Comparator<List<String>>() {

			@Override
			public int compare(List<String> o1, List<String> o2) {
				if(ascending) {
					return o1.get(indexSort).compareTo(o2.get(indexSort));
				}
				else {
					return o2.get(indexSort).compareTo(o1.get(indexSort));
				}
			}
		});
	}

	public void removeDrill(int key) {
		stockDrills.remove(key);
	}
}
