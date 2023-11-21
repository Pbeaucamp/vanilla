package bpm.fd.runtime.model;

import java.util.Date;

import bpm.fd.api.core.model.FdProject;
import bpm.vanilla.platform.core.IObjectIdentifier;

/**
 * Used to keep track of the loading date of a dashboard
 * When we build a dahboard, we store within this object 
 * modificationDate of the different items used by the dashboard
 * 
 * After this, when a dahboard is called, we can check 
 * if it needs to be rebuilt because of an update, or still
 * can be used.
 * 
 * @author ludo
 *
 */
public class DashBoardMeta {
	private Date loadingDate;
	private IObjectIdentifier identifier;
	private String projectName;
	public DashBoardMeta(Date loadingDate, IObjectIdentifier identifier, FdProject project) {
		super();
		this.loadingDate = loadingDate;
		this.identifier = identifier;
		this.projectName = project.getProjectDescriptor().getProjectName();
	}

	/**
	 * @return the loadingDate
	 */
	public Date getLoadingDate() {
		return loadingDate;
	}

	/**
	 * @param loadingDate the loadingDate to set
	 */
	public void setLoadingDate(Date loadingDate) {
		this.loadingDate = loadingDate;
	}

	/**
	 * @return the identifier
	 */
	public IObjectIdentifier getIdentifier() {
		return identifier;
	}
	
	public String getIdentifierString(){
		StringBuilder b = new StringBuilder();
		b.append(identifier.getRepositoryId());
		b.append("_");
		b.append(identifier.getDirectoryItemId());
		b.append("_");
		b.append(projectName);
		return b.toString();
	}
}
