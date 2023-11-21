package bpm.es.datasource.analyzer.remapper;


public interface IRemapperPerformer {
	
	public boolean checkModification() throws Exception;
	
	public void performModification() throws Exception;
	
	public String getTaskPerfomed();
//	public RepositoryItem getTargetRepositoryItem();
//	
//	public void setOriginalRequestedItem(RepositoryItem item);
//	
//	public void setNewRequestedItem(RepositoryItem item);
	
	
}
