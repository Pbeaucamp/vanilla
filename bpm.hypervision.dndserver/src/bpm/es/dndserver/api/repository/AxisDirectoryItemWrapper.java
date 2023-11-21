package bpm.es.dndserver.api.repository;

import java.util.ArrayList;
import java.util.List;

import bpm.es.dndserver.api.fmdt.FMDTDataSource;
import bpm.es.dndserver.api.fmdt.FMDTMigration;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

/**
 * Wrapper object for our temporary adds
 * 
 * @author ereynard
 *
 */
public class AxisDirectoryItemWrapper {

	private RepositoryItem axisItem;
	
	private List<AxisDirectoryItemWrapper> dependencies = new ArrayList<AxisDirectoryItemWrapper>();
	
	private List<AxisDirectoryItemWrapper> metadataDependencies 
		= new ArrayList<AxisDirectoryItemWrapper>();
	
	//
	private List<FMDTMigration> migrations = new ArrayList<FMDTMigration>();
	private boolean isRemaped = false;
	
	private List<FMDTDataSource> existingSources;
	
	private RepositoryDirectory directory;
	
	public AxisDirectoryItemWrapper(RepositoryItem axisItem) {
		this.axisItem = axisItem;
	}
	
	/**
	 * is remaped or is replicated
	 * @return
	 */
	public boolean isRemaped() {
		for(AxisDirectoryItemWrapper i : metadataDependencies){
			if (i.isRemaped()){
				return true;
			}
		}
		return isRemaped;
	}
	
	public void setRemaped(boolean isRemaped) {
		this.isRemaped = isRemaped;
	}

	public RepositoryItem getAxisItem() {
		return axisItem;
	}

	public void setDirectory(RepositoryDirectory directory) {
		this.directory = directory;
	}
	
	public RepositoryDirectory getDirectory() {
		return directory;
	}
	
	public List<AxisDirectoryItemWrapper> getDependencies() {
		return dependencies;
	}
	
	public void setDependencies(List<AxisDirectoryItemWrapper> dependencies) {
		this.dependencies = dependencies;
	}
	
	public boolean hasMetaData() {
		return !metadataDependencies.isEmpty();
	}
	
	public List<AxisDirectoryItemWrapper> getMetadataDependencies() {
		return metadataDependencies;
	}
	
	public void setMetadataDependencies(List<AxisDirectoryItemWrapper> metadataDependencies) {
		this.metadataDependencies = metadataDependencies;
		
		//add incomplete migrations
		for (AxisDirectoryItemWrapper dep : metadataDependencies) {
			migrations.add(new FMDTMigration(dep.getAxisItem().getId(), null));
		}
	}
	
	
	
	public FMDTMigration getExistingMigration(int metadataDirItemId) {
		for (FMDTMigration mig : migrations) {
			if (mig.getDirItemId() == metadataDirItemId)
				return mig;
		}
		
		return null;
	}
//	
//	public void setExistingSources(List<FMDTDataSource> existingSources) {
//		this.existingSources = existingSources;
//	}
//	
//	public FMDTDataSource getExistingForFMDTDirItemId(int dirItemId) {
//		
//		for (FMDTDataSource source : existingSources) {
//			if (source.getDirItemId() == dirItemId) {
//				return source;
//			}
//		}
//		
//		return null;
//	}

	public boolean remapFullyDefined() {
		for(FMDTMigration m : migrations){
			if (m.getTarget() == null){
				return false;
			}
			else{
				if (m.getTarget().getDirItemId() <= 0 || m.getTarget().getBusinessModel() == null || m.getTarget().getBusinessModel().equals("") || m.getTarget().getBusinessPackage() == null || m.getTarget().getBusinessPackage().equals("")|| m.getTarget().getConnectionName() == null || m.getTarget().getConnectionName().equals("")){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					return false;
				}
			}
		}
		return true;
	}
}
